package com.ruoyi.system.domain;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 基带参数定义对象 sys_baseband_param_def
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public class SysBasebandParamDef extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 参数定义主键 */
    private Long paramId;

    /** 适用单元类型 */
    @Excel(name = "单元类型")
    private String unitType;

    /** 唯一编码 */
    @Excel(name = "参数编码")
    private String paramCode;

    /** 显示名称 */
    @Excel(name = "参数名称")
    private String paramName;

    /** 值类型 ENUM/UINT/FLOAT/SWITCH */
    @Excel(name = "值类型")
    private String valueType;

    /** 枚举选项 JSON */
    private String enumOptions;

    /** 最小值 */
    @Excel(name = "最小值")
    private BigDecimal minValue;

    /** 最大值 */
    @Excel(name = "最大值")
    private BigDecimal maxValue;

    /** 浮点转无符号整数时的缩放倍数 */
    @Excel(name = "缩放倍数")
    private Integer scaleFactor;

    /** 硬件位宽 */
    @Excel(name = "位宽")
    private Integer bitLength;

    /** 硬件顺序字段 */
    @Excel(name = "硬件顺序")
    private Integer hardwareOrder;

    /** 默认值 */
    @Excel(name = "默认值")
    private String defaultValue;

    public void setParamId(Long paramId) 
    {
        this.paramId = paramId;
    }

    public Long getParamId() 
    {
        return paramId;
    }

    public void setUnitType(String unitType) 
    {
        this.unitType = unitType;
    }

    public String getUnitType() 
    {
        return unitType;
    }

    public void setParamCode(String paramCode) 
    {
        this.paramCode = paramCode;
    }

    public String getParamCode() 
    {
        return paramCode;
    }

    public void setParamName(String paramName) 
    {
        this.paramName = paramName;
    }

    public String getParamName() 
    {
        return paramName;
    }

    public void setValueType(String valueType) 
    {
        this.valueType = valueType;
    }

    public String getValueType() 
    {
        return valueType;
    }

    public void setEnumOptions(String enumOptions) 
    {
        this.enumOptions = enumOptions;
    }

    public String getEnumOptions() 
    {
        return enumOptions;
    }

    public void setMinValue(BigDecimal minValue) 
    {
        this.minValue = minValue;
    }

    public BigDecimal getMinValue() 
    {
        return minValue;
    }

    public void setMaxValue(BigDecimal maxValue) 
    {
        this.maxValue = maxValue;
    }

    public BigDecimal getMaxValue() 
    {
        return maxValue;
    }

    public void setScaleFactor(Integer scaleFactor) 
    {
        this.scaleFactor = scaleFactor;
    }

    public Integer getScaleFactor() 
    {
        return scaleFactor;
    }

    public void setBitLength(Integer bitLength) 
    {
        this.bitLength = bitLength;
    }

    public Integer getBitLength() 
    {
        return bitLength;
    }

    public void setHardwareOrder(Integer hardwareOrder) 
    {
        this.hardwareOrder = hardwareOrder;
    }

    public Integer getHardwareOrder() 
    {
        return hardwareOrder;
    }

    public void setDefaultValue(String defaultValue) 
    {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() 
    {
        return defaultValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("paramId", getParamId())
            .append("unitType", getUnitType())
            .append("paramCode", getParamCode())
            .append("paramName", getParamName())
            .append("valueType", getValueType())
            .append("enumOptions", getEnumOptions())
            .append("minValue", getMinValue())
            .append("maxValue", getMaxValue())
            .append("scaleFactor", getScaleFactor())
            .append("bitLength", getBitLength())
            .append("hardwareOrder", getHardwareOrder())
            .append("defaultValue", getDefaultValue())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .toString();
    }
}
