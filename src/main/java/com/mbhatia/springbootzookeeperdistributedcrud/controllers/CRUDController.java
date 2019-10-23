package com.mbhatia.springbootzookeeperdistributedcrud.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/persons")
public class CRUDController {
    @GetMapping
    public String getPersons(){
        return "Person";
    }

    @PostMapping
    public ResponseEntity<String> postPerson(@RequestBody String person){
        return ResponseEntity.ok("SUCCESS");
    }

    private boolean amILeader(){
        return false;
    }
}
