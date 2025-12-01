package com.ruoyi.system.domain.vo;

/**
 * 基带参数值VO（用于下发）
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public class BasebandParamVo
{
    /** 参数ID */
    private Long paramId;

    /** 参数名称 */
    private String paramName;

    /** 参数编码 */
    private String paramCode;

    /** 转换后的无符号整型值（已除以量化单位） */
    private Long uintValue;

    /** 位宽类型 */
    private String bitWidthType;

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public Long getUintValue() {
        return uintValue;
    }

    public void setUintValue(Long uintValue) {
        this.uintValue = uintValue;
    }

    public String getBitWidthType() {
        return bitWidthType;
    }

    public void setBitWidthType(String bitWidthType) {
        this.bitWidthType = bitWidthType;
    }
}
