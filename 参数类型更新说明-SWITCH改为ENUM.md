# 参数类型更新说明：SWITCH → ENUM

## 更新概述

将参数定义中的**"开关"(SWITCH)**类型统一改为**"枚举"(ENUM)**类型，使用下拉选择器代替开关控件。

## 更新原因

### 1. 统一界面风格
- 所有参数使用统一的选择器控件
- 避免多种控件类型混用

### 2. 更灵活的配置
- 枚举类型可以支持多个选项（不限于开/关）
- 可以自定义选项文字
- 便于扩展

### 3. 更好的一致性
- 与其他枚举参数保持一致
- 减少特殊处理逻辑

## 已完成的更新

### 1. 前端界面

#### 参数定义界面
**文件**：`RuoYi-Vue3/src/views/system/baseband/paramDef/index.vue`

- ✅ 删除"开关"类型选项
- ✅ 只保留：枚举、无符号整数、浮点

**更新前**：
```vue
<el-option label="枚举" value="ENUM" />
<el-option label="无符号整数" value="UINT" />
<el-option label="浮点" value="FLOAT" />
<el-option label="开关" value="SWITCH" />  ← 删除
```

**更新后**：
```vue
<el-option label="枚举" value="ENUM" />
<el-option label="无符号整数" value="UINT" />
<el-option label="浮点" value="FLOAT" />
```

#### 参数配置界面
**文件**：`RuoYi-Vue3/src/views/system/baseband/paramValue/index.vue`

- ✅ 删除开关控件代码
- ✅ 删除SWITCH类型的样式
- ✅ 删除类型映射中的SWITCH

**更新前**：
```vue
<div v-else-if="param.valueType === 'SWITCH'" class="control-switch">
  <el-switch
    v-model="param.rawValue"
    active-text="开"
    inactive-text="关"
  />
</div>
```

**更新后**：
```
（删除，SWITCH类型的参数会自动使用ENUM的下拉选择器）
```

### 2. 数据库

#### 更新SQL
**文件**：
- `update_switch_to_enum_simple.sql` - 简化版（推荐）
- `update_switch_to_enum.sql` - 完整版
- `update_switch_to_enum.bat` - 批处理脚本

**更新内容**：
```sql
-- 将SWITCH类型改为ENUM类型
UPDATE sys_baseband_param_def
SET 
    value_type = 'ENUM',
    enum_options = '{"0": "关", "1": "开"}'
WHERE value_type = 'SWITCH';
```

## 部署步骤

### 方法1：使用批处理脚本（推荐）

1. 双击运行：`RuoYi-Vue/sql/update_switch_to_enum.bat`
2. 输入数据库密码
3. 等待执行完成

### 方法2：手动执行SQL

```bash
cd RuoYi-Vue/sql
mysql -u root -p ry-vue < update_switch_to_enum_simple.sql
```

### 方法3：使用数据库工具

1. 打开Navicat/DBeaver/MySQL Workbench
2. 连接到数据库
3. 打开并执行：`update_switch_to_enum_simple.sql`

### 验证

执行以下SQL检查更新结果：

```sql
-- 检查是否还有SWITCH类型
SELECT * FROM sys_baseband_param_def WHERE value_type = 'SWITCH';
-- 应该返回空结果

-- 查看已更新的参数
SELECT param_code, param_name, value_type, enum_options
FROM sys_baseband_param_def
WHERE enum_options LIKE '%关%';
```

## 界面效果对比

### 参数定义界面

**更新前**：
```
值类型：[枚举 ▼] [无符号整数] [浮点] [开关]
```

**更新后**：
```
值类型：[枚举 ▼] [无符号整数] [浮点]
```

### 参数配置界面

**更新前（开关控件）**：
```
参数名称：ACM使能
控件：[○────●] 开
```

**更新后（下拉选择器）**：
```
参数名称：ACM使能
控件：[开 ▼]
选项：关 / 开
```

