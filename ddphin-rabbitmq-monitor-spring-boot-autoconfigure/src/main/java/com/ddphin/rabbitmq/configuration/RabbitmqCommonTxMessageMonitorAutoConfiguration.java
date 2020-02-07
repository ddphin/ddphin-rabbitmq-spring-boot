package com.ddphin.rabbitmq.configuration;

import com.ddphin.jedis.helper.JedisHelper;
import com.ddphin.rabbitmq.scheduler.RabbitmqRetrySchedulerConfigurer;
import com.ddphin.rabbitmq.sender.RabbitmqCommonTxMessageMonitor;
import com.ddphin.rabbitmq.sender.impl.RabbitmqCommonTxMessageMonitorImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RabbitmqCommonTxMessageMonitorAutoConfiguration {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JedisHelper jedisHelper;

    @Autowired
    private DdphinRabbitmqProperties properties;

    @Bean
    public RabbitmqCommonTxMessageMonitor rabbitmqCommonTxMessageMonitor() throws IOException {
        return new RabbitmqCommonTxMessageMonitorImpl(
                rabbitTemplate,
                jedisHelper,
                properties);
    }

    @Bean
    public SchedulingConfigurer rabbitmqRetrySchedulerConfigurer() throws IOException {
        return new RabbitmqRetrySchedulerConfigurer(
                this.rabbitmqCommonTxMessageMonitor(),
                properties);
    }
}
