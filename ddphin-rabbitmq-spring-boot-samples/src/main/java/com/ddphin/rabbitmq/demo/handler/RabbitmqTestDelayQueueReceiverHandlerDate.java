package com.ddphin.rabbitmq.demo.handler;

import com.alibaba.fastjson.JSONObject;
import com.ddphin.rabbitmq.receiver.RabbitmqCommonQueueReceiverHandler;
import com.ddphin.rabbitmq.receiver.impl.RabbitmqCommonDelayQueueReceiver;
import com.ddphin.rabbitmq.receiver.impl.RabbitmqCommonQueueReceiverHandlerRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * RabbitmqTestDelayQueueReceiverHandlerDate
 *
 * @Date 2019/7/25 下午3:23
 * @Author ddphin
 */
@Slf4j
@Service
public class RabbitmqTestDelayQueueReceiverHandlerDate
        extends RabbitmqCommonQueueReceiverHandlerRegister<RabbitmqCommonDelayQueueReceiver, Date>
        implements RabbitmqCommonQueueReceiverHandler<Date> {

    @Override
    public Boolean process(Date data) {
        log.info("RabbitmqTestDelayQueueReceiverHandlerDate:\n" +
                "    Date: {}", JSONObject.toJSONString(data));
        return null;
    }
}
