package com.example.mobile.controller;

import com.example.mobile.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class WelcomeController extends BaseController {
    AtomicInteger atomicInteger = new AtomicInteger();
    @GetMapping("/")
    public String welcome() throws InterruptedException {
        System.out.println(atomicInteger.incrementAndGet());
        return "base-service-api1";
    }
}
