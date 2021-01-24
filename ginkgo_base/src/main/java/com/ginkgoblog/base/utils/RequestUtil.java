package com.ginkgoblog.base.utils;

import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.utils.IpUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * AOP相关的工具
 *
 * @author maomao
 * @date 2021-01-24
 */
public class RequestUtil {
    public static String getHeader(String headerName) {
        HttpServletRequest request = RequestHolder.getRequest();
        if (null == request) {
            return null;
        }
        return request.getHeader(headerName);
    }

    public static String getUa() {
        return getHeader("User-Agent");
    }

    public static String getIp() {
        HttpServletRequest request = RequestHolder.getRequest();
        if (null == request) {
            return null;
        }
        return IpUtils.getRealIp(request);
    }

    public static String getRequestUrl() {
        HttpServletRequest request = RequestHolder.getRequest();
        if (null == request) {
            return null;
        }
        return request.getRequestURL().toString();
    }

    public static String getMethod() {
        HttpServletRequest request = RequestHolder.getRequest();
        if (null == request) {
            return null;
        }
        return request.getMethod();
    }
}