## 枚举选项格式

### 标准格式
```json
{
  "0": "关",
  "1": "开"
}
```

### 自定义格式
如果你的开关参数使用其他值，可以自定义：

```json
{
  "false": "禁用",
  "true": "启用"
}
```

或

```json
{
  "0": "否",
  "1": "是"
}
```

## 数据兼容性

### 参数值不受影响
- 已有的参数值（0或1）完全兼容
- 不需要修改参数值数据
- 只是显示方式从开关变为下拉选择

### 约束规则兼容
- 约束验证逻辑不变
- 仍然可以使用 `==0` 或 `==1` 作为约束条件

## 优势总结

### 1. 界面统一
- ✅ 所有参数使用统一的选择器
- ✅ 减少控件类型，界面更简洁

### 2. 功能增强
- ✅ 可以支持多个选项（不限于2个）
- ✅ 可以自定义选项文字
- ✅ 便于扩展新的选项

### 3. 维护简化
- ✅ 减少特殊处理代码
- ✅ 统一的数据结构
- ✅ 更容易理解和维护

## 示例场景

### 场景1：标准开关
```
参数：ACM使能
类型：ENUM
选项：{"0": "关", "1": "开"}
默认值：0
```

### 场景2：是否选择
```
参数：启用环回
类型：ENUM
选项：{"0": "否", "1": "是"}
默认值：0
```

### 场景3：多选项扩展
```
参数：工作模式
类型：ENUM
选项：{"0": "关闭", "1": "正常", "2": "测试"}
默认值：0
```

## 注意事项

### 1. 前端已自动更新
- 刷新浏览器即可看到新界面
- 无需重启应用

### 2. 数据库需要更新
- 必须执行SQL更新脚本
- 将SWITCH类型改为ENUM类型
- 添加枚举选项

### 3. 已有数据兼容
- 参数值不需要修改
- 约束规则不需要修改
- 完全向后兼容

### 4. 新增参数
- 不再使用SWITCH类型
- 统一使用ENUM类型
- 设置枚举选项：`{"0": "关", "1": "开"}`

## 常见问题

### Q1：更新后开关参数不显示了？

**A**：需要执行数据库更新SQL，将SWITCH类型改为ENUM类型。

### Q2：可以恢复开关控件吗？

**A**：不建议。统一使用枚举类型更好维护。如果确实需要，可以回退代码。

### Q3：已有的参数值会丢失吗？

**A**：不会。参数值（0或1）完全兼容，只是显示方式变了。

### Q4：如何添加新的开关参数？

**A**：
1. 类型选择：枚举
2. 枚举选项：`{"0": "关", "1": "开"}`
3. 最小值：0
4. 最大值：1
5. 默认值：0

### Q5：可以自定义开关文字吗？

**A**：可以！修改枚举选项即可，例如：
- `{"0": "禁用", "1": "启用"}`
- `{"0": "否", "1": "是"}`
- `{"0": "关闭", "1": "打开"}`

## 文件清单

### SQL文件
- ✅ `update_switch_to_enum_simple.sql` - 简化版更新脚本
- ✅ `update_switch_to_enum.sql` - 完整版更新脚本
- ✅ `update_switch_to_enum.bat` - 批处理脚本

### 前端文件（已更新）
- ✅ `RuoYi-Vue3/src/views/system/baseband/paramDef/index.vue`
- ✅ `RuoYi-Vue3/src/views/system/baseband/paramValue/index.vue`

### 文档
- ✅ `参数类型更新说明-SWITCH改为ENUM.md` - 本文档

## 总结

✅ 前端已完成更新
✅ 删除开关控件代码
✅ 统一使用枚举类型
✅ 提供数据库更新脚本
✅ 完全向后兼容

**下一步**：
1. 执行数据库更新SQL
2. 刷新浏览器
3. 测试参数配置功能

现在系统中不再有"开关"类型，统一使用更灵活的"枚举"类型！
