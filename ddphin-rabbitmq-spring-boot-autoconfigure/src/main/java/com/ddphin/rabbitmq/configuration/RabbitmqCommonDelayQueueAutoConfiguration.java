package com.ddphin.rabbitmq.configuration;

import com.ddphin.rabbitmq.receiver.impl.RabbitmqCommonDelayQueueReceiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitmqCommonDelayQueueAutoConfiguration
 *
 * @Date 2019/7/24 下午6:12
 * @Author ddphin
 */
@Configuration
public class RabbitmqCommonDelayQueueAutoConfiguration {
    @Bean
    public Queue ddphinCommonDelayQueue() {
        return new Queue(RabbitmqCommonDelayQueueReceiver.DDPHIN_COMMON_DELAY_QUEUE, true);
    }
    @Bean
    public DirectExchange ddphinCommonDelayExchange() {
        DirectExchange exchange = new DirectExchange(RabbitmqCommonDelayQueueReceiver.DDPHIN_COMMON_DELAY_EXCHANGE, true, false);
        exchange.setDelayed(true);
        return exchange;
    }

    @Bean
    public Binding ddphinCommonDelayBinding() {
        return BindingBuilder
                .bind(ddphinCommonDelayQueue())
                .to(ddphinCommonDelayExchange())
                .with(RabbitmqCommonDelayQueueReceiver.DDPHIN_COMMON_DELAY_ROUTING_KEY);
    }
}
