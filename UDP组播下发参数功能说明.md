# UDP组播下发参数功能说明

## 功能概述

基带单元参数配置完成后，可以通过UDP组播方式将参数下发到网络上。硬件设备监听组播地址即可接收参数数据。

## 帧结构

```
+--------+--------+--------+--------+--------+--------+--------+
| 帧头   | 单元类型| 单元ID | 参数数量| 参数数据...      | CRC16  |
| 2字节  | 1字节  | 4字节  | 2字节  | N字节           | 2字节  |
+--------+--------+--------+--------+--------+--------+--------+
```

### 字段说明

| 字段 | 长度 | 说明 |
|------|------|------|
| 帧头 | 2字节 | 固定值 `0xBB66` |
| 单元类型 | 1字节 | `0x00`=编码, `0x01`=调制, `0x02`=解调, `0x03`=译码 |
| 单元ID | 4字节 | 无符号整数，大端序 |
| 参数数量 | 2字节 | 无符号整数，大端序 |
| 参数数据 | N字节 | 按 `sortOrder` 顺序排列，每个参数根据 `bitWidthType` 占用不同字节数 |
| CRC16 | 2字节 | Modbus CRC16校验 |

### 参数数据格式

参数按照 `sortOrder` 字段排序后依次写入，每个参数根据 `bitWidthType` 占用不同字节数：

| bitWidthType | 字节数 | 说明 |
|--------------|--------|------|
| U8 / I8 | 1字节 | 8位整数 |
| U16 / I16 | 2字节 | 16位整数，大端序 |
| U32 / I32 | 4字节 | 32位整数，大端序（默认） |

## 配置参数

在 `application.yml` 中配置组播参数：

```yaml
baseband:
  multicast:
    address: 239.255.0.1    # 组播地址
    port: 9000              # 组播端口
    ttl: 16                 # TTL值
    netIf: eth0             # 网络接口名称（可选）
```

## 日志输出示例

下发参数时会在后端日志中打印详细的帧结构：

```
========== 开始下发参数 ==========
单元ID: 100, 类型: DECODE, 链路模式: 返向中低速数传
参数数量: 5
---------- 参数列表 ----------
[1] 帧头 (FRAME_HEADER) = 452984861 [U32]
[2] 码速率 (CODE_RATE) = 2 [U8]
[3] 编码方式 (ENCODE_TYPE) = 1 [U8]
[4] 交织深度 (INTERLEAVE_DEPTH) = 4 [U16]
[5] 同步字 (SYNC_WORD) = 65535 [U16]
---------- 帧结构 ----------
帧长度: 21 字节
帧数据(HEX): BB 66 03 00 00 00 64 00 05 1A FC FC 1D 02 01 00 04 FF FF XX XX
帧数据(格式化):
  [0000-0001] 帧头:     BB 66 (0xBB66)
  [0002]      单元类型: 03 (译码)
  [0003-0006] 单元ID:   00 00 00 64 (100)
  [0007-0008] 参数数量: 00 05 (5)
  ---------- 参数数据 ----------
  [0009-0012] 帧头: 1A FC FC 1D (452984861) [U32]
  [0013]      码速率: 02 (2) [U8]
  [0014]      编码方式: 01 (1) [U8]
  [0015-0016] 交织深度: 00 04 (4) [U16]
  [0017-0018] 同步字: FF FF (65535) [U16]
  ---------- 校验 ----------
  [0019-0020] CRC16:    XX XX
---------- 发送成功 ----------
目标地址: 239.255.0.1:9000
========== 下发完成 ==========
```

## 使用方式

### 前端操作
1. 进入：基带监控 > 基带单元
2. 选择一个单元，点击"配置"
3. 配置参数值
4. 点击"下发参数"按钮

### API接口
```
POST /system/baseband/value/{unitId}/dispatch
```

## 接收端示例（Python）

```python
import socket
import struct

# 组播配置
MCAST_GRP = '239.255.0.1'
MCAST_PORT = 9000

# 创建UDP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
sock.bind(('', MCAST_PORT))

# 加入组播组
mreq = struct.pack("4sl", socket.inet_aton(MCAST_GRP), socket.INADDR_ANY)
sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)

print(f"监听组播 {MCAST_GRP}:{MCAST_PORT}")

while True:
    data, addr = sock.recvfrom(1024)
    print(f"收到数据 ({len(data)} 字节) 来自 {addr}")
    print(f"HEX: {data.hex(' ').upper()}")
    
    # 解析帧头
    if len(data) >= 9:
        header = struct.unpack('>H', data[0:2])[0]
        unit_type = data[2]
        unit_id = struct.unpack('>I', data[3:7])[0]
        param_count = struct.unpack('>H', data[7:9])[0]
        
        print(f"帧头: 0x{header:04X}")
        print(f"单元类型: {unit_type}")
        print(f"单元ID: {unit_id}")
        print(f"参数数量: {param_count}")
```

## 相关文件

### 后端
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/BasebandDispatchServiceImpl.java` - 下发服务实现
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/config/BasebandMulticastProperties.java` - 组播配置
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/BasebandParamVo.java` - 参数VO

### 前端
- `RuoYi-Vue3/src/views/system/baseband/paramValue/index.vue` - 参数配置页面

### 数据库
- `sys_baseband_dispatch_log` - 下发日志表

---

**创建日期**：2025-12-21  
**状态**：✅ 已完成
