package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;

/**
 * 参数定义与当前值组合视图
 */
public class BasebandParamVo
{
    private Long paramId;
    private String unitType;
    private String paramCode;
    private String paramName;
    private String valueType;
    private String enumOptions;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private Integer scaleFactor;
    private Integer bitLength;
    private Integer hardwareOrder;
    private String defaultValue;
    private String remark;

    private Long valueId;
    private Long unitId;
    private String rawValue;
    private Long uintValue;

    public Long getParamId()
    {
        return paramId;
    }

    public void setParamId(Long paramId)
    {
        this.paramId = paramId;
    }

    public String getUnitType()
    {
        return unitType;
    }

    public void setUnitType(String unitType)
    {
        this.unitType = unitType;
    }

    public String getParamCode()
    {
        return paramCode;
    }

    public void setParamCode(String paramCode)
    {
        this.paramCode = paramCode;
    }

    public String getParamName()
    {
        return paramName;
    }

    public void setParamName(String paramName)
    {
        this.paramName = paramName;
    }

    public String getValueType()
    {
        return valueType;
    }

    public void setValueType(String valueType)
    {
        this.valueType = valueType;
    }

    public String getEnumOptions()
    {
        return enumOptions;
    }

    public void setEnumOptions(String enumOptions)
    {
        this.enumOptions = enumOptions;
    }

    public BigDecimal getMinValue()
    {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue)
    {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue)
    {
        this.maxValue = maxValue;
    }

    public Integer getScaleFactor()
    {
        return scaleFactor;
    }

    public void setScaleFactor(Integer scaleFactor)
    {
        this.scaleFactor = scaleFactor;
    }

    public Integer getBitLength()
    {
        return bitLength;
    }

    public void setBitLength(Integer bitLength)
    {
        this.bitLength = bitLength;
    }

    public Integer getHardwareOrder()
    {
        return hardwareOrder;
    }

    public void setHardwareOrder(Integer hardwareOrder)
    {
        this.hardwareOrder = hardwareOrder;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Long getValueId()
    {
        return valueId;
    }

    public void setValueId(Long valueId)
    {
        this.valueId = valueId;
    }

    public Long getUnitId()
    {
        return unitId;
    }

    public void setUnitId(Long unitId)
    {
        this.unitId = unitId;
    }

    public String getRawValue()
    {
        return rawValue;
    }

    public void setRawValue(String rawValue)
    {
        this.rawValue = rawValue;
    }

    public Long getUintValue()
    {
        return uintValue;
    }

    public void setUintValue(Long uintValue)
    {
        this.uintValue = uintValue;
    }
}


