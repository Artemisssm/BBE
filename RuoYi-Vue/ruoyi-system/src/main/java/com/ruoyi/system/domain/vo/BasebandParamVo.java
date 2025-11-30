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

    /** 硬件顺序 */
    private Integer hardwareOrder;

    /** 转换后的无符号整型值 */
    private Long uintValue;

    /** 位宽 */
    private Integer bitLength;

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

    public Integer getHardwareOrder() {
        return hardwareOrder;
    }

    public void setHardwareOrder(Integer hardwareOrder) {
        this.hardwareOrder = hardwareOrder;
    }

    public Long getUintValue() {
        return uintValue;
    }

    public void setUintValue(Long uintValue) {
        this.uintValue = uintValue;
    }

    public Integer getBitLength() {
        return bitLength;
    }

    public void setBitLength(Integer bitLength) {
        this.bitLength = bitLength;
    }
}
