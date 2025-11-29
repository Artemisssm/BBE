package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandUnit;

/**
 * 基带单元Mapper接口
 *
 * @author
 */
public interface SysBasebandUnitMapper
{
    /**
     * 查询单元列表
     *
     * @param unit 查询条件
     * @return 列表
     */
    public List<SysBasebandUnit> selectBasebandUnitList(SysBasebandUnit unit);

    /**
     * 按ID查询
     *
     * @param unitId ID
     * @return 单元
     */
    public SysBasebandUnit selectBasebandUnitById(Long unitId);

    /**
     * 新增
     *
     * @param unit 单元
     * @return 结果
     */
    public int insertBasebandUnit(SysBasebandUnit unit);

    /**
     * 修改
     *
     * @param unit 单元
     * @return 结果
     */
    public int updateBasebandUnit(SysBasebandUnit unit);

    /**
     * 批量删除
     *
     * @param unitIds id数组
     * @return 结果
     */
    public int deleteBasebandUnitByIds(Long[] unitIds);

    /**
     * 删除
     *
     * @param unitId id
     * @return 结果
     */
    public int deleteBasebandUnitById(Long unitId);

    /**
     * 按名称唯一性检查
     *
     * @param unitName 名称
     * @return 记录
     */
    public SysBasebandUnit checkUnitNameUnique(String unitName);
}


