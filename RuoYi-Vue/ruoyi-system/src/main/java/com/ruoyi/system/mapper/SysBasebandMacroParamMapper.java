package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 宏参数配置Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
public interface SysBasebandMacroParamMapper 
{
    /**
     * 查询宏的参数配置（关联参数定义）
     * 
     * @param macroId 宏ID
     * @return 参数配置列表
     */
    public List<Map<String, Object>> selectMacroParams(@Param("macroId") Long macroId);

    /**
     * 删除宏的所有参数配置
     * 
     * @param macroId 宏ID
     * @return 结果
     */
    public int deleteMacroParamsByMacroId(@Param("macroId") Long macroId);

    /**
     * 批量删除宏的参数配置
     * 
     * @param macroIds 宏ID数组
     * @return 结果
     */
    public int deleteMacroParamsByMacroIds(@Param("macroIds") Long[] macroIds);

    /**
     * 插入宏参数配置
     * 
     * @param macroId 宏ID
     * @param paramId 参数ID
     * @param paramValue 参数值
     * @return 结果
     */
    public int insertMacroParam(@Param("macroId") Long macroId, 
                                @Param("paramId") Long paramId, 
                                @Param("paramValue") String paramValue);

    /**
     * 统计宏的参数数量
     * 
     * @param macroId 宏ID
     * @return 参数数量
     */
    public int countMacroParams(@Param("macroId") Long macroId);
}
