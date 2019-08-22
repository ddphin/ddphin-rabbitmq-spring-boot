package com.ddphin.rabbitmq.configuration;

import com.ddphin.rabbitmq.scheduler.RabbitmqRetrySchedulerConfigurer;
import com.ddphin.rabbitmq.sender.RabbitmqCommonTxMessageSender;
import com.ddphin.rabbitmq.sender.impl.RabbitmqCommonTxMessageSenderImpl;
import com.ddphin.redis.helper.RedisHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;

/**
 * RabbitmqCommonTxMessageAutoConfiguration
 *
 * @Date 2019/7/24 下午6:12
 * @Author ddphin
 */
@Configuration
@EnableScheduling
public class RabbitmqCommonTxMessageAutoConfiguration {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisHelper redisHelper;

    @Bean
    public RabbitmqCommonTxMessageSender rabbitmqCommonTxMessageSender() {
        return new RabbitmqCommonTxMessageSenderImpl(rabbitTemplate, redisHelper);
    }

    @Bean
    public SchedulingConfigurer rabbitmqRetrySchedulerConfigurer() {
        return new RabbitmqRetrySchedulerConfigurer(this.rabbitmqCommonTxMessageSender());
    }
}
