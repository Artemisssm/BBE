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

    <el-table v-loading="loading" :data="paramDefList" @selection-change="handleSelectionChange" :row-class-name="tableRowClassName">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="60" align="center" />
      <el-table-column label="单元类型" align="center" prop="unitType" width="100" fixed>
        <template #default="scope">
          <el-tag :type="getUnitTypeTag(scope.row.unitType)" size="small">
            {{ getUnitTypeName(scope.row.unitType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="参数编码" align="center" prop="paramCode" width="180" :show-overflow-tooltip="true">
        <template #default="scope">
          <span class="param-code-text">{{ scope.row.paramCode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="参数名称" align="center" prop="paramName" width="180" :show-overflow-tooltip="true">
        <template #default="scope">
          <span class="param-name-text">{{ scope.row.paramName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="值类型" align="center" prop="valueType" width="110">
        <template #default="scope">
          <el-tag :type="getValueTypeTag(scope.row.valueType)" size="small">
            {{ getValueTypeName(scope.row.valueType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="取值范围" align="center" width="150">
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
      <el-table-column label="硬件顺序" align="center" prop="hardwareOrder" width="100">
        <template #default="scope">
          <el-tag size="small" type="info">{{ scope.row.hardwareOrder }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="位宽" align="center" prop="bitLength" width="80" />
      <el-table-column label="缩放" align="center" prop="scaleFactor" width="80" />
      <el-table-column label="默认值" align="center" prop="defaultValue" width="100" :show-overflow-tooltip="true" />
      <el-table-column label="备注" align="center" prop="remark" width="200" :show-overflow-tooltip="true" />
      <el-table-column label="操作" width="160" align="center" class-name="small-padding fixed-width" fixed="right">
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
                <el-option label="开关" value="SWITCH" />
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
            <el-form-item label="缩放倍数" prop="scaleFactor">
              <el-input-number v-model="form.scaleFactor" controls-position="right" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="位宽" prop="bitLength">
              <el-input-number v-model="form.bitLength" controls-position="right" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="硬件顺序" prop="hardwareOrder">
              <el-input-number v-model="form.hardwareOrder" controls-position="right" :min="0" style="width: 100%" />
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
              <el-input-number v-model="form.minValue" controls-position="right" style="width: 100%" :precision="form.valueType === 'FLOAT' ? 2 : 0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大值" prop="maxValue">
              <el-input-number v-model="form.maxValue" controls-position="right" style="width: 100%" :precision="form.valueType === 'FLOAT' ? 2 : 0" />
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
import { listBasebandParamDef, getBasebandParamDef, delBasebandParamDef, addBasebandParamDef, updateBasebandParamDef, exportBasebandParamDef } from "@/api/system/baseband/paramDef"

const { proxy } = getCurrentInstance()

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
    pageSize: 10,
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
    scaleFactor: [{ required: true, message: "缩放倍数不能为空", trigger: "blur" }],
    bitLength: [{ required: true, message: "位宽不能为空", trigger: "blur" }],
    hardwareOrder: [{ required: true, message: "硬件顺序不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

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
    'FLOAT': '浮点',
    'SWITCH': '开关'
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

/** 查询基带参数定义列表 */
function getList() {
  loading.value = true
  listBasebandParamDef(queryParams.value).then(response => {
    paramDefList.value = response.rows
    total.value = response.total
    loading.value = false
  })
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
    scaleFactor: 1,
    bitLength: 8,
    hardwareOrder: 0,
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
        updateBasebandParamDef(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addBasebandParamDef(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const paramIds = row.paramId || ids.value
  proxy.$modal.confirm('是否确认删除参数编号为"' + paramIds + '"的数据项？').then(function() {
    return delBasebandParamDef(paramIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
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
.param-code-text {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #606266;
  font-weight: 500;
}

.param-name-text {
  font-weight: 500;
  color: #303133;
}

.range-text {
  font-size: 12px;
  color: #909399;
}

:deep(.even-row) {
  background-color: #fafafa;
}

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}
</style>
