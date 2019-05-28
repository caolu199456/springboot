package com.example.pay;

import com.example.BaseTest;
import com.example.pay.module.order.dto.OrderDto;
import com.example.pay.module.order.service.OrderService;
import com.example.util.kit.OrderUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class PayTest extends BaseTest {
    @Autowired
    OrderService orderService;

    @Test
    public void testOrderUtils() {
        OrderDto orderDto = new OrderDto();
        orderDto.setOutTradeNo("bbb");
        orderService.save(orderDto);
    }
}
