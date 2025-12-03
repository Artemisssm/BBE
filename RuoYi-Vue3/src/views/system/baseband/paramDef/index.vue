<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="参数编码" prop="paramCode">
        <el-input
          v-model="queryParams.paramCode"
          placeholder="请输入参数编码"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参数名称" prop="paramName">
        <el-input
          v-model="queryParams.paramName"
          placeholder="请输入参数名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单元类型" prop="unitType">
        <el-select v-model="queryParams.unitType" placeholder="单元类型" clearable style="width: 200px">
          <el-option label="编码" value="ENCODE" />
          <el-option label="调制" value="MODULATE" />
          <el-option label="解调" value="DEMODULATE" />
          <el-option label="译码" value="DECODE" />
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
          v-hasPermi="['system:basebandParam:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:basebandParam:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:basebandParam:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['system:basebandParam:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table 
      v-loading="loading" 
      :data="paramDefList" 
      @selection-change="handleSelectionChange" 
      :row-class-name="tableRowClassName" 
      class="param-def-table"
      row-key="paramId"
      ref="tableRef"
    >
      <el-table-column type="selection" width="50" align="center" fixed />
      <el-table-column label="序号" width="80" align="center" fixed>
        <template #default="scope">
          <div class="sort-handle" style="cursor: move;">
            <el-icon><DCaret /></el-icon>
            <span style="margin-left: 5px;">{{ scope.$index + 1 }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="单元类型" align="center" prop="unitType" width="100" fixed>
        <template #default="scope">
          <el-tag :type="getUnitTypeTag(scope.row.unitType)" size="small">
            {{ getUnitTypeName(scope.row.unitType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="参数编码" align="center" prop="paramCode" min-width="180" :show-overflow-tooltip="true">
        <template #default="scope">
          <span class="param-code-text">{{ scope.row.paramCode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="参数名称" align="center" prop="paramName" min-width="180" :show-overflow-tooltip="true">
        <template #default="scope">
          <span class="param-name-text">{{ scope.row.paramName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="值类型" align="center" prop="valueType" width="100">
        <template #default="scope">
          <el-tag :type="getValueTypeTag(scope.row.valueType)" size="small">
            {{ getValueTypeName(scope.row.valueType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="取值范围" align="center" min-width="140">
        <template #default="scope">
          <span v-if="scope.row.valueType === 'ENUM'" class="range-text">
            {{ getEnumCount(scope.row.enumOptions) }}个选项
          </span>
          <span v-else-if="scope.row.valueType === 'SWITCH'" class="range-text">
            开/关
          </span>
          <span v-else-if="scope.row.minValue !== null && scope.row.maxValue !== null" class="range-text">
            {{ scope.row.minValue }} ~ {{ scope.row.maxValue }}
          </span>
          <span v-else class="range-text">-</span>
        </template>
      </el-table-column>
      <el-table-column label="位宽类型" align="center" prop="bitWidthType" width="100">
        <template #default="scope">
          <el-tag size="small" type="warning" v-if="scope.row.bitWidthType">{{ scope.row.bitWidthType }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="量化单位" align="center" prop="quantizationUnit" width="100" />
      <el-table-column label="步进" align="center" prop="stepValue" width="80" />
      <el-table-column label="默认值" align="center" prop="defaultValue" min-width="100" :show-overflow-tooltip="true" />
      <el-table-column label="备注" align="center" prop="remark" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:basebandParam:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:basebandParam:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改基带参数定义对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="paramDefRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="单元类型" prop="unitType">
              <el-select v-model="form.unitType" placeholder="请选择单元类型" style="width: 100%">
                <el-option label="编码" value="ENCODE" />
                <el-option label="调制" value="MODULATE" />
                <el-option label="解调" value="DEMODULATE" />
                <el-option label="译码" value="DECODE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="值类型" prop="valueType">
              <el-select v-model="form.valueType" placeholder="请选择值类型" style="width: 100%" @change="handleValueTypeChange">
                <el-option label="枚举" value="ENUM" />
                <el-option label="无符号整数" value="UINT" />
                <el-option label="浮点" value="FLOAT" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="参数编码" prop="paramCode">
              <el-input v-model="form.paramCode" placeholder="请输入参数编码" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="参数名称" prop="paramName">
              <el-input v-model="form.paramName" placeholder="请输入参数名称" maxlength="64" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="量化单位" prop="quantizationUnit">
              <el-input-number v-model="form.quantizationUnit" controls-position="right" :min="1" style="width: 100%" placeholder="下发给硬件时除以此值" />
              <div style="color: #909399; font-size: 12px; margin-top: 5px;">下发给硬件时需要除以此值</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="位宽类型" prop="bitWidthType">
              <el-select v-model="form.bitWidthType" placeholder="请选择位宽类型" style="width: 100%" @change="handleBitWidthTypeChange">
                <el-option label="U8 (8位无符号)" value="U8" />
                <el-option label="U16 (16位无符号)" value="U16" />
                <el-option label="U32 (32位无符号)" value="U32" />
                <el-option label="I8 (8位有符号)" value="I8" />
                <el-option label="I16 (16位有符号)" value="I16" />
                <el-option label="I32 (32位有符号)" value="I32" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="步进值" prop="stepValue">
              <el-input-number v-model="form.stepValue" controls-position="right" :min="0" :step="0.01" :precision="2" style="width: 100%" placeholder="参数调整步进" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="默认值" prop="defaultValue">
              <el-input v-model="form.defaultValue" placeholder="请输入默认值" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.valueType === 'ENUM'">
          <el-col :span="24">
            <el-form-item label="枚举选项" prop="enumOptions">
              <el-input 
                v-model="form.enumOptions" 
                type="textarea" 
                :rows="4"
                placeholder='请输入JSON格式的枚举选项，例如：{"0":"选项1","1":"选项2"}' 
                popper-class="baseband-select-popper"
              />
              <div style="color: #909399; font-size: 12px; margin-top: 5px;">JSON格式：{"key":"value"}，例如：{"0":"关闭","1":"开启"}</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="form.valueType === 'UINT' || form.valueType === 'FLOAT'">
          <el-col :span="12">
            <el-form-item label="最小值" prop="minValue">
              <el-input-number 
                v-model="form.minValue" 
                controls-position="right" 
                style="width: 100%" 
                :precision="form.valueType === 'FLOAT' ? 2 : 0"
                :min="bitWidthRange.min"
                :max="bitWidthRange.max"
                @change="handleMinValueChange"
              />
              <div style="color: #909399; font-size: 12px; margin-top: 5px;">
                {{ bitWidthRange.label }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大值" prop="maxValue">
              <el-input-number 
                v-model="form.maxValue" 
                controls-position="right" 
                style="width: 100%" 
                :precision="form.valueType === 'FLOAT' ? 2 : 0"
                :min="bitWidthRange.min"
                :max="bitWidthRange.max"
                @change="handleMaxValueChange"
              />
              <div style="color: #909399; font-size: 12px; margin-top: 5px;">
                {{ bitWidthRange.label }}
              </div>
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

<script setup name="BasebandParamDef">
import { listBasebandParamDef, getBasebandParamDef, delBasebandParamDef, addBasebandParamDef, updateBasebandParamDef, batchUpdateSortOrder } from "@/api/system/baseband/paramDef"
import Sortable from 'sortablejs'
import { useBasebandStore } from '@/store/modules/baseband'

const { proxy } = getCurrentInstance()
const basebandStore = useBasebandStore()
const tableRef = ref(null)
let sortableInstance = null  // 保存Sortable实例

// 组件卸载时销毁Sortable实例
onBeforeUnmount(() => {
  if (sortableInstance) {
    sortableInstance.destroy()
    sortableInstance = null
  }
})

const paramDefList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 50,
    paramCode: undefined,
    paramName: undefined,
    unitType: undefined
  },
  rules: {
    unitType: [{ required: true, message: "单元类型不能为空", trigger: "change" }],
    paramCode: [
      { required: true, message: "参数编码不能为空", trigger: "blur" },
      { max: 64, message: "参数编码长度不能超过64个字符", trigger: "blur" }
    ],
    paramName: [
      { required: true, message: "参数名称不能为空", trigger: "blur" },
      { max: 64, message: "参数名称长度不能超过64个字符", trigger: "blur" }
    ],
    valueType: [{ required: true, message: "值类型不能为空", trigger: "change" }],
    quantizationUnit: [{ required: false, message: "量化单位不能为空", trigger: "blur" }],
    bitWidthType: [{ required: false, message: "位宽类型不能为空", trigger: "change" }],
    stepValue: [{ required: false, message: "步进值不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 位宽类型的取值范围 */
const bitWidthRanges = {
  'U8': { min: 0, max: 255, label: '范围: 0 ~ 255' },
  'U16': { min: 0, max: 65535, label: '范围: 0 ~ 65,535' },
  'U32': { min: 0, max: 4294967295, label: '范围: 0 ~ 4,294,967,295' },
  'I8': { min: -128, max: 127, label: '范围: -128 ~ 127' },
  'I16': { min: -32768, max: 32767, label: '范围: -32,768 ~ 32,767' },
  'I32': { min: -2147483648, max: 2147483647, label: '范围: -2,147,483,648 ~ 2,147,483,647' }
}

/** 计算当前位宽类型的范围 */
const bitWidthRange = computed(() => {
  const bitWidthType = form.value.bitWidthType
  if (bitWidthType && bitWidthRanges[bitWidthType]) {
    return bitWidthRanges[bitWidthType]
  }
  // 默认返回U16的范围
  return { min: 0, max: 65535, label: '请先选择位宽类型' }
})

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

/** 获取值类型名称 */
function getValueTypeName(valueType) {
  const typeMap = {
    'ENUM': '枚举',
    'UINT': '整数',
    'FLOAT': '浮点'
  }
  return typeMap[valueType] || valueType
}

/** 获取值类型标签样式 */
function getValueTypeTag(valueType) {
  const tagMap = {
    'ENUM': '',
    'UINT': 'success',
    'FLOAT': 'warning',
    'SWITCH': 'danger'
  }
  return tagMap[valueType] || 'info'
}

/** 获取枚举选项数量 */
function getEnumCount(enumOptions) {
  if (!enumOptions) return 0
  try {
    const options = JSON.parse(enumOptions)
    return Object.keys(options).length
  } catch (e) {
    return 0
  }
}

/** 表格行样式 */
function tableRowClassName({ row, rowIndex }) {
  if (rowIndex % 2 === 1) {
    return 'even-row'
  }
  return ''
}

/** 值类型变化 */
function handleValueTypeChange() {
  // 清空相关字段
  if (form.value.valueType !== 'ENUM') {
    form.value.enumOptions = undefined
  }
  if (form.value.valueType !== 'UINT' && form.value.valueType !== 'FLOAT') {
    form.value.minValue = undefined
    form.value.maxValue = undefined
  }
}

/** 位宽类型变化处理 */
function handleBitWidthTypeChange() {
  // 当位宽类型变化时，检查并调整最小值和最大值
  if (form.value.minValue !== null && form.value.minValue !== undefined) {
    handleMinValueChange(form.value.minValue)
  }
  if (form.value.maxValue !== null && form.value.maxValue !== undefined) {
    handleMaxValueChange(form.value.maxValue)
  }
}

/** 最小值变化处理 */
function handleMinValueChange(value) {
  if (value === null || value === undefined) return
  
  const range = bitWidthRange.value
  
  // 如果超出范围，自动调整到边界值
  if (value < range.min) {
    form.value.minValue = range.min
    proxy.$modal.msgWarning(`最小值不能小于 ${range.min}，已自动调整`)
  } else if (value > range.max) {
    form.value.minValue = range.max
    proxy.$modal.msgWarning(`最小值不能大于 ${range.max}，已自动调整`)
  }
  
  // 确保最小值不大于最大值
  if (form.value.maxValue !== null && form.value.maxValue !== undefined) {
    if (form.value.minValue > form.value.maxValue) {
      form.value.minValue = form.value.maxValue
      proxy.$modal.msgWarning('最小值不能大于最大值，已自动调整')
    }
  }
}

/** 最大值变化处理 */
function handleMaxValueChange(value) {
  if (value === null || value === undefined) return
  
  const range = bitWidthRange.value
  
  // 如果超出范围，自动调整到边界值
  if (value < range.min) {
    form.value.maxValue = range.min
    proxy.$modal.msgWarning(`最大值不能小于 ${range.min}，已自动调整`)
  } else if (value > range.max) {
    form.value.maxValue = range.max
    proxy.$modal.msgWarning(`最大值不能大于 ${range.max}，已自动调整`)
  }
  
  // 确保最大值不小于最小值
  if (form.value.minValue !== null && form.value.minValue !== undefined) {
    if (form.value.maxValue < form.value.minValue) {
      form.value.maxValue = form.value.minValue
      proxy.$modal.msgWarning('最大值不能小于最小值，已自动调整')
    }
  }
}

/** 查询基带参数定义列表 */
function getList() {
  loading.value = true
  listBasebandParamDef(queryParams.value).then(response => {
    paramDefList.value = response.rows
    total.value = response.total
    loading.value = false
    // 初始化拖拽排序
    nextTick(() => {
      initSortable()
    })
  })
}

/** 初始化拖拽排序 */
function initSortable() {
  if (!tableRef.value) return
  
  // 如果已存在实例，先销毁
  if (sortableInstance) {
    sortableInstance.destroy()
    sortableInstance = null
  }
  
  const tbody = tableRef.value.$el.querySelector('.el-table__body-wrapper tbody')
  if (!tbody) return
  
  sortableInstance = Sortable.create(tbody, {
    handle: '.sort-handle',
    animation: 150,
    onEnd: ({ newIndex, oldIndex }) => {
      if (newIndex === oldIndex) return
      
      // 交换数组中的元素
      const movedItem = paramDefList.value.splice(oldIndex, 1)[0]
      paramDefList.value.splice(newIndex, 0, movedItem)
      
      // 更新sort_order
      updateSortOrder()
    }
  })
}

// 防抖定时器
let updateSortTimer = null

/** 更新排序值并保存到数据库 */
async function updateSortOrder() {
  // 清除之前的定时器
  if (updateSortTimer) {
    clearTimeout(updateSortTimer)
  }
  
  // 防抖：延迟300ms执行
  updateSortTimer = setTimeout(async () => {
    try {
      loading.value = true
      
      // 按单元类型分组计算排序值
      const unitTypeGroups = {}
      
      // 先按单元类型分组
      paramDefList.value.forEach(item => {
        if (!unitTypeGroups[item.unitType]) {
          unitTypeGroups[item.unitType] = []
        }
        unitTypeGroups[item.unitType].push(item)
      })
      
      // 为每个单元类型内的参数分配排序值
      const updates = []
      const updatedUnitTypes = new Set()
      Object.keys(unitTypeGroups).forEach(unitType => {
        unitTypeGroups[unitType].forEach((item, index) => {
          const sortOrder = index + 1
          updates.push({
            paramId: item.paramId,
            sortOrder: sortOrder
          })
          // 同时更新本地数据
          item.sortOrder = sortOrder
          updatedUnitTypes.add(unitType)
        })
      })
      
      // 参数校验
      if (updates.length === 0) {
        proxy.$modal.msgWarning('没有需要更新的参数')
        return
      }
      
      // 批量更新排序
      await batchUpdateSortOrder(updates)
      
      proxy.$modal.msgSuccess('排序已更新')
      
      // 通知其他页面参数定义已更新
      updatedUnitTypes.forEach(unitType => {
        basebandStore.notifyParamDefUpdated(unitType)
      })
    } catch (error) {
      proxy.$modal.msgError('排序更新失败：' + (error.message || '未知错误'))
      console.error('更新排序失败:', error)
      // 失败时重新加载列表恢复原状态
      getList()
    } finally {
      loading.value = false
    }
  }, 300)
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    paramId: undefined,
    unitType: undefined,
    paramCode: undefined,
    paramName: undefined,
    valueType: undefined,
    enumOptions: undefined,
    minValue: undefined,
    maxValue: undefined,
    quantizationUnit: 1,
    bitWidthType: 'U16',
    stepValue: 1,
    defaultValue: undefined,
    remark: undefined
  }
  proxy.resetForm("paramDefRef")
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
  ids.value = selection.map(item => item.paramId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加基带参数定义"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const paramId = row.paramId || ids.value[0]
  getBasebandParamDef(paramId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改基带参数定义"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["paramDefRef"].validate(valid => {
    if (valid) {
      // 验证枚举选项JSON格式
      if (form.value.valueType === 'ENUM' && form.value.enumOptions) {
        try {
          JSON.parse(form.value.enumOptions)
        } catch (e) {
          proxy.$modal.msgError("枚举选项格式错误，请输入有效的JSON格式")
          return
        }
      }
      
      if (form.value.paramId != undefined) {
        updateBasebandParamDef(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
          // 通知其他页面参数定义已更新
          basebandStore.notifyParamDefUpdated(form.value.unitType)
        }).catch(error => {
          proxy.$modal.msgError("修改失败：" + (error.message || '未知错误'))
        })
      } else {
        addBasebandParamDef(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
          // 通知其他页面参数定义已更新
          basebandStore.notifyParamDefUpdated(form.value.unitType)
        }).catch(error => {
          proxy.$modal.msgError("新增失败：" + (error.message || '未知错误'))
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const paramIds = row?.paramId || ids.value
  const unitType = row?.unitType
  
  proxy.$modal.confirm('是否确认删除参数编号为"' + paramIds + '"的数据项？').then(function() {
    return delBasebandParamDef(paramIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
    // 通知其他页面参数定义已更新
    if (unitType) {
      basebandStore.notifyParamDefUpdated(unitType)
    }
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download("system/baseband/param/export", {
    ...queryParams.value
  }, `baseband_param_def_${new Date().getTime()}.xlsx`)
}

getList()
</script>

<style scoped>
/* 容器样式 */
.app-container {
  padding: 20px;
  background: #f0f2f5;
}

/* 表格容器 */
.param-def-table {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 参数编码样式 */
.param-code-text {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #606266;
  font-weight: 500;
}

/* 参数名称样式 */
.param-name-text {
  font-weight: 500;
  color: #303133;
}

/* 范围文本样式 */
.range-text {
  font-size: 12px;
  color: #909399;
}

/* 表格行样式 */
:deep(.even-row) {
  background-color: #fafafa;
}

/* 表格基础样式 */
:deep(.el-table) {
  font-size: 13px;
  width: 100% !important;
}

/* 表格头部样式 */
:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
  padding: 12px 0;
}

/* 表格单元格样式 */
:deep(.el-table td) {
  padding: 10px 0;
}

/* 表格行悬停效果 */
:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 表格滚动条 */
:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

/* 搜索表单优化 */
:deep(.el-form--inline .el-form-item) {
  margin-right: 16px;
  margin-bottom: 12px;
}

/* 按钮组优化 */
.mb8 {
  margin-bottom: 16px;
}

/* 分页器样式 */
:deep(.el-pagination) {
  margin-top: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  justify-content: center;
}
</style>
