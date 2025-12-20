package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysBasebandMacro;

/**
 * 基带宏定义Service接口
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
public interface ISysBasebandMacroService 
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
     * 批量删除基带宏定义
     * 
     * @param macroIds 需要删除的基带宏定义主键集合
     * @return 结果
     */
    public int deleteSysBasebandMacroByMacroIds(Long[] macroIds);

    /**
     * 删除基带宏定义信息
     * 
     * @param macroId 基带宏定义主键
     * @return 结果
     */
    public int deleteSysBasebandMacroByMacroId(Long macroId);

    /**
     * 根据单元类型和模式类型查询可用宏
     * 
     * @param unitType 单元类型
     * @param modeType 模式类型
     * @return 宏列表
     */
    public List<SysBasebandMacro> selectAvailableMacros(String unitType, String modeType);

    /**
     * 获取宏的参数配置
     * 
     * @param macroId 宏ID
     * @return 参数配置列表
     */
    public List<Map<String, Object>> getMacroParams(Long macroId);

    /**
     * 保存宏的参数配置
     * 
     * @param macroId 宏ID
     * @param params 参数配置列表
     * @return 结果
     */
    public int saveMacroParams(Long macroId, List<Map<String, Object>> params);

    /**
     * 检查宏编码是否唯一
     * 
     * @param macro 宏信息
     * @return 结果
     */
    public String checkMacroCodeUnique(SysBasebandMacro macro);
}
