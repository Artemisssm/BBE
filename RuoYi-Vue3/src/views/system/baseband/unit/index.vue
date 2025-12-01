<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="单元名称" prop="unitName">
        <el-select v-model="queryParams.unitName" placeholder="请选择单元名称" clearable style="width: 200px">
          <el-option label="前向中低速" value="前向中低速" />
          <el-option label="返向高速" value="返向高速" />
          <el-option label="小环" value="小环" />
          <el-option label="返向低速" value="返向低速" />
          <el-option label="ACM数传" value="ACM数传" />
          <el-option label="模拟源" value="模拟源" />
        </el-select>
      </el-form-item>
      <el-form-item label="单元类型" prop="unitType">
        <el-select v-model="queryParams.unitType" placeholder="单元类型" clearable style="width: 200px">
          <el-option label="编码" value="ENCODE" />
          <el-option label="调制" value="MODULATE" />
          <el-option label="解调" value="DEMODULATE" />
          <el-option label="译码" value="DECODE" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="单元状态" clearable style="width: 200px">
          <el-option
            v-for="dict in sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['system:basebandUnit:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:basebandUnit:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:basebandUnit:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:basebandUnit:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unitList" @selection-change="handleSelectionChange" class="baseband-table" :row-class-name="getRowClassName">
      <el-table-column type="selection" width="50" align="center" fixed />
      <el-table-column label="序号" type="index" width="70" align="center" :index="indexMethod" fixed />
      <el-table-column label="板卡资源" align="center" prop="channelNo" min-width="160" fixed sortable :sort-method="sortByResource">
        <template #default="scope">
          <div class="resource-info-enhanced">
            <div class="resource-badge" :class="getResourceBadgeClass(scope.row.channelNo, scope.row.unitName)">
              <el-icon class="resource-icon"><Cpu /></el-icon>
              <div class="resource-details">
                <span class="resource-board">{{ getBoardInfo(scope.row.channelNo) }}</span>
                <span class="resource-fpga">{{ getFpgaInfo(scope.row.channelNo, scope.row.unitName) }}</span>
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="单元名称" align="center" prop="unitName" min-width="140">
        <template #default="scope">
          <el-tag :type="getUnitNameTag(scope.row.unitName)" size="default" effect="plain">
            {{ scope.row.unitName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="单元类型" align="center" prop="unitType" min-width="100">
        <template #default="scope">
          <el-tag :type="getUnitTypeTag(scope.row.unitType)" size="small">
            {{ getUnitTypeName(scope.row.unitType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模式类型" align="center" prop="modeType" min-width="110">
        <template #default="scope">
          <el-tag v-if="scope.row.modeType" :type="getModeTypeTag(scope.row.modeType)" size="small" effect="plain">
            {{ scope.row.modeType }}
          </el-tag>
          <span v-else style="color: #909399;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="版本" align="center" prop="version" min-width="120" :show-overflow-tooltip="true" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" align="center" class-name="small-padding fixed-width" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:basebandUnit:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:basebandUnit:remove']">删除</el-button>
          <el-button link type="success" icon="Setting" @click="handleConfig(scope.row)" v-hasPermi="['system:basebandValue:list']">配置</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      :page-sizes="[20, 50, 100, 200]"
      @pagination="getList"
    />

    <!-- 添加或修改基带单元对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body @close="handleDialogClose">
      <el-form ref="unitRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="单元名称" prop="unitName">
              <el-select v-model="form.unitName" placeholder="请选择单元名称" style="width: 100%" @change="handleUnitNameChange">
                <el-option label="前向中低速" value="前向中低速" />
                <el-option label="返向高速" value="返向高速" />
                <el-option label="小环" value="小环" />
                <el-option label="返向低速" value="返向低速" />
                <el-option label="ACM数传" value="ACM数传" />
                <el-option label="模拟源" value="模拟源" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="单元类型" prop="unitTypes">
              <el-checkbox-group v-model="form.unitTypes">
                <el-checkbox label="ENCODE">编码</el-checkbox>
                <el-checkbox label="MODULATE">调制</el-checkbox>
                <el-checkbox label="DEMODULATE">解调</el-checkbox>
                <el-checkbox label="DECODE">译码</el-checkbox>
              </el-checkbox-group>
              <div style="color: #909399; font-size: 12px; margin-top: 5px;">
                可同时选择多个类型，将批量创建单元
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模式类型" prop="modeType">
              <el-select v-model="form.modeType" placeholder="请选择模式类型" style="width: 100%">
                <el-option label="KSA" value="KSA" />
                <el-option label="KMA" value="KMA" />
                <el-option label="SSA" value="SSA" />
                <el-option label="基带数传" value="基带数传" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="板卡资源" prop="channelNo">
              <el-select v-model="form.channelNo" placeholder="请选择板卡资源" style="width: 100%" :disabled="!form.unitName">
                <el-option
                  v-for="resource in availableResources"
                  :key="resource.value"
                  :label="resource.label"
                  :value="resource.value"
                  :disabled="resource.disabled"
                >
                  <span>{{ resource.label }}</span>
                  <span v-if="resource.disabled" style="color: #f56c6c; margin-left: 10px;">
                    ({{ resource.reason }})
                  </span>
                </el-option>
              </el-select>
              <div style="color: #909399; font-size: 12px; margin-top: 5px;">
                <span v-if="form.unitName === '返向高速'" style="color: #e6a23c;">
                  ⚠️ 返向高速需要占用整块板（FPGA0和FPGA1）
                </span>
                <span v-else-if="form.unitName">
                  ℹ️ 该模式只占用一个FPGA
                </span>
                <span v-else>
                  请先选择单元名称
                </span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="版本" prop="version">
              <el-input v-model="form.version" placeholder="请输入硬件/配置版本" maxlength="32" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="BasebandUnit">
import { listBasebandUnit, getBasebandUnit, delBasebandUnit, addBasebandUnit, updateBasebandUnit, exportBasebandUnit } from "@/api/system/baseband/unit"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")
const router = useRouter()

const unitList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 板卡资源列表（假设有4块板，每块板2个FPGA）
const totalBoards = 4
const fpgasPerBoard = 2

// 可用资源列表
const availableResources = ref([])

// 已占用资源
const occupiedResources = ref(new Set())

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 50,
    unitName: undefined,
    unitType: undefined,
    status: undefined
  },
  rules: {
    unitName: [
      { required: true, message: "单元名称不能为空", trigger: "change" }
    ],
    unitTypes: [
      { 
        type: 'array',
        required: true, 
        message: "请至少选择一个单元类型", 
        trigger: "change" 
      }
    ],
    modeType: [
      { required: true, message: "请选择模式类型", trigger: "change" }
    ],
    channelNo: [{ required: true, message: "请选择板卡资源", trigger: "change" }],
    version: [{ max: 32, message: "版本信息不能超过32个字符", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 单元名称预设选项 */
const unitNameOptions = [
  { label: '前向中低速', value: '前向中低速' },
  { label: '返向高速', value: '返向高速' },
  { label: '小环', value: '小环' },
  { label: '返向低速', value: '返向低速' },
  { label: 'ACM数传', value: 'ACM数传' },
  { label: '模拟源', value: '模拟源' }
]

/** 获取单元名称标签样式 */
function getUnitNameTag(unitName) {
  const tagMap = {
    '前向中低速': 'primary',
    '返向高速': 'success',
    '小环': 'warning',
    '返向低速': 'danger',
    'ACM数传': 'info',
    '模拟源': ''
  }
  return tagMap[unitName] || 'info'
}

/** 获取单元类型名称 */
function getUnitTypeName(unitType) {
  const typeMap = {
    'ENCODE': '编码',
    'MODULATE': '调制',
    'DEMODULATE': '解调',
    'DECODE': '译码'
  }
  return typeMap[unitType] || unitType
}

/** 获取单元类型标签样式 */
function getUnitTypeTag(unitType) {
  const tagMap = {
    'ENCODE': 'primary',
    'MODULATE': 'success',
    'DEMODULATE': 'warning',
    'DECODE': 'danger'
  }
  return tagMap[unitType] || 'info'
}

/** 获取模式类型标签样式 */
function getModeTypeTag(modeType) {
  const tagMap = {
    'KSA': 'primary',
    'KMA': 'success',
    'SSA': 'warning',
    '基带数传': 'info'
  }
  return tagMap[modeType] || 'info'
}

/** 格式化板卡资源显示 */
function formatResource(channelNo, unitName) {
  const boardNo = Math.floor((channelNo - 1) / fpgasPerBoard) + 1
  const fpgaNo = (channelNo - 1) % fpgasPerBoard
  
  if (unitName === '返向高速') {
    return `板${boardNo}（整板）`
  } else {
    return `板${boardNo}-FPGA${fpgaNo}`
  }
}

/** 获取板卡编号 */
function getBoardInfo(channelNo) {
  const boardNo = Math.floor((channelNo - 1) / fpgasPerBoard) + 1
  return `板${boardNo}`
}

/** 获取FPGA信息 */
function getFpgaInfo(channelNo, unitName) {
  if (unitName === '返向高速') {
    return '整板'
  }
  const fpgaNo = (channelNo - 1) % fpgasPerBoard
  return `FPGA${fpgaNo}`
}

/** 获取资源徽章样式类 */
function getResourceBadgeClass(channelNo, unitName) {
  const boardNo = Math.floor((channelNo - 1) / fpgasPerBoard) + 1
  const fpgaNo = (channelNo - 1) % fpgasPerBoard
  
  if (unitName === '返向高速') {
    return `board-${boardNo} full-board`
  }
  return `board-${boardNo} fpga-${fpgaNo}`
}

/** 获取表格行样式类 */
function getRowClassName({ row }) {
  const boardNo = Math.floor((row.channelNo - 1) / fpgasPerBoard) + 1
  const fpgaNo = (row.channelNo - 1) % fpgasPerBoard
  return `board-row-${boardNo} fpga-row-${fpgaNo}`
}

/** 板卡资源排序方法 */
function sortByResource(a, b) {
  return a.channelNo - b.channelNo
}

/** 表格序号方法（升序） */
function indexMethod(index) {
  return (queryParams.value.pageNum - 1) * queryParams.value.pageSize + index + 1
}

/** 查询基带单元列表 */
function getList() {
  loading.value = true
  listBasebandUnit(queryParams.value).then(response => {
    unitList.value = response.rows
    total.value = response.total
    loading.value = false
    // 更新已占用资源
    updateOccupiedResources()
  })
}

/** 更新已占用资源 */
function updateOccupiedResources() {
  occupiedResources.value.clear()
  
  unitList.value.forEach(unit => {
    const channelNo = unit.channelNo
    const boardNo = Math.floor((channelNo - 1) / fpgasPerBoard) + 1
    const fpgaNo = (channelNo - 1) % fpgasPerBoard
    
    // 如果是返向高速，占用整块板
    if (unit.unitName === '返向高速') {
      occupiedResources.value.add(`board-${boardNo}`)
      occupiedResources.value.add(`${boardNo}-0`)
      occupiedResources.value.add(`${boardNo}-1`)
    } else {
      // 其他模式只占用一个FPGA
      occupiedResources.value.add(`${boardNo}-${fpgaNo}`)
      
      // 检查该板的另一个FPGA是否也被占用
      const otherFpga = fpgaNo === 0 ? 1 : 0
      if (occupiedResources.value.has(`${boardNo}-${otherFpga}`)) {
        // 如果两个FPGA都被占用，标记整块板被占用
        occupiedResources.value.add(`board-${boardNo}`)
      }
    }
  })
  
  // 更新可用资源列表
  updateAvailableResources()
}

/** 更新可用资源列表 */
function updateAvailableResources() {
  const resources = []
  const isHighSpeed = form.value.unitName === '返向高速'
  
  for (let board = 1; board <= totalBoards; board++) {
    if (isHighSpeed) {
      // 返向高速需要整块板
      const boardOccupied = occupiedResources.value.has(`board-${board}`) ||
                           occupiedResources.value.has(`${board}-0`) ||
                           occupiedResources.value.has(`${board}-1`)
      
      resources.push({
        label: `板${board}（整板）`,
        value: (board - 1) * fpgasPerBoard + 1, // 使用板的第一个通道号
        disabled: boardOccupied,
        reason: boardOccupied ? '板卡已被占用' : ''
      })
    } else {
      // 其他模式只需要一个FPGA
      for (let fpga = 0; fpga < fpgasPerBoard; fpga++) {
        const channelNo = (board - 1) * fpgasPerBoard + fpga + 1
        const resourceKey = `${board}-${fpga}`
        const boardKey = `board-${board}`
        
        const fpgaOccupied = occupiedResources.value.has(resourceKey)
        const boardOccupied = occupiedResources.value.has(boardKey)
        
        resources.push({
          label: `板${board}-FPGA${fpga}`,
          value: channelNo,
          disabled: fpgaOccupied || boardOccupied,
          reason: boardOccupied ? '整板被占用' : (fpgaOccupied ? 'FPGA已被占用' : '')
        })
      }
    }
  }
  
  availableResources.value = resources
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    unitId: undefined,
    unitName: undefined,
    unitTypes: [],
    modeType: undefined,
    channelNo: undefined,
    status: "0",
    version: undefined,
    remark: undefined
  }
  proxy.resetForm("unitRef")
  availableResources.value = []
}

/** 单元名称变化处理 */
function handleUnitNameChange() {
  // 清空通道号选择
  form.value.channelNo = undefined
  // 更新可用资源列表
  updateAvailableResources()
}

/** 对话框关闭处理 */
function handleDialogClose() {
  reset()
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.unitId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  updateOccupiedResources()
  open.value = true
  title.value = "批量添加基带单元"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  updateOccupiedResources()
  const unitId = row.unitId || ids.value[0]
  getBasebandUnit(unitId).then(response => {
    form.value = response.data
    // 修改时，unitTypes只包含当前的unitType
    form.value.unitTypes = [response.data.unitType]
    // 确保modeType被正确加载
    form.value.modeType = response.data.modeType
    // 更新可用资源（排除当前单元占用的资源）
    updateAvailableResources()
    open.value = true
    title.value = "修改基带单元"
  })
}

/** 参数配置按钮操作 */
function handleConfig(row) {
  router.push("/system/baseband/value/" + row.unitId)
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["unitRef"].validate(valid => {
    if (valid) {
      if (form.value.unitId != undefined) {
        // 修改模式：单个单元
        const updateData = {
          ...form.value,
          unitType: form.value.unitTypes[0], // 修改时只能有一个类型
          modeType: form.value.modeType
        }
        updateBasebandUnit(updateData).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        // 新增模式：批量创建
        // 定义固定的单元类型顺序：编码 -> 调制 -> 解调 -> 译码
        const unitTypeOrder = ['ENCODE', 'MODULATE', 'DEMODULATE', 'DECODE']
        
        // 按照固定顺序过滤出用户选择的类型
        const orderedUnitTypes = unitTypeOrder.filter(type => form.value.unitTypes.includes(type))
        
        // 按顺序创建单元（使用串行方式确保顺序）
        const createUnitsSequentially = async () => {
          for (const unitType of orderedUnitTypes) {
            const unitData = {
              unitName: form.value.unitName,
              unitType: unitType,
              channelNo: form.value.channelNo,
              modeType: form.value.modeType,
              status: form.value.status,
              version: form.value.version,
              remark: form.value.remark
            }
            await addBasebandUnit(unitData)
          }
        }
        
        // 执行批量创建
        createUnitsSequentially().then(() => {
          proxy.$modal.msgSuccess(`成功创建 ${orderedUnitTypes.length} 个基带单元`)
          open.value = false
          getList()
        }).catch(error => {
          proxy.$modal.msgError("批量创建失败：" + error.message)
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const unitIds = row.unitId || ids.value
  proxy.$modal.confirm('是否确认删除单元编号为"' + unitIds + '"的数据项？').then(function() {
    return delBasebandUnit(unitIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/baseband/unit/export", {
    ...queryParams.value
  }, `baseband_unit_${new Date().getTime()}.xlsx`)
}

getList()
</script>

<style scoped>
/* 容器优化 */
.app-container {
  padding: 20px;
  background: #f0f2f5;
}

/* 搜索表单优化 */
:deep(.el-form--inline .el-form-item) {
  margin-right: 16px;
  margin-bottom: 12px;
}

/* 表格容器优化 */
.baseband-table {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 表格样式优化 */
:deep(.el-table) {
  font-size: 14px;
  width: 100%;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #303133;
  font-weight: 600;
  font-size: 14px;
  padding: 12px 0;
}

:deep(.el-table td) {
  padding: 8px 0;
}

:deep(.el-table__row) {
  transition: background-color 0.2s;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 单元名称标签样式 */
:deep(.el-tag) {
  font-weight: 500;
  padding: 6px 14px;
  border-radius: 4px;
  font-size: 13px;
}

/* 板卡资源增强显示样式 */
.resource-info-enhanced {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
}

.resource-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(102, 126, 234, 0.25);
  transition: all 0.3s ease;
  min-width: 120px;
}

.resource-badge:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(102, 126, 234, 0.35);
}

.resource-badge .resource-icon {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  flex-shrink: 0;
}

.resource-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1px;
  line-height: 1.2;
}

.resource-board {
  font-size: 13px;
  font-weight: 700;
  font-family: 'Courier New', monospace;
  letter-spacing: 0.3px;
}

.resource-fpga {
  font-size: 10px;
  font-weight: 500;
  opacity: 0.85;
  font-family: 'Courier New', monospace;
}

/* 不同板卡的颜色区分 */
.resource-badge.board-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.resource-badge.board-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.resource-badge.board-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.resource-badge.board-4 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

/* 整板占用特殊样式 */
.resource-badge.full-board {
  border: 2px solid rgba(255, 255, 255, 0.5);
  font-weight: 700;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.resource-badge.full-board .resource-fpga {
  font-size: 12px;
  font-weight: 600;
}

/* 表格行背景色区分（同一块板） */
:deep(.el-table__row.board-row-1) {
  background-color: rgba(102, 126, 234, 0.03);
}

:deep(.el-table__row.board-row-2) {
  background-color: rgba(240, 147, 251, 0.03);
}

:deep(.el-table__row.board-row-3) {
  background-color: rgba(79, 172, 254, 0.03);
}

:deep(.el-table__row.board-row-4) {
  background-color: rgba(67, 233, 123, 0.03);
}

/* 鼠标悬停时加深背景色 */
:deep(.el-table__row.board-row-1:hover) {
  background-color: rgba(102, 126, 234, 0.08) !important;
}

:deep(.el-table__row.board-row-2:hover) {
  background-color: rgba(240, 147, 251, 0.08) !important;
}

:deep(.el-table__row.board-row-3:hover) {
  background-color: rgba(79, 172, 254, 0.08) !important;
}

:deep(.el-table__row.board-row-4:hover) {
  background-color: rgba(67, 233, 123, 0.08) !important;
}

/* 复选框组样式 */
:deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

:deep(.el-checkbox) {
  margin-right: 0;
  padding: 8px 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  transition: all 0.3s;
}

:deep(.el-checkbox:hover) {
  border-color: #409eff;
  background-color: #ecf5ff;
}

:deep(.el-checkbox.is-checked) {
  border-color: #409eff;
  background-color: #ecf5ff;
}

/* 下拉选项禁用样式 */
:deep(.el-select-dropdown__item.is-disabled) {
  color: #c0c4cc;
  cursor: not-allowed;
  background-color: #f5f7fa;
}

/* 对话框样式优化 */
:deep(.el-dialog__header) {
  background-color: #f5f7fa;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

/* 表单项样式 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

/* 按钮组优化 */
.mb8 {
  margin-bottom: 16px;
}

:deep(.el-button) {
  font-weight: 500;
}

/* 操作按钮样式 */
:deep(.el-button--text) {
  padding: 4px 8px;
}

/* 分页器样式 */
:deep(.el-pagination) {
  margin-top: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
</style>
