package com.ruoyi.system.domain;

import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 基带参数下发日志对象 sys_baseband_dispatch_log
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public class SysBasebandDispatchLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    private Long logId;

    /** 下发单元 */
    private Long unitId;

    /** 下发类型 MANUAL/AUTO */
    private String dispatchType;

    /** 组播IP */
    private String multicastIp;

    /** 组播端口 */
    private Integer multicastPort;

    /** 报文长度 */
    private Integer packetLen;

    /** 结果状态 0成功 1失败 */
    private String resultStatus;

    /** 结果消息 */
    private String resultMsg;

    /** 发送时间 */
    private Date sendTime;

    public void setLogId(Long logId) 
    {
        this.logId = logId;
    }

    public Long getLogId() 
    {
        return logId;
    }

    public void setUnitId(Long unitId) 
    {
        this.unitId = unitId;
    }

    public Long getUnitId() 
    {
        return unitId;
    }

    public void setDispatchType(String dispatchType) 
    {
        this.dispatchType = dispatchType;
    }

    public String getDispatchType() 
    {
        return dispatchType;
    }

    public void setMulticastIp(String multicastIp) 
    {
        this.multicastIp = multicastIp;
    }

    public String getMulticastIp() 
    {
        return multicastIp;
    }

    public void setMulticastPort(Integer multicastPort) 
    {
        this.multicastPort = multicastPort;
    }

    public Integer getMulticastPort() 
    {
        return multicastPort;
    }

    public void setPacketLen(Integer packetLen) 
    {
        this.packetLen = packetLen;
    }

    public Integer getPacketLen() 
    {
        return packetLen;
    }

    public void setResultStatus(String resultStatus) 
    {
        this.resultStatus = resultStatus;
    }

    public String getResultStatus() 
    {
        return resultStatus;
    }

    public void setResultMsg(String resultMsg) 
    {
        this.resultMsg = resultMsg;
    }

    public String getResultMsg() 
    {
        return resultMsg;
    }

    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("unitId", getUnitId())
            .append("dispatchType", getDispatchType())
            .append("multicastIp", getMulticastIp())
            .append("multicastPort", getMulticastPort())
            .append("packetLen", getPacketLen())
            .append("resultStatus", getResultStatus())
            .append("resultMsg", getResultMsg())
            .append("sendTime", getSendTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
