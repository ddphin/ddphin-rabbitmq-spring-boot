package com.ddphin.rabbitmq.configuration;

import com.ddphin.rabbitmq.receiver.impl.RabbitmqCommonDelayQueueReceiver;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitmqCommonDelayQueueAutoConfiguration
 *
 * @Date 2019/7/24 下午6:12
 * @Author ddphin
 */
@Configuration
public class RabbitmqCommonDelayQueueAutoConfiguration {
    /**
     * 延迟队列 TTL 名称
     */
    private static final String SENDER_COMMON_DELAY_QUEUE = "sender.common.delay.queue";
    /**
     * DLX，dead letter发送到的 exchange
     * 此处的 exchange 很重要,具体延时消息就是发送到该交换机的
     */
    public static final String SENDER_COMMON_DELAY_EXCHANGE = "sender.common.delay.exchange";

    /**
     * routing key 名称
     * 此处的 routingKey 很重要要,具体消息发送在该 routingKey 的
     */
    public static final String SENDER_COMMON_DELAY_ROUTING_KEY = "sender.common.delay.routing.key";


    private static final String RECEIVER_COMMON_DELAY_QUEUE = RabbitmqCommonDelayQueueReceiver.RECEIVER_COMMON_DELAY_QUEUE;
    private static final String RECEIVER_COMMON_DELAY_EXCHANGE = RabbitmqCommonDelayQueueReceiver.RECEIVER_COMMON_DELAY_EXCHANGE;
    private static final String RECEIVER_COMMON_DELAY_ROUTING_KEY = RabbitmqCommonDelayQueueReceiver.RECEIVER_COMMON_DELAY_ROUTING_KEY;
    /**
     * 延迟队列配置
     * <p>
     * 1、params.put("x-message-ttl", 5 * 1000);
     * 第一种方式是直接设置 Queue 延迟时间 但如果直接给队列设置过期时间,这种做法不是很灵活,（当然二者是兼容的,默认是时间小的优先）
     * 2、rabbitTemplate.convertAndSend(book, message -> {
     * message.getMessageProperties().setExpiration(2 * 1000 + "");
     * return message;
     * });
     * 第二种就是每次发送消息动态设置延迟时间,这样我们可以灵活控制
     **/
    @Bean
    public Queue senderCommonDelayQueue() {
        Map<String, Object> params = new HashMap<>();
        // x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称，
        params.put("x-dead-letter-exchange", RECEIVER_COMMON_DELAY_EXCHANGE);
        // x-dead-letter-routing-key 声明了这些死信在转发时携带的 routing-key 名称。
        params.put("x-dead-letter-routing-key", RECEIVER_COMMON_DELAY_ROUTING_KEY);
        return new Queue(SENDER_COMMON_DELAY_QUEUE, true, false, false, params);
    }
    /**
     * 需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。
     * 这是一个完整的匹配。如果一个队列绑定到该交换机上要求路由键 “dog”，则只有被标记为“dog”的消息才被转发，不会转发dog.puppy，也不会转发dog.guard，只会转发dog。
     * 它不像 TopicExchange 那样可以使用通配符适配多个
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange senderCommonDelayExchange() {
        return new DirectExchange(SENDER_COMMON_DELAY_EXCHANGE);
    }

    @Bean
    public Binding senderCommonDelayBinding() {
        return BindingBuilder
                .bind(senderCommonDelayQueue())
                .to(senderCommonDelayExchange())
                .with(SENDER_COMMON_DELAY_ROUTING_KEY);
    }


    @Bean
    public Queue receiverCommonDelayQueue() {
        return new Queue(RECEIVER_COMMON_DELAY_QUEUE, true);
    }
    /**
     * 将路由键和某模式进行匹配。此时队列需要绑定要一个模式上。
     * 符号“#”匹配一个或多个词，符号“*”匹配不多不少一个词。因此“audit.#”能够匹配到“audit.irs.corporate”，但是“audit.*” 只会匹配到“audit.irs”。
     **/
    @Bean
    public TopicExchange receiverCommonDelayExchange() {
        return new TopicExchange(RECEIVER_COMMON_DELAY_EXCHANGE);
    }
    @Bean
    public Binding receiverCommonDelayBinding() {
        // 如果要让延迟队列之间有关联,这里的 routingKey 和 绑定的交换机很关键
        return BindingBuilder
                .bind(receiverCommonDelayQueue())
                .to(receiverCommonDelayExchange())
                .with(RECEIVER_COMMON_DELAY_ROUTING_KEY);
    }

    @Bean
    public RabbitmqCommonDelayQueueReceiver rabbitmqCommonDelayQueueReceiver() {
        return new RabbitmqCommonDelayQueueReceiver();
    }
}
