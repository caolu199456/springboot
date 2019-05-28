package com.example;

import com.example.pay.PayServer;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServer.class)
@WebAppConfiguration
public class BaseTest {

    protected static final Logger LOGGER = Logger.getLogger(BaseTest.class);
    @Autowired
    protected Gson gson;
    @Test
    public void run() {

    }
}
