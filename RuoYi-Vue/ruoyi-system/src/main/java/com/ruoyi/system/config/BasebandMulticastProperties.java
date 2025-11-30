package com.ruoyi.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 基带组播配置属性
 * 
 * @author ruoyi
 * @date 2025-11-27
 */
@Component
@ConfigurationProperties(prefix = "baseband.multicast")
public class BasebandMulticastProperties 
{
    /** 组播地址 */
    private String address = "239.255.0.1";

    /** 组播端口 */
    private Integer port = 9000;

    /** TTL值 */
    private Integer ttl = 16;

    /** 网络接口名称（可选） */
    private String netIf;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public String getNetIf() {
        return netIf;
    }

    public void setNetIf(String netIf) {
        this.netIf = netIf;
    }
}
