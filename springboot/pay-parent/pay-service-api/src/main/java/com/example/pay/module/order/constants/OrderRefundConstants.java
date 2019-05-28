package com.example.pay.module.order.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class OrderRefundConstants {

    @AllArgsConstructor
    public enum RefundType{
        //自动结算
        AUTO(new Byte("1")),
        //人工
        PERSON(new Byte("2"));
        @Getter@Setter
        private Byte terminalTypeValue;
    }
    @AllArgsConstructor
    public enum OrderRefundStatus{
        REFUNDING(new Byte("1")),
        REFUND_SUCCESS(new Byte("2"));
        @Getter@Setter
        private Byte orderRefundStatusValue;
    }
}
