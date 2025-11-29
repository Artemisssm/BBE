package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysBasebandParamDef;
import com.ruoyi.system.service.ISysBasebandParamDefService;

/**
 * 基带参数定义管理
 */
@RestController
@RequestMapping("/system/baseband/param")
public class SysBasebandParamDefController extends BaseController
{
    @Autowired
    private ISysBasebandParamDefService paramDefService;

    /**
     * 列表
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBasebandParamDef paramDef)
    {
        startPage();
        List<SysBasebandParamDef> list = paramDefService.selectBasebandParamDefList(paramDef);
        return getDataTable(list);
    }

    /**
     * 导出
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:export')")
    @Log(title = "基带参数定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBasebandParamDef paramDef)
    {
        List<SysBasebandParamDef> list = paramDefService.selectBasebandParamDefList(paramDef);
        ExcelUtil<SysBasebandParamDef> util = new ExcelUtil<>(SysBasebandParamDef.class);
        util.exportExcel(response, list, "基带参数定义");
    }

    /**
     * 详情
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:query')")
    @GetMapping("/{paramId}")
    public AjaxResult getInfo(@PathVariable Long paramId)
    {
        return success(paramDefService.selectBasebandParamDefById(paramId));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:add')")
    @Log(title = "基带参数定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysBasebandParamDef paramDef)
    {
        if (!paramDefService.checkParamCodeUnique(paramDef))
        {
            return error("参数编码已存在");
        }
        paramDef.setCreateTime(DateUtils.getNowDate());
        return toAjax(paramDefService.insertBasebandParamDef(paramDef));
    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:edit')")
    @Log(title = "基带参数定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysBasebandParamDef paramDef)
    {
        if (!paramDefService.checkParamCodeUnique(paramDef))
        {
            return error("参数编码已存在");
        }
        return toAjax(paramDefService.updateBasebandParamDef(paramDef));
    }

    /**
     * 删除
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:remove')")
    @Log(title = "基带参数定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{paramIds}")
    public AjaxResult remove(@PathVariable Long[] paramIds)
    {
        return toAjax(paramDefService.deleteBasebandParamDefByIds(paramIds));
    }
}


