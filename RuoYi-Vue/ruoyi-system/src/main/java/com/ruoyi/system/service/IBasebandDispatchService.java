package com.ruoyi.system.service;

/**
 * 基带参数下发服务
 */
public interface IBasebandDispatchService
{
    /**
     * 触发对某个单元的下发
     *
     * @param unitId 单元ID
     * @param operator 操作人
     * @param dispatchType 类型 MANUAL/AUTO
     */
    void dispatch(Long unitId, String operator, String dispatchType);
}


