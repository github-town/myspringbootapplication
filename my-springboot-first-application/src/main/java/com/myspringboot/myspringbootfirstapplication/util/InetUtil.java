package com.myspringboot.myspringbootfirstapplication.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.*;

/**
 * 本机网络工具类
 */
@Slf4j
public class InetUtil {

    private static InetAddress inetAddress;
    private static NetworkInterface networkInterface;

    static {
        try {
            inetAddress = InetAddress.getLocalHost();
            networkInterface = NetworkInterface.getByInetAddress(inetAddress);
        } catch (UnknownHostException e) {
            log.error("UnknownHostException",e);
        } catch (SocketException e) {
            log.error("SocketException",e);
        }
    }

    public static String getIp(){
        log.info("inetAddress.getHostAddress :{}",inetAddress.getHostAddress());
        log.info("inetAddress.getAddress :{}",String.valueOf(inetAddress.getAddress()));
        return String.valueOf(inetAddress.getAddress());
    }

    public static String getHardWareId(){
        try {
            log.info("networkInterface.getHardwareAddress : {}",String.valueOf(networkInterface.getHardwareAddress()));
            log.info("networkInterface.getInetAddresses : {}",networkInterface.getInetAddresses());
            log.info("networkInterface.getInterfaceAddresses : {}",networkInterface.getInterfaceAddresses());
            return String.valueOf(networkInterface.getHardwareAddress());
        } catch (SocketException e) {
            log.error("SocketException",e);
        }
        return null;
    }

    public static String getProcessId(){
        //获取JVM进程的PID
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    /**
     * 获取本机IP地址
     *
     * @return
     */
    public static String getLocalHostExactAddress() {
        try {
            InetAddress candidateAddress = null;
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface iface = networkInterfaces.nextElement();
                // 该网卡接口下的ip会有多个，也需要一个个的遍历，找到自己所需要的
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    // 排除loopback回环类型地址（不管是IPv4还是IPv6 只要是回环地址都会返回true）
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了 就是我们要找的
                            // ~~~~~~~~~~~~~绝大部分情况下都会在此处返回你的ip地址值~~~~~~~~~~~~~
                            return inetAddr.getHostAddress();
                        }
                        // 若不是site-local地址 那就记录下该地址当作候选
                        if (candidateAddress == null) {
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }

            // 如果出去loopback回环地之外无其它地址了，那就回退到原始方案吧
            return candidateAddress == null ? InetAddress.getLocalHost().getHostAddress() : candidateAddress.getHostAddress();
        } catch (Exception e) {
            log.error("getLocalHostExactAddress error!", e);
        }
        return null;
    }

    /**
     * 获取本机地址
     * @return
     */
    public static Set<String> getLocalIp4Address() {
        Set<String> result = new HashSet<>();
        List<Inet4Address> list = null;
        try {
            list = getLocalIp4AddressFromNetworkInterface();
            list.forEach(d -> {
                result.add(d.toString().replaceAll("/", ""));
            });
        } catch (Exception e) {
            log.error("getLocalIp4Address error!", e);
        }
        return result;
    }

    /**
     * 获取本机所有网卡信息   得到所有IPv4信息
     * @return Inet4Address>
     */
    private static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() throws SocketException {
        List<Inet4Address> addresses = new ArrayList<>(8);
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        if (networkInterfaces == null) {
            return addresses;
        }
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress instanceof Inet4Address) {
                    addresses.add((Inet4Address) inetAddress);
                }
            }
        }
        return addresses;
    }

    private static String INTRANET_IP = getIntranetIp(); // 内网IP

    /**
     * 获得外网IP
     *
     * @return 外网IP
     */
    public static String getInternetIp() {
        try {
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            Enumeration<InetAddress> addrs;
            while (networks.hasMoreElements()) {
                addrs = networks.nextElement().getInetAddresses();
                while (addrs.hasMoreElements()) {
                    ip = addrs.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && ip.isSiteLocalAddress()
                            && !ip.getHostAddress().equals(INTRANET_IP)) {
                        return ip.getHostAddress();
                    }
                }
            }

            // 如果没有外网IP，就返回内网IP
            return INTRANET_IP;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得内网IP
     *
     * @return 内网IP
     */
    public static String getIntranetIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
