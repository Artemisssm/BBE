package com.ruoyi.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 基带单元表 sys_baseband_unit
 *
 * @author
 */
public class SysBasebandUnit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Excel(name = "单元ID", cellType = Excel.ColumnType.NUMERIC)
    private Long unitId;

    /** 单元名称 */
    @Excel(name = "单元名称")
    private String unitName;

    /** 单元类型 */
    @Excel(name = "单元类型", readConverterExp = "ENCODE=编码,MODULATE=调制,DEMODULATE=解调,DECODE=译码")
    private String unitType;

    /** 通道号 */
    @Excel(name = "通道号")
    private Integer channelNo;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 硬件/配置版本 */
    @Excel(name = "版本")
    private String version;

    public Long getUnitId()
    {
        return unitId;
    }

    public void setUnitId(Long unitId)
    {
        this.unitId = unitId;
    }

    @NotBlank(message = "单元名称不能为空")
    @Size(max = 64, message = "单元名称不能超过64个字符")
    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    @NotBlank(message = "单元类型不能为空")
    public String getUnitType()
    {
        return unitType;
    }

    public void setUnitType(String unitType)
    {
        this.unitType = unitType;
    }

    @NotNull(message = "通道号不能为空")
    public Integer getChannelNo()
    {
        return channelNo;
    }

    public void setChannelNo(Integer channelNo)
    {
        this.channelNo = channelNo;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Size(max = 32, message = "版本信息不能超过32个字符")
    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("unitId", getUnitId())
            .append("unitName", getUnitName())
            .append("unitType", getUnitType())
            .append("channelNo", getChannelNo())
            .append("status", getStatus())
            .append("version", getVersion())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}


