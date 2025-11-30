package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysBasebandParamDef;
import com.ruoyi.system.domain.SysBasebandParamValue;
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.mapper.SysBasebandParamDefMapper;
import com.ruoyi.system.mapper.SysBasebandParamValueMapper;
import com.ruoyi.system.mapper.SysBasebandUnitMapper;
import com.ruoyi.system.service.ISysBasebandParamValueService;

/**
 * 基带参数取值Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@Service
public class SysBasebandParamValueServiceImpl implements ISysBasebandParamValueService 
{
    private static final Logger log = LoggerFactory.getLogger(SysBasebandParamValueServiceImpl.class);

    @Autowired
    private SysBasebandParamValueMapper paramValueMapper;

    @Autowired
    private SysBasebandParamDefMapper paramDefMapper;

    @Autowired
    private SysBasebandUnitMapper unitMapper;

    /**
     * 根据单元ID获取参数配置列表（包含参数定义信息）
     * 
     * @param unitId 单元ID
     * @return 参数配置列表
     */
    @Override
    public List<Map<String, Object>> selectParamValueListByUnitId(Long unitId)
    {
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        // 获取单元信息
        SysBasebandUnit unit = unitMapper.selectSysBasebandUnitByUnitId(unitId);
        if (unit == null) {
            return resultList;
        }
        
        // 获取该单元类型的所有参数定义
        List<SysBasebandParamDef> paramDefs = paramDefMapper.selectSysBasebandParamDefByUnitType(unit.getUnitType());
        
        // 获取该单元已配置的参数值
        List<SysBasebandParamValue> paramValues = paramValueMapper.selectSysBasebandParamValueByUnitId(unitId);
        Map<Long, SysBasebandParamValue> valueMap = new HashMap<>();
        for (SysBasebandParamValue value : paramValues) {
            valueMap.put(value.getParamId(), value);
        }
        
        // 组合参数定义和参数值
        for (SysBasebandParamDef def : paramDefs) {
            Map<String, Object> item = new HashMap<>();
            item.put("paramId", def.getParamId());
            item.put("paramCode", def.getParamCode());
            item.put("paramName", def.getParamName());
            item.put("valueType", def.getValueType());
            item.put("enumOptions", def.getEnumOptions());
            item.put("minValue", def.getMinValue());
            item.put("maxValue", def.getMaxValue());
            item.put("scaleFactor", def.getScaleFactor());
            item.put("bitLength", def.getBitLength());
            item.put("hardwareOrder", def.getHardwareOrder());
            item.put("defaultValue", def.getDefaultValue());
            
            // 如果有配置值，使用配置值；否则使用默认值
            SysBasebandParamValue value = valueMap.get(def.getParamId());
            if (value != null) {
                item.put("rawValue", value.getRawValue());
                item.put("valueId", value.getValueId());
            } else {
                item.put("rawValue", def.getDefaultValue());
                item.put("valueId", null);
            }
            
            resultList.add(item);
        }
        
        return resultList;
    }

    /**
     * 保存单元的参数配置
     * 
     * @param unitId 单元ID
     * @param paramValues 参数值列表
     * @return 结果
     */
    @Override
    @Transactional
    public int saveParamValues(Long unitId, List<Map<String, Object>> paramValues)
    {
        String username = SecurityUtils.getUsername();
        int count = 0;
        
        for (Map<String, Object> item : paramValues) {
            Long paramId = Long.valueOf(item.get("paramId").toString());
            String rawValue = item.get("rawValue").toString();
            
            // 获取参数定义
            SysBasebandParamDef paramDef = paramDefMapper.selectSysBasebandParamDefByParamId(paramId);
            if (paramDef == null) {
                continue;
            }
            
            // 计算uint_value
            Long uintValue = calculateUintValue(rawValue, paramDef);
            
            // 查询是否已存在
            SysBasebandParamValue existValue = paramValueMapper.selectSysBasebandParamValueByUnitAndParam(unitId, paramId);
            
            if (existValue != null) {
                // 更新
                existValue.setRawValue(rawValue);
                existValue.setUintValue(uintValue);
                existValue.setUpdateBy(username);
                existValue.setUpdateTime(DateUtils.getNowDate());
                count += paramValueMapper.updateSysBasebandParamValue(existValue);
            } else {
                // 新增
                SysBasebandParamValue newValue = new SysBasebandParamValue();
                newValue.setUnitId(unitId);
                newValue.setParamId(paramId);
                newValue.setRawValue(rawValue);
                newValue.setUintValue(uintValue);
                newValue.setUpdateBy(username);
                newValue.setUpdateTime(DateUtils.getNowDate());
                count += paramValueMapper.insertSysBasebandParamValue(newValue);
            }
        }
        
        return count;
    }

    @Autowired
    private com.ruoyi.system.service.IBasebandDispatchService dispatchService;

    /**
     * 下发参数到硬件设备
     * 
     * @param unitId 单元ID
     * @return 结果
     */
    @Override
    public int dispatchParams(Long unitId)
    {
        try {
            String username = SecurityUtils.getUsername();
            dispatchService.dispatch(unitId, username, "MANUAL");
            return 1;
        } catch (Exception e) {
            log.error("下发参数失败: unitId={}", unitId, e);
            throw e;
        }
    }

    /**
     * 计算uint_value
     * 根据参数类型和缩放因子，将原始值转换为无符号整数
     */
    private Long calculateUintValue(String rawValue, SysBasebandParamDef paramDef)
    {
        try {
            String valueType = paramDef.getValueType();
            Integer scaleFactor = paramDef.getScaleFactor() != null ? paramDef.getScaleFactor() : 1;
            
            if ("ENUM".equals(valueType) || "SWITCH".equals(valueType)) {
                // 枚举和开关类型，直接转换
                return Long.valueOf(rawValue);
            } else if ("UINT".equals(valueType)) {
                // 无符号整数，乘以缩放因子
                return Long.valueOf(rawValue) * scaleFactor;
            } else if ("FLOAT".equals(valueType)) {
                // 浮点数，乘以缩放因子后转为整数
                BigDecimal decimal = new BigDecimal(rawValue);
                BigDecimal scaled = decimal.multiply(new BigDecimal(scaleFactor));
                return scaled.longValue();
            }
            
            return Long.valueOf(rawValue);
        } catch (Exception e) {
            log.error("计算uint_value失败: rawValue={}, paramDef={}", rawValue, paramDef, e);
            return 0L;
        }
    }
}
