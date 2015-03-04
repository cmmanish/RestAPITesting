package com.lyve.test;

import example.App;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AppTest {
    Logger log = Logger.getLogger(AppTest.class);

    @Test
    public void testGetIntCountFromString() {

        log.info("In testGetIntCountFromString()");
        App app = new App();
        String a = app.getIntCountFromString();
        assertEquals("Did not match the Count ", "50", a);
        log.info(a);
    }

    @Test
    public void testPatternMatch() {

        String reportTime = "reports/firefox_01-28-2015-03_22/screenshot7155091069562271739.png";
        reportTime = reportTime.split("screenshot")[0];
        reportTime = reportTime.split("[a-zA-Z]+_")[1];
        reportTime.replace("_", ":");
        log.info("reportTime " + reportTime);
        String boldGrayLine = "\033This is a BOLD line\033";
        log.info(boldGrayLine);

    }


}