package com.lyve.service.object;

/**
 * Created by mmadhusoodan on 3/3/15.
 */
public class AgentObject {

    public AgentObject instance;
    public String agentId = "";

    public String deviceClass = "";
    public String displayName = "";
    public String devicePlatform = "";
    public String deviceType = "";

    public long storageCapacityTotalBytes ;
    public String lastSeen = "";

    public boolean wasOnline = false;
    public boolean isReplicationTarget = false;


    public Long imageCount = 0l;
    public Long videoCount = 0l;

    public synchronized AgentObject newInstance() {

        if (instance == null) {
            instance = new AgentObject();
        }
        return instance;
    }

}
