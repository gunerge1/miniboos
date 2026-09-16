package com.miniboos;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.miniboos.mapper")
public class MiniboosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniboosApplication.class, args);
    }
}
