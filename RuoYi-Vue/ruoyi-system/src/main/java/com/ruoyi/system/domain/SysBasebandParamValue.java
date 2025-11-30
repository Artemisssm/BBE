package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 基带参数取值对象 sys_baseband_param_value
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public class SysBasebandParamValue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 参数值主键 */
    private Long valueId;

    /** 所属单元 */
    private Long unitId;

    /** 参数定义 */
    private Long paramId;

    /** 用户输入原值（字符串表示） */
    private String rawValue;

    /** 转换后的无符号整型 */
    private Long uintValue;

    public void setValueId(Long valueId) 
    {
        this.valueId = valueId;
    }

    public Long getValueId() 
    {
        return valueId;
    }

    public void setUnitId(Long unitId) 
    {
        this.unitId = unitId;
    }

    public Long getUnitId() 
    {
        return unitId;
    }

    public void setParamId(Long paramId) 
    {
        this.paramId = paramId;
    }

    public Long getParamId() 
    {
        return paramId;
    }

    public void setRawValue(String rawValue) 
    {
        this.rawValue = rawValue;
    }

    public String getRawValue() 
    {
        return rawValue;
    }

    public void setUintValue(Long uintValue) 
    {
        this.uintValue = uintValue;
    }

    public Long getUintValue() 
    {
        return uintValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
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
