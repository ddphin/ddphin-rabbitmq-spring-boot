package com.ddphin.rabbitmq.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * DemoService
 *
 * @Date 2019/7/25 下午5:47
 * @Author ddphin
 */
@Service
public class DemoController {
    @Autowired
    private DemoService service;

    @PostConstruct
    public void testSender() {
        for (int i= 0; i < 10; ++i) {
            int finalI = (10 - i);
            new Thread(() -> {
                service.testSender(finalI * 10*1000, finalI + "s");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
