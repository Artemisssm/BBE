package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysBasebandParamDef;
import com.ruoyi.system.mapper.SysBasebandParamDefMapper;
import com.ruoyi.system.service.ISysBasebandParamDefService;

/**
 * 基带参数定义Service业务层
 */
@Service
public class SysBasebandParamDefServiceImpl implements ISysBasebandParamDefService
{
    @Autowired
    private SysBasebandParamDefMapper paramDefMapper;

    @Override
    public List<SysBasebandParamDef> selectBasebandParamDefList(SysBasebandParamDef paramDef)
    {
        return paramDefMapper.selectBasebandParamDefList(paramDef);
    }

    @Override
    public SysBasebandParamDef selectBasebandParamDefById(Long paramId)
    {
        return paramDefMapper.selectBasebandParamDefById(paramId);
    }

    @Override
    public int insertBasebandParamDef(SysBasebandParamDef paramDef)
    {
        return paramDefMapper.insertBasebandParamDef(paramDef);
    }

    @Override
    public int updateBasebandParamDef(SysBasebandParamDef paramDef)
    {
        return paramDefMapper.updateBasebandParamDef(paramDef);
    }

    @Override
    public int deleteBasebandParamDefByIds(Long[] paramIds)
    {
        return paramDefMapper.deleteBasebandParamDefByIds(paramIds);
    }

    @Override
    public int deleteBasebandParamDefById(Long paramId)
    {
        return paramDefMapper.deleteBasebandParamDefById(paramId);
    }

    @Override
    public boolean checkParamCodeUnique(SysBasebandParamDef paramDef)
    {
        Long paramId = StringUtils.isNull(paramDef.getParamId()) ? -1L : paramDef.getParamId();
        SysBasebandParamDef info = paramDefMapper.checkParamCodeUnique(paramDef.getParamCode());
        if (StringUtils.isNotNull(info) && info.getParamId().longValue() != paramId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}


