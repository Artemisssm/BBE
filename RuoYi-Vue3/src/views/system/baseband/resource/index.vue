<template>
  <div class="app-container resource-monitor" v-loading="isRefreshing" element-loading-text="正在刷新数据...">
    <el-row :gutter="20" class="stats-row">
      <!-- 总体统计卡片 -->
      <el-col :span="6">
        <el-card class="stat-card total">
          <div class="stat-content">
            <el-icon class="stat-icon"><Cpu /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ resourceStats.totalBoards }}</div>
              <div class="stat-label">板卡总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card used">
          <div class="stat-content">
            <el-icon class="stat-icon"><CircleCheck /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ resourceStats.usedFpgas }}</div>
              <div class="stat-label">已用资源</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card available">
          <div class="stat-content">
            <el-icon class="stat-icon"><CircleClose /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ resourceStats.availableFpgas }}</div>
              <div class="stat-label">可用资源</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card usage">
          <div class="stat-content">
            <el-icon class="stat-icon"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ resourceStats.usageRate }}%</div>
              <div class="stat-label">使用率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 板卡资源分布图 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="card-title">板卡资源分布</span>
          </template>
          <div ref="boardChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 单元类型分布 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span class="card-title">单元类型分布</span>
          </template>
          <div ref="unitTypeChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 板卡详细使用情况 -->
      <el-col :span="24">
        <el-card>
          <template #header>
            <span class="card-title">板卡详细使用情况</span>
          </template>
          <div ref="detailChartRef" class="chart-container-large"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 板卡资源详情表格 -->
    <el-card class="detail-table">
      <template #header>
        <span class="card-title">板卡资源详情</span>
      </template>
      <el-table :data="boardDetails" border>
        <el-table-column label="板卡" prop="boardName" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getBoardTagType(row.boardNo)">{{ row.boardName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="FPGA0" width="200" align="center">
          <template #default="{ row }">
            <div v-if="row.fpga0" class="fpga-cell used">
              <el-icon><CircleCheckFilled /></el-icon>
              <span>{{ row.fpga0.unitName }}</span>
            </div>
            <div v-else class="fpga-cell available">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>空闲</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="FPGA1" width="200" align="center">
          <template #default="{ row }">
            <div v-if="row.fpga1" class="fpga-cell used">
              <el-icon><CircleCheckFilled /></el-icon>
              <span>{{ row.fpga1.unitName }}</span>
            </div>
            <div v-else class="fpga-cell available">
              <el-icon><CircleCloseFilled /></el-icon>
              <span>空闲</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单元数量" prop="unitCount" width="100" align="center" />
        <el-table-column label="使用率" width="150" align="center">
          <template #default="{ row }">
            <el-progress :percentage="row.usageRate" :color="getProgressColor(row.usageRate)" />
          </template>
        </el-table-column>
        <el-table-column label="单元列表" min-width="300">
          <template #default="{ row }">
            <el-tag
              v-for="unit in row.units"
              :key="unit.unitId"
              :type="getUnitTypeTag(unit.unitType)"
              size="small"
              class="unit-tag"
            >
              {{ unit.unitName }}-{{ unit.unitType }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup name="BasebandResource">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { getResourceStats } from '@/api/system/basebandResource'
import { onBasebandEvent, BASEBAND_EVENTS } from '@/utils/basebandEvent'

const boardChartRef = ref(null)
const unitTypeChartRef = ref(null)
const detailChartRef = ref(null)

let boardChart = null
let unitTypeChart = null
let detailChart = null

const resourceStats = ref({
  totalBoards: 0,
  usedFpgas: 0,
  availableFpgas: 0,
  usageRate: 0
})

const boardDetails = ref([])
const isRefreshing = ref(false)

/** 获取资源统计数据 */
function getStats() {
  isRefreshing.value = true
  getResourceStats().then(response => {
    const data = response.data
    
    // 更新统计数据
    resourceStats.value = {
      totalBoards: data.totalBoards,
      usedFpgas: data.usedFpgas,
      availableFpgas: data.availableFpgas,
      usageRate: data.usageRate
    }
    
    // 更新板卡详情
    boardDetails.value = data.boardDetails
    
    // 更新图表
    initBoardChart(data.boardUsage)
    initUnitTypeChart(data.unitTypeStats)
    initDetailChart(data.boardDetails)
  }).finally(() => {
    isRefreshing.value = false
  })
}

/** 初始化板卡资源分布图 */
function initBoardChart(data) {
  if (!boardChart) {
    boardChart = echarts.init(boardChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: 'FPGA使用情况',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c}个'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: data.used, name: '已使用', itemStyle: { color: '#67C23A' } },
          { value: data.available, name: '空闲', itemStyle: { color: '#E6A23C' } }
        ]
      }
    ]
  }
  
  boardChart.setOption(option)
}

/** 初始化单元类型分布图 */
function initUnitTypeChart(data) {
  if (!unitTypeChart) {
    unitTypeChart = echarts.init(unitTypeChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.unitType)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '单元数量',
        type: 'bar',
        data: data.map(item => ({
          value: item.count,
          itemStyle: {
            color: getUnitTypeColor(item.unitType)
          }
        })),
        barWidth: '60%',
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }
  
  unitTypeChart.setOption(option)
}

