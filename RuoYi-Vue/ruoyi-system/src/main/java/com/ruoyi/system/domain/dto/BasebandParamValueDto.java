package com.ruoyi.system.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 前端提交的参数值 DTO
 */
public class BasebandParamValueDto
{
    @NotNull(message = "参数ID不能为空")
    private Long paramId;

    @NotBlank(message = "参数值不能为空")
    private String rawValue;

    public Long getParamId()
    {
        return paramId;
    }

    public void setParamId(Long paramId)
    {
        this.paramId = paramId;
    }

    public String getRawValue()
    {
        return rawValue;
    }

    public void setRawValue(String rawValue)
    {
        this.rawValue = rawValue;
    }
}


