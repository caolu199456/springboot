package com.example.util.kit;

import java.text.SimpleDateFormat;

public class OrderUtils {
    /**
     *  高性能产生分布式不重复id
     * @return 25位的不重复订单号
     */
    public static String getOrderNo() {

        long nextId = SnowFlake.nextId();
        SnowFlake.IdParseInfo parse = SnowFlake.parse(nextId);

        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(parse.getCreateTime()));
        sb.append(String.format("%04d", parse.getMachineId()));
        sb.append(String.format("%04d", parse.getSequence()));
        return sb.toString();
    }
}
