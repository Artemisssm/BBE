package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandDispatchLog;

/**
 * 下发日志Mapper
 */
public interface SysBasebandDispatchLogMapper
{
    public SysBasebandDispatchLog selectById(Long logId);

    public List<SysBasebandDispatchLog> selectList(SysBasebandDispatchLog query);

    public int insertLog(SysBasebandDispatchLog log);
}


