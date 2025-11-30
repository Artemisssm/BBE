package com.ruoyi.system.service;

/**
 * 基带参数下发Service接口
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public interface IBasebandDispatchService 
{
    /**
     * 下发参数到硬件设备
     * 
     * @param unitId 单元ID
     * @param operator 操作人
     * @param dispatchType 下发类型 MANUAL/AUTO
     */
    void dispatch(Long unitId, String operator, String dispatchType);
}
