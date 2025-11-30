package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandParamDef;

/**
 * 基带参数定义Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public interface SysBasebandParamDefMapper 
{
    /**
     * 查询基带参数定义
     * 
     * @param paramId 基带参数定义主键
     * @return 基带参数定义
     */
    public SysBasebandParamDef selectSysBasebandParamDefByParamId(Long paramId);

    /**
     * 查询基带参数定义列表
     * 
     * @param sysBasebandParamDef 基带参数定义
     * @return 基带参数定义集合
     */
    public List<SysBasebandParamDef> selectSysBasebandParamDefList(SysBasebandParamDef sysBasebandParamDef);

    /**
     * 根据单元类型查询参数定义列表
     * 
     * @param unitType 单元类型
     * @return 基带参数定义集合
     */
    public List<SysBasebandParamDef> selectSysBasebandParamDefByUnitType(String unitType);

    /**
     * 新增基带参数定义
     * 
     * @param sysBasebandParamDef 基带参数定义
     * @return 结果
     */
    public int insertSysBasebandParamDef(SysBasebandParamDef sysBasebandParamDef);

    /**
     * 修改基带参数定义
     * 
     * @param sysBasebandParamDef 基带参数定义
     * @return 结果
     */
    public int updateSysBasebandParamDef(SysBasebandParamDef sysBasebandParamDef);

    /**
     * 删除基带参数定义
     * 
     * @param paramId 基带参数定义主键
     * @return 结果
     */
    public int deleteSysBasebandParamDefByParamId(Long paramId);

    /**
     * 批量删除基带参数定义
     * 
     * @param paramIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysBasebandParamDefByParamIds(Long[] paramIds);
}