/** 初始化板卡详细使用情况图 */
function initDetailChart(data) {
  if (!detailChart) {
    detailChart = echarts.init(detailChartRef.value)
  }
  
  const boardNames = data.map(item => item.boardName)
  const fpga0Data = data.map(item => item.fpga0 ? 1 : 0)
  const fpga1Data = data.map(item => item.fpga1 ? 1 : 0)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params) {
        const boardIndex = params[0].dataIndex
        const board = data[boardIndex]
        let html = `<div style="font-weight:bold;margin-bottom:5px;">${board.boardName}</div>`
        html += `<div>FPGA0: ${board.fpga0 ? board.fpga0.unitName : '空闲'}</div>`
        html += `<div>FPGA1: ${board.fpga1 ? board.fpga1.unitName : '空闲'}</div>`
        html += `<div>使用率: ${board.usageRate}%</div>`
        return html
      }
    },
    legend: {
      data: ['FPGA0', 'FPGA1']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: boardNames
    },
    yAxis: {
      type: 'value',
      max: 1,
      axisLabel: {
        formatter: function(value) {
          return value === 1 ? '已用' : '空闲'
        }
      }
    },
    series: [
      {
        name: 'FPGA0',
        type: 'bar',
        stack: 'total',
        data: fpga0Data,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: 'FPGA1',
        type: 'bar',
        stack: 'total',
        data: fpga1Data,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  
  detailChart.setOption(option)
}

/** 获取板卡标签类型 */
function getBoardTagType(boardNo) {
  const types = ['', 'success', 'info', 'warning', 'danger']
  return types[boardNo % types.length]
}

/** 获取单元类型标签 */
function getUnitTypeTag(unitType) {
  const typeMap = {
    '编码': 'primary',
    '调制': 'success',
    '解调': 'warning',
    '译码': 'danger'
  }
  return typeMap[unitType] || ''
}

/** 获取单元类型颜色 */
function getUnitTypeColor(unitType) {
  const colorMap = {
    '编码': '#409EFF',
    '调制': '#67C23A',
    '解调': '#E6A23C',
    '译码': '#F56C6C'
  }
  return colorMap[unitType] || '#909399'
}

/** 获取进度条颜色 */
function getProgressColor(percentage) {
  if (percentage < 50) return '#67C23A'
  if (percentage < 80) return '#E6A23C'
  return '#F56C6C'
}

/** 窗口大小改变时重绘图表 */
function handleResize() {
  boardChart?.resize()
  unitTypeChart?.resize()
  detailChart?.resize()
}

let refreshTimer = null
let unsubscribe = null

onMounted(() => {
  getStats()
  window.addEventListener('resize', handleResize)
  
  // 定时刷新数据（每30秒）
  refreshTimer = setInterval(() => {
    getStats()
  }, 30000)
  
  // 监听基带单元变更事件，实时刷新
  unsubscribe = onBasebandEvent(BASEBAND_EVENTS.UNIT_CHANGED, (event) => {
    console.log('[板卡资源] 收到单元变更通知:', event.data.action)
    // 延迟100ms刷新，确保后端数据已更新
    setTimeout(() => {
      getStats()
    }, 100)
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  
  // 清理定时器
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
  
  // 取消事件监听
  if (unsubscribe) {
    unsubscribe()
    unsubscribe = null
  }
  
  // 销毁图表
  boardChart?.dispose()
  unitTypeChart?.dispose()
  detailChart?.dispose()
})
</script>

<style scoped lang="scss">
.resource-monitor {
  .stats-row {
    margin-bottom: 20px;
  }
  
  .stat-card {
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    .stat-content {
      display: flex;
      align-items: center;
      
      .stat-icon {
        font-size: 48px;
        margin-right: 20px;
      }
      
      .stat-info {
        flex: 1;
        
        .stat-value {
          font-size: 32px;
          font-weight: bold;
          line-height: 1;
          margin-bottom: 8px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
    
    &.total {
      .stat-icon {
        color: #409EFF;
      }
      .stat-value {
        color: #409EFF;
      }
    }
    
    &.used {
      .stat-icon {
        color: #67C23A;
      }
      .stat-value {
        color: #67C23A;
      }
    }
    
    &.available {
      .stat-icon {
        color: #E6A23C;
      }
      .stat-value {
        color: #E6A23C;
      }
    }
    
    &.usage {
      .stat-icon {
        color: #F56C6C;
      }
      .stat-value {
        color: #F56C6C;
      }
    }
  }
  
  .charts-row {
    margin-bottom: 20px;
  }
  
  .card-title {
    font-size: 16px;
    font-weight: bold;
  }
  
  .chart-container {
    height: 350px;
  }
  
  .chart-container-large {
    height: 400px;
  }
  
  .detail-table {
    margin-top: 20px;
    
    .fpga-cell {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      
      &.used {
        color: #67C23A;
      }
      
      &.available {
        color: #909399;
      }
    }
    
    .unit-tag {
      margin: 2px 4px;
    }
  }
}
</style>
