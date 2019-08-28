package com.ddphin.rabbitmq.demo.service;

import com.ddphin.rabbitmq.receiver.impl.RabbitmqCommonDelayQueueReceiver;
import com.ddphin.rabbitmq.sender.RabbitmqCommonTxMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * DemoService
 *
 * @Date 2019/7/25 下午5:47
 * @Author ddphin
 */
@Service
public class DemoService {
    @Autowired
    private RabbitmqCommonTxMessageSender sender;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor=Exception.class)
    public void testSender(Integer millis, String msg) {
        this.sender.send(
                RabbitmqCommonDelayQueueReceiver.DDPHIN_COMMON_DELAY_EXCHANGE,
                RabbitmqCommonDelayQueueReceiver.DDPHIN_COMMON_DELAY_ROUTING_KEY,
                millis,
                msg);

    }
}
