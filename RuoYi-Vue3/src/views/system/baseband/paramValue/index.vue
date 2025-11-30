<template>
  <div class="app-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-title">
          <h2 class="title">基带参数配置</h2>
          <div class="unit-info">
            <el-tag :type="getUnitTypeTag(unitInfo.unitType)" class="unit-tag">
              {{ getUnitTypeName(unitInfo.unitType) }}
            </el-tag>
            <span class="unit-name">{{ unitInfo.unitName || '未知单元' }}</span>
            <span v-if="unitInfo.channelNo" class="channel-no">通道 {{ unitInfo.channelNo }}</span>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" icon="Check" @click="handleSave" v-hasPermi="['system:basebandValue:edit']">
            保存配置
          </el-button>
          <el-button type="success" icon="Upload" @click="handleDispatch" v-hasPermi="['system:basebandValue:dispatch']">
            下发参数
          </el-button>
          <el-button icon="Back" @click="handleBack">返回</el-button>
        </div>
      </div>
    </div>

    <!-- 参数配置区域 -->
    <div class="param-config-area">
      <div class="config-content">
        <!-- 参数网格 -->
        <div class="param-grid" v-loading="loading">
          <div
            class="param-card"
            v-for="(param, index) in paramList"
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
                >
                  <el-option
                    v-for="(label, value) in getEnumOptions(param.enumOptions)"
                    :key="value"
                    :label="label"
                    :value="value"
                  />
                </el-select>
              </div>

              <div v-else-if="param.valueType === 'SWITCH'" class="control-switch">
                <el-switch
                  v-model="param.rawValue"
                  :active-value="param.maxValue ? param.maxValue.toString() : '1'"
                  :inactive-value="param.minValue ? param.minValue.toString() : '0'"
                  :disabled="param.disabled"
                  active-text="开"
                  inactive-text="关"
                />
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
                />
              </div>

              <div v-else class="control-number">
                <el-input-number
                  v-model.number="param.rawValue"
                  :min="param.minValue"
                  :max="param.maxValue"
                  :step="1"
                  :disabled="param.disabled"
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
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="paramList.length === 0 && !loading" class="empty-state">
          <div class="empty-content">
            <el-icon class="empty-icon"><Document /></el-icon>
            <p class="empty-text">暂无参数配置</p>
            <p class="empty-desc">该基带单元暂无可配置的参数</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="BasebandParamValue">
import { listBasebandParamValue, saveBasebandParamValue, dispatchBasebandParam } from "@/api/system/baseband/paramValue"
import { getBasebandUnit } from "@/api/system/baseband/unit"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()

const unitId = ref(null)
const unitInfo = ref({})
const paramList = ref([])
const loading = ref(true)

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

/** 获取值类型样式类 */
function getValueTypeClass(valueType) {
  return `value-type-${valueType.toLowerCase()}`
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

/** 获取单元信息 */
function getUnitInfo() {
  if (!unitId.value) return
  getBasebandUnit(unitId.value).then(response => {
    unitInfo.value = response.data || {}
  }).catch(() => {
    unitInfo.value = {}
  })
}

/** 获取参数列表 */
function getParamList() {
  if (!unitId.value) return
  loading.value = true
  listBasebandParamValue(unitId.value).then(response => {
    paramList.value = (response.data || []).map(param => {
      // 如果参数值不存在，使用默认值
      if (!param.rawValue && param.defaultValue) {
        param.rawValue = param.defaultValue
      }
      // 根据值类型转换数据类型
      if (param.valueType === 'UINT' || param.valueType === 'FLOAT') {
        param.rawValue = param.rawValue ? parseFloat(param.rawValue) : (param.minValue || 0)
      }
      return param
    })
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

/** 保存配置 */
function handleSave() {
  // 构建保存数据
  const saveData = paramList.value.map(param => ({
    paramId: param.paramId,
    rawValue: param.rawValue ? String(param.rawValue) : ''
  }))
  
  // 验证必填项
  for (let i = 0; i < saveData.length; i++) {
    if (!saveData[i].rawValue && saveData[i].rawValue !== '0') {
      proxy.$modal.msgError(`参数"${paramList.value[i].paramName}"的值不能为空`)
      return
    }
  }
  
  proxy.$modal.confirm('确认要保存参数配置吗？').then(() => {
    return saveBasebandParamValue(unitId.value, saveData)
  }).then(() => {
    proxy.$modal.msgSuccess("保存成功")
    getParamList()
  }).catch(() => {})
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
</script>

<style scoped>
/* 页面头部 */
.page-header {
  background: #fff;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.header-title {
  flex: 1;
}

.title {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.unit-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #666;
}

.unit-tag {
  font-size: 12px;
}

.unit-name {
  font-weight: 500;
  color: #333;
}

.channel-no {
  color: #999;
}

.header-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 参数配置区域 */
.param-config-area {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.config-content {
  min-height: 400px;
}

/* 参数网格 */
.param-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
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

.value-type-switch {
  background: #f3e5f5;
  color: #7b1fa2;
}

/* 参数控件 */
.param-control {
  flex: 1;
}

.control-enum,
.control-switch,
.control-number {
  width: 100%;
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
@media (max-width: 768px) {
  .param-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .header-actions .el-button {
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
