package com.ddphin.rabbitmq.configuration;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitmqAutoConfiguration
 *
 * @Date 2019/7/24 下午6:12
 * @Author ddphin
 */
@Slf4j
@Configuration
public class RabbitmqAutoConfiguration {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(this.messageConverter());
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(
                (correlationData, ack, cause) -> {
                    if (ack) {
                        log.info("消息发送到exchange成功:\n" +
                                        "    data: {}",
                                JSONObject.toJSONString(correlationData));
                    } else {
                        log.warn("消息发送到exchange失败:\n" +
                                        "     data: {}\n" +
                                        "    cause: {}",
                                JSONObject.toJSONString(correlationData),
                                cause);
                    }
                }
        );
        rabbitTemplate.setReturnCallback(
                (message, replyCode, replyText, exchange, routingKey) -> {
                    log.warn("消息发送到queue失败:\n" +
                                    "       message: {}\n" +
                                    "     replyCode: {}\n" +
                                    "     replyText: {}\n" +
                                    "      exchange: {}\n" +
                                    "    routingKey: {}",
                            JSONObject.toJSONString(message),
                            replyCode,
                            replyText,
                            exchange,
                            routingKey);
                }
        );
        return rabbitTemplate;
    }
}
