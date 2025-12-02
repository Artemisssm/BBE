package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysBasebandParamConstraint;

/**
 * 基带参数约束关系Service接口
 * 
 * @author ruoyi
 * @date 2024-12-02
 */
public interface ISysBasebandParamConstraintService 
{
    /**
     * 查询基带参数约束关系
     * 
     * @param constraintId 基带参数约束关系主键
     * @return 基带参数约束关系
     */
    public SysBasebandParamConstraint selectSysBasebandParamConstraintByConstraintId(Long constraintId);

    /**
     * 查询基带参数约束关系列表
     * 
     * @param sysBasebandParamConstraint 基带参数约束关系
     * @return 基带参数约束关系集合
     */
    public List<SysBasebandParamConstraint> selectSysBasebandParamConstraintList(SysBasebandParamConstraint sysBasebandParamConstraint);

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
    public String validateParamConstraint(String unitName, String unitType, String modeType,
                                          String paramName, String paramValue, Map<String, String> allParams);

    /**
     * 根据源参数获取受影响的目标参数列表
     * 
     * @param unitName 单元名称
     * @param unitType 单元类型
     * @param modeType 模式类型
     * @param paramName 参数名称
     * @return 受影响的参数列表
     */
    public List<SysBasebandParamConstraint> getAffectedParams(String unitName, String unitType, 
                                                              String modeType, String paramName);
    
    /**
     * 获取参数的所有约束（用于前端显示约束提示和应用约束规则）
     * 
     * @param unitName 单元名称
     * @param unitType 单元类型
     * @param modeType 模式类型
     * @return 约束列表（按参数分组）
     */
    public Map<String, List<SysBasebandParamConstraint>> getAllConstraintsForUnit(
        String unitName, String unitType, String modeType);

    /**
     * 新增基带参数约束关系
     * 
     * @param sysBasebandParamConstraint 基带参数约束关系
     * @return 结果
     */
    public int insertSysBasebandParamConstraint(SysBasebandParamConstraint sysBasebandParamConstraint);

    /**
     * 修改基带参数约束关系
     * 
     * @param sysBasebandParamConstraint 基带参数约束关系
     * @return 结果
     */
    public int updateSysBasebandParamConstraint(SysBasebandParamConstraint sysBasebandParamConstraint);

    /**
     * 批量删除基带参数约束关系
     * 
     * @param constraintIds 需要删除的基带参数约束关系主键集合
     * @return 结果
     */
    public int deleteSysBasebandParamConstraintByConstraintIds(Long[] constraintIds);

    /**
     * 删除基带参数约束关系信息
     * 
     * @param constraintId 基带参数约束关系主键
     * @return 结果
     */
    public int deleteSysBasebandParamConstraintByConstraintId(Long constraintId);
}
