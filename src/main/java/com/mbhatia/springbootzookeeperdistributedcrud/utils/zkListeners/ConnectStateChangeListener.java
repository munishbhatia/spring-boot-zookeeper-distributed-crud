package com.mbhatia.springbootzookeeperdistributedcrud.utils.zkListeners;

import com.mbhatia.springbootzookeeperdistributedcrud.models.ClusterInfo;
import com.mbhatia.springbootzookeeperdistributedcrud.models.Person;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.PersonService;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.ZookeeperService;
import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

//@Configurable(autowire = Autowire.BY_TYPE)
public class ConnectStateChangeListener implements IZkStateListener {
//    @Autowired
    private ZookeeperService zkService;
    private RestTemplate restTemplate;
//    @Autowired
//    @Qualifier("InMemoryPersonService")
    private PersonService personService;
    @Value("${request.urls.patterns.getPersonsRequestUrlFormat}")
    private String getPersonsRequestUrlFormat;
    @Value("${zookeeper.leader.election.algo}")
    private String zkLeaderElectionAlgorithm;

//    @Autowired
    public ConnectStateChangeListener(ZookeeperService zkService,
                                      PersonService personService,
                                      RestTemplate restTemplate) {
        this.zkService = zkService;
        this.personService = personService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void handleStateChanged(Watcher.Event.KeeperState keeperState) {
        System.out.println(keeperState.name()); // 1. disconnected, 2. expired, 3. SyncConnected
    }

    @Override
    public void handleNewSession() {
        System.out.println("New session established");
        syncDataFromMaster();

        //Add new node to /live_nodes
        zkService.addToLiveNodes(zkService.getHostPort(), "cluster node");
        ClusterInfo.getClusterInfo().getLiveNodes().clear();
        ClusterInfo.getClusterInfo().setLiveNodes(zkService.getLiveNodes());

        //Attempt leader election
        if (!zkService.masterExists()) {
            zkService.electForMaster();
        } else {
            ClusterInfo.getClusterInfo().setMaster(zkService.getLeaderNodeData());
        }
    }

    @Override
    public void handleSessionEstablishmentError(Throwable throwable) {
        System.out.println("Could not establish session");
    }

    private void syncDataFromMaster(){
        if(!zkService.getHostPort().equals(ClusterInfo.getClusterInfo().getMaster())){
            //Request persons list
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            String getPersonsRequestUrl = String.format(getPersonsRequestUrlFormat, ClusterInfo.getClusterInfo().getMaster());
            HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

            ResponseEntity<List<Person>> responseEntity =
                    restTemplate.exchange(getPersonsRequestUrl, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<Person>>(){});

            if (responseEntity.getStatusCode() != HttpStatus.OK){
                //TODO: Use retries before throwing exception
                throw new RuntimeException("Error communicating with cluster master. Details: " + responseEntity.getStatusCode());
            }

            List<Person> dataSnapshotAtLeaderNode = responseEntity.getBody();
            personService.removeAllPeople();
            personService.saveAllPeople(dataSnapshotAtLeaderNode);
        }
    }
}
