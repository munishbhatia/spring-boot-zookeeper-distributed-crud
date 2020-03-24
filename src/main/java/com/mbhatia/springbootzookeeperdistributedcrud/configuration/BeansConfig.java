package com.mbhatia.springbootzookeeperdistributedcrud.configuration;

import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.PersonService;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.ZookeeperService;
import com.mbhatia.springbootzookeeperdistributedcrud.services.implementation.PersonServiceInMemoryImpl;
import com.mbhatia.springbootzookeeperdistributedcrud.services.implementation.PersonServiceJPAImpl;
import com.mbhatia.springbootzookeeperdistributedcrud.services.implementation.ZookeeperServiceImpl;
import com.mbhatia.springbootzookeeperdistributedcrud.utils.zkListeners.AllNodesChangeListener;
import com.mbhatia.springbootzookeeperdistributedcrud.utils.zkListeners.ConnectStateChangeListener;
import com.mbhatia.springbootzookeeperdistributedcrud.utils.zkListeners.LiveNodesChangeListener;
import com.mbhatia.springbootzookeeperdistributedcrud.utils.zkListeners.MasterChangeListener;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeansConfig {
    @Bean(name = "InMemoryPersonService")
    public PersonService personServiceInMemoryImpl(){
        return new PersonServiceInMemoryImpl();
    }

    @Bean(name = "JPAPersonService")
    public PersonService personServiceJPAImpl(){
        return new PersonServiceJPAImpl();
    }

    @Bean
    public ZookeeperService zookeeperService() {
        return new ZookeeperServiceImpl("localhost:2181");
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean(name = "AllNodesChangeListener")
    public IZkChildListener allNodesChangeListener(){
        return new AllNodesChangeListener();
    }

    @Bean(name = "LiveNodesChangeListener")
    public IZkChildListener liveNodesChangeListener(){
        return new LiveNodesChangeListener();
    }

    @Bean(name = "LeaderChangeListener")
//    @ConditionalOnProperty(name = "zookeeper.leader.election.algo", havingValue = "1")
    public IZkChildListener MasterChangeListener(){
        return new MasterChangeListener(zookeeperService());
    }

    @Bean(name = "ConnectStateChangeListener")
    public IZkStateListener ConnectStateChangeListener(){
        return new ConnectStateChangeListener(zookeeperService(), personServiceInMemoryImpl(), restTemplate());
    }
}
