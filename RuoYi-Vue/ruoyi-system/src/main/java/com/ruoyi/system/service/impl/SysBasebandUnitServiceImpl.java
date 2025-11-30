package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysBasebandUnitMapper;
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.service.ISysBasebandUnitService;

/**
 * 基带单元Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@Service
public class SysBasebandUnitServiceImpl implements ISysBasebandUnitService 
{
    @Autowired
    private SysBasebandUnitMapper sysBasebandUnitMapper;

    /**
     * 查询基带单元
     * 
     * @param unitId 基带单元主键
     * @return 基带单元
     */
    @Override
    public SysBasebandUnit selectSysBasebandUnitByUnitId(Long unitId)
    {
        return sysBasebandUnitMapper.selectSysBasebandUnitByUnitId(unitId);
    }

    /**
     * 查询基带单元列表
     * 
     * @param sysBasebandUnit 基带单元
     * @return 基带单元
     */
    @Override
    public List<SysBasebandUnit> selectSysBasebandUnitList(SysBasebandUnit sysBasebandUnit)
    {
        return sysBasebandUnitMapper.selectSysBasebandUnitList(sysBasebandUnit);
    }

    /**
     * 新增基带单元
     * 
     * @param sysBasebandUnit 基带单元
     * @return 结果
     */
    @Override
    public int insertSysBasebandUnit(SysBasebandUnit sysBasebandUnit)
    {
        sysBasebandUnit.setCreateTime(DateUtils.getNowDate());
        return sysBasebandUnitMapper.insertSysBasebandUnit(sysBasebandUnit);
    }

    /**
     * 修改基带单元
     * 
     * @param sysBasebandUnit 基带单元
     * @return 结果
     */
    @Override
    public int updateSysBasebandUnit(SysBasebandUnit sysBasebandUnit)
    {
        sysBasebandUnit.setUpdateTime(DateUtils.getNowDate());
        return sysBasebandUnitMapper.updateSysBasebandUnit(sysBasebandUnit);
    }

    /**
     * 批量删除基带单元
     * 
     * @param unitIds 需要删除的基带单元主键
     * @return 结果
     */
    @Override
    public int deleteSysBasebandUnitByUnitIds(Long[] unitIds)
    {
        return sysBasebandUnitMapper.deleteSysBasebandUnitByUnitIds(unitIds);
    }

    /**
     * 删除基带单元信息
     * 
     * @param unitId 基带单元主键
     * @return 结果
     */
    @Override
    public int deleteSysBasebandUnitByUnitId(Long unitId)
    {
        return sysBasebandUnitMapper.deleteSysBasebandUnitByUnitId(unitId);
    }
}
