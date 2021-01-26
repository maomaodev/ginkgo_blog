package com.ginkgoblog.sms.listener;

import com.ginkgoblog.sms.utils.SendMailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Map;

/**
 * 邮件发送消费者
 *
 * @author maomao
 * @date 2021-01-26
 */
@Slf4j
@Component
public class MailListener {
    @Autowired
    private SendMailUtils sendMailUtils;

    @RabbitListener(queues = "ginkgo.email")
    public void sendMail(Map<String, String> map) {
        if(map != null) {
            try {
                sendMailUtils.sendEmail(
                        map.get("receiver"),
                        map.get("text")
                );
            } catch (MessagingException e) {
                log.error("发送邮件失败！");
            }
        }

    }
}
