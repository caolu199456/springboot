package com.example.util.common;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * ip帮助类
 *
 * @author: CL
 * @date: 2019-01-16 15:24:00
 */
public class IPUtils {

    /**
     * 得到所有的IPv4地址 不包含127.x.x.x 也不包含虚拟机网卡
     *
     * @return
     */
    public static List<String> getIPv4List() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces != null) {
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    if (!networkInterface.isUp()
                            || networkInterface.isLoopback()
                            || networkInterface.isVirtual()) {
                        //关闭的网卡接口 虚拟网卡接口 loopback接口排除掉
                        continue;
                    }

                    Enumeration<InetAddress> interfaceInetAddresses = networkInterface.getInetAddresses();
                    if (interfaceInetAddresses != null) {
                        while (interfaceInetAddresses.hasMoreElements()) {
                            InetAddress inetAddress = interfaceInetAddresses.nextElement();
                            if (inetAddress.isLoopbackAddress()) {
                                continue;
                            }
                            if (inetAddress instanceof Inet6Address) {
                                //IPv6地址
                                continue;
                            }
                            ipList.add(inetAddress.getHostAddress());
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipList;
    }


    /**
     * ipv4转无符号int
     * @param ipv4
     * @return
     */
    public static long IPv4ToUnsignedInt(String ipv4) {
        String[] split = ipv4.split("\\.");
        if (split.length != 4) {
            return 0;
        }
        return Long.valueOf(split[0])<<24
                |Integer.valueOf(split[1])<<16
                |Integer.valueOf(split[2])<<8
                |Integer.valueOf(split[3]);
    }
    /**
     * 无符号int转ipv4
     * @param num
     * @return
     */
    public static String unsignedIntToIPv4(long num) {
        int[] ipv4Arr = new int[4];
        StringBuilder sb = new StringBuilder();
        sb.append((num >> 24) & 0xFF)
                .append(".").append((num >> 16) & 0xFF)
                .append(".").append((num >> 8) & 0xFF)
                .append(".").append((num >> 0) & 0xFF);
        return sb.toString();
    }

    public static void main(String[] args) {
    }
}
