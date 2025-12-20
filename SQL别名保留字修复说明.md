# SQL别名保留字修复说明

## 🐛 问题
点击"配置参数"报错：
```
You have an error in your SQL syntax... near 'maxValue'
```

## ✅ 根本原因
`maxValue` 和 `defaultValue` 在MySQL中是保留关键字（用于分区表），当作为列别名使用时会导致SQL语法错误。

## 🔧 解决方案
为别名添加反引号，告诉MySQL这是标识符而不是关键字：

**修改前：**
```sql
pd.max_value AS maxValue,
pd.default_value AS defaultValue
```

**修改后：**
```sql
pd.max_value AS `maxValue`,
pd.default_value AS `defaultValue`
```

## 📁 修改文件
`RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysBasebandMacroParamMapper.xml`

## 🚀 使后生效

### 方法1：使用批处理脚本
```bash
重启后端服务.bat
```

### 方法2：手动重启
```bash
cd RuoYi-Vue/ruoyi-admin
mvn clean package -DskipTests
# 然后在IDE中重新运行 RuoYiApplication.java
```

### 方法3：仅重新编译（如果配置了热部署）
```bash
cd RuoYi-Vue
mvn compile
# 等待自动重启
```

## ✅ 验证修复
1. 重启后端服务
2. 登录系统
3. 进入：基带监控 > 宏管理
4. 点击任意宏的"配置参数"按钮
5. 应该能正常打开参数配置对话框

## 📝 MySQL保留关键字说明

以下是常见的MySQL保留关键字，在SQL中作为标识符使用时需要加反引号：

| 关键字 | 用途 | 示例 |
|--------|------|------|
| `maxvalue` | 分区表最大值 | `AS \`maxValue\`` |
| `default` | 默认值 | `AS \`defaultValue\`` |
| `key` | 索引 | `AS \`key\`` |
| `index` | 索引 | `AS \`index\`` |
| `order` | 排序 | `AS \`order\`` |
| `group` | 分组 | `AS \`group\`` |
| `select` | 查询 | `AS \`select\`` |
| `from` | 来源 | `AS \`from\`` |

## 💡 最佳实践

### 1. 避免使用保留关键字作为别名
**推荐：**
```sql
pd.max_value AS max_val,
pd.default_value AS def_val
```

### 2. 使用下划线命名而不是驼峰
**推荐：**
```sql
pd.max_value AS max_value,
pd.default_value AS default_value
```

### 3. 如果必须使用驼峰，加反引号
**可接受：**
```sql
pd.max_value AS `maxValue`,
pd.default_value AS `defaultValue`
```

## 🔍 相关错误模式

如果遇到类似错误：
```
You have an error in your SQL syntax... near 'xxxxx'
```

检查步骤：
1. 确认 `xxxxx` 是否是MySQL保留关键字
2. 检查是否用作列名、表名或别名
3. 添加反引号：`` `xxxxx` ``
4. 或者更改为非保留关键字的名称

## 📞 参考资料
- MySQL保留关键字列表：https://dev.mysql.com/doc/refman/8.0/en/keywords.html
- MyBatis XML映射文件：https://mybatis.org/mybatis-3/zh/sqlmap-xml.html

---

**修复日期**：2025-12-09  
**修复人员**：Kiro AI  
**状态**：✅ 已修复，等待重启验证
