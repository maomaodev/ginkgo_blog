package com.ginkgoblog.sms.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置文件
 *
 * @author maomao
 * @date 2021-01-26
 */
@Configuration
public class RabbitMqConfig {
    public static final  String GINKGO_BLOG = "ginkgo.blog";
    public static final  String GINKGO_EMAIL = "ginkgo.email";
    public static final  String GINKGO_SMS = "ginkgo.sms";
    public static final  String EXCHANGE_DIRECT = "exchange.direct";
    public static final  String ROUTING_KEY_BLOG = "ginkgo.blog";
    public static final  String ROUTING_KEY_EMAIL = "ginkgo.email";
    public static final  String ROUTING_KEY_SMS = "ginkgo.sms";


    /**
     * 申明交换机
     */
    @Bean(EXCHANGE_DIRECT)
    public Exchange EXCHANGE_DIRECT() {
        // 申明路由交换机，durable:在rabbitmq重启后，交换机还在
        return ExchangeBuilder.directExchange(EXCHANGE_DIRECT).durable(true).build();
    }

    /**
     * 申明Blog队列
     * @return
     */
    @Bean(GINKGO_BLOG)
    public Queue GINKGO_BLOG() {
        return new Queue(GINKGO_BLOG);
    }

    /**
     * 申明Email队列
     * @return
     */
    @Bean(GINKGO_EMAIL)
    public Queue GINKGO_EMAIL() {
        return new Queue(GINKGO_EMAIL);
    }

    /**
     * 申明SMS队列
     * @return
     */
    @Bean(GINKGO_SMS)
    public Queue GINKGO_SMS() {
        return new Queue(GINKGO_SMS);
    }

    /**
     * ginkgo.blog 队列绑定交换机，指定routingKey
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_BLOG(@Qualifier(GINKGO_BLOG) Queue queue, @Qualifier(EXCHANGE_DIRECT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_BLOG).noargs();
    }

    /**
     * ginkgo.mail 队列绑定交换机，指定routingKey
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier(GINKGO_EMAIL) Queue queue, @Qualifier(EXCHANGE_DIRECT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_EMAIL).noargs();
    }

    /**
     * ginkgo.sms 队列绑定交换机，指定routingKey
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(GINKGO_SMS) Queue queue, @Qualifier(EXCHANGE_DIRECT) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_SMS).noargs();
    }


    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
