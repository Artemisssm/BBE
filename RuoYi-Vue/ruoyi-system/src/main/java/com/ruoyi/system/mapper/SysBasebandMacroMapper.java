package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandMacro;

/**
 * 基带宏定义Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
public interface SysBasebandMacroMapper 
{
    /**
     * 查询基带宏定义
     * 
     * @param macroId 基带宏定义主键
     * @return 基带宏定义
     */
    public SysBasebandMacro selectSysBasebandMacroByMacroId(Long macroId);

    /**
     * 查询基带宏定义列表
     * 
     * @param sysBasebandMacro 基带宏定义
     * @return 基带宏定义集合
     */
    public List<SysBasebandMacro> selectSysBasebandMacroList(SysBasebandMacro sysBasebandMacro);

    /**
     * 新增基带宏定义
     * 
     * @param sysBasebandMacro 基带宏定义
     * @return 结果
     */
    public int insertSysBasebandMacro(SysBasebandMacro sysBasebandMacro);

    /**
     * 修改基带宏定义
     * 
     * @param sysBasebandMacro 基带宏定义
     * @return 结果
     */
    public int updateSysBasebandMacro(SysBasebandMacro sysBasebandMacro);

    /**
     * 删除基带宏定义
     * 
     * @param macroId 基带宏定义主键
     * @return 结果
     */
    public int deleteSysBasebandMacroByMacroId(Long macroId);

    /**
     * 批量删除基带宏定义
     * 
     * @param macroIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysBasebandMacroByMacroIds(Long[] macroIds);

    /**
     * 根据单元类型和模式类型查询可用宏
     * 
     * @param unitType 单元类型
     * @param modeType 模式类型
     * @return 宏列表
     */
    public List<SysBasebandMacro> selectAvailableMacros(@org.apache.ibatis.annotations.Param("unitType") String unitType, 
                                                        @org.apache.ibatis.annotations.Param("modeType") String modeType);

    /**
     * 检查宏编码是否唯一
     * 
     * @param macroCode 宏编码
     * @return 结果
     */
    public SysBasebandMacro checkMacroCodeUnique(String macroCode);
}
