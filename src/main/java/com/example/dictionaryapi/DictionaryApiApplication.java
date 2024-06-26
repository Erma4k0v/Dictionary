package com.example.dictionaryapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.dictionaryapi")
public class DictionaryApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DictionaryApiApplication.class, args);
    }
}
