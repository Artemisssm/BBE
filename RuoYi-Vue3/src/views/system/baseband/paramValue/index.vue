<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>基带参数配置 - {{ unitInfo.unitName || '未知单元' }}</span>
          <div>
            <el-button type="primary" icon="Check" @click="handleSave" v-hasPermi="['system:basebandValue:edit']">保存配置</el-button>
            <el-button type="success" icon="Upload" @click="handleDispatch" v-hasPermi="['system:basebandValue:dispatch']">下发参数</el-button>
            <el-button icon="Back" @click="handleBack">返回</el-button>
          </div>
        </div>
      </template>

      <el-form v-loading="loading" label-width="200px">
        <el-row :gutter="20">
          <el-col :span="12" v-for="(param, index) in paramList" :key="param.paramId">
            <el-form-item :label="param.paramName" :prop="'params.' + index + '.rawValue'">
              <div v-if="param.valueType === 'ENUM'">
                <el-select 
                  v-model="param.rawValue" 
                  placeholder="请选择" 
                  style="width: 100%"
                  :disabled="param.disabled"
                >
                  <el-option
                    v-for="(label, value) in getEnumOptions(param.enumOptions)"
                    :key="value"
                    :label="label"
                    :value="value"
                  />
                </el-select>
              </div>
              <div v-else-if="param.valueType === 'SWITCH'">
                <el-switch
                  v-model="param.rawValue"
                  :active-value="param.maxValue ? param.maxValue.toString() : '1'"
                  :inactive-value="param.minValue ? param.minValue.toString() : '0'"
                  :disabled="param.disabled"
                />
              </div>
              <div v-else-if="param.valueType === 'FLOAT'">
                <el-input-number
                  v-model.number="param.rawValue"
                  :min="param.minValue"
                  :max="param.maxValue"
                  :precision="2"
                  :step="0.01"
                  style="width: 100%"
                  :disabled="param.disabled"
                />
              </div>
              <div v-else>
                <el-input-number
                  v-model.number="param.rawValue"
                  :min="param.minValue"
                  :max="param.maxValue"
                  :step="1"
                  style="width: 100%"
                  :disabled="param.disabled"
                />
              </div>
              <div style="font-size: 12px; color: #909399; margin-top: 5px;">
                <span>编码: {{ param.paramCode }}</span>
                <span v-if="param.unitType" style="margin-left: 10px;">类型: {{ getUnitTypeName(param.unitType) }}</span>
                <span v-if="param.defaultValue" style="margin-left: 10px;">默认: {{ param.defaultValue }}</span>
              </div>
              <div v-if="param.minValue !== null && param.maxValue !== null" style="font-size: 12px; color: #909399;">
                范围: {{ param.minValue }} ~ {{ param.maxValue }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <div v-if="paramList.length === 0 && !loading" style="text-align: center; padding: 40px; color: #909399;">
          暂无参数配置
        </div>
      </el-form>
    </el-card>
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
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-weight: bold;
  font-size: 16px;
}
</style>
