package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 基带单元对象 sys_baseband_unit
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public class SysBasebandUnit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long unitId;

    /** 单元名称 */
    @Excel(name = "单元名称")
    private String unitName;

    /** 单元类型 ENCODE/MODULATE/DEMODULATE/DECODE */
    @Excel(name = "单元类型")
    private String unitType;

    /** 通道号 */
    @Excel(name = "通道号")
    private Integer channelNo;

    /** 状态 0正常 1停用 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 硬件/配置版本 */
    @Excel(name = "版本")
    private String version;

    /** 模式类型 KSA/KMA/SSA/基带数传 */
    @Excel(name = "模式类型")
    private String modeType;

    /** 宏配置ID */
    private Long macroId;

    /** 宏配置名称（非数据库字段，用于显示） */
    private String macroName;

    public void setUnitId(Long unitId) 
    {
        this.unitId = unitId;
    }

    public Long getUnitId() 
    {
        return unitId;
    }

    public void setUnitName(String unitName) 
    {
        this.unitName = unitName;
    }

    public String getUnitName() 
    {
        return unitName;
    }

    public void setUnitType(String unitType) 
    {
        this.unitType = unitType;
    }

    public String getUnitType() 
    {
        return unitType;
    }

    public void setChannelNo(Integer channelNo) 
    {
        this.channelNo = channelNo;
    }

    public Integer getChannelNo() 
    {
        return channelNo;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setVersion(String version) 
    {
        this.version = version;
    }

    public String getVersion() 
    {
        return version;
    }

    public void setModeType(String modeType) 
    {
        this.modeType = modeType;
    }

    public String getModeType() 
    {
        return modeType;
    }

    public void setMacroId(Long macroId) 
    {
        this.macroId = macroId;
    }

    public Long getMacroId() 
    {
        return macroId;
    }

    public void setMacroName(String macroName) 
    {
        this.macroName = macroName;
    }

    public String getMacroName() 
    {
        return macroName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("unitId", getUnitId())
            .append("unitName", getUnitName())
            .append("unitType", getUnitType())
            .append("channelNo", getChannelNo())
            .append("status", getStatus())
            .append("version", getVersion())
            .append("modeType", getModeType())
            .append("macroId", getMacroId())
            .append("macroName", getMacroName())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
