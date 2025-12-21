package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysBasebandParamConstraint;
import com.ruoyi.system.domain.SysBasebandParamDef;
import com.ruoyi.system.domain.SysBasebandParamValue;
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.mapper.SysBasebandParamConstraintMapper;
import com.ruoyi.system.mapper.SysBasebandParamDefMapper;
import com.ruoyi.system.mapper.SysBasebandParamValueMapper;
import com.ruoyi.system.mapper.SysBasebandUnitMapper;
import com.ruoyi.system.service.ISysBasebandParamValueService;

/**
 * 基带参数取值Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@Service
public class SysBasebandParamValueServiceImpl implements ISysBasebandParamValueService 
{
    private static final Logger log = LoggerFactory.getLogger(SysBasebandParamValueServiceImpl.class);

    @Autowired
    private SysBasebandParamValueMapper paramValueMapper;

    @Autowired
    private SysBasebandParamDefMapper paramDefMapper;

    @Autowired
    private SysBasebandUnitMapper unitMapper;

    @Autowired
    private SysBasebandParamConstraintMapper constraintMapper;

    @Autowired
    private com.ruoyi.system.service.ISysBasebandParamConstraintService constraintService;

    /**
     * 根据单元ID获取参数配置列表（包含参数定义信息）
     * 
     * @param unitId 单元ID
     * @return 参数配置列表
     */
    @Override
    public List<Map<String, Object>> selectParamValueListByUnitId(Long unitId)
    {
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        // 获取单元信息
        SysBasebandUnit unit = unitMapper.selectSysBasebandUnitByUnitId(unitId);
        if (unit == null) {
            return resultList;
        }
        
        // 获取该单元类型的所有参数定义
        List<SysBasebandParamDef> paramDefs = paramDefMapper.selectSysBasebandParamDefByUnitType(unit.getUnitType());
        
        // 获取该单元已配置的参数值
        List<SysBasebandParamValue> paramValues = paramValueMapper.selectSysBasebandParamValueByUnitId(unitId);
        Map<Long, SysBasebandParamValue> valueMap = new HashMap<>();
        for (SysBasebandParamValue value : paramValues) {
            valueMap.put(value.getParamId(), value);
        }
        
        // 组合参数定义和参数值
        for (SysBasebandParamDef def : paramDefs) {
            Map<String, Object> item = new HashMap<>();
            item.put("paramId", def.getParamId());
            item.put("paramCode", def.getParamCode());
            item.put("paramName", def.getParamName());
            item.put("valueType", def.getValueType());
            item.put("enumOptions", def.getEnumOptions());
            item.put("minValue", def.getMinValue());
            item.put("maxValue", def.getMaxValue());
            item.put("quantizationUnit", def.getQuantizationUnit());
            item.put("bitWidthType", def.getBitWidthType());
            item.put("stepValue", def.getStepValue());
            item.put("defaultValue", def.getDefaultValue());
            
            // 如果有配置值，使用配置值；否则使用默认值
            SysBasebandParamValue value = valueMap.get(def.getParamId());
            if (value != null) {
                item.put("rawValue", value.getRawValue());
                item.put("valueId", value.getValueId());
            } else {
                item.put("rawValue", def.getDefaultValue());
                item.put("valueId", null);
            }
            
            resultList.add(item);
        }
        
        return resultList;
    }

    /**
     * 保存单元的参数配置
     * 
     * @param unitId 单元ID
     * @param paramValues 参数值列表
     * @return 结果
     */
    @Override
    @Transactional
    public int saveParamValues(Long unitId, List<Map<String, Object>> paramValues)
    {
        // 获取单元信息
        SysBasebandUnit unit = unitMapper.selectSysBasebandUnitByUnitId(unitId);
        if (unit == null) {
            throw new RuntimeException("单元不存在");
        }
        
        // 构建所有参数值的Map，用于约束验证
        Map<String, String> allParamsMap = new HashMap<>();
        Map<Long, String> paramIdToNameMap = new HashMap<>();
        
        for (Map<String, Object> item : paramValues) {
            // 检查是否启用
            Boolean enabled = true;
            if (item.containsKey("enabled")) {
                Object enabledObj = item.get("enabled");
                enabled = enabledObj == null || Boolean.TRUE.equals(enabledObj) || "true".equals(enabledObj.toString());
            }
            
            // 跳过禁用的参数
            if (!enabled) {
                continue;
            }
            
            Long paramId = Long.valueOf(item.get("paramId").toString());
            Object rawValueObj = item.get("rawValue");
            String rawValue = rawValueObj != null ? rawValueObj.toString() : "";
            
            if (rawValue.isEmpty()) {
                continue;
            }
            
            SysBasebandParamDef paramDef = paramDefMapper.selectSysBasebandParamDefByParamId(paramId);
            if (paramDef != null) {
                allParamsMap.put(paramDef.getParamCode(), rawValue);
                paramIdToNameMap.put(paramId, paramDef.getParamCode());
            }
        }
        
        // 验证所有参数的约束（只验证启用的参数）
        for (Map<String, Object> item : paramValues) {
            // 检查是否启用
            Boolean enabled = true;
            if (item.containsKey("enabled")) {
                Object enabledObj = item.get("enabled");
                enabled = enabledObj == null || Boolean.TRUE.equals(enabledObj) || "true".equals(enabledObj.toString());
            }
            
            // 跳过禁用的参数
            if (!enabled) {
                continue;
            }
            
            Long paramId = Long.valueOf(item.get("paramId").toString());
            Object rawValueObj = item.get("rawValue");
            String rawValue = rawValueObj != null ? rawValueObj.toString() : "";
            
            if (rawValue.isEmpty()) {
                continue;
            }
            
            String paramName = paramIdToNameMap.get(paramId);
            
            if (paramName != null) {
                String error = constraintService.validateParamConstraint(
                    unit.getUnitName(), unit.getUnitType(), unit.getModeType(), 
                    paramName, rawValue, allParamsMap);
                
                if (error != null) {
                    throw new RuntimeException("参数约束验证失败: " + paramName + " - " + error);
                }
            }
        }
        
        // 验证通过后，保存参数值
        String username = SecurityUtils.getUsername();
        int count = 0;
        
        for (Map<String, Object> item : paramValues) {
            Long paramId = Long.valueOf(item.get("paramId").toString());
            Object rawValueObj = item.get("rawValue");
            String rawValue = rawValueObj != null ? rawValueObj.toString() : "";
            
            // 检查是否启用（enabled字段）
            Boolean enabled = true;
            if (item.containsKey("enabled")) {
                Object enabledObj = item.get("enabled");
                enabled = enabledObj == null || Boolean.TRUE.equals(enabledObj) || "true".equals(enabledObj.toString());
            }
            
            // 查询是否已存在
            SysBasebandParamValue existValue = paramValueMapper.selectSysBasebandParamValueByUnitAndParam(unitId, paramId);
            
            // 如果禁用或值为空，删除已存在的配置
            if (!enabled || rawValue.isEmpty()) {
                if (existValue != null) {
                    paramValueMapper.deleteSysBasebandParamValueByValueId(existValue.getValueId());
                    count++;
                }
                continue;
            }
            
            // 获取参数定义
            SysBasebandParamDef paramDef = paramDefMapper.selectSysBasebandParamDefByParamId(paramId);
            if (paramDef == null) {
                continue;
            }
            
            // 计算uint_value
            Long uintValue = calculateUintValue(rawValue, paramDef);
            
            if (existValue != null) {
                // 更新
                existValue.setRawValue(rawValue);
                existValue.setUintValue(uintValue);
                existValue.setUpdateBy(username);
                existValue.setUpdateTime(DateUtils.getNowDate());
                count += paramValueMapper.updateSysBasebandParamValue(existValue);
            } else {
                // 新增
                SysBasebandParamValue newValue = new SysBasebandParamValue();
                newValue.setUnitId(unitId);
                newValue.setParamId(paramId);
                newValue.setRawValue(rawValue);
                newValue.setUintValue(uintValue);
                newValue.setUpdateBy(username);
                newValue.setUpdateTime(DateUtils.getNowDate());
                count += paramValueMapper.insertSysBasebandParamValue(newValue);
            }
        }
        
        return count;
    }

    @Autowired
    private com.ruoyi.system.service.IBasebandDispatchService dispatchService;

    /**
     * 下发参数到硬件设备
     * 
     * @param unitId 单元ID
     * @return 结果
     */
    @Override
    public int dispatchParams(Long unitId)
    {
        try {
            String username = SecurityUtils.getUsername();
            dispatchService.dispatch(unitId, username, "MANUAL");
            return 1;
        } catch (Exception e) {
            log.error("下发参数失败: unitId={}", unitId, e);
            throw e;
        }
    }

    /**
     * 计算uint_value
     * 根据参数类型和量化单位，将原始值转换为无符号整数
     * 注意：下发给硬件时需要除以量化单位
     */
    private Long calculateUintValue(String rawValue, SysBasebandParamDef paramDef)
    {
        try {
            String valueType = paramDef.getValueType();
            Integer quantizationUnit = paramDef.getQuantizationUnit() != null ? paramDef.getQuantizationUnit() : 1;
            
            if ("ENUM".equals(valueType) || "SWITCH".equals(valueType)) {
                // 枚举和开关类型，直接转换
                return Long.valueOf(rawValue);
            } else if ("UINT".equals(valueType)) {
                // 无符号整数，除以量化单位（下发给硬件的值）
                return Long.valueOf(rawValue) / quantizationUnit;
            } else if ("FLOAT".equals(valueType)) {
                // 浮点数，除以量化单位后转为整数（下发给硬件的值）
                BigDecimal decimal = new BigDecimal(rawValue);
                BigDecimal quantized = decimal.divide(new BigDecimal(quantizationUnit), 0, BigDecimal.ROUND_HALF_UP);
                return quantized.longValue();
            }
            
            return Long.valueOf(rawValue);
        } catch (Exception e) {
            log.error("计算uint_value失败: rawValue={}, paramDef={}", rawValue, paramDef, e);
            return 0L;
        }
    }

    @Override
    public List<Map<String, Object>> generateSmartValues(Long unitId)
    {
        // 获取单元信息
        SysBasebandUnit unit = unitMapper.selectSysBasebandUnitByUnitId(unitId);
        if (unit == null) {
            throw new RuntimeException("单元不存在");
        }

        // 获取所有参数定义
        List<Map<String, Object>> paramList = selectParamValueListByUnitId(unitId);
        
        // 获取该单元的所有约束
        List<SysBasebandParamConstraint> constraints = 
            constraintMapper.selectConstraintsByUnit(unit.getUnitName(), unit.getUnitType(), unit.getModeType());

        // 智能生成参数值
        Map<String, String> smartValues = generateConstraintCompliantValues(paramList, constraints);
        
        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> param : paramList) {
            String paramCode = safeToString(param.get("paramCode"));
            String smartValue = smartValues.get(paramCode);
            
            if (smartValue != null) {
                Map<String, Object> smartParam = new HashMap<>();
                smartParam.put("paramId", param.get("paramId"));
                smartParam.put("paramName", param.get("paramName"));
                smartParam.put("paramCode", paramCode);
                smartParam.put("rawValue", smartValue);
                smartParam.put("enabled", true);
                result.add(smartParam);
            }
        }
        
        return result;
    }

    /**
     * 生成符合约束的参数值
     */
    private Map<String, String> generateConstraintCompliantValues(
            List<Map<String, Object>> paramList, 
            List<SysBasebandParamConstraint> constraints)
    {
        Map<String, String> values = new HashMap<>();
        Map<String, Map<String, Object>> paramMap = new HashMap<>();
        
        // 构建参数映射
        for (Map<String, Object> param : paramList) {
            String paramCode = safeToString(param.get("paramCode"));
            paramMap.put(paramCode, param);
        }
        
        // 按依赖关系排序参数（被依赖的参数优先设置）
        List<String> sortedParams = sortParametersByDependency(paramMap.keySet(), constraints);
        
        // 逐个设置参数值
        for (String paramCode : sortedParams) {
            Map<String, Object> param = paramMap.get(paramCode);
            
            // 获取该参数的约束
            List<SysBasebandParamConstraint> paramConstraints = constraints.stream()
                .filter(c -> paramCode.equals(c.getTargetParamName()))
                .collect(Collectors.toList());
            
            String smartValue = generateSingleParamValue(param, paramConstraints, values);
            if (smartValue != null) {
                values.put(paramCode, smartValue);
            }
        }
        
        return values;
    }

    /**
     * 按依赖关系排序参数
     */
    private List<String> sortParametersByDependency(Set<String> paramCodes, 
                                                   List<SysBasebandParamConstraint> constraints)
    {
        List<String> result = new ArrayList<>();
        Set<String> processed = new HashSet<>();
        
        // 简单的拓扑排序：先处理没有依赖的参数
        while (processed.size() < paramCodes.size()) {
            for (String paramCode : paramCodes) {
                if (processed.contains(paramCode)) {
                    continue;
                }
                
                // 检查是否所有依赖都已处理
                boolean canProcess = true;
                for (SysBasebandParamConstraint constraint : constraints) {
                    if (paramCode.equals(constraint.getTargetParamName()) && 
                        constraint.getSourceParamName() != null &&
                        !processed.contains(constraint.getSourceParamName())) {
                        canProcess = false;
                        break;
                    }
                }
                
                if (canProcess) {
                    result.add(paramCode);
                    processed.add(paramCode);
                }
            }
        }
        
        return result;
    }

    /**
     * 生成单个参数的智能值
     */
    private String generateSingleParamValue(Map<String, Object> param, 
                                          List<SysBasebandParamConstraint> constraints,
                                          Map<String, String> existingValues)
    {
        String valueType = safeToString(param.get("valueType"));
        
        // 如果没有约束，使用默认值或类型默认值
        if (constraints.isEmpty()) {
            return getTypeDefaultValue(param);
        }
        
        // 根据约束生成值
        for (SysBasebandParamConstraint constraint : constraints) {
            // 检查约束条件是否满足
            if (!isConstraintConditionMet(constraint, existingValues)) {
                continue;
            }
            
            String constraintType = constraint.getConstraintType();
            String constraintValue = constraint.getConstraintValue();
            
            switch (constraintType) {
                case "FIXED_VALUE":
                    // 固定值约束：使用第一个允许的值
                    String[] allowedValues = constraintValue.split(",");
                    if (allowedValues.length > 0) {
                        return allowedValues[0].trim();
                    }
                    break;
                    
                case "VALUE_RANGE":
                    // 范围约束：使用范围内的合理值
                    String[] range = constraintValue.split(",");
                    if (range.length >= 2) {
                        try {
                            double min = Double.parseDouble(range[0]);
                            double max = Double.parseDouble(range[1]);
                            // 使用最小值或中间值
                            double value = min;
                            if ("FLOAT".equals(valueType)) {
                                value = (min + max) / 2; // 浮点数用中间值
                            }
                            return String.valueOf((long)value);
                        } catch (NumberFormatException e) {
                            // 忽略格式错误
                        }
                    }
                    break;
                    
                case "ENUM_LIMIT":
                    // 枚举限制：使用第一个允许的枚举值
                    String[] enumValues = constraintValue.split(",");
                    if (enumValues.length > 0) {
                        return enumValues[0].trim();
                    }
                    break;
            }
        }
        
        // 如果约束都不适用，使用类型默认值
        return getTypeDefaultValue(param);
    }

    /**
     * 获取类型默认值
     */
    private String getTypeDefaultValue(Map<String, Object> param)
    {
        String valueType = safeToString(param.get("valueType"));
        String defaultValue = safeToString(param.get("defaultValue"));
        String enumOptions = safeToString(param.get("enumOptions"));
        String minValue = safeToString(param.get("minValue"));
        
        // 优先使用默认值
        if (defaultValue != null && !defaultValue.trim().isEmpty()) {
            return defaultValue.trim();
        }
        
        // 根据类型生成合理默认值
        switch (valueType) {
            case "ENUM":
                if (enumOptions != null && !enumOptions.trim().isEmpty()) {
                    // 解析枚举选项，取第一个值
                    String[] options = enumOptions.split(",");
                    for (String option : options) {
                        String[] parts = option.trim().split(":");
                        if (parts.length >= 2) {
                            return parts[0].trim(); // 返回枚举值
                        }
                    }
                }
                return "0";
                
            case "UINT":
            case "FLOAT":
                if (minValue != null && !minValue.trim().isEmpty()) {
                    return minValue.trim();
                }
                return "0";
                
            case "HEX":
                return "0x0";
                
            default:
                return "0";
        }
    }

    /**
     * 安全地将Object转换为String
     */
    private String safeToString(Object obj)
    {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).toPlainString();
        }
        if (obj instanceof Number) {
            return obj.toString();
        }
        return obj.toString();
    }

    /**
     * 检查约束条件是否满足
     */
    private boolean isConstraintConditionMet(SysBasebandParamConstraint constraint, 
                                           Map<String, String> existingValues)
    {
        String condition = constraint.getConstraintCondition();
        if (condition == null || condition.isEmpty()) {
            return true; // 无条件，总是满足
        }
        
        String sourceParam = constraint.getSourceParamName();
        if (sourceParam == null || existingValues == null) {
            return false;
        }
        
        String sourceValue = existingValues.get(sourceParam);
        if (sourceValue == null) {
            return false;
        }
        
        return evaluateCondition(sourceValue, condition);
    }

    /**
     * 评估条件表达式
     */
    private boolean evaluateCondition(String value, String condition)
    {
        try {
            // 简单的条件评估：支持 =, !=, >, <, >=, <= 
            if (condition.contains("=") && !condition.contains("!") && !condition.contains(">") && !condition.contains("<")) {
                String expectedValue = condition.substring(condition.indexOf("=") + 1).trim();
                return value.equals(expectedValue);
            } else if (condition.contains("!=")) {
                String expectedValue = condition.substring(condition.indexOf("!=") + 2).trim();
                return !value.equals(expectedValue);
            } else if (condition.contains(">=")) {
                String expectedValue = condition.substring(condition.indexOf(">=") + 2).trim();
                return Double.parseDouble(value) >= Double.parseDouble(expectedValue);
            } else if (condition.contains("<=")) {
                String expectedValue = condition.substring(condition.indexOf("<=") + 2).trim();
                return Double.parseDouble(value) <= Double.parseDouble(expectedValue);
            } else if (condition.contains(">")) {
                String expectedValue = condition.substring(condition.indexOf(">") + 1).trim();
                return Double.parseDouble(value) > Double.parseDouble(expectedValue);
            } else if (condition.contains("<")) {
                String expectedValue = condition.substring(condition.indexOf("<") + 1).trim();
                return Double.parseDouble(value) < Double.parseDouble(expectedValue);
            }
        } catch (Exception e) {
            // 条件评估失败，返回false
        }
        
        return false;
    }
}
