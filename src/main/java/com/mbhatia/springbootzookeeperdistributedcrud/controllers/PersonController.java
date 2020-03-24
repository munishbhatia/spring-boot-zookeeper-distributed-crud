package com.mbhatia.springbootzookeeperdistributedcrud.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mbhatia.springbootzookeeperdistributedcrud.models.ClusterInfo;
import com.mbhatia.springbootzookeeperdistributedcrud.models.Person;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.PersonService;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.ZookeeperService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/v1/persons")
public class PersonController {
    private PersonService personService;
    private ZookeeperService zookeeperService;
    private RestTemplate restTemplate;
    private String sendPostToSlaveNodesUrlFormat = "http://%s/v1/persons";

    public PersonController(@Qualifier(value = "InMemoryPersonService") PersonService personService,
                            ZookeeperService zookeeperService,
                            RestTemplate restTemplate){
        this.personService = personService;
        this.zookeeperService = zookeeperService;
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public List<Person> getPersons(){
        return personService.getPersons();
    }

    @PostMapping
    public ResponseEntity<String> postPerson(@RequestBody Person person,
                                             @RequestHeader String requestFrom){
        String leader = ClusterInfo.getClusterInfo().getMaster();

        /*If current node is the leader, update local copy and ask other nodes to update copy*/
        if(amILeader()){
            List<String> liveNodes = zookeeperService.getLiveNodes();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("requestFrom", leader);
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String requestBody;

            try{
                requestBody = ow.writeValueAsString(person);
            } catch (JsonProcessingException jpe){
                throw new RuntimeException("Unable to parse request body: " + person.toString());
            }

            int successCount = 0;
            for(String liveNode:liveNodes){
                //Live node is the current node which is also the leader
                if(zookeeperService.getHostPort().equals(liveNode)){
                    //Update local copy
                    personService.savePerson(person);
                    ++successCount;
                }
                else { //Send update to slave live node
                    String requestUrl = String.format(sendPostToSlaveNodesUrlFormat, liveNode);
                    HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
                    ResponseEntity<String> postResponseEntity = restTemplate.postForEntity(requestUrl, requestEntity, String.class);
                    if(postResponseEntity.getStatusCode().is2xxSuccessful())
                        ++successCount;
                }
            }
            return ResponseEntity.ok()
                    .body("Successfully update ".concat(String.valueOf(successCount)).concat(" nodes"));
        }
        /*If the request is from the leader/master, simply update local copy*/
        else if(!StringUtils.isEmpty(requestFrom) && requestFrom.equals(leader)){
            personService.savePerson(person);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else { //Forward the request to master/leader node
            String requestUrl = String.format(sendPostToSlaveNodesUrlFormat, leader);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("requestFrom", "178.168.99.1");
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String requestBody;

            try{
                requestBody = ow.writeValueAsString(person);
            } catch (JsonProcessingException jpe){
                throw new RuntimeException("Unable to parse request body: " + person.toString());
            }

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
            ResponseEntity<String> postResponseEntity = restTemplate.postForEntity(requestUrl, requestEntity, String.class);
            return postResponseEntity;
        }
    }

    private boolean amILeader(){
        return ClusterInfo.getClusterInfo().getMaster().equals(zookeeperService.getHostPort());
    }
}
