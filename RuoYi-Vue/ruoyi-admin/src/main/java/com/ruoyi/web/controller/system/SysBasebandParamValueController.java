package com.ruoyi.web.controller.system;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.dto.BasebandParamValueDto;
import com.ruoyi.system.domain.vo.BasebandParamVo;
import com.ruoyi.system.service.IBasebandDispatchService;
import com.ruoyi.system.service.ISysBasebandParamValueService;

/**
 * 参数值配置 + 下发
 */
@Validated
@RestController
@RequestMapping("/system/baseband/value")
public class SysBasebandParamValueController extends BaseController
{
    @Autowired
    private ISysBasebandParamValueService paramValueService;

    @Autowired
    private IBasebandDispatchService dispatchService;

    @PreAuthorize("@ss.hasPermi('system:basebandValue:list')")
    @GetMapping("/{unitId}")
    public AjaxResult list(@PathVariable Long unitId)
    {
        List<BasebandParamVo> data = paramValueService.selectParamVoByUnit(unitId);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('system:basebandValue:edit')")
    @Log(title = "基带参数配置", businessType = BusinessType.UPDATE)
    @PutMapping("/{unitId}")
    public AjaxResult save(@PathVariable Long unitId, @Valid @RequestBody List<BasebandParamValueDto> body)
    {
        paramValueService.saveParamValues(unitId, body, getUsername());
        return success();
    }

    @PreAuthorize("@ss.hasPermi('system:basebandValue:dispatch')")
    @Log(title = "基带参数下发", businessType = BusinessType.OTHER)
    @PostMapping("/{unitId}/dispatch")
    public AjaxResult dispatch(@PathVariable Long unitId)
    {
        dispatchService.dispatch(unitId, getUsername(), "MANUAL");
        return success();
    }
}


