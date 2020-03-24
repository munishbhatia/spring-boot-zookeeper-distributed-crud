package com.mbhatia.springbootzookeeperdistributedcrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableSpringConfigured
@ComponentScan(basePackages = {"com.mbhatia.springbootzookeeperdistributedcrud.controllers",
                                "com.mbhatia.springbootzookeeperdistributedcrud.services",
                                "com.mbhatia.springbootzookeeperdistributedcrud.configuration",
                                "com.mbhatia.springbootzookeeperdistributedcrud.eventProcessors"})
public class SpringBootZookeeperDistributedCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootZookeeperDistributedCrudApplication.class, args);
    }

}
