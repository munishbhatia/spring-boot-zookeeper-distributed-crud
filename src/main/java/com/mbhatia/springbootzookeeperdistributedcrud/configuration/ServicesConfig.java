package com.mbhatia.springbootzookeeperdistributedcrud.configuration;

import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.PersonService;
import com.mbhatia.springbootzookeeperdistributedcrud.services.implementation.PersonServiceInMemoryImpl;
import com.mbhatia.springbootzookeeperdistributedcrud.services.implementation.PersonServiceJPAImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {
    @Bean(name = "InMemoryPersonService")
    public PersonService PersonServiceInMemoryImpl(){
        return new PersonServiceInMemoryImpl();
    }

    @Bean(name = "JPAPersonService")
    public PersonService PersonServiceJPAImpl(){
        return new PersonServiceJPAImpl();
    }
}
