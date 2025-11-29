package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandUnit;

/**
 * 基带单元Service接口
 */
public interface ISysBasebandUnitService
{
    /**
     * 查询列表
     */
    public List<SysBasebandUnit> selectBasebandUnitList(SysBasebandUnit unit);

    /**
     * 通过ID查询
     */
    public SysBasebandUnit selectBasebandUnitById(Long unitId);

    /**
     * 新增
     */
    public int insertBasebandUnit(SysBasebandUnit unit);

    /**
     * 修改
     */
    public int updateBasebandUnit(SysBasebandUnit unit);

    /**
     * 批量删除
     */
    public int deleteBasebandUnitByIds(Long[] unitIds);

    /**
     * 单个删除
     */
    public int deleteBasebandUnitById(Long unitId);

    /**
     * 校验名称唯一
     */
    public boolean checkUnitNameUnique(SysBasebandUnit unit);
}


