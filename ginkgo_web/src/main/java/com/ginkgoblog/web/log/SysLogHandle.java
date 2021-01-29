package com.ginkgoblog.web.log;

import com.ginkgoblog.base.constants.BaseSysConf;
import com.ginkgoblog.base.holder.RequestHolder;
import com.ginkgoblog.commons.entity.WebVisit;
import com.ginkgoblog.utils.IpUtils;
import com.ginkgoblog.utils.RedisUtil;
import com.ginkgoblog.utils.StringUtils;
import com.ginkgoblog.web.constants.SysConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 异步记录日志
 *
 * @author maomao
 * @date 2021-01-24
 */
@Component("WebSysLogHandle")
public class SysLogHandle extends RequestAwareRunnable {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 模块UID
     */
    private String moduleUid;
    /**
     * 其它数据
     */
    private String otherData;
    /**
     * 用户UID
     */
    private String userUid;
    /**
     * 用户行为
     */
    private String behavior;

    /**
     * 构造方法，用于初始化成员变量
     */
    public void setSysLogHandle(String userUid, String behavior, String moduleUid, String otherData) {
        this.userUid = userUid;
        this.behavior = behavior;
        this.moduleUid = moduleUid;
        this.otherData = otherData;
    }

    @Override
    protected void onRun() {
        HttpServletRequest request = RequestHolder.getRequest();
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get(SysConf.OS);
        String browser = map.get(SysConf.BROWSER);
        WebVisit webVisit = new WebVisit();
        String ip = IpUtils.getIpAddr(request);
        webVisit.setIp(ip);

        //从Redis中获取IP来源
        String jsonResult = redisUtil.get(SysConf.IP_SOURCE + BaseSysConf.REDIS_SEGMENTATION + ip);
        if (StringUtils.isEmpty(jsonResult)) {
            String addresses = IpUtils.getAddresses(SysConf.IP + SysConf.EQUAL_TO + ip, SysConf.UTF_8);
            if (StringUtils.isNotEmpty(addresses)) {
                webVisit.setIpSource(addresses);
                redisUtil.setEx(SysConf.IP_SOURCE + BaseSysConf.REDIS_SEGMENTATION + ip, addresses, 24, TimeUnit.HOURS);
            }
        } else {
            webVisit.setIpSource(jsonResult);
        }
        webVisit.setOs(os);
        webVisit.setBrowser(browser);
        webVisit.setUserUid(userUid);
        webVisit.setBehavior(behavior);
        webVisit.setModuleUid(moduleUid);
        webVisit.setOtherData(otherData);
        webVisit.insert();
    }
}
