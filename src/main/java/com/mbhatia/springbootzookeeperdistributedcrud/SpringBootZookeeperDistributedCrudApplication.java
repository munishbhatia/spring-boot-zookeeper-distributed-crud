package com.mbhatia.springbootzookeeperdistributedcrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mbhatia.springbootzookeeperdistributedcrud.controllers",
                                "com.mbhatia.springbootzookeeperdistributedcrud.services",
                                "com.mbhatia.springbootzookeeperdistributedcrud.configuration"})
public class SpringBootZookeeperDistributedCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootZookeeperDistributedCrudApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
