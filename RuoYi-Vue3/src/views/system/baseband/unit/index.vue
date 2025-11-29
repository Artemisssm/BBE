<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="单元名称" prop="unitName">
        <el-input
          v-model="queryParams.unitName"
          placeholder="请输入单元名称"
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

    <el-table v-loading="loading" :data="unitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="单元ID" align="center" prop="unitId" width="80" />
      <el-table-column label="单元名称" align="center" prop="unitName" :show-overflow-tooltip="true" />
      <el-table-column label="单元类型" align="center" prop="unitType" width="100">
        <template #default="scope">
          <span v-if="scope.row.unitType === 'ENCODE'">编码</span>
          <span v-else-if="scope.row.unitType === 'MODULATE'">调制</span>
          <span v-else-if="scope.row.unitType === 'DEMODULATE'">解调</span>
          <span v-else-if="scope.row.unitType === 'DECODE'">译码</span>
          <span v-else>{{ scope.row.unitType }}</span>
        </template>
      </el-table-column>
      <el-table-column label="通道号" align="center" prop="channelNo" width="100" />
      <el-table-column label="版本" align="center" prop="version" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:basebandUnit:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:basebandUnit:remove']">删除</el-button>
          <el-button link type="primary" icon="Setting" @click="handleConfig(scope.row)" v-hasPermi="['system:basebandValue:list']">参数配置</el-button>
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

    <!-- 添加或修改基带单元对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="unitRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="单元名称" prop="unitName">
              <el-input v-model="form.unitName" placeholder="请输入单元名称" maxlength="64" />
            </el-form-item>
          </el-col>
        </el-row>
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
            <el-form-item label="通道号" prop="channelNo">
              <el-input-number v-model="form.channelNo" controls-position="right" :min="1" style="width: 100%" />
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

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    unitName: undefined,
    unitType: undefined,
    status: undefined
  },
  rules: {
    unitName: [
      { required: true, message: "单元名称不能为空", trigger: "blur" },
      { max: 64, message: "单元名称不能超过64个字符", trigger: "blur" }
    ],
    unitType: [{ required: true, message: "单元类型不能为空", trigger: "change" }],
    channelNo: [{ required: true, message: "通道号不能为空", trigger: "blur" }],
    version: [{ max: 32, message: "版本信息不能超过32个字符", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询基带单元列表 */
function getList() {
  loading.value = true
  listBasebandUnit(queryParams.value).then(response => {
    unitList.value = response.rows
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
    unitId: undefined,
    unitName: undefined,
    unitType: undefined,
    channelNo: 1,
    status: "0",
    version: undefined,
    remark: undefined
  }
  proxy.resetForm("unitRef")
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
  open.value = true
  title.value = "添加基带单元"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const unitId = row.unitId || ids.value[0]
  getBasebandUnit(unitId).then(response => {
    form.value = response.data
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
        updateBasebandUnit(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addBasebandUnit(form.value).then(response => {
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
