package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysBasebandMacroMapper;
import com.ruoyi.system.mapper.SysBasebandMacroParamMapper;
import com.ruoyi.system.domain.SysBasebandMacro;
import com.ruoyi.system.service.ISysBasebandMacroService;

/**
 * 基带宏定义Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
@Service
public class SysBasebandMacroServiceImpl implements ISysBasebandMacroService 
{
    @Autowired
    private SysBasebandMacroMapper sysBasebandMacroMapper;

    @Autowired
    private SysBasebandMacroParamMapper macroParamMapper;

    /**
     * 查询基带宏定义
     * 
     * @param macroId 基带宏定义主键
     * @return 基带宏定义
     */
    @Override
    public SysBasebandMacro selectSysBasebandMacroByMacroId(Long macroId)
    {
        return sysBasebandMacroMapper.selectSysBasebandMacroByMacroId(macroId);
    }

    /**
     * 查询基带宏定义列表
     * 
     * @param sysBasebandMacro 基带宏定义
     * @return 基带宏定义
     */
    @Override
    public List<SysBasebandMacro> selectSysBasebandMacroList(SysBasebandMacro sysBasebandMacro)
    {
        return sysBasebandMacroMapper.selectSysBasebandMacroList(sysBasebandMacro);
    }

    /**
     * 新增基带宏定义
     * 
     * @param sysBasebandMacro 基带宏定义
     * @return 结果
     */
    @Override
    public int insertSysBasebandMacro(SysBasebandMacro sysBasebandMacro)
    {
        sysBasebandMacro.setCreateTime(DateUtils.getNowDate());
        return sysBasebandMacroMapper.insertSysBasebandMacro(sysBasebandMacro);
    }

    /**
     * 修改基带宏定义
     * 
     * @param sysBasebandMacro 基带宏定义
     * @return 结果
     */
    @Override
    public int updateSysBasebandMacro(SysBasebandMacro sysBasebandMacro)
    {
        sysBasebandMacro.setUpdateTime(DateUtils.getNowDate());
        return sysBasebandMacroMapper.updateSysBasebandMacro(sysBasebandMacro);
    }

    /**
     * 批量删除基带宏定义
     * 
     * @param macroIds 需要删除的基带宏定义主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysBasebandMacroByMacroIds(Long[] macroIds)
    {
        // 删除宏时，同时删除关联的参数配置
        macroParamMapper.deleteMacroParamsByMacroIds(macroIds);
        return sysBasebandMacroMapper.deleteSysBasebandMacroByMacroIds(macroIds);
    }

    /**
     * 删除基带宏定义信息
     * 
     * @param macroId 基带宏定义主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSysBasebandMacroByMacroId(Long macroId)
    {
        // 删除宏时，同时删除关联的参数配置
        macroParamMapper.deleteMacroParamsByMacroId(macroId);
        return sysBasebandMacroMapper.deleteSysBasebandMacroByMacroId(macroId);
    }

    /**
     * 根据单元类型和模式类型查询可用宏
     * 
     * @param unitType 单元类型
     * @param modeType 模式类型
     * @return 宏列表
     */
    @Override
    public List<SysBasebandMacro> selectAvailableMacros(String unitType, String modeType)
    {
        return sysBasebandMacroMapper.selectAvailableMacros(unitType, modeType);
    }

    /**
     * 获取宏的参数配置
     * 
     * @param macroId 宏ID
     * @return 参数配置列表
     */
    @Override
    public List<Map<String, Object>> getMacroParams(Long macroId)
    {
        return macroParamMapper.selectMacroParams(macroId);
    }

    /**
     * 保存宏的参数配置
     * 
     * @param macroId 宏ID
     * @param params 参数配置列表
     * @return 结果
     */
    @Override
    @Transactional
    public int saveMacroParams(Long macroId, List<Map<String, Object>> params)
    {
        // 1. 删除该宏的所有旧参数配置
        macroParamMapper.deleteMacroParamsByMacroId(macroId);
        
        // 2. 批量插入新的参数配置（先去重，防止重复paramId导致唯一键冲突）
        if (params != null && !params.isEmpty()) {
            // 使用Map去重，相同paramId只保留最后一个
            Map<Long, Map<String, Object>> uniqueParams = new java.util.LinkedHashMap<>();
            for (Map<String, Object> param : params) {
                Long paramId = Long.valueOf(param.get("paramId").toString());
                uniqueParams.put(paramId, param);
            }
            
            // 插入去重后的参数
            for (Map<String, Object> param : uniqueParams.values()) {
                Long paramId = Long.valueOf(param.get("paramId").toString());
                String paramValue = param.get("paramValue") != null ? param.get("paramValue").toString() : "";
                macroParamMapper.insertMacroParam(macroId, paramId, paramValue);
            }
        }
        
        return 1;
    }

    /**
     * 检查宏编码是否唯一
     * 
     * @param macro 宏信息
     * @return 结果
     */
    @Override
    public String checkMacroCodeUnique(SysBasebandMacro macro)
    {
        Long macroId = StringUtils.isNull(macro.getMacroId()) ? -1L : macro.getMacroId();
        SysBasebandMacro info = sysBasebandMacroMapper.checkMacroCodeUnique(macro.getMacroCode());
        if (StringUtils.isNotNull(info) && info.getMacroId().longValue() != macroId.longValue())
        {
            return "1"; // 不唯一
        }
        return "0"; // 唯一
    }
}
