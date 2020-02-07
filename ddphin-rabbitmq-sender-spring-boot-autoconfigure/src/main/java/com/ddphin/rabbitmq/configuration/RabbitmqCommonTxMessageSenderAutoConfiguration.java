package com.ddphin.rabbitmq.configuration;

import com.ddphin.jedis.helper.JedisHelper;
import com.ddphin.rabbitmq.scheduler.RabbitmqRetrySchedulerConfigurer;
import com.ddphin.rabbitmq.sender.RabbitmqCommonTxMessageMonitor;
import com.ddphin.rabbitmq.sender.RabbitmqCommonTxMessageSender;
import com.ddphin.rabbitmq.sender.impl.RabbitmqCommonTxMessageMonitorImpl;
import com.ddphin.rabbitmq.sender.impl.RabbitmqCommonTxMessageSenderImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;

import java.io.IOException;

/**
 * RabbitmqCommonTxMessageAutoConfiguration
 *
 * @Date 2019/7/24 下午6:12
 * @Author ddphin
 */
@Configuration
@EnableScheduling
public class RabbitmqCommonTxMessageSenderAutoConfiguration {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JedisHelper jedisHelper;

    @Autowired
    private DdphinRabbitmqProperties properties;

    @Bean
    public RabbitmqCommonTxMessageSender rabbitmqCommonTxMessageSender() throws IOException {
        return new RabbitmqCommonTxMessageSenderImpl(
                rabbitTemplate,
                jedisHelper,
                properties);
    }
}
