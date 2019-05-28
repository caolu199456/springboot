package com.example.util.rocketmq.trans;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionProducer {
    public static void main(String[] args) throws MQClientException, InterruptedException, UnsupportedEncodingException {
        TransactionListener transactionListener = new TransactionListenerImpl();

        TransactionMQProducer producer = new TransactionMQProducer("TEST_TRANSACTION");
        producer.setNamesrvAddr("192.168.2.11:9876");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 100,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(200),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("client-transaction-msg-check-thread");

                        return thread;
                    }
                });

        producer.setExecutorService(executor);
        producer.setTransactionListener(transactionListener);
        producer.start();


        Message msg =
                new Message("TopicTest1234", "TAG", "KEY",
                        ("Hello RocketMQ ").getBytes(RemotingHelper.DEFAULT_CHARSET));
        SendResult sendResult = producer.sendMessageInTransaction(msg, null);

        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
        }
        producer.shutdown();
    }



    static class TransactionListenerImpl implements TransactionListener {

        Map<String, LocalTransactionState> executeResult = new ConcurrentHashMap<>();

        @Override
        public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            byte[] body = msg.getBody();
            String params = new String(body);
            System.out.println("收到body:"+params);

            executeResult.put(msg.getTransactionId(), LocalTransactionState.UNKNOW);

            return LocalTransactionState.UNKNOW;
        }

        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {

            System.out.println(new Date());

            LocalTransactionState localTransactionState = executeResult.get(msg.getTransactionId());
            System.out.println("checkLocalTransaction收到消息消费状态:" + localTransactionState);

            return LocalTransactionState.UNKNOW;
        }
    }
}

