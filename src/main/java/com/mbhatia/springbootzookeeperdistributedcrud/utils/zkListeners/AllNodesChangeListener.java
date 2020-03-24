package com.mbhatia.springbootzookeeperdistributedcrud.utils.zkListeners;

import com.mbhatia.springbootzookeeperdistributedcrud.models.ClusterInfo;
import org.I0Itec.zkclient.IZkChildListener;

import java.util.List;

public class AllNodesChangeListener implements IZkChildListener {
    @Override
    public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
        ClusterInfo.getClusterInfo().getAllNodes().clear();
        ClusterInfo.getClusterInfo().setAllNodes(currentChildren);
    }
}
