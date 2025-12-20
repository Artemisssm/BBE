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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysBasebandMacro;
import com.ruoyi.system.service.ISysBasebandMacroService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 基带宏定义Controller
 * 
 * @author ruoyi
 * @date 2025-12-09
 */
@RestController
@RequestMapping("/system/basebandMacro")
public class SysBasebandMacroController extends BaseController
{
    @Autowired
    private ISysBasebandMacroService sysBasebandMacroService;

    /**
     * 查询基带宏定义列表
     */
    @PreAuthorize("@ss.hasPermi('system:basebandMacro:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBasebandMacro sysBasebandMacro)
    {
        startPage();
        List<SysBasebandMacro> list = sysBasebandMacroService.selectSysBasebandMacroList(sysBasebandMacro);
        return getDataTable(list);
    }

    /**
     * 导出基带宏定义列表
     */
    @PreAuthorize("@ss.hasPermi('system:basebandMacro:export')")
    @Log(title = "基带宏定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBasebandMacro sysBasebandMacro)
    {
        List<SysBasebandMacro> list = sysBasebandMacroService.selectSysBasebandMacroList(sysBasebandMacro);
        ExcelUtil<SysBasebandMacro> util = new ExcelUtil<SysBasebandMacro>(SysBasebandMacro.class);
        util.exportExcel(response, list, "基带宏定义数据");
    }

    /**
     * 获取基带宏定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:basebandMacro:query')")
    @GetMapping(value = "/{macroId}")
    public AjaxResult getInfo(@PathVariable("macroId") Long macroId)
    {
        return success(sysBasebandMacroService.selectSysBasebandMacroByMacroId(macroId));
    }

    /**
     * 新增基带宏定义
     */
    @PreAuthorize("@ss.hasPermi('system:basebandMacro:add')")
    @Log(title = "基带宏定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysBasebandMacro sysBasebandMacro)
    {
        // 检查宏编码唯一性
        if ("1".equals(sysBasebandMacroService.checkMacroCodeUnique(sysBasebandMacro)))
        {
            return error("新增宏'" + sysBasebandMacro.getMacroName() + "'失败，宏编码已存在");
        }
        sysBasebandMacro.setCreateBy(getUsername());
        return toAjax(sysBasebandMacroService.insertSysBasebandMacro(sysBasebandMacro));
    }

    /**
     * 修改基带宏定义
     */
    @PreAuthorize("@ss.hasPermi('system:basebandMacro:edit')")
    @Log(title = "基带宏定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysBasebandMacro sysBasebandMacro)
    {
        // 检查宏编码唯一性
        if ("1".equals(sysBasebandMacroService.checkMacroCodeUnique(sysBasebandMacro)))
        {
            return error("修改宏'" + sysBasebandMacro.getMacroName() + "'失败，宏编码已存在");
        }
        sysBasebandMacro.setUpdateBy(getUsername());
        return toAjax(sysBasebandMacroService.updateSysBasebandMacro(sysBasebandMacro));
    }

    /**
     * 删除基带宏定义
     */
    @PreAuthorize("@ss.hasPermi('system:basebandMacro:remove')")
    @Log(title = "基带宏定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{macroIds}")
    public AjaxResult remove(@PathVariable Long[] macroIds)
    {
        return toAjax(sysBasebandMacroService.deleteSysBasebandMacroByMacroIds(macroIds));
    }

    /**
     * 根据单元类型和模式类型查询可用宏
     */
    @PreAuthorize("@ss.hasPermi('system:basebandMacro:query')")
    @GetMapping("/available")
    public AjaxResult getAvailableMacros(
        @RequestParam(required = false) String unitType,
        @RequestParam(required = false) String modeType)
    {
        List<SysBasebandMacro> list = sysBasebandMacroService.selectAvailableMacros(unitType, modeType);
        return success(list);
    }

    /**
     * 获取宏的参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:basebandMacro:config')")
    @GetMapping("/{macroId}/params")
    public AjaxResult getMacroParams(@PathVariable Long macroId)
    {
        List<Map<String, Object>> params = sysBasebandMacroService.getMacroParams(macroId);
        return success(params);
    }

    /**
     * 保存宏的参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:basebandMacro:config')")
    @Log(title = "宏参数配置", businessType = BusinessType.UPDATE)
    @PostMapping("/{macroId}/params")
    public AjaxResult saveMacroParams(
        @PathVariable Long macroId,
        @RequestBody List<Map<String, Object>> params)
    {
        return toAjax(sysBasebandMacroService.saveMacroParams(macroId, params));
    }
}
