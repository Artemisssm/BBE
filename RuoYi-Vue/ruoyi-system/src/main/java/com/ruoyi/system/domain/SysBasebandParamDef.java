package com.ruoyi.system.domain;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 基带参数定义表 sys_baseband_param_def
 *
 * @author
 */
public class SysBasebandParamDef extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 参数ID */
    @Excel(name = "参数ID", cellType = Excel.ColumnType.NUMERIC)
    private Long paramId;

    /** 对应单元类型 */
    @Excel(name = "单元类型")
    private String unitType;

    /** 参数编码 */
    @Excel(name = "参数编码")
    private String paramCode;

    /** 参数名称 */
    @Excel(name = "参数名称")
    private String paramName;

    /** 值类型 */
    @Excel(name = "值类型", readConverterExp = "ENUM=枚举,UINT=无符号整数,FLOAT=浮点,SWITCH=开关")
    private String valueType;

    /** 枚举 JSON 字符串 */
    private String enumOptions;

    /** 最小值 */
    private BigDecimal minValue;

    /** 最大值 */
    private BigDecimal maxValue;

    /** 缩放系数 */
    @Excel(name = "缩放倍数")
    private Integer scaleFactor;

    /** 位宽 */
    @Excel(name = "位宽")
    private Integer bitLength;

    /** 硬件顺序 */
    @Excel(name = "硬件顺序")
    private Integer hardwareOrder;

    /** 默认值 */
    @Excel(name = "默认值")
    private String defaultValue;

    public Long getParamId()
    {
        return paramId;
    }

    public void setParamId(Long paramId)
    {
        this.paramId = paramId;
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

    @NotBlank(message = "参数编码不能为空")
    @Size(max = 64, message = "参数编码长度不能超过64个字符")
    public String getParamCode()
    {
        return paramCode;
    }

    public void setParamCode(String paramCode)
    {
        this.paramCode = paramCode;
    }

    @NotBlank(message = "参数名称不能为空")
    @Size(max = 64, message = "参数名称长度不能超过64个字符")
    public String getParamName()
    {
        return paramName;
    }

    public void setParamName(String paramName)
    {
        this.paramName = paramName;
    }

    @NotBlank(message = "值类型不能为空")
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

    @NotNull(message = "缩放倍数不能为空")
    public Integer getScaleFactor()
    {
        return scaleFactor;
    }

    public void setScaleFactor(Integer scaleFactor)
    {
        this.scaleFactor = scaleFactor;
    }

    @NotNull(message = "位宽不能为空")
    public Integer getBitLength()
    {
        return bitLength;
    }

    public void setBitLength(Integer bitLength)
    {
        this.bitLength = bitLength;
    }

    @NotNull(message = "硬件顺序不能为空")
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
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


