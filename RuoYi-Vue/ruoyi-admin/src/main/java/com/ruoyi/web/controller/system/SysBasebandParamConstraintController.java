package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysBasebandParamConstraint;
import com.ruoyi.system.service.ISysBasebandParamConstraintService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 基带参数约束关系Controller
 * 
 * @author ruoyi
 * @date 2024-12-02
 */
@RestController
@RequestMapping("/system/paramConstraint")
public class SysBasebandParamConstraintController extends BaseController
{
    @Autowired
    private ISysBasebandParamConstraintService sysBasebandParamConstraintService;

    /**
     * 查询基带参数约束关系列表
     */
    @PreAuthorize("@ss.hasPermi('system:paramConstraint:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBasebandParamConstraint sysBasebandParamConstraint)
    {
        startPage();
        List<SysBasebandParamConstraint> list = sysBasebandParamConstraintService.selectSysBasebandParamConstraintList(sysBasebandParamConstraint);
        return getDataTable(list);
    }

    /**
     * 导出基带参数约束关系列表
     */
    @PreAuthorize("@ss.hasPermi('system:paramConstraint:export')")
    @Log(title = "基带参数约束关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBasebandParamConstraint sysBasebandParamConstraint)
    {
        List<SysBasebandParamConstraint> list = sysBasebandParamConstraintService.selectSysBasebandParamConstraintList(sysBasebandParamConstraint);
        ExcelUtil<SysBasebandParamConstraint> util = new ExcelUtil<SysBasebandParamConstraint>(SysBasebandParamConstraint.class);
        util.exportExcel(response, list, "基带参数约束关系数据");
    }

    /**
     * 获取基带参数约束关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:paramConstraint:query')")
    @GetMapping(value = "/{constraintId}")
    public AjaxResult getInfo(@PathVariable("constraintId") Long constraintId)
    {
        return success(sysBasebandParamConstraintService.selectSysBasebandParamConstraintByConstraintId(constraintId));
    }

    /**
     * 验证参数约束
     */
    @PostMapping("/validate")
    public AjaxResult validateConstraint(@RequestBody Map<String, Object> params)
    {
        String unitName = (String) params.get("unitName");
        String unitType = (String) params.get("unitType");
        String modeType = (String) params.get("modeType");
        String paramName = (String) params.get("paramName");
        String paramValue = (String) params.get("paramValue");
        @SuppressWarnings("unchecked")
        Map<String, String> allParams = (Map<String, String>) params.get("allParams");
        
        String error = sysBasebandParamConstraintService.validateParamConstraint(
            unitName, unitType, modeType, paramName, paramValue, allParams);
        
        if (error != null) {
            return AjaxResult.error(error);
        }
        return AjaxResult.success();
    }

    /**
     * 获取受影响的参数列表
     */
    @GetMapping("/affected/{unitName}/{unitType}/{modeType}/{paramName}")
    public AjaxResult getAffectedParams(@PathVariable String unitName, 
                                       @PathVariable String unitType,
                                       @PathVariable String modeType,
                                       @PathVariable String paramName)
    {
        List<SysBasebandParamConstraint> list = sysBasebandParamConstraintService.getAffectedParams(
            unitName, unitType, modeType, paramName);
        return success(list);
    }
    
    /**
     * 获取单元的所有约束（用于前端显示和应用约束规则）
     */
    @GetMapping("/unit/{unitName}/{unitType}/{modeType}")
    public AjaxResult getAllConstraintsForUnit(@PathVariable String unitName,
                                               @PathVariable String unitType,
                                               @PathVariable String modeType)
    {
        Map<String, List<SysBasebandParamConstraint>> constraints = 
            sysBasebandParamConstraintService.getAllConstraintsForUnit(unitName, unitType, modeType);
        return success(constraints);
    }

    /**
     * 新增基带参数约束关系
     */
    @PreAuthorize("@ss.hasPermi('system:paramConstraint:add')")
    @Log(title = "基带参数约束关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysBasebandParamConstraint sysBasebandParamConstraint)
    {
        return toAjax(sysBasebandParamConstraintService.insertSysBasebandParamConstraint(sysBasebandParamConstraint));
    }

    /**
     * 修改基带参数约束关系
     */
    @PreAuthorize("@ss.hasPermi('system:paramConstraint:edit')")
    @Log(title = "基带参数约束关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysBasebandParamConstraint sysBasebandParamConstraint)
    {
        return toAjax(sysBasebandParamConstraintService.updateSysBasebandParamConstraint(sysBasebandParamConstraint));
    }

    /**
     * 删除基带参数约束关系
     */
    @PreAuthorize("@ss.hasPermi('system:paramConstraint:remove')")
    @Log(title = "基带参数约束关系", businessType = BusinessType.DELETE)
	@DeleteMapping("/{constraintIds}")
    public AjaxResult remove(@PathVariable Long[] constraintIds)
    {
        return toAjax(sysBasebandParamConstraintService.deleteSysBasebandParamConstraintByConstraintIds(constraintIds));
    }
}
