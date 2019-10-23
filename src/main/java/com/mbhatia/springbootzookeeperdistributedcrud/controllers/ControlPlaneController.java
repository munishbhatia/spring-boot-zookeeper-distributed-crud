package com.mbhatia.springbootzookeeperdistributedcrud.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/clusterInfo")
public class ControlPlaneController {
    @GetMapping
    public ResponseEntity<String> getClusterInfo(){
        return ResponseEntity.ok("Suc");
    }
}
