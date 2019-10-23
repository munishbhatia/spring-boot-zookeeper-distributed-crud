package com.mbhatia.springbootzookeeperdistributedcrud.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public final class ClusterInfo {
    private static ClusterInfo clusterInfo = new ClusterInfo();
    private String master;
    /*Live nodes are ephemeral nodes*/
    private List<String> liveNodes = new ArrayList<>();
    /*These would be persistent nodes*/
    private List<String> allNodes = new ArrayList<>();

    public static ClusterInfo getClusterInfo(){
        return clusterInfo;
    }
}
