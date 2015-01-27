package com.lyve.app;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mmadhusoodan on 1/23/15.
 */
public class MeshServicesTestClient {

    private final static Logger log = Logger.getLogger(MeshServicesTestClient.class);
    private String envelopeHeaders = " -H 'X-BP-Envelope: EgIIARoBMQ==' ";

    private String tokenHeaders = " -H 'X-BP-Token: MSwxLE5Lb05Uc0ZEbDA2RnBGYjlVUGVMcWc9PSxzRXkzNFJXQTh0SjJzQXVYc1Z6akswY0h4UDdqMzJ3OEZ2MHlBN1E5SUxRWmhXRklCV1hRZjEyZlV6T1ZoK0duNlZnTUhvL2N6cVZRcjlXeGdZWThzTmkrSWo1azVVZW92c0NpdkhkQ05HYkVnR0prQWQrM2NoSU5DMGFUZ1c0MDJGUjBpZU81djdFRGx4RS9xM0NKdEhBRElieVJEQUhPSWVMNEhtb3IxVUk9' ";
    private String jsonHeaders = " -H 'Content-Type: application/json' " +
            " -H 'Accept': application/json' ";

    private String jsonEnvelopeHeaders = jsonHeaders + envelopeHeaders;
    private String jsonTokenEnvelopeHeaders = jsonHeaders + envelopeHeaders + tokenHeaders;
    private static MeshServicesTestClient instance;

    public static synchronized MeshServicesTestClient getInstance() {
        if (instance == null) {
            instance = new MeshServicesTestClient();
        }
        return instance;
    }

    public boolean accountExists(String encodedEmail) throws IOException {

        String URL = "https://accounts.dogfood.blackpearlsystems.net/accounts/emails/";
        String command = "curl -v -k -X GET " + jsonEnvelopeHeaders + URL + encodedEmail;
        log.info("command:" + command);
        String line = "";
        StringBuffer sb = new StringBuffer();
        Process curlProc;
        try {
            curlProc = Runtime.getRuntime().exec(command);
            DataInputStream curlIn = new DataInputStream(curlProc.getInputStream());

            while ((line = curlIn.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e1) {

            e1.printStackTrace();
        }

        String response = sb.toString();
        Map<String, ArrayList> JSONResponse;
        ObjectMapper jsonMapper = new ObjectMapper();
        JSONResponse = jsonMapper.readValue(response, HashMap.class);
        ArrayList<String> agentList = new ArrayList<String>();
        if (JSONResponse.get("code") != null) {
            log.info(JSONResponse.get("message"));
            return false;
        } else {
            log.info("account_id: " + JSONResponse.get("account_id"));
            log.info("email_primary: " + JSONResponse.get("email_primary"));
            log.info("mesh_ids: " + JSONResponse.get("mesh_ids"));
            agentList = JSONResponse.get("agent_ids");

            for (int j = 0; j < agentList.size(); j++) {
                log.info("agent_ids: " + j + " " + agentList.get(j));
            }

            return true;
        }
    }

    /**
     * curl -v -k -X POST -H 'X-BP-Envelope: EgIIARoBMQ=='  -H 'Content-Type:application/json'
     * -d "{\"first_name\": \"fname\",\"last_name\": \"lname\",\"email\": \"portal_test_02@lyveminds.com\", \"password\": \"123456\"}"
     * https://accounts.test.blackpearlsystems.net/accounts
     *
     * @return
     * @throws IOException
     */

    public boolean accountCreate() throws IOException {
        //public boolean accountCreate(String firstName, String lastName, String password, String email) throws IOException {

        String json = "-d '{\"first_name\":\"fname\",\"last_name\":\"lname\",\"email\":\"portal_test_03@lyveminds.com\",\"password\":\"123456\"}'";
        //json = "-d '{ \"first_name\" : \"fname\" }'";
        String URL = " https://accounts.test.blackpearlsystems.net/accounts";
        String command = "curl -v -k -X POST " + jsonEnvelopeHeaders + json + URL;

        log.info("command: " + command);
        String line = "";
        StringBuffer sb = new StringBuffer();
        Process curlProc;
        try {
            curlProc = Runtime.getRuntime().exec(command);
            DataInputStream curlIn = new DataInputStream(curlProc.getInputStream());
            while ((line = curlIn.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e1) {

            e1.printStackTrace();
        }

        String response = sb.toString();
        Map<String, ArrayList> JSONResponse;
        ObjectMapper jsonMapper = new ObjectMapper();
        JSONResponse = jsonMapper.readValue(response, HashMap.class);
        ArrayList<String> agentList = new ArrayList<String>();
        if (JSONResponse.get("code") != null) {
            log.info(JSONResponse.get("message"));
            return false;
        } else {
            log.info("account_id: " + JSONResponse.get("account_id"));
            log.info("email_primary: " + JSONResponse.get("email_primary"));
            log.info("mesh_ids: " + JSONResponse.get("mesh_ids"));
            agentList = JSONResponse.get("agent_ids");

            for (int j = 0; j < agentList.size(); j++) {
                log.info("agent_ids: " + j + " " + agentList.get(j));
            }
            return true;
        }

    }

    //From SARK
    public boolean getAgentsFromMeshID(String meshId) throws IOException {

        String URL = "https://sark.dogfood.blackpearlsystems.net/sark/rest/v1/" + meshId + "/agents";
        String command = "curl -I -v -k -X GET " + jsonTokenEnvelopeHeaders + URL;

        log.info("command: " + command);
        String line = "";
        StringBuffer sb = new StringBuffer();
        Process curlProc;
        try {
            curlProc = Runtime.getRuntime().exec(command);
            DataInputStream curlIn = new DataInputStream(curlProc.getInputStream());

            while ((line = curlIn.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e1) {

            e1.printStackTrace();
        }

        String response = sb.toString();
        log.info(response);
        return true;

    }

    //From SARK
    public boolean getAccountsFromMeshID(String meshId) throws IOException {

        String URL = "https://sark.dogfood.blackpearlsystems.net/sark/rest/v1/accounts/mesh/" + meshId;
        String command = "curl -v -k -X GET " + jsonTokenEnvelopeHeaders + URL;

        log.info("command: " + command);
        String line = "";
        StringBuffer sb = new StringBuffer();
        Process curlProc;
        try {
            curlProc = Runtime.getRuntime().exec(command);
            DataInputStream curlIn = new DataInputStream(curlProc.getInputStream());

            while ((line = curlIn.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e1) {

            e1.printStackTrace();
        }

        String response = sb.toString();
        log.info(response);
        return true;

    }
    //com.blackpearl.clu.v1.exception.ServiceException: category=Authentication errorCode=1 description=Empty credentials cause=unable to decode null actionCode=NO_ACTION

    public static void main(String arg[]) throws IOException {

        //String encodedEmail = "mmadhusoodan+120@lyveminds.com";

        //MeshServicesTestClient.getInstance().accountExists(encodedEmail);
        //log.info("accountCreate() Status: "+MeshServicesTestClient.getInstance().accountCreate());

        //MeshServicesTestClient.getInstance().accountExists(encodedEmail);

        String meshId = "8D30F010-F1C8-4BDF-B11E-F6BC7EB511DF";
        //MeshServicesTestClient.getInstance().getAccountsFromMeshID(meshId);
        MeshServicesTestClient.getInstance().getAgentsFromMeshID(meshId);


    }
}