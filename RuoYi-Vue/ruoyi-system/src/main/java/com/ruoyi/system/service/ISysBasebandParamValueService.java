package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysBasebandParamValue;

/**
 * 基带参数取值Service接口
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public interface ISysBasebandParamValueService 
{
    /**
     * 根据单元ID获取参数配置列表（包含参数定义信息）
     * 
     * @param unitId 单元ID
     * @return 参数配置列表
     */
    public List<Map<String, Object>> selectParamValueListByUnitId(Long unitId);

    /**
     * 保存单元的参数配置
     * 
     * @param unitId 单元ID
     * @param paramValues 参数值列表
     * @return 结果
     */
    public int saveParamValues(Long unitId, List<Map<String, Object>> paramValues);

    /**
     * 下发参数到硬件设备
     * 
     * @param unitId 单元ID
     * @return 结果
     */
    public int dispatchParams(Long unitId);
}
