package com.lyve.test;

import com.lyve.app.App;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AppTest{
    Logger log = Logger.getLogger(AppTest.class);

    @Test
    public void testGetIntCountFromString(){

        log.info("In testGetIntCountFromString()");
        App app = new App();
        String a = app.getIntCountFromString();
        assertEquals("Did not match the Count ","50",a);
        log.info(a);

    }


}