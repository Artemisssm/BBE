package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.ISysBasebandParamValueService;

/**
 * 基带参数取值Controller
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@RestController
@RequestMapping("/system/baseband/value")
public class SysBasebandParamValueController extends BaseController
{
    @Autowired
    private ISysBasebandParamValueService sysBasebandParamValueService;

    /**
     * 根据单元ID获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:basebandValue:list')")
    @GetMapping("/{unitId}")
    public AjaxResult getParamValueList(@PathVariable("unitId") Long unitId)
    {
        List<Map<String, Object>> list = sysBasebandParamValueService.selectParamValueListByUnitId(unitId);
        return success(list);
    }

    /**
     * 保存参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:basebandValue:edit')")
    @Log(title = "基带参数配置", businessType = BusinessType.UPDATE)
    @PutMapping("/{unitId}")
    public AjaxResult saveParamValues(@PathVariable("unitId") Long unitId, @RequestBody List<Map<String, Object>> paramValues)
    {
        return toAjax(sysBasebandParamValueService.saveParamValues(unitId, paramValues));
    }

    /**
     * 下发参数到硬件设备
     */
    @PreAuthorize("@ss.hasPermi('system:basebandValue:dispatch')")
    @Log(title = "基带参数下发", businessType = BusinessType.OTHER)
    @PostMapping("/{unitId}/dispatch")
    public AjaxResult dispatchParams(@PathVariable("unitId") Long unitId)
    {
        return toAjax(sysBasebandParamValueService.dispatchParams(unitId));
    }

    /**
     * 智能生成符合约束的参数值
     */
    @PreAuthorize("@ss.hasPermi('system:basebandValue:list')")
    @GetMapping("/{unitId}/smart-values")
    public AjaxResult generateSmartValues(@PathVariable("unitId") Long unitId)
    {
        List<Map<String, Object>> smartValues = sysBasebandParamValueService.generateSmartValues(unitId);
        return success(smartValues);
    }
}
