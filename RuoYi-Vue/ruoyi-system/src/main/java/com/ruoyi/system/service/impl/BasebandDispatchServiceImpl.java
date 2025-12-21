package com.ruoyi.system.service.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.config.BasebandMulticastProperties;
import com.ruoyi.system.domain.SysBasebandDispatchLog;
import com.ruoyi.system.domain.SysBasebandUnit;
import com.ruoyi.system.domain.vo.BasebandParamVo;
import com.ruoyi.system.mapper.SysBasebandDispatchLogMapper;
import com.ruoyi.system.mapper.SysBasebandParamValueMapper;
import com.ruoyi.system.mapper.SysBasebandUnitMapper;
import com.ruoyi.system.service.IBasebandDispatchService;

/**
 * UDP 组播下发实现
 * 
 * 帧结构：
 * +--------+--------+--------+--------+--------+--------+--------+
 * | 帧头   | 单元类型| 单元ID | 参数数量| 参数数据...      | CRC16  |
 * | 2字节  | 1字节  | 4字节  | 2字节  | N字节           | 2字节  |
 * +--------+--------+--------+--------+--------+--------+--------+
 * 
 * 帧头: 0x5555 (固定)
 * 单元类型: 0x00=编码, 0x01=调制, 0x02=解调, 0x03=译码
 * 单元ID: 4字节无符号整数
 * 参数数量: 2字节无符号整数
 * 参数数据: 按sortOrder顺序排列，每个参数根据bitWidthType占用不同字节数
 * CRC16: Modbus CRC16校验
 */
@Service
public class BasebandDispatchServiceImpl implements IBasebandDispatchService
{
    private static final Logger log = LoggerFactory.getLogger(BasebandDispatchServiceImpl.class);

    /** 帧头标识 */
    private static final short FRAME_HEADER = (short) 0x5555;

    @Autowired
    private SysBasebandUnitMapper unitMapper;

    @Autowired
    private SysBasebandParamValueMapper paramValueMapper;

    @Autowired
    private SysBasebandDispatchLogMapper dispatchLogMapper;

    @Autowired
    private BasebandMulticastProperties multicastProperties;

    @Override
    public void dispatch(Long unitId, String operator, String dispatchType)
    {
        // 1. 获取单元信息
        SysBasebandUnit unit = unitMapper.selectSysBasebandUnitByUnitId(unitId);
        if (unit == null)
        {
            throw new ServiceException("单元不存在");
        }
        
        log.info("========== 开始下发参数 ==========");
        log.info("单元ID: {}, 类型: {}, 链路模式: {}", unitId, unit.getUnitType(), unit.getUnitName());
        
        // 2. 获取参数列表（按sortOrder排序）
        List<BasebandParamVo> params = paramValueMapper.selectParamVoListByUnitId(unitId);
        if (params.isEmpty())
        {
            throw new ServiceException("该单元尚未定义参数");
        }
        
        log.info("参数数量: {}", params.size());
        
        // 3. 打印参数详情
        log.info("---------- 参数列表 ----------");
        for (int i = 0; i < params.size(); i++)
        {
            BasebandParamVo vo = params.get(i);
            long finalValue = vo.getFinalValue();
            String valueSource = "";
            if (vo.getUintValue() != null && vo.getUintValue() != 0) {
                valueSource = "uint_value";
            } else if (vo.getRawValue() != null && !vo.getRawValue().trim().isEmpty()) {
                valueSource = "raw_value:" + vo.getRawValue();
            } else {
                valueSource = "default:" + vo.getDefaultValue();
            }
            log.info("[{}] {} ({}) = {} (0x{}) [{}] 来源:{}", 
                i + 1, 
                vo.getParamName(), 
                vo.getParamCode(),
                finalValue,
                Long.toHexString(finalValue).toUpperCase(),
                vo.getBitWidthType() != null ? vo.getBitWidthType() : "U32",
                valueSource);
        }
        
        // 4. 构建帧数据
        byte[] payload = buildPayload(unit, params);
        
        // 5. 打印帧结构
        log.info("---------- 帧结构 ----------");
        log.info("帧长度: {} 字节", payload.length);
        log.info("帧数据(HEX): {}", bytesToHex(payload));
        log.info("帧数据(格式化):\n{}", formatFrameStructure(payload, params));
        
        // 6. 记录日志
        SysBasebandDispatchLog logEntity = new SysBasebandDispatchLog();
        logEntity.setUnitId(unitId);
        logEntity.setDispatchType(dispatchType);
        logEntity.setMulticastIp(multicastProperties.getAddress());
        logEntity.setMulticastPort(multicastProperties.getPort());
        logEntity.setPacketLen(payload.length);
        logEntity.setSendTime(DateUtils.getNowDate());
        logEntity.setCreateBy(operator);
        logEntity.setCreateTime(new Date());
        
        // 7. 发送数据
        try
        {
            sendPayload(payload);
            logEntity.setResultStatus("0");
            logEntity.setResultMsg("SUCCESS");
            log.info("---------- 发送成功 ----------");
            log.info("目标地址: {}:{}", multicastProperties.getAddress(), multicastProperties.getPort());
        }
        catch (Exception ex)
        {
            log.error("下发失败", ex);
            logEntity.setResultStatus("1");
            logEntity.setResultMsg(ex.getMessage());
            dispatchLogMapper.insertLog(logEntity);
            throw new ServiceException("下发失败：" + ex.getMessage());
        }
        
        dispatchLogMapper.insertLog(logEntity);
        log.info("========== 下发完成 ==========\n");
    }

