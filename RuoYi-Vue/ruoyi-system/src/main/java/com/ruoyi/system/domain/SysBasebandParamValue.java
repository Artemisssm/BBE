package com.ruoyi.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 基带参数取值表 sys_baseband_param_value
 */
public class SysBasebandParamValue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Excel(name = "记录ID", cellType = Excel.ColumnType.NUMERIC)
    private Long valueId;

    /** 单元ID */
    private Long unitId;

    /** 参数ID */
    private Long paramId;

    /** 原始输入值 */
    @Excel(name = "原始值")
    private String rawValue;

    /** 转换后的无符号整数值 */
    @Excel(name = "硬件值")
    private Long uintValue;

    public Long getValueId()
    {
        return valueId;
    }

    public void setValueId(Long valueId)
    {
        this.valueId = valueId;
    }

    @NotNull(message = "单元ID不能为空")
    public Long getUnitId()
    {
        return unitId;
    }

    public void setUnitId(Long unitId)
    {
        this.unitId = unitId;
    }

    @NotNull(message = "参数ID不能为空")
    public Long getParamId()
    {
        return paramId;
    }

    public void setParamId(Long paramId)
    {
        this.paramId = paramId;
    }

    @NotBlank(message = "原始值不能为空")
    public String getRawValue()
    {
        return rawValue;
    }

    public void setRawValue(String rawValue)
    {
        this.rawValue = rawValue;
    }

    @NotNull(message = "硬件值不能为空")
    public Long getUintValue()
    {
        return uintValue;
    }

    public void setUintValue(Long uintValue)
    {
        this.uintValue = uintValue;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("valueId", getValueId())
            .append("unitId", getUnitId())
            .append("paramId", getParamId())
            .append("rawValue", getRawValue())
            .append("uintValue", getUintValue())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}


