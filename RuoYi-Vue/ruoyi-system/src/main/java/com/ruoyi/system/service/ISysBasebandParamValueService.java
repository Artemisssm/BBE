package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.dto.BasebandParamValueDto;
import com.ruoyi.system.domain.vo.BasebandParamVo;

/**
 * 参数值服务
 */
public interface ISysBasebandParamValueService
{
    /**
     * 拉取指定单元下的参数定义及当前值
     */
    List<BasebandParamVo> selectParamVoByUnit(Long unitId);

    /**
     * 批量保存参数值
     */
    void saveParamValues(Long unitId, List<BasebandParamValueDto> values, String operator);
}


