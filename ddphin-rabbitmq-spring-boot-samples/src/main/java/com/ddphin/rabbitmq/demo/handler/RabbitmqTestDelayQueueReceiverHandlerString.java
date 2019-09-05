package com.ddphin.rabbitmq.demo.handler;

import com.ddphin.rabbitmq.entity.Result;
import com.ddphin.rabbitmq.receiver.RabbitmqCommonQueueReceiverHandler;
import com.ddphin.rabbitmq.receiver.impl.RabbitmqCommonDelayQueueReceiver;
import com.ddphin.rabbitmq.receiver.impl.RabbitmqCommonQueueReceiverHandlerRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * RabbitmqTestDelayQueueReceiverHandlerString
 *
 * @Date 2019/7/25 下午3:23
 * @Author ddphin
 */
@Slf4j
@Service
public class RabbitmqTestDelayQueueReceiverHandlerString
        extends RabbitmqCommonQueueReceiverHandlerRegister<RabbitmqCommonDelayQueueReceiver, String>
        implements RabbitmqCommonQueueReceiverHandler<String> {

    @Override
    public Result process(String data) {
        log.info("RabbitmqTestDelayQueueReceiverHandlerString:\n" +
                "    String: {}", data);
        return new Result(true, false, null);
    }
}
