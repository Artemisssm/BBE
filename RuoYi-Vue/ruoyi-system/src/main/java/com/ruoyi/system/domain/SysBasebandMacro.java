package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 基带宏定义对象 sys_baseband_macro
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
public class SysBasebandMacro extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 宏ID */
    private Long macroId;

    /** 宏名称 */
    @Excel(name = "宏名称")
    private String macroName;

    /** 宏编码 */
    @Excel(name = "宏编码")
    private String macroCode;

    /** 适用单元类型 */
    @Excel(name = "单元类型")
    private String unitType;

    /** 适用模式类型 */
    @Excel(name = "模式类型")
    private String modeType;

    /** 宏描述 */
    @Excel(name = "描述")
    private String description;

    /** 是否默认宏 */
    @Excel(name = "默认", readConverterExp = "0=否,1=是")
    private String isDefault;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 排序 */
    private Integer sortOrder;

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

    public void setMacroCode(String macroCode) 
    {
        this.macroCode = macroCode;
    }

    public String getMacroCode() 
    {
        return macroCode;
    }

    public void setUnitType(String unitType) 
    {
        this.unitType = unitType;
    }

    public String getUnitType() 
    {
        return unitType;
    }

    public void setModeType(String modeType) 
    {
        this.modeType = modeType;
    }

    public String getModeType() 
    {
        return modeType;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setIsDefault(String isDefault) 
    {
        this.isDefault = isDefault;
    }

    public String getIsDefault() 
    {
        return isDefault;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("macroId", getMacroId())
            .append("macroName", getMacroName())
            .append("macroCode", getMacroCode())
            .append("unitType", getUnitType())
            .append("modeType", getModeType())
            .append("description", getDescription())
            .append("isDefault", getIsDefault())
            .append("status", getStatus())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
