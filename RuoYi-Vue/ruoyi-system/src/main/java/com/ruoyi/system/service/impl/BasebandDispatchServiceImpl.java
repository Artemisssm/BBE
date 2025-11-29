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
 */
@Service
public class BasebandDispatchServiceImpl implements IBasebandDispatchService
{
    private static final Logger log = LoggerFactory.getLogger(BasebandDispatchServiceImpl.class);

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
        SysBasebandUnit unit = unitMapper.selectBasebandUnitById(unitId);
        if (unit == null)
        {
            throw new ServiceException("单元不存在");
        }
        List<BasebandParamVo> params = paramValueMapper.selectParamVoByUnitId(unitId);
        if (params.isEmpty())
        {
            throw new ServiceException("该单元尚未配置参数");
        }
        for (BasebandParamVo vo : params)
        {
            if (vo.getUintValue() == null)
            {
                throw new ServiceException("参数【" + vo.getParamName() + "】未配置，无法下发");
            }
        }
        byte[] payload = buildPayload(unit, params);
        SysBasebandDispatchLog logEntity = new SysBasebandDispatchLog();
        logEntity.setUnitId(unitId);
        logEntity.setDispatchType(dispatchType);
        logEntity.setMulticastIp(multicastProperties.getAddress());
        logEntity.setMulticastPort(multicastProperties.getPort());
        logEntity.setPacketLen(payload.length);
        logEntity.setSendTime(DateUtils.getNowDate());
        logEntity.setCreateBy(operator);
        logEntity.setCreateTime(new Date());
        try
        {
            sendPayload(payload);
            logEntity.setResultStatus("0");
            logEntity.setResultMsg("SUCCESS");
        }
        catch (Exception ex)
        {
            log.error("Baseband dispatch failed", ex);
            logEntity.setResultStatus("1");
            logEntity.setResultMsg(ex.getMessage());
            dispatchLogMapper.insertLog(logEntity);
            throw new ServiceException("下发失败：" + ex.getMessage());
        }
        dispatchLogMapper.insertLog(logEntity);
    }

    private byte[] buildPayload(SysBasebandUnit unit, List<BasebandParamVo> params)
    {
        int capacity = 2 + 1 + 4 + 2 + params.size() * (2 + 8) + 2;
        ByteBuffer buffer = ByteBuffer.allocate(capacity).order(ByteOrder.BIG_ENDIAN);
        buffer.putShort((short) 0xBB66);
        buffer.put(resolveUnitTypeCode(unit.getUnitType()));
        buffer.putInt(unit.getUnitId().intValue());
        buffer.putShort((short) params.size());
        for (BasebandParamVo vo : params)
        {
            buffer.putShort((short) (vo.getParamId() & 0xFFFF));
            buffer.putLong(vo.getUintValue());
        }
        short crc = calcCrc16(buffer.array(), buffer.position());
        buffer.putShort(crc);
        return buffer.array();
    }

    private void sendPayload(byte[] payload) throws IOException
    {
        InetSocketAddress target = new InetSocketAddress(multicastProperties.getAddress(), multicastProperties.getPort());
        try (DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET))
        {
            channel.setOption(java.net.StandardSocketOptions.IP_MULTICAST_TTL, multicastProperties.getTtl());
            if (StringUtils.isNotEmpty(multicastProperties.getNetIf()))
            {
                NetworkInterface ni = NetworkInterface.getByName(multicastProperties.getNetIf());
                channel.setOption(java.net.StandardSocketOptions.IP_MULTICAST_IF, ni);
            }
            channel.send(ByteBuffer.wrap(payload), target);
        }
    }

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
}


