package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandParamConstraint;
import org.apache.ibatis.annotations.Param;

/**
 * 基带参数约束关系Mapper接口
 * 
 * @author ruoyi
 * @date 2024-12-02
 */
public interface SysBasebandParamConstraintMapper 
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
     * 根据源参数查询约束关系
     * 
     * @param unitName 单元名称
     * @param unitType 单元类型
     * @param paramName 参数名称
     * @param modeType 模式类型
     * @return 约束关系列表
     */
    public List<SysBasebandParamConstraint> selectConstraintsBySourceParam(
        @Param("unitName") String unitName,
        @Param("unitType") String unitType,
        @Param("paramName") String paramName,
        @Param("modeType") String modeType
    );

    /**
     * 根据目标参数查询约束关系
     * 
     * @param unitName 单元名称
     * @param unitType 单元类型
     * @param paramName 参数名称
     * @param modeType 模式类型
     * @return 约束关系列表
     */
    public List<SysBasebandParamConstraint> selectConstraintsByTargetParam(
        @Param("unitName") String unitName,
        @Param("unitType") String unitType,
        @Param("paramName") String paramName,
        @Param("modeType") String modeType
    );

    /**
     * 根据单元查询所有约束关系
     * 
     * @param unitName 单元名称
     * @param unitType 单元类型
     * @param modeType 模式类型
     * @return 约束关系列表
     */
    public List<SysBasebandParamConstraint> selectConstraintsByUnit(
        @Param("unitName") String unitName,
        @Param("unitType") String unitType,
        @Param("modeType") String modeType
    );

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
     * 删除基带参数约束关系
     * 
     * @param constraintId 基带参数约束关系主键
     * @return 结果
     */
    public int deleteSysBasebandParamConstraintByConstraintId(Long constraintId);

    /**
     * 批量删除基带参数约束关系
     * 
     * @param constraintIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysBasebandParamConstraintByConstraintIds(Long[] constraintIds);
}
