package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysBasebandParamDef;
import com.ruoyi.system.domain.SysBasebandParamValue;
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.domain.dto.BasebandParamValueDto;
import com.ruoyi.system.domain.vo.BasebandParamVo;
import com.ruoyi.system.mapper.SysBasebandParamDefMapper;
import com.ruoyi.system.mapper.SysBasebandParamValueMapper;
import com.ruoyi.system.mapper.SysBasebandUnitMapper;
import com.ruoyi.system.service.ISysBasebandParamValueService;

/**
 * 参数值服务实现
 */
@Service
public class SysBasebandParamValueServiceImpl implements ISysBasebandParamValueService
{
    @Autowired
    private SysBasebandUnitMapper basebandUnitMapper;

    @Autowired
    private SysBasebandParamDefMapper paramDefMapper;

    @Autowired
    private SysBasebandParamValueMapper paramValueMapper;

    @Override
    public List<BasebandParamVo> selectParamVoByUnit(Long unitId)
    {
        return paramValueMapper.selectParamVoByUnitId(unitId);
    }

    @Override
    public void saveParamValues(Long unitId, List<BasebandParamValueDto> values, String operator)
    {
        if (StringUtils.isEmpty(values))
        {
            throw new ServiceException("参数列表不能为空");
        }
        SysBasebandUnit unit = basebandUnitMapper.selectBasebandUnitById(unitId);
        if (unit == null)
        {
            throw new ServiceException("单元不存在");
        }
        List<SysBasebandParamDef> defs = paramDefMapper.selectBasebandParamDefList(buildDefQuery(unit.getUnitType()));
        Map<Long, SysBasebandParamDef> defMap = defs.stream().collect(Collectors.toMap(SysBasebandParamDef::getParamId, d -> d));
        Date now = DateUtils.getNowDate();
        List<SysBasebandParamValue> entities = values.stream().map(dto -> {
            SysBasebandParamDef def = defMap.get(dto.getParamId());
            if (def == null)
            {
                throw new ServiceException("参数不存在或不属于该单元: " + dto.getParamId());
            }
            long uintVal = convertToUint(def, dto.getRawValue());
            SysBasebandParamValue entity = new SysBasebandParamValue();
            entity.setUnitId(unitId);
            entity.setParamId(dto.getParamId());
            entity.setRawValue(dto.getRawValue());
            entity.setUintValue(uintVal);
            entity.setUpdateBy(operator);
            entity.setUpdateTime(now);
            return entity;
        }).collect(Collectors.toList());
        paramValueMapper.batchUpsert(entities);
    }

    private SysBasebandParamDef buildDefQuery(String unitType)
    {
        SysBasebandParamDef query = new SysBasebandParamDef();
        query.setUnitType(unitType);
        return query;
    }

    private long convertToUint(SysBasebandParamDef def, String rawValue)
    {
        if (StringUtils.isEmpty(rawValue))
        {
            throw new ServiceException(def.getParamName() + " 的值不能为空");
        }
        String type = def.getValueType();
        long result;
        switch (type)
        {
            case "FLOAT":
                result = convertFloat(def, rawValue);
                break;
            case "UINT":
                result = convertUint(def, rawValue);
                break;
            case "ENUM":
            case "SWITCH":
                result = convertEnum(rawValue);
                break;
            default:
                throw new ServiceException("未知的值类型: " + type);
        }
        validateRange(def, result);
        return result;
    }

    private long convertFloat(SysBasebandParamDef def, String rawValue)
    {
        BigDecimal raw = new BigDecimal(rawValue);
        validateDecimalRange(def, raw);
        int scaleFactor = def.getScaleFactor() == null ? 1 : def.getScaleFactor();
        BigDecimal scaled = raw.multiply(BigDecimal.valueOf(scaleFactor));
        return scaled.setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private long convertUint(SysBasebandParamDef def, String rawValue)
    {
        BigDecimal raw = new BigDecimal(rawValue);
        validateDecimalRange(def, raw);
        return raw.longValueExact();
    }

    private long convertEnum(String rawValue)
    {
        return Long.parseLong(rawValue);
    }

    private void validateDecimalRange(SysBasebandParamDef def, BigDecimal raw)
    {
        if (Objects.nonNull(def.getMinValue()) && raw.compareTo(def.getMinValue()) < 0)
        {
            throw new ServiceException(def.getParamName() + " 小于最小值");
        }
        if (Objects.nonNull(def.getMaxValue()) && raw.compareTo(def.getMaxValue()) > 0)
        {
            throw new ServiceException(def.getParamName() + " 大于最大值");
        }
    }

    private void validateRange(SysBasebandParamDef def, long value)
    {
        if (def.getBitLength() == null)
        {
            return;
        }
        int bitLen = def.getBitLength();
        long max = bitLen >= 63 ? Long.MAX_VALUE : (1L << bitLen) - 1;
        if (value < 0 || value > max)
        {
            throw new ServiceException(def.getParamName() + " 超出位宽范围");
        }
    }
}


