package com.example.util.redis.callback;

public interface TakeLeaderCallback {
    /**
     * 当获取到leader的时候需要的操作  一个集群只有一个leader 选举成功只掉一次
     */
    void onTakeLeader();

    /**
     * 当获取到Follower的时候需要的操作 每选举一次就有可能调用一次
     */
    default void onTakeFollower() {
        System.out.println("Get follower");
    }

    /**
     * 当失败后的操作
     *
     * @param e 失败后的对象
     */
    default void onFailed(Exception e) {
        e.printStackTrace();
    }
}
