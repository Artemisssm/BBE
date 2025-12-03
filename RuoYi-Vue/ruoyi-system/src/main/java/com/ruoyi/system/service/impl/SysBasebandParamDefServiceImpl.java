package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 批量更新参数排序
     * 
     * @param paramList 参数列表（包含paramId和sortOrder）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateSortOrder(List<SysBasebandParamDef> paramList)
    {
        // 参数校验
        if (paramList == null || paramList.isEmpty()) {
            throw new IllegalArgumentException("参数列表不能为空");
        }
        
        int result = 0;
        for (SysBasebandParamDef param : paramList) {
            // 校验参数ID和排序值
            if (param.getParamId() == null) {
                throw new IllegalArgumentException("参数ID不能为空");
            }
            if (param.getSortOrder() == null || param.getSortOrder() < 1) {
                throw new IllegalArgumentException("排序值必须大于0");
            }
            
            result += sysBasebandParamDefMapper.updateSortOrder(param.getParamId(), param.getSortOrder());
        }
        return result;
    }
}
