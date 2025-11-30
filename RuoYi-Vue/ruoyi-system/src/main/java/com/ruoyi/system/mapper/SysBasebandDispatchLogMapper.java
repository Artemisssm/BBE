package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandDispatchLog;

/**
 * 基带参数下发日志Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public interface SysBasebandDispatchLogMapper 
{
    /**
     * 查询基带参数下发日志
     * 
     * @param logId 基带参数下发日志主键
     * @return 基带参数下发日志
     */
    public SysBasebandDispatchLog selectLogById(Long logId);

    /**
     * 查询基带参数下发日志列表
     * 
     * @param log 基带参数下发日志
     * @return 基带参数下发日志集合
     */
    public List<SysBasebandDispatchLog> selectLogList(SysBasebandDispatchLog log);

    /**
     * 新增基带参数下发日志
     * 
     * @param log 基带参数下发日志
     * @return 结果
     */
    public int insertLog(SysBasebandDispatchLog log);

    /**
     * 删除基带参数下发日志
     * 
     * @param logId 基带参数下发日志主键
     * @return 结果
     */
    public int deleteLogById(Long logId);

    /**
     * 批量删除基带参数下发日志
     * 
     * @param logIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteLogByIds(Long[] logIds);
}
