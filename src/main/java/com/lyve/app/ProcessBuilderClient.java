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
public class ProcessBuilderClient {

    private final static Logger log = Logger.getLogger(ProcessBuilderClient.class);
    private String URL = "https://accounts.test.blackpearlsystems.net/accounts/emails/";
    private String header = " -H 'Content-Type: application/json' "
            + " -H 'X-BP-Envelope: EgIIARoBMQ==' ";
    private static ProcessBuilderClient instance;

    public static synchronized ProcessBuilderClient getInstance() {
        if (instance == null) {
            instance = new ProcessBuilderClient();
        }
        return instance;
    }

    public String accountExists(String encodedEmail) {

        String outputString = "";
        String command = "curl -v -k -X GET " + header + URL + encodedEmail;
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

        return sb.toString();
    }

    public static void main(String arg[]) throws IOException {

        String encodedEmail = "portal_test_01@lyveminds.com";
        String response = ProcessBuilderClient.getInstance().accountExists(encodedEmail);

        Map<String, ArrayList> JSONResponse;
        ObjectMapper jsonMapper = new ObjectMapper();
        JSONResponse = jsonMapper.readValue(response, HashMap.class);
        log.info(JSONResponse.get("account_id"));
        log.info(JSONResponse.get("mesh_ids"));
        log.info(JSONResponse.get("agent_ids"));



    }
}