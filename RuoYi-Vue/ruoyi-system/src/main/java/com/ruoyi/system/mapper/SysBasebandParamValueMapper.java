package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysBasebandParamValue;
import org.apache.ibatis.annotations.Param;

/**
 * 基带参数取值Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
public interface SysBasebandParamValueMapper 
{
    /**
     * 查询基带参数取值
     * 
     * @param valueId 基带参数取值主键
     * @return 基带参数取值
     */
    public SysBasebandParamValue selectSysBasebandParamValueByValueId(Long valueId);

    /**
     * 根据单元ID和参数ID查询参数值
     * 
     * @param unitId 单元ID
     * @param paramId 参数ID
     * @return 基带参数取值
     */
    public SysBasebandParamValue selectSysBasebandParamValueByUnitAndParam(@Param("unitId") Long unitId, @Param("paramId") Long paramId);

    /**
     * 根据单元ID查询所有参数值
     * 
     * @param unitId 单元ID
     * @return 基带参数取值集合
     */
    public List<SysBasebandParamValue> selectSysBasebandParamValueByUnitId(Long unitId);

    /**
     * 新增基带参数取值
     * 
     * @param sysBasebandParamValue 基带参数取值
     * @return 结果
     */
    public int insertSysBasebandParamValue(SysBasebandParamValue sysBasebandParamValue);

    /**
     * 修改基带参数取值
     * 
     * @param sysBasebandParamValue 基带参数取值
     * @return 结果
     */
    public int updateSysBasebandParamValue(SysBasebandParamValue sysBasebandParamValue);

    /**
     * 删除基带参数取值
     * 
     * @param valueId 基带参数取值主键
     * @return 结果
     */
    public int deleteSysBasebandParamValueByValueId(Long valueId);

    /**
     * 根据单元ID删除所有参数值
     * 
     * @param unitId 单元ID
     * @return 结果
     */
    public int deleteSysBasebandParamValueByUnitId(Long unitId);

    /**
     * 根据单元ID查询参数值VO列表（用于下发）
     * 包含参数定义信息，按硬件顺序排序
     * 
     * @param unitId 单元ID
     * @return 参数值VO集合
     */
    public List<com.ruoyi.system.domain.vo.BasebandParamVo> selectParamVoListByUnitId(Long unitId);
}
