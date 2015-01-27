package com.lyve.app;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mmadhusoodan on 12/18/14.
 */
public class TestRailsMethods {

    //Project 12
    //Suite 16047
    //Section
    private final static Logger log = Logger.getLogger(TestRailsMethods.class);
    private static TestRailsMethods instance;
    private String BaseURL = "";

    private String TRUserName = "";
    private String TRPassword = "";

    private TestRailsMethods() {
        getRailsCredentials();
    }

    public static synchronized TestRailsMethods getInstance() {
        if (instance == null) {
            instance = new TestRailsMethods();
        }
        return instance;
    }

    public void getRailsCredentials() {
        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("/Users/mmadhusoodan/.testrailreporter.json"));

            JSONObject jsonObject = (JSONObject) obj;

            BaseURL = (String) jsonObject.get("TR_Base_URL");
            TRUserName = (String) jsonObject.get("TR_User_Name");
            TRPassword = (String) jsonObject.get("TR_User_PW");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getProjectsList(String reqParameter) throws Exception {

        APIClient client = new APIClient(BaseURL);
        client.setUser(TRUserName);
        client.setPassword(TRPassword);
        JSONArray jArray = (JSONArray) client.sendGet(reqParameter);
        JSONObject jObject = null;
        for (int i = 0; i < jArray.size(); i++) {
            jObject = (JSONObject) jArray.get(i);


            log.info(jObject.toString());
            log.info("ProjectName: " + jObject.get("name"));
        }
    }

    public void getSuitesFromAProject(String reqParameter) throws Exception {

        APIClient client = new APIClient(BaseURL);
        client.setUser(TRUserName);
        client.setPassword(TRPassword);
        JSONArray jArray = (JSONArray) client.sendGet(reqParameter);
        JSONObject jObject = null;
        for (int i = 0; i < jArray.size(); i++) {
            jObject = (JSONObject) jArray.get(i);

            log.info(jObject.toString());
            log.info("SuiteName: " + jObject.get("name"));
        }
    }

    /**
     * sections are also called groups in test rails
     * the reason why I was getting All_Tests and All_Test and ALL_TESTS in my report is because each time I change the railstest.json
     * it creates a new section in test rails
     */
    public String getSectionsFromAProjectANDSuites(String reqParameter) throws Exception {

        APIClient client = new APIClient(BaseURL);
        client.setUser(TRUserName);
        client.setPassword(TRPassword);
        JSONArray jArray = (JSONArray) client.sendGet(reqParameter);
        JSONObject jObject = null;
        String sectionNo = "";
        for (int i = 0; i < jArray.size(); i++) {
            jObject = (JSONObject) jArray.get(i);

            log.info(jObject.toString());
            log.info("SuiteName: " + jObject.get("name"));
            sectionNo = jObject.get("id").toString();
            log.info("sectionNo: " + sectionNo);
        }
        return sectionNo;
    }

    public String getCasesFromSuitesANDSection(String reqParameter) throws Exception {

        //get_cases/<suite_id>/<section_id>
        APIClient client = new APIClient(BaseURL);
        client.setUser(TRUserName);
        client.setPassword(TRPassword);
        JSONArray jArray = (JSONArray) client.sendGet(reqParameter);
        JSONObject jObject = null;
        String sectionNo = "";
        log.info("TestCase Count: "+ jArray.size());
        for (int i = 0; i < jArray.size(); i++) {
            jObject = (JSONObject) jArray.get(i);
            log.info("TestCase id: " + jObject.get("id")+ " TestCase title: " + jObject.get("title"));
        }
        return sectionNo;
    }



    public void deleteSection(String reqParameter, Integer sectionNo) throws Exception {

        APIClient client = new APIClient(BaseURL);
        client.setUser(TRUserName);
        client.setPassword(TRPassword);

        Map data = new HashMap();
        data.put("delete_section",sectionNo);
        JSONObject jObject = (JSONObject) client.sendPost(reqParameter, data);
        log.info(jObject);
        log.info("SectionNo "+ sectionNo +" Deleted successfully");

    }

    public void getEachProject(String reqParameter) throws Exception {

        APIClient client = new APIClient(BaseURL);
        client.setUser(TRUserName);
        client.setPassword(TRPassword);
        JSONObject jObject = (JSONObject) client.sendGet(reqParameter);
        log.info(jObject.toJSONString().toString());

    }

    public static void main(String args[]) throws Exception {

        //TestRailsMethods.getInstance().getProjectsList("get_projects");
        //TestRailsMethods.getInstance().getEachProject("get_project/12");
        TestRailsMethods.getInstance().getSuitesFromAProject("get_suites/12");
        String sectionNo = TestRailsMethods.getInstance().getSectionsFromAProjectANDSuites("/get_sections/12&suite_id=16047");
        log.info("sectionNo: "+sectionNo);

        TestRailsMethods.getInstance().getCasesFromSuitesANDSection("/get_cases/12&suite_id=16047");

        TestRailsMethods.getInstance().deleteSection("/delete_section/"+sectionNo,Integer.parseInt(sectionNo));


    }
}
