package com.lyve.service;

/**
 * Created by mmadhusoodan on 2/13/15.
 */

import com.lyve.service.object.AgentObject;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DelhpiService extends AbsractServicesBaseClass {
    private Logger log = Logger.getLogger(DelhpiService.class);

    private static DelhpiService instance;

    private Map<String, Map<String, LinkedHashMap>> responseMap;
    private ObjectMapper jsonMapper = new ObjectMapper();

    public static synchronized DelhpiService getInstance() {
        if (instance == null) {
            instance = new DelhpiService();
        }
        return instance;
    }

    public AgentObject getBlobDetailsFromAgent(JSONObject jAgent, Object OAgentId) {

        AgentObject agentObject = new AgentObject();
        agentObject.agentId = (String) OAgentId;
        if (!jAgent.get("blob_inventory_detail").toString().contains("{}")) {
//            log.info(jAgent.get("blob_inventory_detail").toString());
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
        } else {
            //blob_inventory_detail = {}
//            log.info(jAgent.get("blobs_to_download_details"));
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

    public void getBlobDetailsFromMeshId(CloseableHttpClient httpclient, String meshId) throws Exception {

        String URL = "https://delphi.dogfood.blackpearlsystems.net/delphi/rest/v2/meshstats/" + meshId;

        HttpGet httpGet = new HttpGet(URL);

        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.addHeader("X-BP-Envelope", "EgIIARoBMQ==");

        CloseableHttpResponse httpResponse = httpclient.execute(httpGet);

        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        log.info("statusCodes: " + statusCode);

        String httpStatusMessage = httpResponse.getStatusLine().toString();
        HttpEntity entity = httpResponse.getEntity();

        log.info("----------------------------------------");
        log.info("httpStatusMessage: " + httpStatusMessage);
        if (entity != null) {
            log.info("Response content length: " + entity.getContentLength());
        }

        for (Header header : httpResponse.getAllHeaders()) {
            log.info(header);
        }
        log.info("----------------------------------------");
        if (statusCode == HttpStatus.SC_OK) {
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                StringBuffer resultJSON = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    resultJSON.append(line);
                }
                log.info("resultJSON: " + resultJSON.toString());

                JSONParser parser = new JSONParser();
                Object obj = parser.parse(resultJSON.toString());
                JSONObject level1Object = (JSONObject) obj;
                log.info("mesh_status: " + level1Object.get("mesh_status"));
                JSONObject agentStatsJSONObject = (JSONObject) level1Object.get("agent_stats");

                if (agentStatsJSONObject instanceof Map) {
                    Set keySet = agentStatsJSONObject.keySet();
                    log.info("Agent Count : " + keySet.size());

                    for (Object agentIdKey : keySet) {
                        JSONObject jsonAgent = (JSONObject) agentStatsJSONObject.get(agentIdKey);
                        AgentObject agentObject = getBlobDetailsFromAgent(jsonAgent, agentIdKey);

                        log.info("--------------------------------------");
                        log.info("Agent ID: " + agentObject.agentId);
                        log.info("Image count: " + agentObject.imageCount);
                        log.info("Video count: " + agentObject.videoCount);
                        log.info("--------------------------------------");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                httpResponse.close();
                httpGet.releaseConnection(); // stop connection
            }
        } else {
            log.info(statusCode);
        }
        EntityUtils.consume(entity);
    }


    public static void main(String[] args) throws Exception {
        // Trust all certs
        SSLContext sslcontext = buildSSLContext();

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        //build a HTTP client with all the above
        httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

        //String email = "a@lyveminds.com";
        //ArrayList<String> meshIDs = AccountsService.getInstance().getAccountDetailsFromEmail(httpclient, email).get("mesh_ids");

        //DelhpiService.getInstance().getMeshInfoFromMeshId(httpclient, meshIDs.get(0));

        //One Agent
        //DelhpiService.getInstance().getBlobDetailsFromMeshId(httpclient, "E89EC84A-3A3E-44FD-8863-F510E2754ED0");

        //Multiple Agent
        DelhpiService.getInstance().getBlobDetailsFromMeshId(httpclient, "8D30F010-F1C8-4BDF-B11E-F6BC7EB511DF");

    }
}