package com.nmghr.util;

public class IPUtil {

    /*
     *验证IP是否属于某个IP段
     */
    public static boolean ipExistsInRange(String ip, String beginIp,String endIp) {

        ip = ip.trim();
        beginIp = beginIp.trim();
        endIp = endIp.trim();
        return getIp2long(beginIp) <= getIp2long(ip) && getIp2long(ip) <= getIp2long(endIp);
    }

    public static long getIp2long(String ip) {
        ip = ip.trim();
        String[] ips = ip.split("\\.");
        long ip2long = 0L;
        for (int i = 0; i < 4; ++i) {
            ip2long = ip2long << 8 | Integer.parseInt(ips[i]);
        }
        return ip2long;
    }

    public static long getIp2long2(String ip) {
        ip = ip.trim();
        String[] ips = ip.split("\\.");
        long ip1 = Integer.parseInt(ips[0]);
        long ip2 = Integer.parseInt(ips[1]);
        long ip3 = Integer.parseInt(ips[2]);
        long ip4 = Integer.parseInt(ips[3]);
        long ip2long = 1L * ip1 * 256 * 256 * 256 + ip2 * 256 * 256 + ip3 * 256 + ip4;
        return ip2long;
    }





    public static void main(String[] args){

        //10.10.10.116 是否属于固定格式的IP段10.10.1.00-10.10.255.255

        String ip="192.168.1.109";

String ipStart = "192.168.1.101";
String ipEnd = "192.177.1.109";
        boolean exists=ipExistsInRange(ip,ipStart,ipEnd);

        System.out.println(exists);

        System.out.println(getIp2long(ip));
        System.out.println(getIp2long2(ip));
    }






}
