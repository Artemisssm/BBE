package com.ruoyi.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "baseband.multicast")
public class BasebandMulticastProperties
{
    /** 组播地址 */
    private String address = "239.1.1.100";

    /** 端口 */
    private int port = 50000;

    /** TTL */
    private int ttl = 32;

    /** 网络接口名称 */
    private String netIf;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getTtl()
    {
        return ttl;
    }

    public void setTtl(int ttl)
    {
        this.ttl = ttl;
    }

    public String getNetIf()
    {
        return netIf;
    }

    public void setNetIf(String netIf)
    {
        this.netIf = netIf;
    }
}


