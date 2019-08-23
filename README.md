# Rabbitmq 事务消息 & 延迟消息 & 通用消息接收器
- pom 引入
```$xslt
        <dependency>
            <groupId>com.github.ddphin</groupId>
            <artifactId>ddphin-rabbitmq-spring-boot-starter</artifactId>
            <version>1.0.0</version>
        </dependency>
```
## 功能
- 事务消息
<br>指生产者侧的事务消息发送。保证消息的事务性与可靠性发送，可能会出现重复消息，极端情况下消息会保存为死消息需要人工处理，对于重复消息，消费者端需要做到幂等性
  - 事务消息逻辑
      - spring 事务中发送消息
         - 缓存当前事务需要发送的消息
         - spring 事务提交之前保存消息`redis:data-normal` 并将id加入预发送队列`redis:id-prepare`，但不发送消息，预发送队列超时时间为60s
         - spring 事务成功提交之后,移动消息id到正式发送队列`redis:id-prepare -> redis:id-do`，并立即发送消息，正式送队列超时时间为60s
         - spring 事务完成后如果事务状态不等于`已提交`状态则移除保存的消息`redis:data-normal`和`redis:id-prepare`
      - 异步确认消息是否投递到rabbitmq
        - 发送到exchange成功，移除保存的消息`redis:data-normal`和`redis:id-do`
        - 发送到exchange失败，重新发送3次(第一次等1秒，第二次等2秒，第三次等3秒)，如果还是失败则将消息id移动到重试队列`redis:id-do -> redis:id-redo`，重试队列无超时时间
        - 发送到queue失败，保存死消息`redis:data-death`等待人工处理
      - 定时重新发送消息
      <br>每隔60s将消息id从重试队列移到正式发送队列`redis:id-redo -> redis:id-do`，并根据id从保存的消息`redis:data-normal`中取出消息进行发送
      - 定时刷新正式发送消息
      <br>每隔60s将已超时的消息id从正式发送队列移动到重试队列`redis:id-do -> redis:id-redo`
      - 定时清理预发送消息
      <br>每隔60s将预发送队列`redis:id-prepare`已超时的消息id移除，并将对应的消息保存为死消息`redis:data-normal -> redis:data-death`等待人工处理
      
  - 依赖:
      - [ddphin-redis](https://github.com/ddphin/ddphin-redis-spring-boot)：负责消息持久化
      - [ddphin-id](https://github.com/ddphin/ddphin-id-spring-boot)：负责消息id生成
      - spring 事务：负责消息事务管理
- 延迟消息
  - 依赖:
      - DLX
      - DLK
- 通用消息接收器

## 配置
rabbitmq 和 redis 配置，样例：
```$xslt
spring
  redis:
    database: 0
    host: xxx.xxx.xxx.xxx
    port: 16379
    password: ddphin

  rabbitmq:
    host: xxx.xxx.xxx.xxx
    port: 15674
    username: admin
    password: admin
    virtual-host: /
    connection-timeout: 15000
    template:
      mandatory: true
    listener:
      direct:
        acknowledge-mode: manual
      simple:
        acknowledge-mode: manual
        concurrency: 5
        max-concurrency: 10
    publisher-confirms: true
    publisher-returns: true
```
## 用法
### 事务消息
- 注入事务消息发送器
```$xslt
@Autowired
private RabbitmqCommonTxMessageSender rabbitmqCommonTxMessageSender;
```
- 发送普通消息
```$xslt
rabbitmqCommonTxMessageSender.send(String exchange, String routingKey, final Object message)
```
- 发送延迟消息
```$xslt
rabbitmqCommonTxMessageSender.send(String exchange, String routingKey, Long millis, final Object message)
```
## 通用延时消息
- 自定义消息类
```$xslt
@Data
public class DDphinMessage {
    private Long id;
    private String data;
    private Date time;    
    public DDphinMessage(Long id, String data, Date time) {
        this.id = id;
        this.data = data;
        this.time = time;
    }
}
```
- 创建待发送消息
```$xslt
DDphinMessage message = new DDphinMessage(1L, "ddphin", new Date());
```
- 发送延时消息
```$xslt
rabbitmqCommonTxMessageSender.send(
    RabbitmqCommonDelayQueueAutoConfiguration.SENDER_COMMON_DELAY_EXCHANGE,
    RabbitmqCommonDelayQueueAutoConfiguration.SENDER_COMMON_DELAY_ROUTING_KEY,
    5*1000L,
    message);
```
- 实现消息处理器<br>
基于延时消息监听器`RabbitmqCommonDelayQueueReceiver`和自定义消息类`DDphinMessage`实现消息处理器<br>
只需要继承`RabbitmqCommonQueueReceiverHandlerRegister<H,T>`并实现`RabbitmqCommonQueueReceiverHandler<T>`接口
  - H: 消息监听器：`RabbitmqCommonDelayQueueReceiver`
  - T: 自定义消息类：`DDphinMessage`
```$xslt
@Slf4j
@Service
public class RabbitmqDelayDDphinMessageReceiverHandler
        extends RabbitmqCommonQueueReceiverHandlerRegister<RabbitmqCommonDelayQueueReceiver, DDphinMessage>
        implements RabbitmqCommonQueueReceiverHandler<DDphinMessage> {

    @Override
    public Boolean process(DDphinMessage message) {
        log.info("RabbitmqDelayDDphinMessageReceiverHandler:\n" +
                "    message: {}", JSONObject.toJSONString(message));
        return null;
    }
}
```

## 通用消息接处理
- 自定义消息监听器<br>
只需要继承`RabbitmqCommonAbstractQueueReceiver`并实现`RabbitmqCommonQueueReceiver`接口<br>
指定监听队列`@RabbitListener(queues = "xxxxx"})`,<br>
然后调用`super.receiver(message, amqpMessage, channel)`即可
```$xslt
@Service
public class DDphinMessageQueueReceiver
        extends RabbitmqCommonAbstractQueueReceiver
        implements RabbitmqCommonQueueReceiver {
    @Override
    @RabbitListener(queues = "DDphinMessageQueue"})
    public void receiver(org.springframework.messaging.Message message, 
                        org.springframework.amqp.core.Message amqpMessage, 
                        Channel channel) throws IOException {
        super.receiver(message, amqpMessage, channel);
    }
}
```
- 自定义消息类 与【通用延时消息 > 自定义消息类】类似
- 创建待发送消息 与【通用延时消息 > 创建待发送消息】类似
- 发送消息 与【通用延时消息 > 发送延时消息】类似
- 实现消息处理器<br>
基于自定义的消息监听器`DDphinMessageQueueReceiver`和自定义的消息类实现消息处理器 与【通用延时消息 > 实现消息处理器】类似
