package com.lyve.service;

/**
 * Created by mmadhusoodan on 2/13/15.
 */

import com.lyve.service.object.AgentObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

public class SarkService extends AbsractServicesBaseClass {
    private static Logger log = Logger.getLogger(SarkService.class);

    private static SarkService instance;
    private static CloseableHttpClient httpclient;
    AgentObject agentObject;
    ArrayList<AgentObject> agentObjectList;

    String baseURL = "https://sark.dogfood.blackpearlsystems.net/sark/rest/v1";

    public static synchronized SarkService getInstance() {

        if (instance == null) {
            instance = new SarkService();
        }
        return instance;
    }

    public String getJSONFromFile(String file) throws Exception {

        String jsonData = "";
        BufferedReader br = null;
        try {
            String line;
            //br = new BufferedReader(new FileReader("meshStatsMultiple.json"));
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                jsonData += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return jsonData;
    }


    public String getJSONFromURL(CloseableHttpClient httpclient, String URL) throws Exception {

        HttpGet httpGet = new HttpGet(URL);
        String resultJSONString = "";
        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.addHeader("X-BP-Envelope", "EgIIARoBMQ==");
        httpGet.addHeader("X-BP-Token", token);

        CloseableHttpResponse httpResponse = httpclient.execute(httpGet);

        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        String httpStatusMessage = httpResponse.getStatusLine().toString();
        HttpEntity entity = httpResponse.getEntity();
        if (statusCode == HttpStatus.SC_OK) {

            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuffer resultJSON = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    resultJSON.append(line);
                }
                resultJSONString = resultJSON.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpResponse.close();
                httpGet.releaseConnection(); // stop connection
            }
        } else {
            log.info(statusCode);
        }
        //log.info("resultJSON: " + resultJSONString);
        EntityUtils.consume(entity);
        return resultJSONString;
    }

    public AgentObject getAgentDeviceDetailsWithMeshIdAndAgentId(String meshId, String agentId) {
        try {
            // Trust all certs
            SSLContext sslcontext = buildSSLContext();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
                    new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            //build a HTTP client with all the above
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            String URL = baseURL + "/" + meshId + "/" + "agent" + "/" + agentId;

            agentObject = new AgentObject();
            String resultJSON = getJSONFromURL(httpclient, URL);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(resultJSON);
            JSONObject level1Object = (JSONObject) obj;

            agentObject.agentId = level1Object.get("agent_id").toString();
            agentObject.displayName = level1Object.get("display_name").toString();
            agentObject.deviceClass = level1Object.get("device_class").toString();
            agentObject.deviceType = level1Object.get("device_type").toString();
            agentObject.devicePlatform = level1Object.get("device_platform").toString();
            agentObject.isReplicationTarget = (boolean) level1Object.get("is_replication_target");
            agentObject.storageCapacityTotalBytes = (long) level1Object.get("storage_capacity_total_bytes");


        } catch (IndexOutOfBoundsException iob) {
            iob.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return agentObject;
    }

    public void runSarkClientWithEmail(String email) {
        //takes email, gets the mesh_ids and gets agentObjectList which can then be iterated for each AgentObject
        try {
            // Trust all certs
            SSLContext sslcontext = buildSSLContext();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
                    new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            //build a HTTP client with all the above
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

            Map<String, ArrayList> accountServiceMap = AccountsService.getInstance().getAccountDetailsAsMapFromEmail(httpclient, email);
            if ((accountServiceMap.get("code")) != null) {
                log.info(accountServiceMap.get("message"));

            } else {

                ArrayList<String> meshIDs = accountServiceMap.get("mesh_ids");

                if (meshIDs.size() != 0) {
                    ArrayList<AgentObject> agentObjectList = DelhpiService.getInstance().getAgentObjectListFromMeshId(httpclient, meshIDs.get(0));

                    for (AgentObject agentObject : agentObjectList) {
                        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                        log.info("Agent Was Online: " + agentObject.wasOnline);
                        log.info("Agent Last Seen: " + agentObject.lastSeen);
                        log.info("Agent Type: " + agentObject.deviceClass);
                        log.info("Agent ID: " + agentObject.agentId);
                        log.info("Agent Image count: " + agentObject.imageCount);
                        log.info("Agent Video count: " + agentObject.videoCount);
                        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    }
                }
            }
        } catch (IndexOutOfBoundsException iob) {
            iob.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {

        //String email = "mmadhusoodan+emptymesh@lyveminds.com";
        //String email = "mmadhusoodan+multiple@lyveminds.com";
        //String email = "mmadhusoodan+morgan@lyveminds.com";
        //String email = "mmadhusoodan+ita@lyveminds.com";
        String email = "mmadhusoodan+events@lyveminds.com";

        String meshId = "DE12719E-F84F-484A-B7BB-3B49D11C1874";

        //SarkService.getInstance().runSarkClientWithEmail(email);
        ArrayList agentList = DelhpiService.getInstance().getAgentListWithMeshId("DE12719E-F84F-484A-B7BB-3B49D11C1874");
        String agentId = "";

        for (int i = 0; i < agentList.size(); i++) {
            agentId = agentList.get(i).toString();
            AgentObject agent = SarkService.getInstance().getAgentDeviceDetailsWithMeshIdAndAgentId(meshId, agentId);

            log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            log.info(agent.agentId);
            log.info(agent.deviceClass);
            log.info(agent.devicePlatform);
            log.info(agent.displayName);
            log.info(agent.deviceType);
            log.info(agent.isReplicationTarget);
            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
    }

}
