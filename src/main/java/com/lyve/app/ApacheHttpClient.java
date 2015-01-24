package com.lyve.app;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ApacheHttpClient {

    private final static Logger log = Logger.getLogger(ApacheHttpClient.class);
    private static ApacheHttpClient instance;
    private String BaseURL = "https://accounts.test.blackpearlsystems.net";
    private String accountsByEmail = "/accounts/emails/";

    public static synchronized ApacheHttpClient getInstance() {
        if (instance == null) {
            instance = new ApacheHttpClient();
        }
        return instance;
    }

    public void accountExists(String encodedEmail) throws IOException {

        String URI = BaseURL + accountsByEmail + encodedEmail;
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(URI);

        // -H 'Content-Type: application/json' \
        //-H  'X-BP-Envelope: EgIIARoBMQ==' \
        getMethod.addRequestHeader("Content-Type", "application/json");
        getMethod.addRequestHeader("X-BP-Envelope", " EgIIARoBMQ==");

        try {
            httpClient.executeMethod(getMethod);
            String response = getMethod.getResponseBodyAsString();
            log.info(response.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws HttpException, IOException {

        String encodedEmail = "portal_test_01%40lyveminds.com";
        ApacheHttpClient.getInstance().accountExists(encodedEmail);

    }
}

