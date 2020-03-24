package com.mbhatia.springbootzookeeperdistributedcrud.services.implementation;

import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.ZookeeperService;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.ZooDefs;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

//@Service
public class ZookeeperServiceImpl implements ZookeeperService {
    private ZkClient zookeeperClient;
    private static String ipPort = null;

    public static final String ELECTION_MASTER = "/election/master";
    public static final String ELECTION_NODE = "/election";
    public static final String ELECTION_NODE_2 = "/election2";
    public static final String LIVE_NODES = "/liveNodes";
    public static final String ALL_NODES = "/allNodes";

    public ZookeeperServiceImpl(String hostPort) {
        zookeeperClient = new ZkClient(hostPort, 12000*100, 3000);
    }

    public void closeConnection(){
        zookeeperClient.close();
    }

    @Override
    public String getLeaderNodeData() {
        return zookeeperClient.readData(ELECTION_MASTER, true);
    }

    @Override
    public void electForMaster() {
        if(!zookeeperClient.exists(ELECTION_NODE))
            zookeeperClient.createPersistent(ELECTION_NODE, "election node");
        try {
            zookeeperClient.createEphemeral(ELECTION_MASTER, getHostPort(), ZooDefs.Ids.OPEN_ACL_UNSAFE);
        } catch (ZkNodeExistsException nee){
            nee.printStackTrace();
        }
    }

    @Override
    public boolean masterExists() {
        return zookeeperClient.exists(ELECTION_MASTER);
    }

    @Override
    public void addToLiveNodes(String nodeName, String data) {
        String nodePath = LIVE_NODES.concat("/").concat(nodeName);
        if(!zookeeperClient.exists(LIVE_NODES)){
            zookeeperClient.createPersistent(LIVE_NODES, "all live nodes are displayed here");
        }

        if(zookeeperClient.exists(nodePath))
            return;
        zookeeperClient.createEphemeral(nodePath, data, ZooDefs.Ids.OPEN_ACL_UNSAFE);
    }

    @Override
    public List<String> getLiveNodes() {
        if (!zookeeperClient.exists(LIVE_NODES)) {
            throw new RuntimeException("No node /liveNodes exists");
        }
        return zookeeperClient.getChildren(LIVE_NODES);
    }

    @Override
    public void addToAllNodes(String nodeName, String data) {
        String nodePath = ALL_NODES.concat("/").concat(nodeName);
        if(!zookeeperClient.exists(ALL_NODES)){
            zookeeperClient.createPersistent(ALL_NODES, "all live nodes are displayed here", ZooDefs.Ids.OPEN_ACL_UNSAFE);
        }
        if(zookeeperClient.exists(nodePath))
            return;
        zookeeperClient.createPersistent(nodePath, data, ZooDefs.Ids.OPEN_ACL_UNSAFE);
    }

    @Override
    public List<String> getAllNodes() {
        if(!zookeeperClient.exists(ALL_NODES)){
            throw new RuntimeException("Node " + ALL_NODES + " does not exists");
        }
        return zookeeperClient.getChildren(ALL_NODES);
    }

    @Override
    public void deleteNodeFromCluster(String node) {
        zookeeperClient.delete(ALL_NODES.concat("/").concat(node));
        zookeeperClient.delete(LIVE_NODES.concat("/").concat(node));
    }

    @Override
    public void createAllParentNodes() {
        if (!zookeeperClient.exists(ALL_NODES)) {
            zookeeperClient.createPersistent(ALL_NODES, "all live nodes are displayed here");
        }
        if (!zookeeperClient.exists(LIVE_NODES)) {
            zookeeperClient.createPersistent(LIVE_NODES, "all live nodes are displayed here");
        }
        if (!zookeeperClient.exists(ELECTION_NODE)) {
            zookeeperClient.createPersistent(ELECTION_NODE, "election node");
        }
    }

    @Override
    public String getZNodeData(String path) {
        if(!zookeeperClient.exists(path)){
            throw new RuntimeException("Node " + path + " does not exist");
        }
        return zookeeperClient.readData(path);
    }

    @Override
    public void addToElectionNode(String data) {
        if (!zookeeperClient.exists(ELECTION_NODE_2)) {
            zookeeperClient.createPersistent(ELECTION_NODE_2, "election node");
        }
        zookeeperClient.createEphemeralSequential(ELECTION_NODE_2.concat("/node"), data);
    }

    @Override
    public void registerChildrenChangeWatcher(String path, IZkChildListener iZkChildListener) {
        zookeeperClient.subscribeChildChanges(path, iZkChildListener);
    }

    @Override
    public void registerZkSessionStateListener(IZkStateListener iZkStateListener) {
        zookeeperClient.subscribeStateChanges(iZkStateListener);
    }

    public String getHostPort(){
        String ip;
        if(ipPort != null)
            return ipPort;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException uhe){
            throw new RuntimeException("Unable to fetch Host IP", uhe);
        }
        ipPort = ip.concat(":").concat(System.getProperty("server.port"));
        return ipPort;
    }
}
