package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.mapper.SysBasebandParamConstraintMapper;
import com.ruoyi.system.domain.SysBasebandParamConstraint;
import com.ruoyi.system.service.ISysBasebandParamConstraintService;

/**
 * 基带参数约束关系Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-12-02
 */
@Service
public class SysBasebandParamConstraintServiceImpl implements ISysBasebandParamConstraintService 
{
    @Autowired
    private SysBasebandParamConstraintMapper sysBasebandParamConstraintMapper;

    /**
     * 查询基带参数约束关系
     * 
     * @param constraintId 基带参数约束关系主键
     * @return 基带参数约束关系
     */
    @Override
    public SysBasebandParamConstraint selectSysBasebandParamConstraintByConstraintId(Long constraintId)
    {
        return sysBasebandParamConstraintMapper.selectSysBasebandParamConstraintByConstraintId(constraintId);
    }

    /**
     * 查询基带参数约束关系列表
     * 
     * @param sysBasebandParamConstraint 基带参数约束关系
     * @return 基带参数约束关系
     */
    @Override
    public List<SysBasebandParamConstraint> selectSysBasebandParamConstraintList(SysBasebandParamConstraint sysBasebandParamConstraint)
    {
        return sysBasebandParamConstraintMapper.selectSysBasebandParamConstraintList(sysBasebandParamConstraint);
    }

    /**
     * 验证参数值是否满足约束条件
     * 
     * @param unitName 单元名称
     * @param unitType 单元类型
     * @param modeType 模式类型
     * @param paramName 参数名称
     * @param paramValue 参数值
     * @param allParams 所有参数值（用于跨参数验证）
     * @return 验证结果，如果通过返回null，否则返回错误信息
     */
    @Override
    public String validateParamConstraint(String unitName, String unitType, String modeType,
                                          String paramName, String paramValue, Map<String, String> allParams)
    {
        // 查询该参数作为目标参数的所有约束
        List<SysBasebandParamConstraint> constraints = 
            sysBasebandParamConstraintMapper.selectConstraintsByTargetParam(unitName, unitType, paramName, modeType);
        
        for (SysBasebandParamConstraint constraint : constraints) {
            // 检查约束条件是否满足
            if (!isConstraintConditionMet(constraint, allParams)) {
                continue; // 条件不满足，跳过此约束
            }
            
            String error = validateSingleConstraint(constraint, paramValue, allParams);
            if (error != null) {
                return error;
            }
        }
        
        return null;
    }
    
    /**
     * 检查约束条件是否满足
     */
    private boolean isConstraintConditionMet(SysBasebandParamConstraint constraint, Map<String, String> allParams)
    {
        String condition = constraint.getConstraintCondition();
        if (condition == null || condition.isEmpty()) {
            return true; // 无条件，总是满足
        }
        
        String sourceParam = constraint.getSourceParamName();
        if (sourceParam == null || allParams == null) {
            return false;
        }
        
        String sourceValue = allParams.get(sourceParam);
        if (sourceValue == null) {
            return false;
        }
        
        return evaluateCondition(sourceValue, condition);
    }

    /**
     * 验证单个约束
     */
    private String validateSingleConstraint(SysBasebandParamConstraint constraint, 
                                           String paramValue, Map<String, String> allParams)
    {
        String constraintType = constraint.getConstraintType();
        String constraintValue = constraint.getConstraintValue();
        
        try {
            switch (constraintType) {
                case "VALUE_RANGE":
                    return validateValueRange(paramValue, constraintValue, constraint.getErrorMessage());
                    
                case "FIXED_VALUE":
                    return validateFixedValue(paramValue, constraintValue, constraint.getErrorMessage());
                    
                case "FORMULA_CALCULATE":
                    return validateFormulaCalculate(paramValue, constraintValue, allParams, constraint.getErrorMessage());
                    
                case "ENUM_LIMIT":
                    return validateEnumLimit(paramValue, constraintValue, constraint.getErrorMessage());
                    
                case "CONTROL_DISABLE":
                    // 控件禁用约束不需要验证值，只需要前端禁用控件
                    return null;
                    
                default:
                    return null;
            }
        } catch (Exception e) {
            return "约束验证异常: " + e.getMessage();
        }
    }

    /**
     * 验证值范围约束
     */
    private String validateValueRange(String value, String range, String errorMsg)
    {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            double numValue = Double.parseDouble(value);
            String[] parts = range.split(",");
            double min = Double.parseDouble(parts[0]);
            double max = Double.parseDouble(parts[1]);
            
            if (numValue < min || numValue > max) {
                return errorMsg != null ? errorMsg : 
                    String.format("参数值 %.2f 超出范围 [%.2f, %.2f]", numValue, min, max);
            }
        } catch (NumberFormatException e) {
            return "参数值格式错误，应为数字";
        }
        
