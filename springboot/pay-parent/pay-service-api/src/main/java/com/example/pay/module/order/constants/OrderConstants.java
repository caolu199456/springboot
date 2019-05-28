package com.example.pay.module.order.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class OrderConstants {

    @AllArgsConstructor
    public enum PayType{
        ZFB(new Byte("1")),
        WX(new Byte("2")),
        SMALL_SOFT(new Byte("3"));
        @Getter@Setter
        private Byte payTypeValue;
    }
    @AllArgsConstructor
    public enum TerminalType{
        H5(new Byte("1")),
        APP(new Byte("2"));
        @Getter@Setter
        private Byte terminalTypeValue;
    }
    @AllArgsConstructor
    public enum Status {
        INIT(new Byte("0")),
        PAY_SUCCESS(new Byte("1")),
        REFUNDING(new Byte("2")),
        REFUND_SUCCESS(new Byte("3"));
        @Getter@Setter
        private Byte statusValue;
    }
}
