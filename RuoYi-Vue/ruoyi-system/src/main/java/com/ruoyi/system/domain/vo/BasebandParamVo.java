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

    /** 值类型 UINT/FLOAT/ENUM/HEX */
    private String valueType;

    /** 枚举选项 */
    private String enumOptions;

    /** 原始值（用户配置的值） */
    private String rawValue;

    /** 转换后的无符号整型值 */
    private Long uintValue;

    /** 位宽类型 U8/U16/U32/I8/I16/I32 */
    private String bitWidthType;

    /** 排序顺序 */
    private Integer sortOrder;

    /** 默认值 */
    private String defaultValue;

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

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getEnumOptions() {
        return enumOptions;
    }

    public void setEnumOptions(String enumOptions) {
        this.enumOptions = enumOptions;
    }

    public String getRawValue() {
        return rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 获取最终要下发的数值
     * 优先级：uintValue > rawValue > defaultValue
     */
    public long getFinalValue() {
        // 1. 如果有uint_value，直接使用
        if (uintValue != null && uintValue != 0) {
            return uintValue;
        }
        
        // 2. 如果有raw_value，解析它
        if (rawValue != null && !rawValue.trim().isEmpty()) {
            return parseValue(rawValue.trim());
        }
        
        // 3. 使用默认值
        if (defaultValue != null && !defaultValue.trim().isEmpty()) {
            return parseValue(defaultValue.trim());
        }
        
        return 0L;
    }

    /**
     * 解析值字符串为数值
     */
    private long parseValue(String value) {
        try {
            // 处理16进制格式 (0x开头或纯16进制)
            if (value.toLowerCase().startsWith("0x")) {
                return Long.parseLong(value.substring(2), 16);
            }
            // 普通十进制数字
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
