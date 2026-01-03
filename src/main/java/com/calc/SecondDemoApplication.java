package com.calc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SecondDemoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SecondDemoApplication.class, args);
    }

}
