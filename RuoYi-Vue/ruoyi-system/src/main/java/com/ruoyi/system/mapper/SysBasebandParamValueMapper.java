package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandParamValue;
import com.ruoyi.system.domain.vo.BasebandParamVo;

/**
 * 参数取值Mapper
 */
public interface SysBasebandParamValueMapper
{
    /**
     * 查询指定单元的参数定义+取值
     */
    public List<BasebandParamVo> selectParamVoByUnitId(Long unitId);

    /**
     * 批量插入或更新
     */
    public int batchUpsert(List<SysBasebandParamValue> list);
}


