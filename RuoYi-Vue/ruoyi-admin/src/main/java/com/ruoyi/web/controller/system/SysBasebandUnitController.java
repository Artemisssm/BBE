package com.ruoyi.web.controller.system;

import java.util.List;
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
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.service.ISysBasebandUnitService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 基带单元Controller
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@RestController
@RequestMapping("/system/baseband/unit")
public class SysBasebandUnitController extends BaseController
{
    @Autowired
    private ISysBasebandUnitService sysBasebandUnitService;

    /**
     * 查询基带单元列表
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBasebandUnit sysBasebandUnit)
    {
        startPage();
        List<SysBasebandUnit> list = sysBasebandUnitService.selectSysBasebandUnitList(sysBasebandUnit);
        return getDataTable(list);
    }

    /**
     * 导出基带单元列表
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:export')")
    @Log(title = "基带单元", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBasebandUnit sysBasebandUnit)
    {
        List<SysBasebandUnit> list = sysBasebandUnitService.selectSysBasebandUnitList(sysBasebandUnit);
        ExcelUtil<SysBasebandUnit> util = new ExcelUtil<SysBasebandUnit>(SysBasebandUnit.class);
        util.exportExcel(response, list, "基带单元数据");
    }

    /**
     * 获取基带单元详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:query')")
    @GetMapping(value = "/{unitId}")
    public AjaxResult getInfo(@PathVariable("unitId") Long unitId)
    {
        return success(sysBasebandUnitService.selectSysBasebandUnitByUnitId(unitId));
    }

    /**
     * 新增基带单元
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:add')")
    @Log(title = "基带单元", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysBasebandUnit sysBasebandUnit)
    {
        return toAjax(sysBasebandUnitService.insertSysBasebandUnit(sysBasebandUnit));
    }

    /**
     * 修改基带单元
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:edit')")
    @Log(title = "基带单元", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysBasebandUnit sysBasebandUnit)
    {
        return toAjax(sysBasebandUnitService.updateSysBasebandUnit(sysBasebandUnit));
    }

    /**
     * 删除基带单元
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:remove')")
    @Log(title = "基带单元", businessType = BusinessType.DELETE)
	@DeleteMapping("/{unitIds}")
    public AjaxResult remove(@PathVariable Long[] unitIds)
    {
        return toAjax(sysBasebandUnitService.deleteSysBasebandUnitByUnitIds(unitIds));
    }
}
