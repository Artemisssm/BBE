package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysBasebandParamDefMapper;
import com.ruoyi.system.domain.SysBasebandParamDef;
import com.ruoyi.system.service.ISysBasebandParamDefService;

/**
 * 基带参数定义Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@Service
public class SysBasebandParamDefServiceImpl implements ISysBasebandParamDefService 
{
    @Autowired
    private SysBasebandParamDefMapper sysBasebandParamDefMapper;

    /**
     * 查询基带参数定义
     * 
     * @param paramId 基带参数定义主键
     * @return 基带参数定义
     */
    @Override
    public SysBasebandParamDef selectSysBasebandParamDefByParamId(Long paramId)
    {
        return sysBasebandParamDefMapper.selectSysBasebandParamDefByParamId(paramId);
    }

    /**
     * 查询基带参数定义列表
     * 
     * @param sysBasebandParamDef 基带参数定义
     * @return 基带参数定义
     */
    @Override
    public List<SysBasebandParamDef> selectSysBasebandParamDefList(SysBasebandParamDef sysBasebandParamDef)
    {
        return sysBasebandParamDefMapper.selectSysBasebandParamDefList(sysBasebandParamDef);
    }

    /**
     * 新增基带参数定义
     * 
     * @param sysBasebandParamDef 基带参数定义
     * @return 结果
     */
    @Override
    public int insertSysBasebandParamDef(SysBasebandParamDef sysBasebandParamDef)
    {
        sysBasebandParamDef.setCreateTime(DateUtils.getNowDate());
        return sysBasebandParamDefMapper.insertSysBasebandParamDef(sysBasebandParamDef);
    }

    /**
     * 修改基带参数定义
     * 
     * @param sysBasebandParamDef 基带参数定义
     * @return 结果
     */
    @Override
    public int updateSysBasebandParamDef(SysBasebandParamDef sysBasebandParamDef)
    {
        return sysBasebandParamDefMapper.updateSysBasebandParamDef(sysBasebandParamDef);
    }

    /**
     * 批量删除基带参数定义
     * 
     * @param paramIds 需要删除的基带参数定义主键
     * @return 结果
     */
    @Override
    public int deleteSysBasebandParamDefByParamIds(Long[] paramIds)
    {
        return sysBasebandParamDefMapper.deleteSysBasebandParamDefByParamIds(paramIds);
    }

    /**
     * 删除基带参数定义信息
     * 
     * @param paramId 基带参数定义主键
     * @return 结果
     */
    @Override
    public int deleteSysBasebandParamDefByParamId(Long paramId)
    {
        return sysBasebandParamDefMapper.deleteSysBasebandParamDefByParamId(paramId);
    }
}
