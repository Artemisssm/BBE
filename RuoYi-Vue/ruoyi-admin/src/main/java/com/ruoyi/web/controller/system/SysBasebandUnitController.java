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
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.service.ISysBasebandUnitService;

/**
 * 基带单元管理
 */
@RestController
@RequestMapping("/system/baseband/unit")
public class SysBasebandUnitController extends BaseController
{
    @Autowired
    private ISysBasebandUnitService basebandUnitService;

    /**
     * 列表
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBasebandUnit unit)
    {
        startPage();
        List<SysBasebandUnit> list = basebandUnitService.selectBasebandUnitList(unit);
        return getDataTable(list);
    }

    /**
     * 导出
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:export')")
    @Log(title = "基带单元", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBasebandUnit unit)
    {
        List<SysBasebandUnit> list = basebandUnitService.selectBasebandUnitList(unit);
        ExcelUtil<SysBasebandUnit> util = new ExcelUtil<>(SysBasebandUnit.class);
        util.exportExcel(response, list, "基带单元数据");
    }

    /**
     * 详情
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:query')")
    @GetMapping("/{unitId}")
    public AjaxResult getInfo(@PathVariable Long unitId)
    {
        return success(basebandUnitService.selectBasebandUnitById(unitId));
    }

    /**
     * 新增
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:add')")
    @Log(title = "基带单元", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysBasebandUnit unit)
    {
        if (!basebandUnitService.checkUnitNameUnique(unit))
        {
            return error("单元名称已存在");
        }
        unit.setCreateBy(getUsername());
        unit.setCreateTime(DateUtils.getNowDate());
        return toAjax(basebandUnitService.insertBasebandUnit(unit));
    }

    /**
     * 修改
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:edit')")
    @Log(title = "基带单元", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysBasebandUnit unit)
    {
        if (!basebandUnitService.checkUnitNameUnique(unit))
        {
            return error("单元名称已存在");
        }
        unit.setUpdateBy(getUsername());
        unit.setUpdateTime(DateUtils.getNowDate());
        return toAjax(basebandUnitService.updateBasebandUnit(unit));
    }

    /**
     * 删除
     */
    @PreAuthorize("@ss.hasPermi('system:basebandUnit:remove')")
    @Log(title = "基带单元", businessType = BusinessType.DELETE)
    @DeleteMapping("/{unitIds}")
    public AjaxResult remove(@PathVariable Long[] unitIds)
    {
        return toAjax(basebandUnitService.deleteBasebandUnitByIds(unitIds));
    }
}


