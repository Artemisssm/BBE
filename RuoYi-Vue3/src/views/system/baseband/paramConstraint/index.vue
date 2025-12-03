<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="源链路模式" prop="sourceUnitName">
        <el-input
          v-model="queryParams.sourceUnitName"
          placeholder="请输入源链路模式"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="源单元类型" prop="sourceUnitType">
        <el-select v-model="queryParams.sourceUnitType" placeholder="请选择源单元类型" clearable>
          <el-option label="编码单元" value="ENCODE" />
          <el-option label="调制单元" value="MODULATE" />
          <el-option label="解调单元" value="DEMODULATE" />
          <el-option label="译码单元" value="DECODE" />
        </el-select>
      </el-form-item>
      <el-form-item label="约束类型" prop="constraintType">
        <el-select v-model="queryParams.constraintType" placeholder="请选择约束类型" clearable>
          <el-option label="值范围约束" value="VALUE_RANGE" />
          <el-option label="启用/禁用约束" value="ENABLE_DISABLE" />
          <el-option label="值同步约束" value="VALUE_SYNC" />
          <el-option label="值计算约束" value="VALUE_CALCULATE" />
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
          v-hasPermi="['system:paramConstraint:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:paramConstraint:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="constraintList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="约束ID" align="center" prop="constraintId" width="80" />
      <el-table-column label="源链路" align="center" min-width="150">
        <template #default="scope">
          <div class="unit-info">
            <el-tag size="small" type="primary">{{ scope.row.sourceUnitName }}</el-tag>
            <span class="unit-type">{{ getUnitTypeName(scope.row.sourceUnitType) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="源参数" align="center" min-width="150" show-overflow-tooltip>
        <template #default="scope">
          <div class="param-info">
            <div class="param-name-cn">{{ getParamNameCn(scope.row.sourceParamName) }}</div>
            <div class="param-code">{{ scope.row.sourceParamName }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="目标链路" align="center" min-width="150">
        <template #default="scope">
          <div class="unit-info">
            <el-tag size="small" type="success">{{ scope.row.targetUnitName }}</el-tag>
            <span class="unit-type">{{ getUnitTypeName(scope.row.targetUnitType) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="目标参数" align="center" min-width="150" show-overflow-tooltip>
        <template #default="scope">
          <div class="param-info">
            <div class="param-name-cn">{{ getParamNameCn(scope.row.targetParamName) }}</div>
            <div class="param-code">{{ scope.row.targetParamName }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="模式类型" align="center" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <div v-if="scope.row.modeTypes">
            <el-tag 
              v-for="mode in scope.row.modeTypes.split(',')" 
              :key="mode" 
              size="small" 
              style="margin: 2px;"
            >
              {{ mode }}
            </el-tag>
          </div>
          <el-tag v-else type="info" size="small">全部模式</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="约束类型" align="center" prop="constraintType" width="120">
        <template #default="scope">
          <el-tag :type="getConstraintTypeTag(scope.row.constraintType)" size="small">
            {{ getConstraintTypeName(scope.row.constraintType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="约束条件" align="center" prop="constraintCondition" width="100" show-overflow-tooltip />
      <el-table-column label="约束值" align="center" prop="constraintValue" width="100" show-overflow-tooltip />
      <el-table-column label="优先级" align="center" prop="priority" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template #default="scope">
          <el-button
            link
            type="primary"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:paramConstraint:edit']"
          >修改</el-button>
          <el-button
            link
            type="danger"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:paramConstraint:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改约束对话框 -->
    <el-dialog :title="title" v-model="open" width="900px" append-to-body>
      <!-- 使用说明 -->
      <el-alert 
        title="使用说明" 
        type="info" 
        :closable="false"
        style="margin-bottom: 20px;"
      >
        <template #default>
          <div style="line-height: 1.8;">
            <div>1. <strong>源链路模式</strong>和<strong>目标链路模式</strong>通常相同（约束发生在同一链路的不同单元类型之间）</div>
            <div>2. <strong>源单元类型</strong>和<strong>目标单元类型</strong>可以相同（如：调制单元内部参数之间的约束）</div>
            <div>3. <strong>参数选择</strong>：先选择单元类型，再从下拉列表选择参数（显示中文名称，自动填充参数代码）</div>
            <div>4. <strong>通配符(*)</strong>：表示适用于所有链路模式，特定链路的约束优先级应高于通配约束</div>
          </div>
        </template>
      </el-alert>
      
      <el-form ref="constraintRef" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="源链路模式" prop="sourceUnitName">
              <el-select 
                v-model="form.sourceUnitName" 
                placeholder="请选择源链路模式"
                filterable
                allow-create
                @change="handleSourceUnitNameChange"
              >
                <el-option label="通配符(*) - 适用所有链路" value="*" />
                <el-option 
                  v-for="name in linkModeOptions" 
                  :key="name" 
                  :label="name" 
                  :value="name" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="源单元类型" prop="sourceUnitType">
              <el-select 
                v-model="form.sourceUnitType" 
                placeholder="请选择"
                @change="handleSourceUnitTypeChange"
              >
                <el-option label="通配符(*) - 适用所有类型" value="*" />
                <el-option label="编码单元" value="ENCODE" />
                <el-option label="调制单元" value="MODULATE" />
                <el-option label="解调单元" value="DEMODULATE" />
                <el-option label="译码单元" value="DECODE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="源参数" prop="sourceParamName">
              <el-select 
                v-model="form.sourceParamName" 
                placeholder="请先选择源单元类型，然后选择参数"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="param in sourceParamOptions"
                  :key="param.paramCode"
                  :label="`${param.paramName} (${param.paramCode})`"
                  :value="param.paramCode"
                >
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span style="font-weight: 500;">{{ param.paramName }}</span>
                    <span style="color: #999; font-size: 12px; margin-left: 10px;">{{ param.paramCode }}</span>
                  </div>
                </el-option>
              </el-select>
              <div style="color: #999; font-size: 12px; margin-top: 4px;">
                提示：选择参数后会自动填充参数代码，显示格式为"参数名称 (参数代码)"
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider>
          <el-icon><ArrowDown /></el-icon>
          <span style="margin: 0 8px;">约束目标</span>
        </el-divider>
        <el-row>
          <el-col :span="12">
            <el-form-item label="目标链路模式" prop="targetUnitName">
              <el-select 
                v-model="form.targetUnitName" 
                placeholder="默认与源链路模式相同"
                filterable
                allow-create
              >
                <el-option label="通配符(*) - 适用所有链路" value="*" />
                <el-option 
                  v-for="name in linkModeOptions" 
                  :key="name" 
                  :label="name" 
                  :value="name" 
                />
              </el-select>
              <div style="color: #999; font-size: 12px; margin-top: 4px;">
                提示：通常与源链路模式相同（约束发生在同一链路内）
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标单元类型" prop="targetUnitType">
              <el-select 
                v-model="form.targetUnitType" 
                placeholder="请选择"
                @change="handleTargetUnitTypeChange"
              >
                <el-option label="通配符(*) - 适用所有类型" value="*" />
                <el-option label="编码单元" value="ENCODE" />
                <el-option label="调制单元" value="MODULATE" />
                <el-option label="解调单元" value="DEMODULATE" />
                <el-option label="译码单元" value="DECODE" />
              </el-select>
              <div style="color: #999; font-size: 12px; margin-top: 4px;">
                提示：可以与源单元类型相同（单元内部约束）
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="目标参数" prop="targetParamName">
              <el-select 
                v-model="form.targetParamName" 
                placeholder="请先选择目标单元类型，然后选择参数"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="param in targetParamOptions"
                  :key="param.paramCode"
                  :label="`${param.paramName} (${param.paramCode})`"
                  :value="param.paramCode"
                >
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span style="font-weight: 500;">{{ param.paramName }}</span>
                    <span style="color: #999; font-size: 12px; margin-left: 10px;">{{ param.paramCode }}</span>
                  </div>
                </el-option>
              </el-select>
              <div style="color: #999; font-size: 12px; margin-top: 4px;">
                提示：这是被约束的参数
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider>
          <el-icon><Setting /></el-icon>
          <span style="margin: 0 8px;">约束规则</span>
        </el-divider>
        <el-row>
          <el-col :span="24">
            <el-form-item label="适用模式类型" prop="modeTypes">
              <el-checkbox-group v-model="selectedModeTypes" @change="updateModeTypes">
                <el-checkbox label="KSA">KSA</el-checkbox>
                <el-checkbox label="KMA">KMA</el-checkbox>
                <el-checkbox label="SSA">SSA</el-checkbox>
                <el-checkbox label="基带数传">基带数传</el-checkbox>
              </el-checkbox-group>
              <div style="color: #999; font-size: 12px; margin-top: 4px;">
                提示：不勾选任何模式表示适用于所有模式类型。大部分约束在不同模式下相同，可以不勾选。
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="约束类型" prop="constraintType">
              <el-select v-model="form.constraintType" placeholder="请选择约束类型" @change="handleConstraintTypeChange">
                <el-option label="值范围约束" value="VALUE_RANGE">
                  <div>
                    <div style="font-weight: 500;">值范围约束</div>
                    <div style="color: #999; font-size: 12px;">限制目标参数的取值范围（如：0.5~0.8）</div>
                  </div>
                </el-option>
                <el-option label="固定值约束" value="FIXED_VALUE">
                  <div>
                    <div style="font-weight: 500;">固定值约束</div>
                    <div style="color: #999; font-size: 12px;">限制目标参数只能取某几个固定值</div>
                  </div>
                </el-option>
                <el-option label="公式计算约束" value="FORMULA_CALCULATE">
                  <div>
                    <div style="font-weight: 500;">公式计算约束</div>
                    <div style="color: #999; font-size: 12px;">根据公式自动计算目标参数值</div>
                  </div>
                </el-option>
                <el-option label="枚举限制约束" value="ENUM_LIMIT">
                  <div>
                    <div style="font-weight: 500;">枚举限制约束</div>
                    <div style="color: #999; font-size: 12px;">限制枚举参数可选的选项（其他置灰）</div>
                  </div>
                </el-option>
                <el-option label="控件禁用约束" value="CONTROL_DISABLE">
                  <div>
                    <div style="font-weight: 500;">控件禁用约束</div>
                    <div style="color: #999; font-size: 12px;">目标控件置灰不可操作</div>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-input-number v-model="form.priority" :min="0" :max="100" style="width: 100%" />
              <div style="color: #999; font-size: 12px; margin-top: 4px;">
                20+强制 | 10-19重要 | 1-9一般 | 0提示
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="约束条件" prop="constraintCondition">
              <!-- 如果源参数是枚举类型，提供下拉选择 -->
              <el-select 
                v-if="getSourceParamInfo()?.valueType === 'ENUM'"
                v-model="form.constraintCondition"
                placeholder="请选择条件"
                clearable
              >
                <el-option 
                  v-for="(label, value) in getSourceParamEnumOptions()"
                  :key="value"
                  :label="`== ${label} (${value})`"
                  :value="`==${value}`"
                />
              </el-select>
              <!-- 否则使用输入框 -->
              <el-input 
                v-else
                v-model="form.constraintCondition" 
                placeholder="如: ==1, >=10, <100"
              >
                <template #prepend>当源参数</template>
              </el-input>
              <div style="color: #999; font-size: 12px; margin-top: 4px;">
                <span v-if="getSourceParamInfo()">
                  源参数类型：{{ getValueTypeName(getSourceParamInfo().valueType) }}
                  <span v-if="getSourceParamInfo().minValue !== null">
                    | 范围：{{ getSourceParamInfo().minValue }} ~ {{ getSourceParamInfo().maxValue }}
                  </span>
                </span>
                <span v-else>
                  支持：==（等于）、!=（不等于）、&gt;、&lt;、&gt;=、&lt;=
                </span>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="约束值" prop="constraintValue">
              <!-- 1. 值范围约束 -->
              <div v-if="form.constraintType === 'VALUE_RANGE'" class="range-input">
                <el-input-number 
                  v-model="rangeMin" 
                  :min="getTargetParamInfo()?.minValue"
                  :max="getTargetParamInfo()?.maxValue"
                  :precision="getTargetParamInfo()?.valueType === 'FLOAT' ? 2 : 0"
                  placeholder="最小值"
                  style="width: 45%"
                  @change="updateRangeValue"
                />
                <span style="margin: 0 10px;">~</span>
                <el-input-number 
                  v-model="rangeMax" 
                  :min="getTargetParamInfo()?.minValue"
                  :max="getTargetParamInfo()?.maxValue"
                  :precision="getTargetParamInfo()?.valueType === 'FLOAT' ? 2 : 0"
                  placeholder="最大值"
                  style="width: 45%"
                  @change="updateRangeValue"
                />
              </div>
              
              <!-- 2. 固定值约束（多个固定值） -->
              <div v-else-if="form.constraintType === 'FIXED_VALUE'">
                <el-select 
                  v-if="getTargetParamInfo()?.valueType === 'ENUM'"
                  v-model="selectedFixedValues"
                  multiple
                  placeholder="选择允许的固定值"
                  style="width: 100%"
                  @change="updateFixedValue"
                >
                  <el-option
                    v-for="(label, value) in getTargetParamEnumOptions()"
                    :key="value"
                    :label="`${label} (${value})`"
                    :value="value"
                  />
                </el-select>
                <el-input 
                  v-else
                  v-model="form.constraintValue" 
                  placeholder="输入固定值，多个用逗号分隔（如：0.5,0.67,0.75）"
                />
              </div>
              
              <!-- 3. 公式计算约束 -->
              <div v-else-if="form.constraintType === 'FORMULA_CALCULATE'">
                <el-input 
                  v-model="form.constraintValue" 
                  placeholder="输入计算公式（如：source * 2, source + 10）"
                >
                  <template #prepend>公式</template>
                </el-input>
                <div style="color: #e6a23c; font-size: 12px; margin-top: 4px; line-height: 1.5;">
                  <div>支持的变量：source（源参数值）</div>
                  <div>支持的运算：+ - * / ( )</div>
                  <div>示例：source * 2 | source + 10 | (source - 1) * 0.5</div>
                </div>
              </div>
              
              <!-- 4. 枚举限制约束 -->
              <el-select 
                v-else-if="form.constraintType === 'ENUM_LIMIT'"
                v-model="selectedEnumValues"
                multiple
                placeholder="选择允许的枚举值（未选的将置灰）"
                style="width: 100%"
                @change="updateEnumValue"
              >
                <el-option
                  v-for="(label, value) in getTargetParamEnumOptions()"
                  :key="value"
                  :label="`${label} (${value})`"
                  :value="value"
                />
              </el-select>
              
              <!-- 5. 控件禁用约束 -->
              <el-input 
                v-else-if="form.constraintType === 'CONTROL_DISABLE'"
                v-model="form.constraintValue" 
                placeholder="留空即可（满足条件时目标控件将置灰）"
                disabled
              />
              
              <!-- 其他类型 -->
              <el-input 
                v-else
                v-model="form.constraintValue" 
                placeholder="根据约束类型填写"
              />
              
              <div style="color: #999; font-size: 12px; margin-top: 4px;">
                <span v-if="getTargetParamInfo()">
                  目标参数：{{ getTargetParamInfo().paramName }} | 
                  类型：{{ getValueTypeName(getTargetParamInfo().valueType) }}
                  <span v-if="getTargetParamInfo().minValue !== null">
                    | 范围：{{ getTargetParamInfo().minValue }} ~ {{ getTargetParamInfo().maxValue }}
                  </span>
                </span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="错误提示" prop="errorMessage">
          <el-input 
            v-model="form.errorMessage" 
            type="textarea" 
            :rows="2"
            placeholder="请输入验证失败时显示的错误信息"
          />
          <div style="color: #999; font-size: 12px; margin-top: 4px;">
            示例：卷积码的码率范围应为0.5~0.8
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
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
  </div>
</template>

<script setup name="ParamConstraint">
import { listParamConstraint, getParamConstraint, delParamConstraint, addParamConstraint, updateParamConstraint } from "@/api/system/baseband/paramConstraint"
import { listBasebandParamDef } from "@/api/system/baseband/paramDef"

const { proxy } = getCurrentInstance()

const constraintList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

// 参数定义列表（用于选择器）
const paramDefList = ref([])
const sourceParamOptions = ref([])
const targetParamOptions = ref([])

// 链路模式选项
const linkModeOptions = ref([
  '返向中低速数传',
  '返向高速数传',
  'ACM数传',
  '前向数传小环',
  '低速模拟源',
  '高速模拟源',
  'ACM模拟源',
  '前向数传'
])

// 约束值辅助变量
const rangeMin = ref(null)
const rangeMax = ref(null)
const selectedEnumValues = ref([])
const selectedFixedValues = ref([])

// 模式类型辅助变量
const selectedModeTypes = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    sourceUnitName: null,
    sourceUnitType: null,
    constraintType: null,
    status: null
  },
  rules: {
    sourceUnitName: [
      { required: true, message: "源链路模式不能为空", trigger: "blur" }
    ],
    sourceUnitType: [
      { required: true, message: "源单元类型不能为空", trigger: "change" }
    ],
    sourceParamName: [
      { required: true, message: "源参数名称不能为空", trigger: "blur" }
    ],
    targetUnitName: [
      { required: true, message: "目标链路模式不能为空", trigger: "blur" }
    ],
    targetUnitType: [
      { required: true, message: "目标单元类型不能为空", trigger: "change" }
    ],
    targetParamName: [
      { required: true, message: "目标参数名称不能为空", trigger: "blur" }
    ],
    constraintType: [
      { required: true, message: "约束类型不能为空", trigger: "change" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 获取单元类型名称 */
function getUnitTypeName(unitType) {
  if (unitType === '*') return '通配'
  const typeMap = {
    'ENCODE': '编码',
    'MODULATE': '调制',
    'DEMODULATE': '解调',
    'DECODE': '译码'
  }
  return typeMap[unitType] || unitType
}

/** 获取约束类型名称 */
function getConstraintTypeName(type) {
  const typeMap = {
    'VALUE_RANGE': '值范围',
    'FIXED_VALUE': '固定值',
    'FORMULA_CALCULATE': '公式计算',
    'ENUM_LIMIT': '枚举限制',
    'CONTROL_DISABLE': '控件禁用'
  }
  return typeMap[type] || type
}

/** 获取约束类型标签 */
function getConstraintTypeTag(type) {
  const tagMap = {
    'VALUE_RANGE': 'success',
    'FIXED_VALUE': 'primary',
    'FORMULA_CALCULATE': 'warning',
    'ENUM_LIMIT': 'info',
    'CONTROL_DISABLE': 'danger'
  }
  return tagMap[type] || ''
}

/** 获取参数中文名称 */
function getParamNameCn(paramCode) {
  if (!paramCode) return '-'
  
  // 从参数定义列表中查找
  const param = paramDefList.value.find(p => p.paramCode === paramCode)
  return param ? param.paramName : paramCode
}

/** 获取源参数信息 */
function getSourceParamInfo() {
  if (!form.value.sourceParamName) return null
  return paramDefList.value.find(p => p.paramCode === form.value.sourceParamName)
}

/** 获取目标参数信息 */
function getTargetParamInfo() {
  if (!form.value.targetParamName) return null
  return paramDefList.value.find(p => p.paramCode === form.value.targetParamName)
}

/** 获取源参数的枚举选项 */
function getSourceParamEnumOptions() {
  const param = getSourceParamInfo()
  if (!param || !param.enumOptions) return {}
  try {
    return JSON.parse(param.enumOptions)
  } catch (e) {
    return {}
  }
}

/** 获取目标参数的枚举选项 */
function getTargetParamEnumOptions() {
  const param = getTargetParamInfo()
  if (!param || !param.enumOptions) return {}
  try {
    return JSON.parse(param.enumOptions)
  } catch (e) {
    return {}
  }
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

/** 约束类型变化处理 */
function handleConstraintTypeChange() {
  // 清空约束值
  form.value.constraintValue = null
  rangeMin.value = null
  rangeMax.value = null
  selectedEnumValues.value = []
  selectedFixedValues.value = []
}

/** 更新约束值（范围） */
function updateRangeValue() {
  if (rangeMin.value !== null && rangeMax.value !== null) {
    form.value.constraintValue = `${rangeMin.value},${rangeMax.value}`
  }
}

/** 更新约束值（固定值） */
function updateFixedValue() {
  form.value.constraintValue = selectedFixedValues.value.join(',')
}

/** 更新约束值（枚举限制） */
function updateEnumValue() {
  form.value.constraintValue = selectedEnumValues.value.join(',')
}

/** 更新模式类型 */
function updateModeTypes() {
  form.value.modeTypes = selectedModeTypes.value.length > 0 ? selectedModeTypes.value.join(',') : null
}

/** 查询参数定义列表 */
function getParamDefList() {
  listBasebandParamDef({ pageNum: 1, pageSize: 1000 }).then(response => {
    paramDefList.value = response.rows || []
  })
}

/** 根据单元类型筛选参数 */
function filterParamsByUnitType(unitType) {
  if (!unitType || unitType === '*') {
    return paramDefList.value
  }
  return paramDefList.value.filter(p => p.unitType === unitType)
}

/** 源单元类型变化 */
function handleSourceUnitTypeChange() {
  sourceParamOptions.value = filterParamsByUnitType(form.value.sourceUnitType)
  // 清空已选参数
  form.value.sourceParamName = null
}

/** 目标单元类型变化 */
function handleTargetUnitTypeChange() {
  targetParamOptions.value = filterParamsByUnitType(form.value.targetUnitType)
  // 清空已选参数
  form.value.targetParamName = null
}

/** 源链路模式变化 - 自动同步到目标链路模式 */
function handleSourceUnitNameChange() {
  // 自动同步到目标链路模式
  form.value.targetUnitName = form.value.sourceUnitName
}

/** 查询约束列表 */
function getList() {
  loading.value = true
  listParamConstraint(queryParams.value).then(response => {
    constraintList.value = response.rows
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
    constraintId: null,
    sourceUnitName: null,
    sourceUnitType: null,
    sourceParamName: null,
    targetUnitName: null,
    targetUnitType: null,
    targetParamName: null,
    constraintType: null,
    constraintCondition: null,
    constraintValue: null,
    modeTypes: null,
    priority: 10,
    errorMessage: null,
    status: "0",
    remark: null
  }
  // 重置辅助变量
  rangeMin.value = null
  rangeMax.value = null
  selectedEnumValues.value = []
  selectedFixedValues.value = []
  selectedModeTypes.value = []
  proxy.resetForm("constraintRef")
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
  ids.value = selection.map(item => item.constraintId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  getParamDefList()
  open.value = true
  title.value = "添加参数约束"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  getParamDefList()
  const _constraintId = row.constraintId || ids.value
  getParamConstraint(_constraintId).then(response => {
    form.value = response.data
    // 加载参数选项
    sourceParamOptions.value = filterParamsByUnitType(form.value.sourceUnitType)
    targetParamOptions.value = filterParamsByUnitType(form.value.targetUnitType)
    
    // 解析约束值
    if (form.value.constraintType === 'VALUE_RANGE' && form.value.constraintValue) {
      const values = form.value.constraintValue.split(',')
      if (values.length === 2) {
        rangeMin.value = parseFloat(values[0])
        rangeMax.value = parseFloat(values[1])
      }
    } else if (form.value.constraintType === 'FIXED_VALUE' && form.value.constraintValue) {
      selectedFixedValues.value = form.value.constraintValue.split(',')
    } else if (form.value.constraintType === 'ENUM_LIMIT' && form.value.constraintValue) {
      selectedEnumValues.value = form.value.constraintValue.split(',')
    }
    
    // 解析模式类型
    if (form.value.modeTypes) {
      selectedModeTypes.value = form.value.modeTypes.split(',')
    } else {
      selectedModeTypes.value = []
    }
    
    open.value = true
    title.value = "修改参数约束"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["constraintRef"].validate(valid => {
    if (valid) {
      if (form.value.constraintId != null) {
        updateParamConstraint(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addParamConstraint(form.value).then(response => {
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
  const _constraintIds = row.constraintId || ids.value
  proxy.$modal.confirm('是否确认删除约束编号为"' + _constraintIds + '"的数据项？').then(function() {
    return delParamConstraint(_constraintIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 状态修改 */
function handleStatusChange(row) {
  let text = row.status === "0" ? "启用" : "停用"
  proxy.$modal.confirm('确认要"' + text + '""' + row.constraintId + '"约束吗？').then(function() {
    return updateParamConstraint(row)
  }).then(() => {
    proxy.$modal.msgSuccess(text + "成功")
  }).catch(function() {
    row.status = row.status === "0" ? "1" : "0"
  })
}

getList()
getParamDefList()
</script>

<style scoped>
.unit-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
}

.unit-type {
  font-size: 12px;
  color: #666;
}

.param-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  align-items: center;
}

.param-name-cn {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.param-code {
  font-size: 11px;
  color: #999;
  font-family: 'Courier New', monospace;
}

.range-input {
  display: flex;
  align-items: center;
  width: 100%;
}
</style>
