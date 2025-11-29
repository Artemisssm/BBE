package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.mapper.SysBasebandUnitMapper;
import com.ruoyi.system.service.ISysBasebandUnitService;

/**
 * 基带单元Service业务层
 */
@Service
public class SysBasebandUnitServiceImpl implements ISysBasebandUnitService
{
    @Autowired
    private SysBasebandUnitMapper basebandUnitMapper;

    @Override
    public List<SysBasebandUnit> selectBasebandUnitList(SysBasebandUnit unit)
    {
        return basebandUnitMapper.selectBasebandUnitList(unit);
    }

    @Override
    public SysBasebandUnit selectBasebandUnitById(Long unitId)
    {
        return basebandUnitMapper.selectBasebandUnitById(unitId);
    }

    @Override
    public int insertBasebandUnit(SysBasebandUnit unit)
    {
        return basebandUnitMapper.insertBasebandUnit(unit);
    }

    @Override
    public int updateBasebandUnit(SysBasebandUnit unit)
    {
        return basebandUnitMapper.updateBasebandUnit(unit);
    }

    @Override
    public int deleteBasebandUnitByIds(Long[] unitIds)
    {
        return basebandUnitMapper.deleteBasebandUnitByIds(unitIds);
    }

    @Override
    public int deleteBasebandUnitById(Long unitId)
    {
        return basebandUnitMapper.deleteBasebandUnitById(unitId);
    }

    @Override
    public boolean checkUnitNameUnique(SysBasebandUnit unit)
    {
        Long unitId = StringUtils.isNull(unit.getUnitId()) ? -1L : unit.getUnitId();
        SysBasebandUnit info = basebandUnitMapper.checkUnitNameUnique(unit.getUnitName());
        if (StringUtils.isNotNull(info) && info.getUnitId().longValue() != unitId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}


