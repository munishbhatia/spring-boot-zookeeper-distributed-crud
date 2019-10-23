package com.mbhatia.springbootzookeeperdistributedcrud.controllers;

import com.mbhatia.springbootzookeeperdistributedcrud.models.Person;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/persons")
public class PersonController {
    @GetMapping
    public List<Person> getPersons(){
        return Arrays.asList(new Person(1, "Person1"), new Person(2,"Person2"));
    }

    @PostMapping
    public ResponseEntity<String> postPerson(@RequestBody String person){
        return ResponseEntity.ok("SUCCESS");
    }

    private boolean amILeader(){
        return false;
    }
}
