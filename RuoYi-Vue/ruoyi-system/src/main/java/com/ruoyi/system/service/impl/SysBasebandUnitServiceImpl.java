package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.SysBasebandUnitMapper;
import com.ruoyi.system.mapper.SysBasebandMacroParamMapper;
import com.ruoyi.system.mapper.SysBasebandParamValueMapper;
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.domain.SysBasebandParamValue;
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

    @Autowired
    private SysBasebandMacroParamMapper macroParamMapper;

    @Autowired
    private SysBasebandParamValueMapper paramValueMapper;

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
     * 如果指定了宏配置ID，会自动复制宏的参数到单元参数值表
     * 
     * @param sysBasebandUnit 基带单元
     * @return 结果
     */
    @Override
    @Transactional
    public int insertSysBasebandUnit(SysBasebandUnit sysBasebandUnit)
    {
        sysBasebandUnit.setCreateTime(DateUtils.getNowDate());
        int result = sysBasebandUnitMapper.insertSysBasebandUnit(sysBasebandUnit);
        
        // 如果指定了宏配置，复制宏参数到单元参数值表
        if (result > 0 && sysBasebandUnit.getMacroId() != null) {
            copyMacroParamsToUnit(sysBasebandUnit.getUnitId(), sysBasebandUnit.getMacroId());
        }
        
        return result;
    }

    /**
     * 复制宏参数到单元参数值表
     * 
     * @param unitId 单元ID
     * @param macroId 宏ID
     */
    private void copyMacroParamsToUnit(Long unitId, Long macroId)
    {
        // 获取宏的参数配置
        List<Map<String, Object>> macroParams = macroParamMapper.selectMacroParams(macroId);
        
        if (macroParams == null || macroParams.isEmpty()) {
            return;
        }
        
        // 复制每个参数到单元参数值表
        for (Map<String, Object> param : macroParams) {
            Long paramId = ((Number) param.get("paramId")).longValue();
            String paramValue = (String) param.get("paramValue");
            
            if (paramId != null && paramValue != null) {
                SysBasebandParamValue value = new SysBasebandParamValue();
                value.setUnitId(unitId);
                value.setParamId(paramId);
                value.setRawValue(paramValue);
                value.setUpdateTime(DateUtils.getNowDate());
                
                // 设置uintValue，尝试将rawValue转换为Long，失败则设为0
                try {
                    value.setUintValue(Long.valueOf(paramValue));
                } catch (NumberFormatException e) {
                    value.setUintValue(0L);
                }
                
                paramValueMapper.insertSysBasebandParamValue(value);
            }
        }
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
