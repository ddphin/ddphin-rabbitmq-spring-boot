package com.ddphin.rabbitmq.demo.service;

import com.ddphin.rabbitmq.sender.RabbitmqCommonDelayQueueSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * DemoService
 *
 * @Date 2019/7/25 下午5:47
 * @Author ddphin
 */
@Service
public class DemoService {
    @Autowired
    private RabbitmqCommonDelayQueueSender sender;

    @PostConstruct
    public void testSender() {
        this.sender.send(new Date(), 5*1000L);
        this.sender.send("ddphin", 10*1000L);

    }
}
