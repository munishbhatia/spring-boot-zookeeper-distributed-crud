package com.mbhatia.springbootzookeeperdistributedcrud.utils.zkListeners;

import com.mbhatia.springbootzookeeperdistributedcrud.models.ClusterInfo;
import org.I0Itec.zkclient.IZkChildListener;

import java.util.List;

public class LiveNodesChangeListener implements IZkChildListener {
    @Override
    public void handleChildChange(String parent, List<String> childNodes) throws Exception {
        ClusterInfo.getClusterInfo().getLiveNodes().clear();
        ClusterInfo.getClusterInfo().setLiveNodes(childNodes);
    }
}
