package com.ginkgoblog.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * IP相关工具类
 *
 * @author maomao
 * @date 2021-01-20
 */
@Slf4j
public class IpUtils {
    /**
     * 获取当前网络ip
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  //"***.***.***.***".length() = 15
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * 获取真实IP
     *
     * @param request
     * @return
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        return checkIp(ip) ? ip : (
                checkIp(ip = request.getHeader("Proxy-Client-IP")) ? ip : (
                        checkIp(ip = request.getHeader("WL-Proxy-Client-IP")) ? ip :
                                request.getRemoteAddr()));
    }

    /**
     * 校验IP
     *
     * @param ip
     * @return
     */
    private static boolean checkIp(String ip) {
        return !StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip);
    }


    /**
     * 获取操作系统,浏览器及浏览器版本信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getOsAndBrowserInfo(HttpServletRequest request) {
        String browserDetails = request.getHeader("User-Agent");
        String userAgent = browserDetails;
        String user = userAgent.toLowerCase();

        String os = "";
        String browser = "";

        //=================OS Info=======================
        if (userAgent.toLowerCase().contains("windows")) {
            os = "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            os = "Mac";
        } else if (userAgent.toLowerCase().contains("x11")) {
            os = "Unix";
        } else if (userAgent.toLowerCase().contains("android")) {
            os = "Android";
        } else if (userAgent.toLowerCase().contains("iphone")) {
            os = "IPhone";
        } else {
            os = "UnKnown, More-Info: " + userAgent;
        }
        //===============Browser===========================
        if (user.contains("edge")) {
            browser = (userAgent.substring(userAgent.indexOf("Edge")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("msie")) {
            String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]
                    + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera")) {
                browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]
                        + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            } else if (user.contains("opr")) {
                browser = ((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-"))
                        .replace("OPR", "Opera");
            }

        } else if (user.contains("chrome")) {
            browser = (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) ||
                (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) ||
                (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
            browser = "Netscape-?";

        } else if (user.contains("firefox")) {
            browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            String IEVersion = (userAgent.substring(userAgent.indexOf("rv")).split(" ")[0]).replace("rv:", "-");
            browser = "IE" + IEVersion.substring(0, IEVersion.length() - 1);
        } else {
            browser = "UnKnown, More-Info: " + userAgent;
        }
        Map<String, String> result = new HashMap<>(2);
        result.put("OS", os);
        result.put("BROWSER", browser);
        return result;
    }

    /**
     * 获取IP地址来源
     *
     * @param content        请求的参数 格式为：name=xxx&pwd=xxx
     * @param encodingString 服务器端请求编码。如GBK,UTF-8等
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getAddresses(String content, String encodingString) {

        String ip = content.substring(3);

        if (!Util.isIpAddress(ip)) {
            log.info("IP地址为空");
            return null;
        }

        // 淘宝IP宕机，目前使用Ip2region：https://github.com/lionsoul2014/ip2region
        log.info("返回的IP信息：{}", getCityInfo(ip));
        return getCityInfo(ip);
    }

    /**
     * 创建ip2region文件
     *
     * @return
     */
    public static String createFtlFileByFtlArray() {
        String ftlPath = "city/";
        return createFtlFile(ftlPath, "ip2region.db");
    }

    /**
     * 创建文件
     *
     * @param ftlPath
     * @param ftlName
     * @return
     */
    private static String createFtlFile(String ftlPath, String ftlName) {
        InputStream certStream = null;
        try {
            //获取当前项目所在的绝对路径
            String proFilePath = System.getProperty("user.dir");

            //获取模板下的路径，然后存放在temp目录下　
            String newFilePath = proFilePath + File.separator + "temp" + File.separator + ftlPath;
            newFilePath = newFilePath.replace("/", File.separator);
            //检查项目运行时的src下的对应路径
            File newFile = new File(newFilePath + ftlName);
            if (newFile.isFile() && newFile.exists()) {
                return newFilePath;
            }
            //当项目打成jar包会运行下面的代码，并且复制一份到src路径下（具体结构看下面图片）
            certStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(ftlPath + ftlName);
            byte[] certData = org.apache.commons.io.IOUtils.toByteArray(certStream);
            org.apache.commons.io.FileUtils.writeByteArrayToFile(newFile, certData);
            return newFilePath;
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                certStream.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    public static String getCityInfo(String ip) {

        String dbPath = createFtlFileByFtlArray() + "ip2region.db";
        File file = new File(dbPath);
        if (!file.exists()) {
            System.out.println("Error: Invalid ip2region.db file");
        }

        //查询算法
        //B-tree, B树搜索（更快）
        int algorithm = DbSearcher.BTREE_ALGORITHM;

        //Binary,使用二分搜索
        //DbSearcher.BINARY_ALGORITHM

        //Memory,加载内存（最快）
        //DbSearcher.MEMORY_ALGORITYM
        try {
            DbConfig config = new DbConfig();
            DbSearcher searcher = new DbSearcher(config, dbPath);

            //define the method
            Method method = null;
            switch (algorithm) {
                case DbSearcher.BTREE_ALGORITHM:
                    method = searcher.getClass().getMethod("btreeSearch", String.class);
                    break;
                case DbSearcher.BINARY_ALGORITHM:
                    method = searcher.getClass().getMethod("binarySearch", String.class);
                    break;
                case DbSearcher.MEMORY_ALGORITYM:
                    method = searcher.getClass().getMethod("memorySearch", String.class);
                    break;
                default:
                    break;
            }

            DataBlock dataBlock;
            if (!Util.isIpAddress(ip)) {
                System.out.println("Error: Invalid ip address");
            }

            dataBlock = (DataBlock) method.invoke(searcher, ip);
            String ipInfo = dataBlock.getRegion();
            if (!StringUtils.isEmpty(ipInfo)) {
                ipInfo = ipInfo.replace("|0", "");
                ipInfo = ipInfo.replace("0|", "");
            }
            return ipInfo;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取IP
     *
     * @return
     */
    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        return "127.0.0.1";
    }

    /**
     * 获取主机名
     *
     * @return
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
        return "未知";
    }
}
