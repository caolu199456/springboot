package com.example.util.factory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态获取logger-factory
 *
 * @author: CL
 * @date: 2019-01-16 12:11:00
 */
public class DynamicLogUtils {
    /**
     * 根据名字获取文件
     */
    private static final Map<String, Logger> LOGGER_MAP = new ConcurrentHashMap<>();

    /**
     * 得到logger实例
     * @param loggerName 尽量用常量
     * @param namePattern 尽量用常量 d:/order%d{yyyyMMddHHmm}.log 里边的表达式尽量不要带空格
     * @param maxHistory 日志保存天数尽量用常量
     * @return
     */
    public static synchronized Logger getInstance(String loggerName,
                                                  String namePattern,
                                                  Integer maxHistory) {

        Logger logger = LOGGER_MAP.get(loggerName);
        if (logger != null) {
            return logger;
        }
        Logger build = build(loggerName, namePattern, maxHistory);
        LOGGER_MAP.put(loggerName, build);
        return build;
    }

    private static Logger build(String loggerName,
                                String namePattern,
                                int maxHistory
    ) {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(loggerName);

        RollingFileAppender rollingFileAppender = new RollingFileAppender();
        rollingFileAppender.setContext(context);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setPrudent(false);

        TimeBasedRollingPolicy timeBasedRollingPolicy = new TimeBasedRollingPolicy();
        timeBasedRollingPolicy.setContext(context);
        timeBasedRollingPolicy.setFileNamePattern(namePattern);
        timeBasedRollingPolicy.setMaxHistory(maxHistory);
        timeBasedRollingPolicy.setParent(rollingFileAppender);
        timeBasedRollingPolicy.start();

        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setContext(context);
//        patternLayoutEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n");
        patternLayoutEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %msg%n");
        patternLayoutEncoder.start();

        rollingFileAppender.setRollingPolicy(timeBasedRollingPolicy);
        rollingFileAppender.setEncoder(patternLayoutEncoder);
        rollingFileAppender.start();

        logger.addAppender(rollingFileAppender);

        return logger;
    }

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        for (int i = 0; i < 30; i++) {
            Logger test1 = getInstance("test1", "d:/order%d{yyyy-MM}.log", 1);
            for (int j = 0; j < 10000; j++) {

                test1.debug("1111");
            }
        }

        System.out.println(System.currentTimeMillis()-l);

    }

}
