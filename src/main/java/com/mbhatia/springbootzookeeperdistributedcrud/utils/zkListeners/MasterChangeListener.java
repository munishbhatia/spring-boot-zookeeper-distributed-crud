package com.mbhatia.springbootzookeeperdistributedcrud.utils.zkListeners;

import com.mbhatia.springbootzookeeperdistributedcrud.models.ClusterInfo;
import com.mbhatia.springbootzookeeperdistributedcrud.services.contract.ZookeeperService;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.List;

public class MasterChangeListener implements IZkChildListener {
    private ZookeeperService zookeeperService;

    public MasterChangeListener(ZookeeperService zookeeperService) {
        this.zookeeperService = zookeeperService;
    }

    @Override
    public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
        if(currentChildren.isEmpty()){
            System.out.println("Master node down, attempting new election");
            ClusterInfo.getClusterInfo().setMaster(null);

            try {
                zookeeperService.electForMaster();
            } catch (ZkNodeExistsException nee){
                System.out.println("Master node already exists. Master: " + ClusterInfo.getClusterInfo().getMaster());
            }
        } else {
            String leader = zookeeperService.getLeaderNodeData();
            System.out.println("Updating Leader Node to " + leader);
            ClusterInfo.getClusterInfo().setMaster(leader);
        }

    }
}
