package com.ruoyi.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 基带下发日志表 sys_baseband_dispatch_log
 */
public class SysBasebandDispatchLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @Excel(name = "日志ID", cellType = Excel.ColumnType.NUMERIC)
    private Long logId;

    /** 单元ID */
    @Excel(name = "单元ID")
    private Long unitId;

    /** 类型 */
    @Excel(name = "下发类型")
    private String dispatchType;

    /** 组播IP */
    @Excel(name = "组播IP")
    private String multicastIp;

    /** 端口 */
    @Excel(name = "组播端口")
    private Integer multicastPort;

    /** 报文长度 */
    @Excel(name = "报文长度")
    private Integer packetLen;

    /** 结果 0成功 1失败 */
    @Excel(name = "结果", readConverterExp = "0=成功,1=失败")
    private String resultStatus;

    /** 结果描述 */
    @Excel(name = "描述")
    private String resultMsg;

    /** 发送时间 */
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getUnitId()
    {
        return unitId;
    }

    public void setUnitId(Long unitId)
    {
        this.unitId = unitId;
    }

    public String getDispatchType()
    {
        return dispatchType;
    }

    public void setDispatchType(String dispatchType)
    {
        this.dispatchType = dispatchType;
    }

    public String getMulticastIp()
    {
        return multicastIp;
    }

    public void setMulticastIp(String multicastIp)
    {
        this.multicastIp = multicastIp;
    }

    public Integer getMulticastPort()
    {
        return multicastPort;
    }

    public void setMulticastPort(Integer multicastPort)
    {
        this.multicastPort = multicastPort;
    }

    public Integer getPacketLen()
    {
        return packetLen;
    }

    public void setPacketLen(Integer packetLen)
    {
        this.packetLen = packetLen;
    }

    public String getResultStatus()
    {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus)
    {
        this.resultStatus = resultStatus;
    }

    public String getResultMsg()
    {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg)
    {
        this.resultMsg = resultMsg;
    }

    public Date getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
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


