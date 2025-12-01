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

    /** 量化单位（下发给硬件时需要除以它） */
    @Excel(name = "量化单位")
    private Integer quantizationUnit;

    /** 硬件位宽类型 U8/U16/U32/I8/I16/I32 */
    @Excel(name = "位宽类型")
    private String bitWidthType;

    /** 步进值 */
    @Excel(name = "步进")
    private BigDecimal stepValue;

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

    public void setQuantizationUnit(Integer quantizationUnit) 
    {
        this.quantizationUnit = quantizationUnit;
    }

    public Integer getQuantizationUnit() 
    {
        return quantizationUnit;
    }

    public void setBitWidthType(String bitWidthType) 
    {
        this.bitWidthType = bitWidthType;
    }

    public String getBitWidthType() 
    {
        return bitWidthType;
    }

    public void setStepValue(BigDecimal stepValue) 
    {
        this.stepValue = stepValue;
    }

    public BigDecimal getStepValue() 
    {
        return stepValue;
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
            .append("quantizationUnit", getQuantizationUnit())
            .append("bitWidthType", getBitWidthType())
            .append("stepValue", getStepValue())
            .append("defaultValue", getDefaultValue())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .toString();
    }
}
