package com.mbhatia.springbootzookeeperdistributedcrud.services.contract;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;

import java.util.List;

public interface ZookeeperService {
    String getLeaderNodeData();
    void electForMaster();
    boolean masterExists();
    void addToLiveNodes(String nodeName, String data);
    List<String> getLiveNodes();
    void addToAllNodes(String nodeName, String data);
    List<String> getAllNodes();
    void deleteNodeFromCluster(String node);
    void createAllParentNodes();
    String getZNodeData(String path);
    void addToElectionNode(String data);
    void registerChildrenChangeWatcher(String path, IZkChildListener iZkChildListener);
    void registerZkSessionStateListener(IZkStateListener iZkStateListener);
    String getHostPort();
}
