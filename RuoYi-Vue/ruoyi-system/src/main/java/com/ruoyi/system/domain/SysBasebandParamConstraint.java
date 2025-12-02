package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 基带参数约束关系对象 sys_baseband_param_constraint
 * 
 * @author ruoyi
 * @date 2024-12-02
 */
public class SysBasebandParamConstraint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 约束ID */
    private Long constraintId;

    /** 源单元名称 */
    @Excel(name = "源单元名称")
    private String sourceUnitName;

    /** 源单元类型 */
    @Excel(name = "源单元类型")
    private String sourceUnitType;

    /** 源参数名称 */
    @Excel(name = "源参数名称")
    private String sourceParamName;

    /** 目标单元名称 */
    @Excel(name = "目标单元名称")
    private String targetUnitName;

    /** 目标单元类型 */
    @Excel(name = "目标单元类型")
    private String targetUnitType;

    /** 目标参数名称 */
    @Excel(name = "目标参数名称")
    private String targetParamName;

    /** 适用模式类型（多个用逗号分隔） */
    @Excel(name = "模式类型")
    private String modeTypes;

    /** 约束类型 */
    @Excel(name = "约束类型")
    private String constraintType;

    /** 约束条件 */
    @Excel(name = "约束条件")
    private String constraintCondition;

    /** 约束值 */
    @Excel(name = "约束值")
    private String constraintValue;

    /** 优先级 */
    @Excel(name = "优先级")
    private Integer priority;

    /** 错误提示 */
    @Excel(name = "错误提示")
    private String errorMessage;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setConstraintId(Long constraintId) 
    {
        this.constraintId = constraintId;
    }

    public Long getConstraintId() 
    {
        return constraintId;
    }

    public void setSourceUnitName(String sourceUnitName) 
    {
        this.sourceUnitName = sourceUnitName;
    }

    public String getSourceUnitName() 
    {
        return sourceUnitName;
    }

    public void setSourceUnitType(String sourceUnitType) 
    {
        this.sourceUnitType = sourceUnitType;
    }

    public String getSourceUnitType() 
    {
        return sourceUnitType;
    }

    public void setSourceParamName(String sourceParamName) 
    {
        this.sourceParamName = sourceParamName;
    }

    public String getSourceParamName() 
    {
        return sourceParamName;
    }

    public void setTargetUnitName(String targetUnitName) 
    {
        this.targetUnitName = targetUnitName;
    }

    public String getTargetUnitName() 
    {
        return targetUnitName;
    }

    public void setTargetUnitType(String targetUnitType) 
    {
        this.targetUnitType = targetUnitType;
    }

    public String getTargetUnitType() 
    {
        return targetUnitType;
    }

    public void setTargetParamName(String targetParamName) 
    {
        this.targetParamName = targetParamName;
    }

    public String getTargetParamName() 
    {
        return targetParamName;
    }

    public void setModeTypes(String modeTypes) 
    {
        this.modeTypes = modeTypes;
    }

    public String getModeTypes() 
    {
        return modeTypes;
    }

    public void setConstraintType(String constraintType) 
    {
        this.constraintType = constraintType;
    }

    public String getConstraintType() 
    {
        return constraintType;
    }

    public void setConstraintCondition(String constraintCondition) 
    {
        this.constraintCondition = constraintCondition;
    }

    public String getConstraintCondition() 
    {
        return constraintCondition;
    }

    public void setConstraintValue(String constraintValue) 
    {
        this.constraintValue = constraintValue;
    }

    public String getConstraintValue() 
    {
        return constraintValue;
    }

    public void setPriority(Integer priority) 
    {
        this.priority = priority;
    }

    public Integer getPriority() 
    {
        return priority;
    }

    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("constraintId", getConstraintId())
            .append("sourceUnitName", getSourceUnitName())
            .append("sourceUnitType", getSourceUnitType())
            .append("sourceParamName", getSourceParamName())
            .append("targetUnitName", getTargetUnitName())
            .append("targetUnitType", getTargetUnitType())
            .append("targetParamName", getTargetParamName())
            .append("modeTypes", getModeTypes())
            .append("constraintType", getConstraintType())
            .append("constraintCondition", getConstraintCondition())
            .append("constraintValue", getConstraintValue())
            .append("priority", getPriority())
            .append("errorMessage", getErrorMessage())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
