package com.ruoyi.quartz.task;


import org.springframework.stereotype.Component;

@Component
public class MyTask {

    public void showTime() {
        System.out.println("Current time: " + java.time.LocalDateTime.now());
    }
}
