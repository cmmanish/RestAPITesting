package com.lyve.service;

/**
 * Created by mmadhusoodan on 2/13/15.
 */


import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SarkService extends AbsractServicesBaseClass {
    private Logger log = Logger.getLogger(SarkService.class);

    private String token = "MSwxLFJEajcxcGw4bW5Fdm9hdFp4K25keUE9PSxVYXBvWVF4N2pFUFFVYWlJL01aUnlkREpGbWlCRm9hTmtTdGU1YlFSNEs4QWE3bnEwY3Q1Qnh4WFREZ2l1QXFqZS85TVF0bVI2QW1VNXc3c2JBVitTTkJYb1pGclMzNGZxV25IV2pzc1BkRDBQb3dsQXJDaDdPalM1VXB2SDlxbkVDQzdOWnZZUWx4SUREYlNFcTNaRXh3TzlqNUVzQU9Zb2RXcjVVcWZqUVU9";
    private static SarkService instance;

    public static synchronized SarkService getInstance() {
        if (instance == null) {
            instance = new SarkService();
        }
        return instance;
    }

    public void getMeshInfoFromMeshId(CloseableHttpClient httpclient, String meshId) throws Exception {

        String URL = "https://sark.dogfood.blackpearlsystems.net/sark/rest/v1" + meshId;
        Map<String, ArrayList> JSONResponse;
        ObjectMapper jsonMapper = new ObjectMapper();
        HttpGet httpGet = new HttpGet(URL);

        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.addHeader("X-BP-Envelope", "EgIIARoBMQ==");
        httpGet.addHeader("X-BP-Token", token);

        log.info("Executing Request: " + httpGet.getRequestLine());

        CloseableHttpResponse httpResponse = httpclient.execute(httpGet);

        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        log.info("statusCodes: " + statusCode);

        String httpStatusMessage = httpResponse.getStatusLine().toString();
        HttpEntity entity = httpResponse.getEntity();

        log.info("----------------------------------------");
        if (entity != null) {
            log.info("Response content length: " + entity.getContentLength());
        }

        for (Header header : httpResponse.getAllHeaders()) {
            log.info(header);
        }
        log.info("----------------------------------------");
        switch (statusCode) {

            case HttpStatus.SC_NOT_ACCEPTABLE:
                log.info("406 Not Acceptable (HTTP/1.1 - RFC 2616)");
                break;
            case HttpStatus.SC_OK:
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                    StringBuffer result = new StringBuffer();
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }

                    log.info(result);
                    JSONResponse = jsonMapper.readValue(result.toString(), HashMap.class);
                    if (JSONResponse.get("code") != null) {
                        log.info(JSONResponse.get("message"));
                        //return false;
                    } else {
                        log.info("account_id: " + JSONResponse.get("account_id"));
                        log.info("email_primary: " + JSONResponse.get("email_primary"));
                        ArrayList<String> meshIDs = JSONResponse.get("mesh_ids");
                        log.info("mesh_ids: " + meshIDs);
                        ArrayList<String> agentList = JSONResponse.get("agent_ids");
                        log.info("Agents Count: " + agentList.size());
                        for (int j = 0; j < agentList.size(); j++) {
                            log.info("agent_ids:[" + j + "] " + agentList.get(j));
                        }
                        //return true;
                    }

                    EntityUtils.consume(entity);

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    httpResponse.close();
                    httpGet.releaseConnection(); // stop connection
                }
                break;
        }
    }

    public static void main(String[] args) throws Exception {

        try {
            // Trust all certs
            SSLContext sslcontext = buildSSLContext();

            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            //build a HTTP client with all the above
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        String email = "mmadhusoodan+avery@lyveminds.com";
        //ArrayList<String> meshIDs = AccountsService.getInstance().getAccountDetailsFromEmail(httpclient, email).get("mesh_ids");

        SarkService.getInstance().getMeshInfoFromMeshId(httpclient, "EE9A3D25-D9AC-4763-B244-887FCE7183C2");

    }


}