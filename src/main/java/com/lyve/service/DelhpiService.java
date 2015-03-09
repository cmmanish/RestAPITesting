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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class DelhpiService extends AbsractServicesBaseClass {
    private static Logger log = Logger.getLogger(DelhpiService.class);

    private static DelhpiService instance;
    private static CloseableHttpClient httpclient;
    AgentObject agentObject;
    ArrayList<AgentObject> agentObjectList;

    public static synchronized DelhpiService getInstance() {

        if (instance == null) {
            instance = new DelhpiService();
        }
        return instance;
    }

    public AgentObject getAgentObjectFromAgent(JSONObject jAgent, Object OAgentId) {

        agentObject = new AgentObject();
        agentObject.agentId = (String) OAgentId;
        agentObject.deviceClass = (String) jAgent.get("device_class");
        agentObject.lastSeen = getDeviceLastSeen((Long) jAgent.get("last_seen_ms"));
        agentObject.wasOnline = (boolean) jAgent.get("was_online");

        //blob_inventory_detail
        if (!jAgent.get("blob_inventory_detail").toString().contains("{}")) {
            try {
                JSONObject blobObject = (JSONObject) jAgent.get("blob_inventory_detail");

                //ORIGINAL
                JSONObject Original = (JSONObject) blobObject.get("ORIGINAL");
                if (Original != null) { //ORIGINAL

                    JSONObject OriginalImage = (JSONObject) Original.get("IMAGE");
                    JSONObject OriginalVideo = (JSONObject) Original.get("VIDEO");

                    JSONObject OriginalImageExternal = (JSONObject) OriginalImage.get("EXTERNALLY_MANAGED");

                    if (OriginalVideo != null) {
                        JSONObject OriginalVideoExternal = (JSONObject) OriginalVideo.get("EXTERNALLY_MANAGED");
                        agentObject.videoCount = (Long) OriginalVideoExternal.get("count");
                    }

                    agentObject.imageCount = (Long) OriginalImageExternal.get("count");
                }
                //ALTERNATIVE
                else {
                    JSONObject Alternative = (JSONObject) blobObject.get("ALTERNATIVE");
                    JSONObject AlternativeImage = (JSONObject) Alternative.get("IMAGE");
                    JSONObject AlternativeImageCache = (JSONObject) AlternativeImage.get("CACHE");
                    agentObject.imageCount = (Long) AlternativeImageCache.get("count");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //blobs_to_download_details
        else if (!jAgent.get("blobs_to_download_details").toString().contains("{}")) {
            try {
                JSONObject blobToDownloadObject = (JSONObject) jAgent.get("blobs_to_download_details");
                JSONObject AlternativeCount = (JSONObject) blobToDownloadObject.get("ALTERNATIVE");

                agentObject.imageCount = (Long) AlternativeCount.get("count");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return agentObject;
    }

    public String getJSONFromURL(CloseableHttpClient httpclient, String URL, String meshId) throws Exception {

        HttpGet httpGet = new HttpGet(URL);
        String resultJSONString = "";
        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.addHeader("X-BP-Envelope", "EgIIARoBMQ==");
        httpGet.addHeader("X-BP-Token", getAccessToken());

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
        log.info("resultJSON: " + resultJSONString);
        EntityUtils.consume(entity);
        return resultJSONString;
    }

    public ArrayList<AgentObject> getAgentObjectListFromMeshId(CloseableHttpClient httpclient, String meshId) throws Exception {

        String URL = "https://delphi.dogfood.blackpearlsystems.net/delphi/rest/v2/meshstats/" + meshId;
        agentObjectList = new ArrayList<AgentObject>();
        agentObject = new AgentObject();
        String resultJSON = getJSONFromURL(httpclient, URL, meshId);
        //String resultJSON = getJSONFromFile("meshStatsMultiple.json");
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(resultJSON);
        JSONObject level1Object = (JSONObject) obj;
        log.info("mesh_status: " + level1Object.get("mesh_status"));
        JSONObject agentStatsJSONObject = (JSONObject) level1Object.get("agent_stats");

        if (agentStatsJSONObject instanceof Map) {
            Set AgentkeySet = agentStatsJSONObject.keySet();
            log.info("Agent Count : " + AgentkeySet.size());

            for (Object agentId : AgentkeySet) {
                JSONObject jsonAgent = (JSONObject) agentStatsJSONObject.get(agentId);
                agentObject = getAgentObjectFromAgent(jsonAgent, agentId);
                agentObjectList.add(agentObject);
            }
        }
        return agentObjectList;
    }

    public ArrayList<String> getAgentListWithMeshId(String meshId) {
        ArrayList<String> agentList = new ArrayList<String>();
        //takes meshDID, gets the AgentID as a ArrayList
        try {
            // Trust all certs
            SSLContext sslcontext = buildSSLContext();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
                    new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            //build a HTTP client with all the above
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

            agentObjectList = DelhpiService.getInstance().getAgentObjectListFromMeshId(httpclient, meshId);

            for (AgentObject agentObject : agentObjectList) {
                log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                log.info("Agent ID: " + agentObject.agentId);
                agentList.add(agentObject.agentId);
                log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            }

        } catch (IndexOutOfBoundsException iob) {
            iob.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return agentList;
    }

    public void getAgentFromSarkClientWithEmail(String email) {
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

    public void getAgentFromSarkClientWithMeshId(String meshId) {
        //takes mesh_ids and gets agentObjectList which can then be iterated for each AgentObject
        try {
            // Trust all certs
            SSLContext sslcontext = buildSSLContext();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
                    new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //build a HTTP client with all the above
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            agentObjectList = DelhpiService.getInstance().getAgentObjectListFromMeshId(httpclient, meshId);

            for (AgentObject agentObject : agentObjectList) {
                log.info("--------------------------------------");
                log.info("Agent Was Online: " + agentObject.wasOnline);
                log.info("Agent Last Seen: " + agentObject.lastSeen);
                log.info("Agent Type: " + agentObject.deviceClass);
                log.info("Agent ID: " + agentObject.agentId);
                log.info("Agent Image count: " + agentObject.imageCount);
                log.info("Agent Video count: " + agentObject.videoCount);
                log.info("--------------------------------------");
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
//        String email = "mmadhusoodan+morgan@lyveminds.com";
        //String email = "mmadhusoodan+ita@lyveminds.com";
        String email = "mmadhusoodan+avery@lyveminds.com";

        DelhpiService.getInstance().getAgentFromSarkClientWithEmail(email);
        //DelhpiService.getInstance().getAgentListWithMeshId("DE12719E-F84F-484A-B7BB-3B49D11C1874");
        //DelhpiService.getInstance().getAgentFromSarkClientWithMeshId("EE9A3D25-D9AC-4763-B244-887FCE7183C2");
    }
}