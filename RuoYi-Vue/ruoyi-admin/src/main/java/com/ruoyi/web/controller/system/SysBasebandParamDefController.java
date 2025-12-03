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
import com.ruoyi.system.domain.SysBasebandParamDef;
import com.ruoyi.system.service.ISysBasebandParamDefService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 基带参数定义Controller
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@RestController
@RequestMapping("/system/baseband/param")
public class SysBasebandParamDefController extends BaseController
{
    @Autowired
    private ISysBasebandParamDefService sysBasebandParamDefService;

    /**
     * 查询基带参数定义列表
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysBasebandParamDef sysBasebandParamDef)
    {
        startPage();
        List<SysBasebandParamDef> list = sysBasebandParamDefService.selectSysBasebandParamDefList(sysBasebandParamDef);
        return getDataTable(list);
    }

    /**
     * 导出基带参数定义列表
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:export')")
    @Log(title = "基带参数定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysBasebandParamDef sysBasebandParamDef)
    {
        List<SysBasebandParamDef> list = sysBasebandParamDefService.selectSysBasebandParamDefList(sysBasebandParamDef);
        ExcelUtil<SysBasebandParamDef> util = new ExcelUtil<SysBasebandParamDef>(SysBasebandParamDef.class);
        util.exportExcel(response, list, "基带参数定义数据");
    }

    /**
     * 获取基带参数定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:query')")
    @GetMapping(value = "/{paramId}")
    public AjaxResult getInfo(@PathVariable("paramId") Long paramId)
    {
        return success(sysBasebandParamDefService.selectSysBasebandParamDefByParamId(paramId));
    }

    /**
     * 新增基带参数定义
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:add')")
    @Log(title = "基带参数定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysBasebandParamDef sysBasebandParamDef)
    {
        return toAjax(sysBasebandParamDefService.insertSysBasebandParamDef(sysBasebandParamDef));
    }

    /**
     * 修改基带参数定义
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:edit')")
    @Log(title = "基带参数定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysBasebandParamDef sysBasebandParamDef)
    {
        return toAjax(sysBasebandParamDefService.updateSysBasebandParamDef(sysBasebandParamDef));
    }

    /**
     * 删除基带参数定义
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:remove')")
    @Log(title = "基带参数定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{paramIds}")
    public AjaxResult remove(@PathVariable Long[] paramIds)
    {
        return toAjax(sysBasebandParamDefService.deleteSysBasebandParamDefByParamIds(paramIds));
    }

    /**
     * 批量更新参数排序
     */
    @PreAuthorize("@ss.hasPermi('system:basebandParam:edit')")
    @Log(title = "基带参数定义-排序", businessType = BusinessType.UPDATE)
    @PutMapping("/sort")
    public AjaxResult batchUpdateSortOrder(@RequestBody List<SysBasebandParamDef> paramList)
    {
        try {
            if (paramList == null || paramList.isEmpty()) {
                return error("参数列表不能为空");
            }
            int result = sysBasebandParamDefService.batchUpdateSortOrder(paramList);
            return result > 0 ? success("排序更新成功") : error("排序更新失败");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        } catch (Exception e) {
            logger.error("批量更新参数排序失败", e);
            return error("排序更新失败：" + e.getMessage());
        }
    }
}
