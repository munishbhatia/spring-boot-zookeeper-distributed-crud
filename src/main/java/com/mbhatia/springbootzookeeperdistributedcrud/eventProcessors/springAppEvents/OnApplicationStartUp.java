package com.mbhatia.springbootzookeeperdistributedcrud.eventProcessors.springAppEvents;

import com.mbhatia.springbootzookeeperdistributedcrud.models.ClusterInfo;
import com.mbhatia.springbootzookeeperdistributedcrud.models.Person;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.PersonService;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.ZookeeperService;
import com.mbhatia.springbootzookeeperdistributedcrud.services.implementation.ZookeeperServiceImpl;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OnApplicationStartUp implements ApplicationListener<ContextRefreshedEvent> {
    private ZookeeperService zookeeperService;
    private PersonService personService;
    private IZkChildListener allNodesChangeListener;
    private IZkChildListener liveNodesChangeListener;
    private IZkChildListener masterChangeListener;
    private IZkStateListener connectStateChangeListener;
    private RestTemplate restTemplate;
    @Value("${zookeeper.leader.election.algo}")
    private String leaderElectionAlgo;
    @Value("${request.urls.patterns.getPersonsRequestUrlFormat}")
    private String getPersonsRequestUrlFormat;

    public OnApplicationStartUp(ZookeeperService zookeeperService,
                                @Qualifier("InMemoryPersonService") PersonService personService,
                                @Qualifier("AllNodesChangeListener") IZkChildListener allNodesChangeListener,
                                @Qualifier("LiveNodesChangeListener") IZkChildListener liveNodesChangeListener,
                                @Qualifier("LeaderChangeListener") IZkChildListener masterChangeListener,
                                @Qualifier("ConnectStateChangeListener") IZkStateListener connectStateChangeListener,
                                RestTemplate restTemplate) {
        this.zookeeperService = zookeeperService;
        this.personService = personService;
        this.allNodesChangeListener = allNodesChangeListener;
        this.liveNodesChangeListener = liveNodesChangeListener;
        this.masterChangeListener = masterChangeListener;
        this.connectStateChangeListener = connectStateChangeListener;
        this.restTemplate = restTemplate;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        zookeeperService.createAllParentNodes();

        //Add this server to cluster by adding znode under /all_nodes
        zookeeperService.addToAllNodes(zookeeperService.getHostPort(), "cluster node");
        ClusterInfo.getClusterInfo().getAllNodes().clear();
        ClusterInfo.getClusterInfo().setAllNodes(zookeeperService.getAllNodes());

        //Elect zookeeper cluster leader
        if (!zookeeperService.masterExists()) {
            zookeeperService.electForMaster();
        }
        ClusterInfo.getClusterInfo().setMaster(zookeeperService.getLeaderNodeData());

        //Sync person data from master/leader node
        syncDataFromMaster();

        //Add child znode under /live_node, to tell other servers that this server is ready to serve
        zookeeperService.addToLiveNodes(zookeeperService.getHostPort(), "cluster node");
        ClusterInfo.getClusterInfo().getLiveNodes().clear();
        ClusterInfo.getClusterInfo().setLiveNodes(zookeeperService.getLiveNodes());

        //Register listeners for leader change, live nodes change, all nodes change and zk session state change
        zookeeperService.registerChildrenChangeWatcher(ZookeeperServiceImpl.ELECTION_NODE, masterChangeListener);
        zookeeperService.registerChildrenChangeWatcher(ZookeeperServiceImpl.LIVE_NODES, liveNodesChangeListener);
        zookeeperService.registerChildrenChangeWatcher(ZookeeperServiceImpl.ALL_NODES, allNodesChangeListener);
        zookeeperService.registerZkSessionStateListener(connectStateChangeListener);
    }

    private void syncDataFromMaster() {
        if(!zookeeperService.getHostPort().equals(ClusterInfo.getClusterInfo().getMaster())){
            //Request persons list
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            String getPersonsRequestUrl = String.format(getPersonsRequestUrlFormat, ClusterInfo.getClusterInfo().getMaster());
            HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);
            ResponseEntity<List<Person>> responseEntity;

            try {
                responseEntity =
                        restTemplate.exchange(getPersonsRequestUrl, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<Person>>() {});

                if (responseEntity.getStatusCode() != HttpStatus.OK) {
                    //TODO: Use retries before throwing exception
                    throw new RuntimeException("Error communicating with cluster master. Details: " + responseEntity.getStatusCode());
                }

                List<Person> dataSnapshotAtLeaderNode = responseEntity.getBody();
                personService.removeAllPeople();
                personService.saveAllPeople(dataSnapshotAtLeaderNode);
            } catch (Exception e){
                int i = 3;
                e.printStackTrace();
            }
        }
    }
}