    /**
     * 构建帧数据
     * 
     * 帧结构：帧头(2) + 单元类型(1) + 单元ID(4) + 参数数量(2) + 参数数据(N) + CRC16(2)
     */
    private byte[] buildPayload(SysBasebandUnit unit, List<BasebandParamVo> params)
    {
        // 计算参数数据总长度
        int paramDataLen = 0;
        for (BasebandParamVo vo : params)
        {
            paramDataLen += getBitWidthBytes(vo.getBitWidthType());
        }
        
        // 总长度 = 帧头(2) + 单元类型(1) + 单元ID(4) + 参数数量(2) + 参数数据(N) + CRC16(2)
        int totalLen = 2 + 1 + 4 + 2 + paramDataLen + 2;
        
        ByteBuffer buffer = ByteBuffer.allocate(totalLen).order(ByteOrder.BIG_ENDIAN);
        
        // 1. 帧头 (2字节)
        buffer.putShort(FRAME_HEADER);
        
        // 2. 单元类型 (1字节)
        buffer.put(resolveUnitTypeCode(unit.getUnitType()));
        
        // 3. 单元ID (4字节)
        buffer.putInt(unit.getUnitId().intValue());
        
        // 4. 参数数量 (2字节)
        buffer.putShort((short) params.size());
        
        // 5. 参数数据 (按位宽类型写入)
        for (BasebandParamVo vo : params)
        {
            long value = vo.getFinalValue();
            writeValueByBitWidth(buffer, value, vo.getBitWidthType());
        }
        
        // 6. CRC16校验 (2字节)
        short crc = calcCrc16(buffer.array(), buffer.position());
        buffer.putShort(crc);
        
        return buffer.array();
    }

    /**
     * 根据位宽类型写入值
     */
    private void writeValueByBitWidth(ByteBuffer buffer, long value, String bitWidthType)
    {
        if (bitWidthType == null)
        {
            bitWidthType = "U32"; // 默认32位
        }
        
        switch (bitWidthType.toUpperCase())
        {
            case "U8":
            case "I8":
                buffer.put((byte) (value & 0xFF));
                break;
            case "U16":
            case "I16":
                buffer.putShort((short) (value & 0xFFFF));
                break;
            case "U32":
            case "I32":
            default:
                buffer.putInt((int) (value & 0xFFFFFFFFL));
                break;
        }
    }

    /**
     * 获取位宽类型对应的字节数
     */
    private int getBitWidthBytes(String bitWidthType)
    {
        if (bitWidthType == null)
        {
            return 4; // 默认32位 = 4字节
        }
        
        switch (bitWidthType.toUpperCase())
        {
            case "U8":
            case "I8":
                return 1;
            case "U16":
            case "I16":
                return 2;
            case "U32":
            case "I32":
            default:
                return 4;
        }
    }

