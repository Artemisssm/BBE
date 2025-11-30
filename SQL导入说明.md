# SQL导入问题解决方案

## 问题原因

错误信息：`Unknown column 'param_id' in 'field list'`

这是因为执行 `baseband_params_enhanced.sql` 时，它只执行了 `TRUNCATE TABLE` 清空数据，但没有重建表结构。如果表结构有问题或被删除，就会出现字段不存在的错误。

## 解决方案

### 方案1：重新创建完整表结构（推荐）

执行以下SQL：

```sql
-- 1. 删除旧表
DROP TABLE IF EXISTS sys_baseband_param_def;

-- 2. 重新创建表
CREATE TABLE sys_baseband_param_def (
    param_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '参数定义主键',
    unit_type      VARCHAR(16)  NOT NULL COMMENT '适用单元类型',
    param_code     VARCHAR(64)  NOT NULL COMMENT '唯一编码',
    param_name     VARCHAR(64)  NOT NULL COMMENT '显示名称',
    value_type     VARCHAR(16)  NOT NULL COMMENT 'ENUM/UINT/FLOAT/SWITCH',
    enum_options   TEXT         DEFAULT NULL COMMENT '枚举选项 JSON',
    min_value      DECIMAL(18,6) DEFAULT NULL,
    max_value      DECIMAL(18,6) DEFAULT NULL,
    scale_factor   INT          DEFAULT 1 COMMENT '浮点转无符号整数时的缩放倍数',
    bit_length     INT          DEFAULT 32 COMMENT '硬件位宽',
    hardware_order INT          NOT NULL COMMENT '硬件顺序字段',
    default_value  VARCHAR(64)  DEFAULT NULL,
    remark         VARCHAR(255) DEFAULT NULL,
    create_time    DATETIME     DEFAULT NULL,
    PRIMARY KEY (param_id),
    UNIQUE KEY uk_param_code (param_code),
    KEY idx_unit_type (unit_type),
    KEY idx_hardware_order (hardware_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基带参数定义';

-- 3. 导入参数数据
-- 执行 baseband_params_enhanced.sql 中的 INSERT 语句
```

### 方案2：使用原始SQL重建（最简单）

```bash
# 重新执行原始的表结构创建SQL
mysql -u root -p ry-vue < RuoYi-Vue/sql/baseband_baseband_monitor.sql

# 然后导入新参数
mysql -u root -p ry-vue < RuoYi-Vue/sql/baseband_params_enhanced.sql
```

### 方案3：手动分步执行

1. **检查表结构**
```sql
DESCRIBE sys_baseband_param_def;
```

2. **如果表不存在或结构错误，重新创建**
```sql
-- 执行 baseband_baseband_monitor.sql 中的 CREATE TABLE 语句
```

3. **导入参数数据**
```sql
-- 执行 baseband_params_enhanced.sql
```

## 快速修复命令

在MySQL命令行中执行：

```sql
-- 使用数据库
USE ry-vue;

-- 删除并重建表
SOURCE D:/Desktop/Work/Code/BBE/RuoYi-Vue/sql/baseband_baseband_monitor.sql;

-- 导入新参数（会清空旧数据）
SOURCE D:/Desktop/Work/Code/BBE/RuoYi-Vue/sql/baseband_params_enhanced.sql;

-- 验证导入
SELECT unit_type, COUNT(*) FROM sys_baseband_param_def GROUP BY unit_type;
```

## 预期结果

执行成功后应该看到：

```
+-------------+-------+
| unit_type   | count |
+-------------+-------+
| DECODE      |    32 |
| DEMODULATE  |    40 |
| ENCODE      |    30 |
| MODULATE    |    35 |
+-------------+-------+
4 rows in set

总计：137个参数
```

## 注意事项

1. **enum_options字段类型**：从 `JSON` 改为 `TEXT`，因为某些MySQL版本不支持JSON类型
2. **字符集**：确保使用 `utf8mb4` 字符集
3. **索引**：添加了 `unit_type` 和 `hardware_order` 索引以提高查询性能
4. **唯一约束**：`param_code` 字段有唯一约束，避免重复

## 故障排查

### 如果还是报错

1. **检查MySQL版本**
```sql
SELECT VERSION();
```

2. **检查表是否存在**
```sql
SHOW TABLES LIKE 'sys_baseband%';
```

3. **检查表结构**
```sql
SHOW CREATE TABLE sys_baseband_param_def;
```

4. **查看错误日志**
```bash
# 查看MySQL错误日志
tail -f /var/log/mysql/error.log
```

## 重启后端服务

导入成功后，重启后端服务：

```bash
# 停止服务
# 重新启动 RuoYiApplication

# 或者使用Maven
cd RuoYi-Vue/ruoyi-admin
mvn spring-boot:run
```

## 验证功能

1. 清除浏览器缓存
2. 重新登录系统
3. 访问"系统管理 > 基带监控 > 参数定义"
4. 应该能看到137个参数，按单元类型分组显示

## 成功标志

- ✅ 表结构正确创建
- ✅ 137个参数全部导入
- ✅ 前端页面正常显示
- ✅ 参数配置功能正常

如果还有问题，请提供完整的错误信息！
