package com.ddphin.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class DDphinRabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(DDphinRabbitmqApplication.class, args);
    }
}
