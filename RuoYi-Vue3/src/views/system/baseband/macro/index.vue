<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="宏名称" prop="macroName">
        <el-input
          v-model="queryParams.macroName"
          placeholder="请输入宏名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单元类型" prop="unitType">
        <el-select v-model="queryParams.unitType" placeholder="请选择单元类型" clearable>
          <el-option label="编码" value="ENCODE" />
          <el-option label="调制" value="MODULATE" />
          <el-option label="解调" value="DEMODULATE" />
          <el-option label="译码" value="DECODE" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
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
          v-hasPermi="['system:basebandMacro:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:basebandMacro:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:basebandMacro:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 视图切换 -->
    <div class="view-switch">
      <el-radio-group v-model="viewMode" size="small">
        <el-radio-button label="task">按任务分组</el-radio-button>
        <el-radio-button label="list">列表视图</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 任务分组视图 -->
    <div v-if="viewMode === 'task'" class="task-group-view">
      <div v-for="(task, taskKey) in groupedMacros" :key="taskKey" class="task-card">
        <div class="task-header" :class="getTaskHeaderClass(task.index)">
          <div class="task-info">
            <el-icon class="task-icon"><MagicStick /></el-icon>
            <span class="task-title">{{ task.baseName }}</span>
            <el-tag v-if="task.modeType" :type="getModeTypeTag(task.modeType)" size="small" style="margin-left: 10px;">
              {{ task.modeType }}
            </el-tag>
            <el-tag v-if="task.hasDefault" type="success" size="small" style="margin-left: 5px;">
              含默认宏
            </el-tag>
          </div>
          <div class="task-actions">
            <el-button type="danger" size="small" plain icon="Delete" @click="handleDeleteTask(task)">
              删除任务
            </el-button>
          </div>
        </div>
        <div class="task-units">
          <div v-for="macro in task.macros" :key="macro.macroId" class="macro-item" :class="getMacroItemClass(macro.unitType)">
            <div class="macro-type">
              <el-tag :type="getUnitTypeTag(macro.unitType)" size="default">
                {{ convertUnitType(macro.unitType) }}
              </el-tag>
            </div>
            <div class="macro-info-detail">
              <span class="macro-code">{{ macro.macroCode }}</span>
              <el-tag v-if="macro.isDefault === '1'" type="success" size="small">默认</el-tag>
              <el-tag v-if="macro.status === '0'" type="success" size="small" effect="plain">正常</el-tag>
              <el-tag v-else type="danger" size="small" effect="plain">停用</el-tag>
            </div>
            <div class="macro-actions">
              <el-button link type="primary" size="small" @click="handleConfig(macro)">配置</el-button>
              <el-button link type="primary" size="small" @click="handleUpdate(macro)">修改</el-button>
              <el-button link type="danger" size="small" @click="handleDelete(macro)">删除</el-button>
            </div>
          </div>
          <!-- 缺失的单元类型提示 -->
          <div v-for="missingType in task.missingTypes" :key="missingType" class="macro-item missing">
            <div class="macro-type">
              <el-tag type="info" size="default" effect="plain">
                {{ convertUnitType(missingType) }}
              </el-tag>
            </div>
            <div class="macro-info-detail">
              <span style="color: #909399; font-size: 12px;">未创建</span>
            </div>
            <div class="macro-actions">
              <el-button link type="primary" size="small" @click="handleAddMissing(task, missingType)">
                + 添加
              </el-button>
            </div>
          </div>
        </div>
        <div class="task-footer" v-if="task.description">
          <el-icon><InfoFilled /></el-icon>
          <span>{{ task.description }}</span>
        </div>
      </div>
      <el-empty v-if="Object.keys(groupedMacros).length === 0" description="暂无宏配置数据" />
    </div>

    <!-- 列表视图 -->
    <el-table v-else v-loading="loading" :data="macroList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="宏名称" align="center" prop="macroName" min-width="150" />
      <el-table-column label="宏编码" align="center" prop="macroCode" min-width="150" />
      <el-table-column label="单元类型" align="center" prop="unitType" width="100">
        <template #default="scope">
          <el-tag :type="getUnitTypeTag(scope.row.unitType)">
            {{ convertUnitType(scope.row.unitType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="模式类型" align="center" prop="modeType" width="120">
        <template #default="scope">
          <el-tag :type="getModeTypeTag(scope.row.modeType)">
            {{ scope.row.modeType || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center" prop="description" min-width="200" show-overflow-tooltip />
      <el-table-column label="默认宏" align="center" prop="isDefault" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.isDefault === '1'" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Setting" @click="handleConfig(scope.row)">配置参数</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 添加或修改宏对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="macroRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="宏名称" prop="macroName">
          <el-input v-model="form.macroName" placeholder="请输入宏名称" />
        </el-form-item>
        <el-form-item label="宏编码" prop="macroCode">
          <el-input v-model="form.macroCode" placeholder="请输入宏编码（唯一标识）" />
        </el-form-item>
        <el-form-item label="单元类型" prop="unitTypes">
          <el-checkbox-group v-model="form.unitTypes" v-if="!form.macroId">
            <el-checkbox label="ENCODE">编码</el-checkbox>
            <el-checkbox label="MODULATE">调制</el-checkbox>
            <el-checkbox label="DEMODULATE">解调</el-checkbox>
            <el-checkbox label="DECODE">译码</el-checkbox>
          </el-checkbox-group>
          <el-select v-model="form.unitType" placeholder="请选择单元类型" v-else>
            <el-option label="编码" value="ENCODE" />
            <el-option label="调制" value="MODULATE" />
            <el-option label="解调" value="DEMODULATE" />
            <el-option label="译码" value="DECODE" />
          </el-select>
          <div class="el-form-item-msg" v-if="!form.macroId">新增时可多选，将为每个单元类型创建一个宏</div>
        </el-form-item>
        <el-form-item label="模式类型" prop="modeType">
          <el-select v-model="form.modeType" placeholder="请选择模式类型" clearable>
            <el-option label="KSA" value="KSA" />
            <el-option label="KMA" value="KMA" />
            <el-option label="SSA" value="SSA" />
            <el-option label="基带数传" value="基带数传" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入宏描述" />
        </el-form-item>
        <el-form-item label="是否默认" prop="isDefault">
          <el-radio-group v-model="form.isDefault">
            <el-radio label="1">是</el-radio>
            <el-radio label="0">否</el-radio>
          </el-radio-group>
          <div class="el-form-item-msg">设为默认后，创建该类型单元时会自动选中此宏</div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 配置参数对话框 -->
    <el-dialog title="配置宏参数" v-model="configOpen" width="80%" append-to-body>
      <div class="macro-info">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="宏名称">{{ currentMacro.macroName }}</el-descriptions-item>
          <el-descriptions-item label="单元类型">{{ convertUnitType(currentMacro.unitType) }}</el-descriptions-item>
          <el-descriptions-item label="模式类型">{{ currentMacro.modeType || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-divider content-position="left">参数配置</el-divider>
      
      <div class="param-config">
        <el-alert
          title="提示：请为宏配置参数默认值，创建单元时会自动应用这些参数"
          type="info"
          :closable="false"
          style="margin-bottom: 15px"
        />
        
        <el-button type="primary" icon="Plus" @click="handleAddParam" style="margin-bottom: 15px">
          添加参数
        </el-button>
        
        <el-table :data="macroParams" border>
          <el-table-column label="参数名称" prop="paramName" width="200" />
          <el-table-column label="参数类型" prop="valueType" width="100" align="center" />
          <el-table-column label="取值范围" width="180" align="left">
            <template #default="scope">
              <div v-if="scope.row.valueType === 'ENUM'" class="enum-options-display">
                <div 
                  v-for="(option, index) in parseEnumOptions(scope.row.enumOptions)" 
                  :key="index"
                  class="enum-option-item"
                >
                  <span class="enum-value">{{ option.value }}</span>
                  <span class="enum-separator">:</span>
                  <span class="enum-label">{{ option.label }}</span>
                </div>
              </div>
              <span v-else-if="scope.row.valueType === 'UINT' || scope.row.valueType === 'FLOAT'">
                {{ scope.row.minValue }} ~ {{ scope.row.maxValue }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="参数值" width="220">
            <template #default="scope">
              <!-- 枚举类型使用下拉选择 -->
              <el-select 
                v-if="scope.row.valueType === 'ENUM'" 
                v-model="scope.row.paramValue" 
                placeholder="请选择"
                style="width: 100%;"
              >
                <el-option
                  v-for="option in parseEnumOptions(scope.row.enumOptions)"
                  :key="option.value"
                  :label="`${option.value}: ${option.label}`"
                  :value="option.value"
                >
                  <span style="float: left">{{ option.value }}</span>
                  <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">
                    {{ option.label }}
                  </span>
                </el-option>
              </el-select>
              <!-- 数值类型使用数字输入框 -->
              <el-input-number
                v-else-if="scope.row.valueType === 'UINT' || scope.row.valueType === 'FLOAT'"
                v-model="scope.row.paramValue"
                :min="parseFloat(scope.row.minValue)"
                :max="parseFloat(scope.row.maxValue)"
                :precision="scope.row.valueType === 'FLOAT' ? 2 : 0"
                :step="scope.row.valueType === 'FLOAT' ? 0.01 : 1"
                controls-position="right"
                style="width: 100%;"
              />
              <!-- 其他类型使用普通输入框 -->
              <el-input 
                v-else 
                v-model="scope.row.paramValue" 
                placeholder="请输入参数值" 
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default="scope">
              <el-button link type="danger" icon="Delete" @click="handleDeleteParam(scope.$index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitMacroParams">保 存</el-button>
          <el-button @click="configOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加参数对话框 -->
    <el-dialog title="选择参数" v-model="paramSelectOpen" width="800px" append-to-body>
      <el-table
        :data="availableParams"
        @selection-change="handleParamSelectionChange"
        max-height="400"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="参数名称" prop="paramName" width="200" />
        <el-table-column label="参数编码" prop="paramCode" width="150" />
        <el-table-column label="参数类型" prop="valueType" width="100" align="center" />
        <el-table-column label="默认值" prop="defaultValue" width="100" align="center" />
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="confirmAddParams">确 定</el-button>
          <el-button @click="paramSelectOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="BasebandMacro">
import { ref, computed, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMacro, getMacro, addMacro, updateMacro, delMacro, getMacroParams, saveMacroParams } from '@/api/system/baseband/macro'
import { listBasebandParamDef } from '@/api/system/baseband/paramDef'

const { proxy } = getCurrentInstance()

// 数据
const macroList = ref([])
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const title = ref('')
const open = ref(false)
const configOpen = ref(false)
const paramSelectOpen = ref(false)
const single = ref(true)
const multiple = ref(true)
const ids = ref([])
const viewMode = ref('task')  // 默认按任务分组视图

// 按任务分组的宏数据
const groupedMacros = computed(() => {
  const groups = {}
  const allUnitTypes = ['ENCODE', 'MODULATE', 'DEMODULATE', 'DECODE']
  let taskIndex = 0
  
  macroList.value.forEach(macro => {
    // 提取基础宏名称（去掉括号中的单元类型）
    let baseName = macro.macroName
    const match = baseName.match(/^(.+?)\s*[\(（]?(编码|调制|解调|译码)[\)）]?$/)
    if (match) {
      baseName = match[1]
    }
    
    // 使用 基础名称+模式类型 作为任务的唯一标识
    const taskKey = `${baseName}-${macro.modeType || 'default'}`
    
    if (!groups[taskKey]) {
      taskIndex++
      groups[taskKey] = {
        taskKey,
        baseName,
        modeType: macro.modeType,
        description: macro.description,
        macros: [],
        existingTypes: new Set(),
        hasDefault: false,
        index: taskIndex
      }
    }
    
    groups[taskKey].macros.push(macro)
    groups[taskKey].existingTypes.add(macro.unitType)
    if (macro.isDefault === '1') {
      groups[taskKey].hasDefault = true
    }
  })
  
  // 计算每个任务缺失的单元类型，并排序
  Object.values(groups).forEach(task => {
    task.missingTypes = allUnitTypes.filter(type => !task.existingTypes.has(type))
    // 按固定顺序排序宏
    task.macros.sort((a, b) => {
      return allUnitTypes.indexOf(a.unitType) - allUnitTypes.indexOf(b.unitType)
    })
  })
  
  return groups
})

const currentMacro = ref({})
const macroParams = ref([])
const availableParams = ref([])
const selectedParams = ref([])

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  macroName: null,
  unitType: null,
  status: null
})

const form = ref({})

const rules = {
  macroName: [{ required: true, message: '宏名称不能为空', trigger: 'blur' }],
  macroCode: [{ required: true, message: '宏编码不能为空', trigger: 'blur' }],
  unitType: [{ required: true, message: '单元类型不能为空', trigger: 'change' }],
  unitTypes: [{ required: true, message: '请至少选择一个单元类型', trigger: 'change', type: 'array', min: 1 }]
}

/** 查询宏列表 */
function getList() {
  loading.value = true
  listMacro(queryParams.value).then(response => {
    macroList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 转换单元类型 */
function convertUnitType(unitType) {
  const map = {
    'ENCODE': '编码',
    'MODULATE': '调制',
    'DEMODULATE': '解调',
    'DECODE': '译码'
  }
  return map[unitType] || unitType
}

/** 获取单元类型标签 */
function getUnitTypeTag(unitType) {
  const map = {
    'ENCODE': 'primary',
    'MODULATE': 'success',
    'DEMODULATE': 'warning',
    'DECODE': 'danger'
  }
  return map[unitType] || ''
}

/** 解析枚举选项字符串为数组 */
function parseEnumOptions(enumOptionsStr) {
  if (!enumOptionsStr) return []
  
  // 去掉两边的花括号（如果有）
  let cleanStr = enumOptionsStr.trim()
  if (cleanStr.startsWith('{')) cleanStr = cleanStr.substring(1)
  if (cleanStr.endsWith('}')) cleanStr = cleanStr.slice(0, -1)
  
  // 格式：0:关闭,1:开启 或 0:OFF,1:ON
  const options = []
  const pairs = cleanStr.split(',')
  
  pairs.forEach(pair => {
    const [value, label] = pair.split(':')
    if (value !== undefined && label !== undefined) {
      options.push({
        value: value.trim(),
        label: label.trim()
      })
    }
  })
  
  return options
}

/** 获取模式类型标签 */
function getModeTypeTag(modeType) {
  const map = {
    'KSA': '',
    'KMA': 'success',
    'SSA': 'warning',
    '基带数传': 'info'
  }
  return map[modeType] || ''
}

/** 获取任务卡片头部样式类 */
function getTaskHeaderClass(index) {
  const colorIndex = ((index - 1) % 4) + 1
  return `task-header-${colorIndex}`
}

/** 获取宏项样式类 */
function getMacroItemClass(unitType) {
  const classMap = {
    'ENCODE': 'macro-encode',
    'MODULATE': 'macro-modulate',
    'DEMODULATE': 'macro-demodulate',
    'DECODE': 'macro-decode'
  }
  return classMap[unitType] || ''
}

/** 删除整个任务（同一名称下的所有宏） */
function handleDeleteTask(task) {
  const macroIds = task.macros.map(m => m.macroId)
  const macroCount = macroIds.length
  
  ElMessageBox.confirm(`是否确认删除该任务下的 ${macroCount} 个宏配置？\n（${task.baseName}）`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delMacro(macroIds)
  }).then(() => {
    getList()
    ElMessage.success(`成功删除 ${macroCount} 个宏配置`)
  }).catch(() => {})
}

/** 添加缺失的单元类型宏 */
function handleAddMissing(task, unitType) {
  reset()
  
  // 单元类型中文映射
  const unitTypeNames = {
    'ENCODE': '编码',
    'MODULATE': '调制',
    'DEMODULATE': '解调',
    'DECODE': '译码'
  }
  
  // 预填充表单
  form.value.macroName = `${task.baseName}(${unitTypeNames[unitType]})`
  form.value.macroCode = `${task.baseName.toUpperCase().replace(/\s+/g, '_')}_${unitType}`
  form.value.modeType = task.modeType
  form.value.unitTypes = [unitType]
  form.value.description = task.description
  form.value.isDefault = '0'
  form.value.status = '0'
  form.value.sortOrder = 0
  
  open.value = true
  title.value = `添加${unitTypeNames[unitType]}宏`
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.macroId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = '添加宏'
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const macroId = row.macroId || ids.value[0]
  getMacro(macroId).then(response => {
    form.value = response.data
    open.value = true
    title.value = '修改宏'
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const macroIds = row.macroId || ids.value
  ElMessageBox.confirm('是否确认删除选中的宏配置？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delMacro(macroIds)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

/** 配置参数按钮操作 */
function handleConfig(row) {
  currentMacro.value = { ...row }
  // 加载宏的参数配置
  getMacroParams(row.macroId).then(response => {
    macroParams.value = response.data || []
    configOpen.value = true
  })
}

/** 添加参数 */
function handleAddParam() {
  // 加载该单元类型的可用参数列表
  const query = {
    unitType: currentMacro.value.unitType,
    pageNum: 1,
    pageSize: 1000
  }
  listBasebandParamDef(query).then(response => {
    // 获取已添加的参数ID列表
    const addedParamIds = macroParams.value.map(p => p.paramId)
    // 过滤掉已添加的参数，只显示未添加的
    availableParams.value = (response.rows || []).filter(p => !addedParamIds.includes(p.paramId))
    paramSelectOpen.value = true
  })
}

/** 参数选择变化 */
function handleParamSelectionChange(selection) {
  selectedParams.value = selection
}

/** 确认添加参数 */
function confirmAddParams() {
  selectedParams.value.forEach(param => {
    macroParams.value.push({
      paramId: param.paramId,
      paramName: param.paramName,
      paramCode: param.paramCode,
      valueType: param.valueType,
      enumOptions: param.enumOptions,
      minValue: param.minValue,
      maxValue: param.maxValue,
      paramValue: param.defaultValue || ''
    })
  })
  paramSelectOpen.value = false
}

/** 删除参数 */
function handleDeleteParam(index) {
  macroParams.value.splice(index, 1)
}

/** 提交宏表单 */
function submitForm() {
  proxy.$refs['macroRef'].validate(valid => {
    if (valid) {
      if (form.value.macroId != null) {
        // 修改模式：单个提交
        updateMacro(form.value).then(response => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        // 新增模式：批量创建（每个单元类型创建一个宏）
        const unitTypes = form.value.unitTypes || []
        if (unitTypes.length === 0) {
          ElMessage.warning('请至少选择一个单元类型')
          return
        }
        
        // 单元类型中文映射
        const unitTypeNames = {
          'ENCODE': '编码',
          'MODULATE': '调制',
          'DEMODULATE': '解调',
          'DECODE': '译码'
        }
        
        // 批量创建宏
        const promises = unitTypes.map(unitType => {
          const macroData = {
            ...form.value,
            unitType: unitType,
            // 自动生成宏编码：原编码_单元类型
            macroCode: unitTypes.length > 1 ? `${form.value.macroCode}_${unitType}` : form.value.macroCode,
            // 自动生成宏名称：原名称(单元类型)
            macroName: unitTypes.length > 1 ? `${form.value.macroName}(${unitTypeNames[unitType]})` : form.value.macroName
          }
          delete macroData.unitTypes
          return addMacro(macroData)
        })
        
        Promise.all(promises).then(() => {
          ElMessage.success(`成功创建 ${unitTypes.length} 个宏配置`)
          open.value = false
          getList()
        }).catch(error => {
          ElMessage.error('部分宏创建失败，请检查')
          getList()
        })
      }
    }
  })
}

/** 保存宏参数配置 */
function submitMacroParams() {
  const params = macroParams.value.map(item => ({
    paramId: item.paramId,
    paramValue: item.paramValue
  }))
  
  saveMacroParams(currentMacro.value.macroId, params).then(response => {
    ElMessage.success('参数配置保存成功')
    configOpen.value = false
  })
}

/** 表单重置 */
function reset() {
  form.value = {
    macroId: null,
    macroName: null,
    macroCode: null,
    unitType: null,
    unitTypes: [],  // 新增时多选
    modeType: null,
    description: null,
    isDefault: '0',
    status: '0',
    sortOrder: 0,
    remark: null
  }
  proxy.resetForm('macroRef')
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

getList()
</script>

<style scoped lang="scss">
.macro-info {
  margin-bottom: 20px;
}

.param-config {
  margin-top: 20px;
}

.el-form-item-msg {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

/* 视图切换样式 */
.view-switch {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
}

/* 任务分组视图样式 */
.task-group-view {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(420px, 1fr));
  gap: 16px;
}

.task-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.task-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 18px;
  color: #fff;
}

.task-header-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.task-header-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.task-header-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.task-header-4 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.task-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.task-icon {
  font-size: 22px;
}

.task-title {
  font-size: 16px;
  font-weight: 600;
}

.task-actions {
  display: flex;
  gap: 8px;
}

.task-units {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.macro-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
  transition: all 0.2s ease;
}

.macro-item:hover {
  background: #ecf5ff;
}

.macro-item.missing {
  border-left-color: #dcdfe6;
  background: #fafafa;
  opacity: 0.7;
}

.macro-item.missing:hover {
  opacity: 1;
  background: #f5f7fa;
}

.macro-item.macro-encode {
  border-left-color: #409eff;
}

.macro-item.macro-modulate {
  border-left-color: #67c23a;
}

.macro-item.macro-demodulate {
  border-left-color: #e6a23c;
}

.macro-item.macro-decode {
  border-left-color: #f56c6c;
}

.macro-type {
  min-width: 70px;
}

.macro-info-detail {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
}

.macro-code {
  font-size: 12px;
  color: #606266;
  background: #e9ecef;
  padding: 2px 8px;
  border-radius: 4px;
  font-family: monospace;
}

.macro-actions {
  display: flex;
  gap: 4px;
}

.task-footer {
  padding: 10px 18px;
  background: #f8f9fa;
  border-top: 1px solid #ebeef5;
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 枚举选项显示样式 */
.enum-options-display {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 8px 0;
}

.enum-option-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  line-height: 1.5;
}

.enum-value {
  min-width: 40px;
  font-weight: 600;
  color: #409eff;
  text-align: right;
  padding-right: 4px;
}

.enum-separator {
  color: #909399;
  padding: 0 4px;
}

.enum-label {
  color: #606266;
  flex: 1;
}

/* 参数值输入框样式优化 */
:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number .el-input__inner) {
  text-align: left;
}
</style>
