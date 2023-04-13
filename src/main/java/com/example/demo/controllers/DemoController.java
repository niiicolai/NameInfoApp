package com.example.demo.controllers;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    
    /*
     * This is the time the slow endpoint will sleep.
     * 
     * It is in milliseconds.
     */
    private final int SLEEP_TIME = 1000*3;

    /*
     * This endpoint will return a random string.
     * 
     * It will not block the thread.
     * 
     * It will return a response in less than 3 seconds.
     *
     * The output will be something like:
     * 
     * iyxq2r2xUb,SO9HbKgbJ8,LauILxF37k
     */
    @GetMapping(value = "/random-string-slow")
    public String slowEndpoint() throws InterruptedException {
        Thread.sleep(SLEEP_TIME);
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
