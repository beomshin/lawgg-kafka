package com.kr.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootTest
public class CallTest {


    @Test
    @DisplayName("call")
    public void call() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        int run = 200;

        for (int i = 0; i < 200; i++) {
            executorService.submit(() -> {
                RestTemplate restTemplate = new RestTemplate();

                String requestUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:23001/test/store/down2").queryParam("name", "item1").toUriString();

                String response = restTemplate.getForObject(requestUrl, String.class);

                System.out.println(response);
            });
            run--;
        }

        System.out.println("run : " + run);

        Thread.sleep(10000);
    }
}
