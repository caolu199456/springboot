package com.example.util.factory;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CuratorUtils {

    public static CuratorFramework getLockClient(String zkHost,String namespace) {
        CuratorFramework client = CuratorFrameworkFactory
                .builder()
                .connectString(zkHost)
                .retryPolicy(new ExponentialBackoffRetry(5000, 3))
                .namespace(namespace)
                .build();

        client.start();

        return client;
    }

    public static void main(String[] args) throws Exception {
        /*CuratorFramework client = getLockClient("localhost:2181","app_lock");
        for (int i = 0; i < 10000; i++) {
            InterProcessMutex interProcessMutex = new InterProcessMutex(client, "/a");
            interProcessMutex.acquire();
            System.out.println(111);
            interProcessMutex.release();
        }

        CloseableUtils.closeQuietly(client);*/

        CountDownLatch countDownLatch = new CountDownLatch(1);

        CuratorFramework client = getLockClient("localhost:2181","task-leader-select");

        LeaderSelector leaderSelector = new LeaderSelector(client, "/task-1", new LeaderSelectorListenerAdapter() {

            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {
                // we are now the leader. This method should not return until we want to relinquish leadership
                System.out.println("I am master");
                countDownLatch.await();
            }
        });
        //可以重新获取充值券 如果没有则不能重新加入集群
        leaderSelector.autoRequeue();


        leaderSelector.start();

        Thread.sleep(1000000);


    }

}
