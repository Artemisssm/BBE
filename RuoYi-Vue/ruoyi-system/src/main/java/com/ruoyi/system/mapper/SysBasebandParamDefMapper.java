package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandParamDef;

/**
 * 基带参数定义Mapper接口
 */
public interface SysBasebandParamDefMapper
{
    /**
     * 查询参数定义列表
     */
    public List<SysBasebandParamDef> selectBasebandParamDefList(SysBasebandParamDef paramDef);

    /**
     * 根据ID查询
     */
    public SysBasebandParamDef selectBasebandParamDefById(Long paramId);

    /**
     * 新增
     */
    public int insertBasebandParamDef(SysBasebandParamDef paramDef);

    /**
     * 修改
     */
    public int updateBasebandParamDef(SysBasebandParamDef paramDef);

    /**
     * 批量删除
     */
    public int deleteBasebandParamDefByIds(Long[] paramIds);

    /**
     * 单个删除
     */
    public int deleteBasebandParamDefById(Long paramId);

    /**
     * 参数编码唯一性
     */
    public SysBasebandParamDef checkParamCodeUnique(String paramCode);
}


