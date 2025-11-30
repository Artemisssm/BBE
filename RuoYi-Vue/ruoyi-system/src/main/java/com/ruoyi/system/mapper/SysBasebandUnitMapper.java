package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandUnit;

/**
 * 基带单元Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public interface SysBasebandUnitMapper 
{
    /**
     * 查询基带单元
     * 
     * @param unitId 基带单元主键
     * @return 基带单元
     */
    public SysBasebandUnit selectSysBasebandUnitByUnitId(Long unitId);

    /**
     * 查询基带单元列表
     * 
     * @param sysBasebandUnit 基带单元
     * @return 基带单元集合
     */
    public List<SysBasebandUnit> selectSysBasebandUnitList(SysBasebandUnit sysBasebandUnit);

    /**
     * 新增基带单元
     * 
     * @param sysBasebandUnit 基带单元
     * @return 结果
     */
    public int insertSysBasebandUnit(SysBasebandUnit sysBasebandUnit);

    /**
     * 修改基带单元
     * 
     * @param sysBasebandUnit 基带单元
     * @return 结果
     */
    public int updateSysBasebandUnit(SysBasebandUnit sysBasebandUnit);

    /**
     * 删除基带单元
     * 
     * @param unitId 基带单元主键
     * @return 结果
     */
    public int deleteSysBasebandUnitByUnitId(Long unitId);

    /**
     * 批量删除基带单元
     * 
     * @param unitIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysBasebandUnitByUnitIds(Long[] unitIds);
}
