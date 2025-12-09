package com.ruoyi.web.controller.system;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.service.ISysBasebandUnitService;

/**
 * 板卡资源监控Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/basebandResource")
public class SysBasebandResourceController extends BaseController
{
    @Autowired
    private ISysBasebandUnitService basebandUnitService;

    /**
     * 单元类型转换：英文转中文
     */
    private String convertUnitType(String unitType) {
        if (unitType == null) return "";
        switch (unitType) {
            case "ENCODE": return "编码";
            case "MODULATE": return "调制";
            case "DEMODULATE": return "解调";
            case "DECODE": return "译码";
            default: return unitType;
        }
    }

    /**
     * 获取板卡资源统计信息
     */
    @GetMapping("/stats")
    public AjaxResult getStats()
    {
        // 查询所有基带单元
        List<SysBasebandUnit> units = basebandUnitService.selectSysBasebandUnitList(new SysBasebandUnit());
        
        // 板卡配置
        int totalBoards = 4;  // 总板卡数
        int fpgasPerBoard = 2; // 每板FPGA数
        int totalFpgas = totalBoards * fpgasPerBoard;
        
        // 统计已使用的FPGA（使用channelNo作为唯一标识）
        Set<Integer> usedChannels = new HashSet<>();
        for (SysBasebandUnit unit : units) {
            if (unit.getChannelNo() != null) {
                usedChannels.add(unit.getChannelNo());
            }
        }
        
        int usedFpgaCount = usedChannels.size();
        int availableFpgaCount = totalFpgas - usedFpgaCount;
        int usageRate = totalFpgas > 0 ? (usedFpgaCount * 100 / totalFpgas) : 0;
        
        // 构建板卡详情
        List<Map<String, Object>> boardDetails = new ArrayList<>();
        for (int i = 1; i <= totalBoards; i++) {
            Map<String, Object> board = new HashMap<>();
            board.put("boardNo", i);
            board.put("boardName", "板" + i);
            
            // 计算该板的通道号范围：板1=1-2, 板2=3-4, 板3=5-6, 板4=7-8
            int startChannel = (i - 1) * fpgasPerBoard + 1;
            int endChannel = i * fpgasPerBoard;
            
            // 查找该板上的单元
            final int boardNo = i;
            List<SysBasebandUnit> boardUnits = units.stream()
                .filter(u -> u.getChannelNo() != null && 
                            u.getChannelNo() >= startChannel && 
                            u.getChannelNo() <= endChannel)
                .collect(Collectors.toList());
            
            // FPGA0 (奇数通道)
            SysBasebandUnit fpga0Unit = boardUnits.stream()
                .filter(u -> u.getChannelNo() == startChannel)
                .findFirst()
                .orElse(null);
            if (fpga0Unit != null) {
                Map<String, Object> fpga0 = new HashMap<>();
                fpga0.put("unitId", fpga0Unit.getUnitId());
                fpga0.put("unitName", fpga0Unit.getUnitName());
                fpga0.put("unitType", convertUnitType(fpga0Unit.getUnitType()));
                board.put("fpga0", fpga0);
            }
            
            // FPGA1 (偶数通道)
            SysBasebandUnit fpga1Unit = boardUnits.stream()
                .filter(u -> u.getChannelNo() == endChannel)
                .findFirst()
                .orElse(null);
            if (fpga1Unit != null) {
                Map<String, Object> fpga1 = new HashMap<>();
                fpga1.put("unitId", fpga1Unit.getUnitId());
                fpga1.put("unitName", fpga1Unit.getUnitName());
                fpga1.put("unitType", convertUnitType(fpga1Unit.getUnitType()));
                board.put("fpga1", fpga1);
            }
            
            // 单元列表
            List<Map<String, Object>> unitList = new ArrayList<>();
            for (SysBasebandUnit unit : boardUnits) {
                Map<String, Object> unitInfo = new HashMap<>();
                unitInfo.put("unitId", unit.getUnitId());
                unitInfo.put("unitName", unit.getUnitName());
                unitInfo.put("unitType", convertUnitType(unit.getUnitType()));
                unitList.add(unitInfo);
            }
            board.put("units", unitList);
            board.put("unitCount", boardUnits.size());
            
            // 计算使用率
            int boardUsedFpgas = 0;
            if (fpga0Unit != null) boardUsedFpgas++;
            if (fpga1Unit != null) boardUsedFpgas++;
            board.put("usageRate", (boardUsedFpgas * 100 / fpgasPerBoard));
            
            boardDetails.add(board);
        }
        
        // 统计单元类型分布
        Map<String, Long> unitTypeCount = units.stream()
            .collect(Collectors.groupingBy(SysBasebandUnit::getUnitType, Collectors.counting()));
        
        List<Map<String, Object>> unitTypeStats = new ArrayList<>();
        for (Map.Entry<String, Long> entry : unitTypeCount.entrySet()) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("unitType", convertUnitType(entry.getKey()));
            stat.put("count", entry.getValue());
            unitTypeStats.add(stat);
        }
        
        // 板卡使用情况
        Map<String, Object> boardUsage = new HashMap<>();
        boardUsage.put("used", usedFpgaCount);
        boardUsage.put("available", availableFpgaCount);
        
        // 组装返回数据
        Map<String, Object> result = new HashMap<>();
        result.put("totalBoards", totalBoards);
        result.put("usedFpgas", usedFpgaCount);
        result.put("availableFpgas", availableFpgaCount);
        result.put("usageRate", usageRate);
        result.put("boardDetails", boardDetails);
        result.put("unitTypeStats", unitTypeStats);
        result.put("boardUsage", boardUsage);
        
        return success(result);
    }
}