        return null;
    }

    /**
     * 验证固定值约束
     */
    private String validateFixedValue(String value, String allowedValues, String errorMsg)
    {
        if (value == null || value.isEmpty() || allowedValues == null || allowedValues.isEmpty()) {
            return null;
        }
        
        String[] allowed = allowedValues.split(",");
        for (String allowedValue : allowed) {
            if (value.trim().equals(allowedValue.trim())) {
                return null; // 值在允许列表中
            }
        }
        
        return errorMsg != null ? errorMsg : 
            String.format("参数值必须是以下值之一: %s", allowedValues);
    }

    /**
     * 验证公式计算约束
     */
    private String validateFormulaCalculate(String value, String formula, 
                                           Map<String, String> allParams, String errorMsg)
    {
        if (value == null || value.isEmpty() || formula == null || formula.isEmpty()) {
            return null;
        }
        
        try {
            // 获取源参数值
            String sourceValue = null;
            if (allParams != null) {
                for (Map.Entry<String, String> entry : allParams.entrySet()) {
                    if (formula.contains("source")) {
                        sourceValue = entry.getValue();
                        break;
                    }
                }
            }
            
            if (sourceValue == null) {
                return null;
            }
            
            // 计算期望值
            double source = Double.parseDouble(sourceValue);
            double expected = evaluateFormula(formula, source);
            double actual = Double.parseDouble(value);
            
            // 允许一定的误差
            if (Math.abs(actual - expected) > 0.01) {
                return errorMsg != null ? errorMsg : 
                    String.format("参数值应为 %.2f (根据公式 %s 计算)", expected, formula);
            }
        } catch (Exception e) {
            // 计算失败，不报错
            return null;
        }
        
        return null;
    }

    /**
     * 验证枚举限制约束
     */
    private String validateEnumLimit(String value, String allowedValues, String errorMsg)
    {
        if (value == null || value.isEmpty() || allowedValues == null || allowedValues.isEmpty()) {
            return null;
        }
        
        String[] allowed = allowedValues.split(",");
        for (String allowedValue : allowed) {
            if (value.trim().equals(allowedValue.trim())) {
                return null; // 值在允许列表中
            }
        }
        
        return errorMsg != null ? errorMsg : 
            String.format("参数值必须是以下枚举值之一: %s", allowedValues);
    }
    
    /**
     * 计算公式
     */
    private double evaluateFormula(String formula, double source)
    {
        // 简单的公式计算，支持基本运算
        String expr = formula.replace("source", String.valueOf(source));
        
        // 这里可以使用更复杂的表达式解析器，暂时只支持简单运算
        // 例如: source * 2, source + 10, (source - 1) * 0.5
        
        try {
            // 简单实现：使用ScriptEngine
            javax.script.ScriptEngineManager manager = new javax.script.ScriptEngineManager();
            javax.script.ScriptEngine engine = manager.getEngineByName("JavaScript");
            Object result = engine.eval(expr);
            return ((Number) result).doubleValue();
        } catch (Exception e) {
            return source; // 计算失败，返回原值
        }
    }

    /**
     * 评估条件表达式
     */
    private boolean evaluateCondition(String value, String condition)
    {
        if (condition == null || condition.isEmpty()) {
            return false;
        }
        
        try {
            if (condition.startsWith("==")) {
                String expected = condition.substring(2).trim();
                return value.equals(expected);
            } else if (condition.startsWith("!=")) {
                String expected = condition.substring(2).trim();
                return !value.equals(expected);
            } else if (condition.startsWith(">=")) {
                double numValue = Double.parseDouble(value);
                double threshold = Double.parseDouble(condition.substring(2).trim());
                return numValue >= threshold;
            } else if (condition.startsWith("<=")) {
                double numValue = Double.parseDouble(value);
                double threshold = Double.parseDouble(condition.substring(2).trim());
                return numValue <= threshold;
            } else if (condition.startsWith(">")) {
                double numValue = Double.parseDouble(value);
                double threshold = Double.parseDouble(condition.substring(1).trim());
                return numValue > threshold;
            } else if (condition.startsWith("<")) {
                double numValue = Double.parseDouble(value);
                double threshold = Double.parseDouble(condition.substring(1).trim());
                return numValue < threshold;
            }
        } catch (Exception e) {
            return false;
        }
        
        return false;
    }

    /**
     * 根据源参数获取受影响的目标参数列表
     * 
     * @param unitName 单元名称
     * @param unitType 单元类型
     * @param modeType 模式类型
     * @param paramName 参数名称
     * @return 受影响的参数列表
     */
    @Override
    public List<SysBasebandParamConstraint> getAffectedParams(String unitName, String unitType, 
                                                              String modeType, String paramName)
    {
        return sysBasebandParamConstraintMapper.selectConstraintsBySourceParam(unitName, unitType, paramName, modeType);
    }
    
    /**
     * 获取参数的所有约束（用于前端显示约束提示和应用约束规则）
     * 
     * @param unitName 单元名称
     * @param unitType 单元类型
     * @param modeType 模式类型
     * @return 约束列表（按参数分组）
     */
    @Override
    public Map<String, List<SysBasebandParamConstraint>> getAllConstraintsForUnit(
        String unitName, String unitType, String modeType)
    {
        // 查询所有约束
        SysBasebandParamConstraint query = new SysBasebandParamConstraint();
        query.setStatus("0"); // 只查询启用的约束
        List<SysBasebandParamConstraint> allConstraints = 
            sysBasebandParamConstraintMapper.selectSysBasebandParamConstraintList(query);
        
        // 按目标参数分组
        Map<String, List<SysBasebandParamConstraint>> result = new java.util.HashMap<>();
        for (SysBasebandParamConstraint constraint : allConstraints) {
            // 检查单元名称匹配
            if (!matchesUnit(constraint.getTargetUnitName(), unitName)) {
                continue;
            }
            // 检查单元类型匹配
            if (!matchesUnit(constraint.getTargetUnitType(), unitType)) {
                continue;
            }
            // 检查模式类型匹配
            if (!matchesModeType(constraint.getModeTypes(), modeType)) {
                continue;
            }
            
            String targetParam = constraint.getTargetParamName();
            result.computeIfAbsent(targetParam, k -> new java.util.ArrayList<>()).add(constraint);
        }
        
        return result;
    }
    
    /**
     * 检查单元是否匹配（支持通配符*）
     */
    private boolean matchesUnit(String pattern, String value)
    {
        if (pattern == null || pattern.isEmpty()) {
            return false;
        }
        return "*".equals(pattern) || pattern.equals(value);
    }
    
    /**
     * 检查模式类型是否匹配
     */
    private boolean matchesModeType(String modeTypes, String modeType)
    {
        if (modeTypes == null || modeTypes.isEmpty()) {
            return true; // 空表示适用所有模式
        }
        if (modeType == null || modeType.isEmpty()) {
            return true; // 如果没有指定模式类型，则匹配所有
        }
        // 检查是否在逗号分隔的列表中
        String[] types = modeTypes.split(",");
        for (String type : types) {
            if (type.trim().equals(modeType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 新增基带参数约束关系
     * 
     * @param sysBasebandParamConstraint 基带参数约束关系
     * @return 结果
     */
    @Override
    public int insertSysBasebandParamConstraint(SysBasebandParamConstraint sysBasebandParamConstraint)
    {
        sysBasebandParamConstraint.setCreateTime(DateUtils.getNowDate());
        return sysBasebandParamConstraintMapper.insertSysBasebandParamConstraint(sysBasebandParamConstraint);
    }

    /**
     * 修改基带参数约束关系
     * 
     * @param sysBasebandParamConstraint 基带参数约束关系
     * @return 结果
     */
    @Override
    public int updateSysBasebandParamConstraint(SysBasebandParamConstraint sysBasebandParamConstraint)
    {
        sysBasebandParamConstraint.setUpdateTime(DateUtils.getNowDate());
        return sysBasebandParamConstraintMapper.updateSysBasebandParamConstraint(sysBasebandParamConstraint);
    }

    /**
     * 批量删除基带参数约束关系
     * 
     * @param constraintIds 需要删除的基带参数约束关系主键
     * @return 结果
     */
    @Override
    public int deleteSysBasebandParamConstraintByConstraintIds(Long[] constraintIds)
    {
        return sysBasebandParamConstraintMapper.deleteSysBasebandParamConstraintByConstraintIds(constraintIds);
    }

    /**
     * 删除基带参数约束关系信息
     * 
     * @param constraintId 基带参数约束关系主键
     * @return 结果
     */
    @Override
    public int deleteSysBasebandParamConstraintByConstraintId(Long constraintId)
    {
        return sysBasebandParamConstraintMapper.deleteSysBasebandParamConstraintByConstraintId(constraintId);
    }
}
