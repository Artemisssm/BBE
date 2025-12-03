<template>
  <div class="app-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <div class="title-row">
            <h2 class="title">
              <span v-if="unitInfo.channelNo" class="title-resource">{{ formatResource(unitInfo.channelNo, unitInfo.unitName) }}</span>
              <span v-if="unitInfo.unitName" class="title-unit">{{ unitInfo.unitName }}</span>
              <span v-if="!unitInfo.unitName && !unitInfo.channelNo">基带参数配置</span>
            </h2>
            <el-tag :type="getUnitTypeTag(unitInfo.unitType)" class="unit-type-tag" size="large">
              {{ getUnitTypeName(unitInfo.unitType) || '未知类型' }}
            </el-tag>
          </div>
          <div class="unit-info">
            <div class="info-item">
              <el-icon class="info-icon"><Cpu /></el-icon>
              <span class="info-label">链路模式：</span>
              <span class="info-value">{{ unitInfo.unitName || '未知链路' }}</span>
            </div>
            <div class="info-item" v-if="unitInfo.channelNo">
              <el-icon class="info-icon"><Connection /></el-icon>
              <span class="info-label">板卡资源：</span>
              <span class="info-value">{{ formatResource(unitInfo.channelNo, unitInfo.unitName) }}</span>
            </div>
            <div class="info-item" v-if="unitInfo.modeType">
              <el-icon class="info-icon"><Setting /></el-icon>
              <span class="info-label">模式类型：</span>
              <el-tag :type="getModeTypeTag(unitInfo.modeType)" size="small" effect="plain">
                {{ unitInfo.modeType }}
              </el-tag>
            </div>
            <div class="info-item" v-if="unitInfo.version">
              <el-icon class="info-icon"><Document /></el-icon>
              <span class="info-label">版本：</span>
              <span class="info-value">{{ unitInfo.version }}</span>
            </div>
          </div>
        </div>
        <div class="header-actions">
          <div class="auto-save-status" v-if="autoSaveStatus">
            <el-icon class="status-icon" :class="autoSaveStatus.type">
              <Loading v-if="autoSaveStatus.type === 'saving'" />
              <CircleCheck v-else-if="autoSaveStatus.type === 'success'" />
              <CircleClose v-else-if="autoSaveStatus.type === 'error'" />
            </el-icon>
            <span class="status-text">{{ autoSaveStatus.text }}</span>
          </div>
          <el-button type="success" icon="Upload" @click="handleDispatch" v-hasPermi="['system:basebandValue:dispatch']">
            下发参数
          </el-button>
          <el-button icon="Back" @click="handleBack">返回</el-button>
        </div>
      </div>
    </div>

    <!-- 参数配置区域 -->
    <div class="param-config-area">
      <!-- 工具栏 -->
      <div class="config-toolbar" v-if="paramList.length > 0">
        <div class="toolbar-left">
          <el-input
            v-model="searchText"
            placeholder="搜索参数名称或代码"
            clearable
            style="width: 300px"
            prefix-icon="Search"
            @input="handleSearch"
          />
          <el-tag type="info" effect="plain">
            共 {{ filteredParamList.length }} 个参数
          </el-tag>
        </div>
        <div class="toolbar-right">
          <el-radio-group v-model="viewMode" size="default">
            <el-radio-button value="grid">
              <el-icon><Grid /></el-icon>
              <span>网格视图</span>
            </el-radio-button>
            <el-radio-button value="list">
              <el-icon><List /></el-icon>
              <span>列表视图</span>
            </el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <div class="config-content">
        <!-- 参数网格视图 -->
        <div v-if="viewMode === 'grid'" class="param-grid" v-loading="loading">
          <div
            class="param-card"
            v-for="(param, index) in filteredParamList"
            :key="param.paramId"
            :class="getParamCardClass(param)"
          >
            <!-- 参数头部 -->
            <div class="param-header">
              <div class="param-title">
                <span class="param-name">{{ param.paramName }}</span>
                <span class="param-code">{{ param.paramCode }}</span>
              </div>
              <div class="param-type-indicator" :class="getValueTypeClass(param.valueType)">
                {{ getValueTypeName(param.valueType) }}
              </div>
            </div>

            <!-- 参数控件 -->
            <div class="param-control">
              <div v-if="param.valueType === 'ENUM'" class="control-enum">
                <el-select
                  v-model="param.rawValue"
                  placeholder="请选择"
                  class="param-select"
                  :disabled="param.disabled"
                  popper-class="baseband-select-popper"
                  @change="handleParamChange(param)"
                >
                  <el-option
                    v-for="(label, value) in getFilteredEnumOptions(param)"
                    :key="value"
                    :label="label"
                    :value="value"
                  />
                </el-select>
              </div>

              <div v-else-if="param.valueType === 'FLOAT'" class="control-number">
                <el-input-number
                  v-model.number="param.rawValue"
                  :min="param.minValue"
                  :max="param.maxValue"
                  :precision="2"
                  :step="0.01"
                  :disabled="param.disabled"
                  controls-position="right"
                  class="param-input-number"
                  @change="handleParamChange(param)"
                />
              </div>

              <div v-else-if="param.valueType === 'HEX'" class="control-hex">
                <el-input
                  v-model="param.rawValue"
                  placeholder="0x0000"
                  :disabled="param.disabled"
                  @change="handleParamChange(param)"
                  class="param-input-hex"
                >
                  <template #prepend>0x</template>
                </el-input>
              </div>

              <div v-else class="control-number">
                <el-input-number
                  v-model.number="param.rawValue"
                  :min="param.minValue"
                  :max="param.maxValue"
                  :step="1"
                  :disabled="param.disabled"
                  @change="handleParamChange(param)"
                  controls-position="right"
                  class="param-input-number"
                />
              </div>
            </div>

            <!-- 参数信息 -->
            <div class="param-footer">
              <div v-if="param.defaultValue" class="param-default">
                <span class="label">默认值:</span>
                <span class="value">{{ param.defaultValue }}</span>
              </div>
              <div v-if="param.minValue !== null && param.maxValue !== null" class="param-range">
                <span class="label">范围:</span>
                <span class="value">{{ param.minValue }} ~ {{ param.maxValue }}</span>
              </div>
              <div v-if="param.constraintHint" class="param-constraint-hint">
                <el-icon class="hint-icon"><InfoFilled /></el-icon>
                <span class="hint-text">{{ param.constraintHint }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 参数列表视图 -->
        <div v-else class="param-list" v-loading="loading">
          <el-table :data="filteredParamList" stripe border>
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column label="参数名称" prop="paramName" min-width="150" show-overflow-tooltip>
              <template #default="scope">
                <div class="list-param-name">
                  <span class="name">{{ scope.row.paramName }}</span>
                  <span class="code">{{ scope.row.paramCode }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="类型" prop="valueType" width="80" align="center">
              <template #default="scope">
                <el-tag :type="getValueTypeTagType(scope.row.valueType)" size="small">
                  {{ getValueTypeName(scope.row.valueType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="参数值" prop="rawValue" width="280" align="center">
              <template #default="scope">
                <div v-if="scope.row.valueType === 'ENUM'" class="list-control">
                  <el-select
                    v-model="scope.row.rawValue"
                    placeholder="请选择"
                    :disabled="scope.row.disabled"
                    style="width: 100%"
                    @change="handleParamChange(scope.row)"
                  >
                    <el-option
                      v-for="(label, value) in getFilteredEnumOptions(scope.row)"
                      :key="value"
                      :label="label"
                      :value="value"
                    />
                  </el-select>
                </div>
                <div v-else-if="scope.row.valueType === 'FLOAT'" class="list-control">
                  <el-input-number
                    v-model.number="scope.row.rawValue"
                    :min="scope.row.minValue"
                    :max="scope.row.maxValue"
                    :precision="2"
                    :step="0.01"
                    :disabled="scope.row.disabled"
                    controls-position="right"
                    style="width: 100%"
                    @change="handleParamChange(scope.row)"
                  />
                </div>
                <div v-else-if="scope.row.valueType === 'HEX'" class="list-control">
                  <el-input
                    v-model="scope.row.rawValue"
                    placeholder="0x0000"
                    :disabled="scope.row.disabled"
                    style="width: 100%"
                    @change="handleParamChange(scope.row)"
                  >
                    <template #prepend>0x</template>
                  </el-input>
                </div>
                
                <div v-else class="list-control">
                  <el-input-number
                    v-model.number="scope.row.rawValue"
                    :min="scope.row.minValue"
                    :max="scope.row.maxValue"
                    :step="1"
                    :disabled="scope.row.disabled"
                    controls-position="right"
                    style="width: 100%"
                    @change="handleParamChange(scope.row)"
                  />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="默认值" prop="defaultValue" width="100" align="center" show-overflow-tooltip />
            <el-table-column label="取值范围" width="120" align="center">
              <template #default="scope">
                <span v-if="scope.row.minValue !== null && scope.row.maxValue !== null" class="range-text">
                  {{ scope.row.minValue }} ~ {{ scope.row.maxValue }}
                </span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="约束提示" min-width="150" show-overflow-tooltip>
              <template #default="scope">
                <div v-if="scope.row.constraintHint" class="constraint-hint-cell">
                  <el-icon class="hint-icon"><InfoFilled /></el-icon>
                  <span>{{ scope.row.constraintHint }}</span>
                </div>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 空状态 -->
        <div v-if="paramList.length === 0 && !loading" class="empty-state">
          <div class="empty-content">
            <el-icon class="empty-icon"><Document /></el-icon>
            <p class="empty-text">暂无参数配置</p>
            <p class="empty-desc">该基带单元暂无可配置的参数</p>
          </div>
        </div>

        <!-- 搜索无结果 -->
        <div v-if="paramList.length > 0 && filteredParamList.length === 0 && !loading" class="empty-state">
          <div class="empty-content">
            <el-icon class="empty-icon"><Search /></el-icon>
            <p class="empty-text">未找到匹配的参数</p>
            <p class="empty-desc">请尝试其他搜索关键词</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="BasebandParamValue">
import { listBasebandParamValue, saveBasebandParamValue, dispatchBasebandParam } from "@/api/system/baseband/paramValue"
import { getBasebandUnit } from "@/api/system/baseband/unit"
import { validateParamConstraint, getAllConstraintsForUnit } from "@/api/system/baseband/paramConstraint"
import useTagsViewStore from '@/store/modules/tagsView'
import { useBasebandStore } from '@/store/modules/baseband'

const { proxy } = getCurrentInstance()
const basebandStore = useBasebandStore()
const route = useRoute()
const router = useRouter()
const tagsViewStore = useTagsViewStore()

const unitId = ref(null)
const unitInfo = ref({})
const paramList = ref([])
const filteredParamList = ref([])
const loading = ref(true)
const searchText = ref('')
const viewMode = ref('grid') // grid 或 list
const autoSaveStatus = ref(null) // 自动保存状态
let autoSaveTimer = null // 自动保存定时器

// 约束相关
const constraintsMap = ref({}) // 约束映射表，key为参数代码，value为约束列表

// 板卡资源配置
const totalBoards = 4
const fpgasPerBoard = 2

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
  if (!channelNo) return '-'
  const boardNo = Math.floor((channelNo - 1) / fpgasPerBoard) + 1
  const fpgaNo = (channelNo - 1) % fpgasPerBoard
  
  if (unitName === '返向高速数传') {
    return `板${boardNo}（整板）`
  } else {
    return `板${boardNo}-FPGA${fpgaNo}`
  }
}

/** 获取值类型名称 */
function getValueTypeName(valueType) {
  const typeMap = {
    'ENUM': '枚举',
    'UINT': '整数',
    'FLOAT': '浮点',
    'HEX': '16进制'
  }
  return typeMap[valueType] || valueType
}

/** 获取值类型样式类 */
function getValueTypeClass(valueType) {
  return `value-type-${valueType.toLowerCase()}`
}

/** 验证16进制输入 */
function validateHexInput(value) {
  // 移除0x前缀
  const hexValue = value.replace(/^0x/i, '')
  // 验证是否为有效的16进制
  return /^[0-9A-Fa-f]+$/.test(hexValue)
}

/** 格式化16进制显示 */
function formatHexValue(value) {
  if (!value) return '0x0'
  const str = String(value)
  if (str.startsWith('0x') || str.startsWith('0X')) {
    return str.toUpperCase()
  }
  return '0x' + str.toUpperCase()
}

/** 获取值类型标签类型（用于列表视图） */
function getValueTypeTagType(valueType) {
  const tagMap = {
    'ENUM': 'primary',
    'UINT': 'success',
    'FLOAT': 'warning',
    'HEX': 'danger'
  }
  return tagMap[valueType] || 'info'
}

/** 获取参数卡片样式类 */
function getParamCardClass(param) {
  return {
    'param-disabled': param.disabled
  }
}

/** 解析枚举选项 */
function getEnumOptions(enumOptionsStr) {
  if (!enumOptionsStr) return {}
  try {
    return JSON.parse(enumOptionsStr)
  } catch (e) {
    console.error('解析枚举选项失败:', e)
    return {}
  }
}

/** 获取过滤后的枚举选项（应用枚举限制约束） */
function getFilteredEnumOptions(param) {
  const allOptions = getEnumOptions(param.enumOptions)
  
  // 如果有枚举限制约束
  if (param.limitedEnumOptions && param.limitedEnumOptions.length > 0) {
    const filtered = {}
    param.limitedEnumOptions.forEach(value => {
      if (allOptions[value]) {
        filtered[value] = allOptions[value]
      }
    })
    return filtered
  }
  
  return allOptions
}

/** 获取单元信息 */
function getUnitInfo() {
  if (!unitId.value) return
  getBasebandUnit(unitId.value).then(response => {
    unitInfo.value = response.data || {}
    // 获取单元信息后立即更新页面标题
    updatePageTitle()
  }).catch(() => {
    unitInfo.value = {}
  })
}

/** 获取参数列表 */
async function getParamList() {
  if (!unitId.value) return
  loading.value = true
  try {
    const response = await listBasebandParamValue(unitId.value)
    paramList.value = (response.data || []).map(param => {
      // 如果参数值不存在，使用默认值
      if (!param.rawValue && param.defaultValue) {
        param.rawValue = param.defaultValue
      }
      // 根据值类型转换数据类型
      if (param.valueType === 'UINT' || param.valueType === 'FLOAT') {
        param.rawValue = param.rawValue ? parseFloat(param.rawValue) : (param.minValue || 0)
      } else if (param.valueType === 'HEX') {
        // HEX类型：确保有值，移除0x前缀（前端会自动添加）
        if (!param.rawValue) {
          param.rawValue = param.defaultValue || '0'
        }
        // 移除0x前缀（如果有）
        param.rawValue = String(param.rawValue).replace(/^0x/i, '').toUpperCase()
      }
      // 初始化约束相关属性
      param.disabled = false
      param.constraintHint = null
      param.limitedEnumOptions = null
      return param
    })
    filteredParamList.value = paramList.value
    
    // 加载约束规则
    await loadConstraints()
  } catch (error) {
    console.error('获取参数列表失败:', error)
  } finally {
    loading.value = false
  }
}

/** 加载约束规则 */
async function loadConstraints() {
  if (!unitInfo.value.unitName || !unitInfo.value.unitType) {
    return
  }
  
  try {
    const response = await getAllConstraintsForUnit(
      unitInfo.value.unitName,
      unitInfo.value.unitType,
      unitInfo.value.modeType || ''
    )
    constraintsMap.value = response.data || {}
    applyConstraints()
  } catch (error) {
    console.error('加载约束失败:', error)
  }
}

/** 应用约束规则 */
function applyConstraints() {
  paramList.value.forEach(param => {
    const constraints = constraintsMap.value[param.paramCode] || []
    
    // 重置约束状态
    param.disabled = false
    param.constraintHint = null
    param.limitedEnumOptions = null
    
    constraints.forEach(constraint => {
      // 检查约束条件是否满足
      if (!isConstraintConditionMet(constraint)) {
        return
      }
      
      switch (constraint.constraintType) {
        case 'CONTROL_DISABLE':
          param.disabled = true
          param.constraintHint = constraint.errorMessage || '该参数在当前条件下不可修改'
          break
          
        case 'ENUM_LIMIT':
          // 限制枚举选项
          if (constraint.constraintValue) {
            param.limitedEnumOptions = constraint.constraintValue.split(',')
            param.constraintHint = constraint.errorMessage || '部分选项已被限制'
          }
          break
          
        case 'VALUE_RANGE':
          // 显示范围提示
          if (!param.constraintHint) {
            param.constraintHint = constraint.errorMessage || 
              `取值范围: ${constraint.constraintValue.replace(',', ' ~ ')}`
          }
          break
          
        case 'FIXED_VALUE':
          // 显示固定值提示
          if (!param.constraintHint) {
            param.constraintHint = constraint.errorMessage || 
              `可选值: ${constraint.constraintValue}`
          }
          break
          
        case 'FORMULA_CALCULATE':
          // 显示计算公式提示
          if (!param.constraintHint) {
            param.constraintHint = constraint.errorMessage || 
              `根据公式计算: ${constraint.constraintValue}`
          }
          break
      }
    })
  })
  
  filteredParamList.value = [...paramList.value]
}

/** 检查约束条件是否满足 */
function isConstraintConditionMet(constraint) {
  const condition = constraint.constraintCondition
  if (!condition) {
    return true // 无条件，总是满足
  }
  
  const sourceParam = constraint.sourceParamName
  if (!sourceParam) {
    return false
  }
  
  const sourceValue = getParamValue(sourceParam)
  if (sourceValue === null || sourceValue === undefined) {
    return false
  }
  
  return evaluateCondition(String(sourceValue), condition)
}

/** 获取参数值 */
function getParamValue(paramCode) {
  const param = paramList.value.find(p => p.paramCode === paramCode)
  return param ? param.rawValue : null
}

/** 评估条件表达式 */
function evaluateCondition(value, condition) {
  try {
    if (condition.startsWith('==')) {
      return value === condition.substring(2).trim()
    } else if (condition.startsWith('!=')) {
      return value !== condition.substring(2).trim()
    } else if (condition.startsWith('>=')) {
      return parseFloat(value) >= parseFloat(condition.substring(2))
    } else if (condition.startsWith('<=')) {
      return parseFloat(value) <= parseFloat(condition.substring(2))
    } else if (condition.startsWith('>')) {
      return parseFloat(value) > parseFloat(condition.substring(1))
    } else if (condition.startsWith('<')) {
      return parseFloat(value) < parseFloat(condition.substring(1))
    }
  } catch (e) {
    return false
  }
  return false
}

/** 搜索参数 */
function handleSearch() {
  if (!searchText.value) {
    filteredParamList.value = paramList.value
    return
  }
  
  const keyword = searchText.value.toLowerCase()
  filteredParamList.value = paramList.value.filter(param => {
    return param.paramName.toLowerCase().includes(keyword) ||
           param.paramCode.toLowerCase().includes(keyword)
  })
}

/** 参数变化处理（自动保存） */
function handleParamChange(param) {
  // 清除之前的定时器
  if (autoSaveTimer) {
    clearTimeout(autoSaveTimer)
  }
  
  // 显示保存中状态
  autoSaveStatus.value = {
    type: 'saving',
    text: '验证中...'
  }
  
  // 重新应用约束（因为源参数可能影响其他参数）
  applyConstraints()
  
  // 防抖：500ms后验证并保存
  autoSaveTimer = setTimeout(() => {
    validateAndSaveParam(param)
  }, 500)
}

/** 验证并保存参数 */
async function validateAndSaveParam(param) {
  try {
    // 构建所有参数的Map
    const allParamsMap = {}
    paramList.value.forEach(p => {
      allParamsMap[p.paramCode] = p.rawValue ? String(p.rawValue) : ''
    })
    
    // 验证约束
    const validateData = {
      unitName: unitInfo.value.unitName,
      unitType: unitInfo.value.unitType,
      modeType: unitInfo.value.modeType || '', // 添加模式类型
      paramName: param.paramCode,
      paramValue: param.rawValue ? String(param.rawValue) : '',
      allParams: allParamsMap
    }
    
    await validateParamConstraint(validateData)
    
    // 验证通过，保存参数
    autoSaveStatus.value = {
      type: 'saving',
      text: '保存中...'
    }
    
    await autoSaveParam(param)
    
  } catch (error) {
    // 验证失败，显示错误信息
    autoSaveStatus.value = {
      type: 'error',
      text: '验证失败'
    }
    
    const errorMsg = error.msg || error.message || '参数约束验证失败'
    proxy.$modal.msgError(errorMsg)
    
    // 恢复原值（如果有的话）
    // 这里可以考虑保存修改前的值，验证失败时恢复
    
    // 5秒后隐藏错误状态
    setTimeout(() => {
      autoSaveStatus.value = null
    }, 5000)
  }
}

/** 自动保存单个参数 */
async function autoSaveParam(param) {
  const saveData = [{
    paramId: param.paramId,
    rawValue: param.rawValue ? String(param.rawValue) : ''
  }]
  
  try {
    await saveBasebandParamValue(unitId.value, saveData)
    autoSaveStatus.value = {
      type: 'success',
      text: '已保存'
    }
    // 3秒后隐藏状态
    setTimeout(() => {
      autoSaveStatus.value = null
    }, 3000)
  } catch (error) {
    autoSaveStatus.value = {
      type: 'error',
      text: '保存失败'
    }
    const errorMsg = error.msg || error.message || '保存失败'
    proxy.$modal.msgError(errorMsg)
    console.error('自动保存失败:', error)
    // 5秒后隐藏错误状态
    setTimeout(() => {
      autoSaveStatus.value = null
    }, 5000)
    throw error
  }
}

/** 下发参数 */
function handleDispatch() {
  proxy.$modal.confirm('确认要下发参数配置吗？下发后参数将立即生效。').then(() => {
    return dispatchBasebandParam(unitId.value)
  }).then(() => {
    proxy.$modal.msgSuccess("下发成功")
  }).catch(() => {})
}

/** 返回 */
function handleBack() {
  router.back()
}

/** 更新页面标题和标签标题 */
function updatePageTitle() {
  if (unitInfo.value.unitName && unitInfo.value.unitType) {
    const unitTypeName = getUnitTypeName(unitInfo.value.unitType)
    const resource = formatResource(unitInfo.value.channelNo, unitInfo.value.unitName)
    const modeType = unitInfo.value.modeType ? ` [${unitInfo.value.modeType}]` : ''
    
    // 更新浏览器标题
    document.title = `${resource} ${unitInfo.value.unitName}-${unitTypeName}${modeType} | 基带参数配置`
    
    // 更新顶部标签标题
    const newTitle = `${resource} ${unitInfo.value.unitName}-${unitTypeName}${modeType}`
    const currentRoute = {
      ...route,
      meta: {
        ...route.meta,
        title: newTitle
      },
      title: newTitle
    }
    tagsViewStore.updateVisitedView(currentRoute)
  }
}

onMounted(() => {
  // 从路由参数获取单元ID
  unitId.value = route.params.unitId || route.query.unitId
  if (!unitId.value) {
    proxy.$modal.msgError("缺少单元ID参数")
    setTimeout(() => {
      router.back()
    }, 1500)
    return
  }
  
  // 转换为数字类型
  unitId.value = Number(unitId.value)
  
  getUnitInfo()
  getParamList()
})

// 监听单元信息变化，更新页面标题
watch(() => unitInfo.value, () => {
  updatePageTitle()
}, { deep: true })

// 监听参数定义更新，自动刷新参数列表
watch(() => basebandStore.paramDefUpdateTime, (newTime, oldTime) => {
  if (newTime > 0 && newTime !== oldTime) {
    console.log('[参数配置] 检测到参数定义更新，准备刷新列表')
    
    // 检查是否是当前单元类型的更新
    const updatedUnitType = basebandStore.paramDefUpdateUnitType
    const currentUnitType = unitInfo.value.unitType
    
    if (!updatedUnitType || updatedUnitType === currentUnitType) {
      console.log('[参数配置] 刷新参数列表，单元类型:', currentUnitType)
      // 延迟一点刷新，确保后端数据已更新
      setTimeout(() => {
        getParamList()
        proxy.$modal.msgSuccess('参数列表已更新')
      }, 300)
    }
  }
})
</script>

<style scoped>
/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px;
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  color: white;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 20px;
}

.header-title {
  flex: 1;
  min-width: 300px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.title-resource {
  background: rgba(255, 255, 255, 0.25);
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 18px;
  font-family: 'Courier New', monospace;
  font-weight: 700;
}

.title-unit {
  font-size: 22px;
}

.unit-type-tag {
  font-size: 14px;
  font-weight: 600;
  padding: 8px 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.unit-info {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 14px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.15);
  padding: 8px 14px;
  border-radius: 6px;
  backdrop-filter: blur(10px);
}

.info-icon {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
}

.info-label {
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
}

.info-value {
  font-weight: 600;
  color: white;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

/* 自动保存状态 */
.auto-save-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  backdrop-filter: blur(10px);
  font-size: 14px;
  color: white;
}

.status-icon {
  font-size: 16px;
}

.status-icon.saving {
  color: #409eff;
  animation: rotate 1s linear infinite;
}

.status-icon.success {
  color: #67c23a;
}

.status-icon.error {
  color: #f56c6c;
}

.status-text {
  font-weight: 500;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 参数配置区域 */
.param-config-area {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

/* 工具栏 */
.config-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.config-content {
  padding: 20px;
  min-height: 400px;
}

/* 参数网格 */
.param-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 16px;
  padding: 0;
}

/* 参数列表 */
.param-list {
  padding: 0;
}

.list-param-name {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.list-param-name .name {
  font-weight: 500;
  color: #333;
}

.list-param-name .code {
  font-size: 12px;
  color: #999;
  font-family: 'Courier New', monospace;
}

.list-control {
  display: flex;
  justify-content: center;
  align-items: center;
}

.range-text {
  color: #28a745;
  font-weight: 500;
  font-size: 13px;
}

/* 参数卡片 */
.param-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.param-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #409eff;
}

.param-card.param-disabled {
  opacity: 0.6;
  background: #f5f5f5;
}

/* 参数头部 */
.param-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
}

.param-title {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.param-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
  line-height: 1.4;
}

.param-code {
  font-size: 12px;
  color: #999;
  font-family: 'Courier New', monospace;
}

.param-type-indicator {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
  white-space: nowrap;
}

.value-type-enum {
  background: #e3f2fd;
  color: #1976d2;
}

.value-type-uint {
  background: #e8f5e9;
  color: #388e3c;
}

.value-type-float {
  background: #fff3e0;
  color: #f57c00;
}

.value-type-hex {
  background: #ffebee;
  color: #c62828;
}

/* 参数控件 */
.param-control {
  flex: 1;
}

.control-enum,
.control-number,
.control-hex {
  width: 100%;
}

.param-input-hex {
  width: 100%;
  font-family: 'Courier New', monospace;
}

.param-select,
.param-input-number {
  width: 100%;
}

/* 参数底部信息 */
.param-footer {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-top: 8px;
  border-top: 1px solid #e9ecef;
  font-size: 12px;
}

.param-default,
.param-range {
  display: flex;
  gap: 4px;
}

.param-default .label,
.param-range .label {
  color: #999;
  font-weight: 500;
}

.param-default .value {
  color: #666;
}

.param-range .value {
  color: #28a745;
  font-weight: 500;
}

.param-constraint-hint {
  display: flex;
  align-items: flex-start;
  gap: 4px;
  padding: 6px 8px;
  background: #fff3cd;
  border-radius: 4px;
  margin-top: 4px;
}

.param-constraint-hint .hint-icon {
  color: #856404;
  font-size: 14px;
  margin-top: 1px;
  flex-shrink: 0;
}

.param-constraint-hint .hint-text {
  color: #856404;
  font-size: 11px;
  line-height: 1.4;
}

.constraint-hint-cell {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #856404;
}

.constraint-hint-cell .hint-icon {
  font-size: 14px;
  flex-shrink: 0;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.empty-content {
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  color: #666;
  margin: 0 0 8px 0;
}

.empty-desc {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .param-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  }
}

@media (max-width: 768px) {
  .param-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-title {
    width: 100%;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .header-actions .el-button {
    flex: 1;
  }
  
  .config-toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .toolbar-left,
  .toolbar-right {
    width: 100%;
    justify-content: space-between;
  }
  
  .toolbar-left :deep(.el-input) {
    flex: 1;
  }
}
</style>

<style>
/* 全局下拉框样式 */
.baseband-select-popper {
  max-height: 300px !important;
  min-width: 280px !important;
}

.baseband-select-popper .el-select-dropdown__item {
  padding: 8px 16px;
  line-height: 1.4;
  white-space: normal;
  word-break: break-word;
  min-height: 36px;
  display: flex;
  align-items: center;
}

.baseband-select-popper .el-select-dropdown__item:hover {
  background-color: #f5f7fa;
}

.baseband-select-popper .el-select-dropdown__item.selected {
  background-color: #409eff;
  color: white;
}

/* 确保下拉框内容完全显示 */
.baseband-select-popper .el-select-dropdown__wrap {
  max-height: 280px !important;
}

/* 优化表单控件样式 */
:deep(.el-form-item) {
  margin-bottom: 8px;
}

:deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-input-number) {
  width: 100%;
}
</style>
