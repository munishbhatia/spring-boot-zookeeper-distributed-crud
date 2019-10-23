package com.mbhatia.springbootzookeeperdistributedcrud.controllers;

import com.mbhatia.springbootzookeeperdistributedcrud.models.ClusterInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/clusterInfo")
public class ControlPlaneController {
    @GetMapping
    public ResponseEntity<ClusterInfo> getClusterInfo(){
        return ResponseEntity.ok(ClusterInfo.getClusterInfo());
    }
}
