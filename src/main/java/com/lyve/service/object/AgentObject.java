package com.lyve.service.object;

/**
 * Created by mmadhusoodan on 3/3/15.
 */
public class AgentObject {

    private static AgentObject instance;
    public String agentId = "";
    public Long imageCount = 0l;
    public Long videoCount = 0l;
    public String deviceClass = "";
    public String lastSeen = "";
    public boolean wasOnline = false;

    public static synchronized AgentObject getInstance() {

        if (instance == null) {
            instance = new AgentObject();
        }
        return instance;
    }

}
