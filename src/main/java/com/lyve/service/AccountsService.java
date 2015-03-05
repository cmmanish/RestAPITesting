package com.lyve.service;

/**
 * Created by mmadhusoodan on 2/13/15.
 */

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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

public class AccountsService extends AbsractServicesBaseClass {
    private static Logger log = Logger.getLogger(AccountsService.class);

    private static AccountsService instance;

    public static synchronized AccountsService getInstance() {
        log.info("In AccountsService() constructor");
        if (instance == null) {
            instance = new AccountsService();
        }
        return instance;
    }

    public Map<String, ArrayList> getAccountDetailsAsMapFromEmail(CloseableHttpClient httpclient, String email) throws Exception {

        String URL = "https://accounts.dogfood.blackpearlsystems.net/accounts/emails/" + email;
        HttpGet httpget = new HttpGet(URL);
        Map<String, ArrayList> AccountsJSONArrayList = null;
        ObjectMapper jsonMapper = new ObjectMapper();

        log.info("Executing Request: " + httpget.getRequestLine());

        CloseableHttpResponse httpResponse = httpclient.execute(httpget);
        try {
            HttpEntity entity = httpResponse.getEntity();

            log.info("----------------------------------------");
            log.info(httpResponse.getStatusLine());
            if (entity != null) {
                log.info("Response content length: " + entity.getContentLength());
            }
            for (Header header : httpResponse.getAllHeaders()) {
                //log.info(header);
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            log.info(result);
            AccountsJSONArrayList = jsonMapper.readValue(result.toString(), HashMap.class);
            if (AccountsJSONArrayList.get("code") != null) {
                log.info(AccountsJSONArrayList.get("message"));
                return AccountsJSONArrayList;
            } else {
                log.info("account_id: " + AccountsJSONArrayList.get("account_id"));
                log.info("email_primary: " + AccountsJSONArrayList.get("email_primary"));
                ArrayList<String> meshIDs = AccountsJSONArrayList.get("mesh_ids");
                log.info("mesh_ids: " + meshIDs);
                ArrayList<String> agentList = AccountsJSONArrayList.get("agent_ids");
                log.info("Agents Count: " + agentList.size());
                for (int j = 0; j < agentList.size(); j++) {
                    log.info("agent_ids:[" + j + "] " + agentList.get(j));
                }
                //return true;
            }

            EntityUtils.consume(entity);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpResponse.close();
        }
        return AccountsJSONArrayList;
    }

    public void runAccountsClient(String email) throws Exception {

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
        Map<String, ArrayList> accounts = AccountsService.getInstance().getAccountDetailsAsMapFromEmail(httpclient, email);
        log.info("mesh_ids: " + accounts.get("mesh_ids"));


    }

    public static void main(String[] args) throws Exception {

        String email = "mmadhusoodan+emptymesh@lyveminds.com";
        //String email = "mmadhusoodan+multiple@lyveminds.com";
        //String email = "mmadhusoodan+events@lyveminds.com";

        AccountsService.getInstance().runAccountsClient(email);
    }
}