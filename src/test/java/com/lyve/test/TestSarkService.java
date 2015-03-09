package com.lyve.test;

import com.lyve.service.SarkService;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created by mmadhusoodan on 3/9/15.
 */
public class TestSarkService {

    Logger log = Logger.getLogger(AppTest.class);

    @Test
    public void testgetDeviceDetailsFromSarkClientWithMeshId() {

        String meshId = "DE12719E-F84F-484A-B7BB-3B49D11C1874";
        //String meshId = "E89EC84A-3A3E-44FD-8863-F510E2754ED0";

        //SarkService.getInstance().getAgentFromSarkClientWithEmail(email);
        SarkService.getInstance().getDeviceDetailsFromSarkClientWithMeshId(meshId);
    }

    @Test
    public void testgetDeviceDetailsFromSarkClientWithEmail() {

        //String email = "mmadhusoodan+emptymesh@lyveminds.com";
        //String email = "mmadhusoodan+multiple@lyveminds.com";
        //String email = "mmadhusoodan+morgan@lyveminds.com";
        //String email = "mmadhusoodan+ita@lyveminds.com";
        String email = "mmadhusoodan+avery@lyveminds.com";

        SarkService.getInstance().getAgentFromSarkClientWithEmail(email);
    }


}
