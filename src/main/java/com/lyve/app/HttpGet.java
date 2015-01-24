package com.lyve.app;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import java.io.IOException;

public class HttpGet {

    private Logger log = Logger.getLogger(HttpGet.class);
    private static HttpGet instance;
    private String BaseURL = "https://graph.facebook.com/";
    private String accessToken = "CAAEz5omIYVQBANpTw2UPfEm1pXU6xZBO4UaNZBN4CyFVgTH1XZC5xPPLFZAq99rhyoy4ZAHwhSK6hw8dZBsNl58tHrZBK2dylfc0ZBiM0ZAKNO7ktE14MFbcWaT7qSaVXL9DI6W8l5Qb63jSuowdKYZBorMa5yoS7UV8Dms4otNFxCpr3k3cjpVYTS7GDZBoFaJbwniJXPiYn0iOLf2yUHXrcnr";
    private String query = "search?limit=10&q=India&type=adcity&";

    private HttpGet() {
    }

    public static synchronized HttpGet getInstance() {

        if (instance == null) {
            instance = new HttpGet();
        }

        return instance;
    }

    public void getRequest(){

        String URI = BaseURL + query + accessToken;
        HttpClient client = new HttpClient();

        HttpMethod method = new GetMethod(URI);

        try {
            client.executeMethod(method);

            if (method.getStatusCode() == HttpStatus.SC_OK) {

                log.info(method.getURI());
                String response = method.getResponseBodyAsString();
                log.info("Response = " + response);
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }

    }

    public static void main(String[] args) {

        HttpGet httpGet = HttpGet.getInstance();
        httpGet.getRequest();

    }
}