    /**
     * 发送UDP组播数据
     */
    private void sendPayload(byte[] payload) throws IOException
    {
        InetSocketAddress target = new InetSocketAddress(multicastProperties.getAddress(), multicastProperties.getPort());
        try (DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET))
        {
            channel.setOption(java.net.StandardSocketOptions.IP_MULTICAST_TTL, multicastProperties.getTtl());
            if (StringUtils.isNotEmpty(multicastProperties.getNetIf()))
            {
                NetworkInterface ni = NetworkInterface.getByName(multicastProperties.getNetIf());
                if (ni != null)
                {
                    channel.setOption(java.net.StandardSocketOptions.IP_MULTICAST_IF, ni);
                }
            }
            channel.send(ByteBuffer.wrap(payload), target);
        }
    }

    /**
     * 解析单元类型代码
     */
    private byte resolveUnitTypeCode(String unitType)
    {
        switch (unitType)
        {
            case "ENCODE":
                return 0x00;
            case "MODULATE":
                return 0x01;
            case "DEMODULATE":
                return 0x02;
            case "DECODE":
                return 0x03;
            default:
                return (byte) 0x7F;
        }
    }

    /**
     * 计算CRC16校验 (Modbus)
     */
    private short calcCrc16(byte[] data, int length)
    {
        int crc = 0xFFFF;
        for (int i = 0; i < length; i++)
        {
            crc ^= (data[i] & 0xFF);
            for (int j = 0; j < 8; j++)
            {
                if ((crc & 0x0001) != 0)
                {
                    crc = (crc >>> 1) ^ 0xA001;
                }
                else
                {
                    crc = crc >>> 1;
                }
            }
        }
        return (short) crc;
    }

    /**
     * 字节数组转16进制字符串
     */
    private String bytesToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            if (i > 0) sb.append(" ");
            sb.append(String.format("%02X", bytes[i] & 0xFF));
        }
        return sb.toString();
    }

    /**
     * 格式化帧结构输出
     */
    private String formatFrameStructure(byte[] payload, List<BasebandParamVo> params)
    {
        StringBuilder sb = new StringBuilder();
        int offset = 0;
        
        // 帧头
        sb.append(String.format("  [%04d-%04d] 帧头:     %02X %02X (0x5555)\n", 
            offset, offset + 1, payload[0] & 0xFF, payload[1] & 0xFF));
        offset += 2;
        
        // 单元类型
        String unitTypeName = "";
        switch (payload[offset] & 0xFF)
        {
            case 0x00: unitTypeName = "编码"; break;
            case 0x01: unitTypeName = "调制"; break;
            case 0x02: unitTypeName = "解调"; break;
            case 0x03: unitTypeName = "译码"; break;
            default: unitTypeName = "未知"; break;
        }
        sb.append(String.format("  [%04d]      单元类型: %02X (%s)\n", 
            offset, payload[offset] & 0xFF, unitTypeName));
        offset += 1;
        
        // 单元ID
        int unitId = ((payload[offset] & 0xFF) << 24) | 
                     ((payload[offset + 1] & 0xFF) << 16) | 
                     ((payload[offset + 2] & 0xFF) << 8) | 
                     (payload[offset + 3] & 0xFF);
        sb.append(String.format("  [%04d-%04d] 单元ID:   %02X %02X %02X %02X (%d)\n", 
            offset, offset + 3, 
            payload[offset] & 0xFF, payload[offset + 1] & 0xFF, 
            payload[offset + 2] & 0xFF, payload[offset + 3] & 0xFF, unitId));
        offset += 4;
        
        // 参数数量
        int paramCount = ((payload[offset] & 0xFF) << 8) | (payload[offset + 1] & 0xFF);
        sb.append(String.format("  [%04d-%04d] 参数数量: %02X %02X (%d)\n", 
            offset, offset + 1, payload[offset] & 0xFF, payload[offset + 1] & 0xFF, paramCount));
        offset += 2;
        
        // 参数数据
        sb.append("  ---------- 参数数据 ----------\n");
        for (int i = 0; i < params.size() && i < paramCount; i++)
        {
            BasebandParamVo vo = params.get(i);
            int bytes = getBitWidthBytes(vo.getBitWidthType());
            StringBuilder hexStr = new StringBuilder();
            long value = 0;
            for (int j = 0; j < bytes; j++)
            {
                if (j > 0) hexStr.append(" ");
                hexStr.append(String.format("%02X", payload[offset + j] & 0xFF));
                value = (value << 8) | (payload[offset + j] & 0xFF);
            }
            sb.append(String.format("  [%04d-%04d] %s: %s (%d) [%s]\n", 
                offset, offset + bytes - 1, 
                vo.getParamName(), hexStr.toString(), value, 
                vo.getBitWidthType() != null ? vo.getBitWidthType() : "U32"));
            offset += bytes;
        }
        
        // CRC16
        sb.append("  ---------- 校验 ----------\n");
        sb.append(String.format("  [%04d-%04d] CRC16:    %02X %02X\n", 
            offset, offset + 1, payload[offset] & 0xFF, payload[offset + 1] & 0xFF));
        
        return sb.toString();
    }
}


