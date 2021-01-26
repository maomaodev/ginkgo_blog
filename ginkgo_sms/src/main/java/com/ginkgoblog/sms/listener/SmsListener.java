package com.ginkgoblog.sms.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.ginkgoblog.sms.utils.SmsUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信发送消费者
 *
 * @author maomao
 * @date 2021-01-26
 */
@Component
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;

    @RabbitListener(queues = "ginkgo.sms")
    public void sendSms(Map<String, String> map) {
        try {
            SendSmsResponse response = smsUtils.sendSms(
                    map.get("mobile"),
                    map.get("template_code"),
                    map.get("sign_name"),
                    map.get("param"));
            System.out.println("code:" + response.getCode());
            System.out.println("message:" + response.getMessage());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
