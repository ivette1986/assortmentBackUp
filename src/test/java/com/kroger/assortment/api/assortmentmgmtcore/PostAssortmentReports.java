package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItem;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItemStatus;
import com.kroger.assortment.model.assortmentmgmtcore.Result;
import com.kroger.assortment.model.assortmentmgmtcore.StorePickStatus;
import com.org.yaapita.YaapitaReport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.kroger.assortment.testUtilities.Constants.*;
import static io.restassured.RestAssured.given;

public class PostAssortmentReports extends InitAssortment {
    protected static YaapitaReport report;


    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();

    }

    /**
     * This method validates Regular reports are exported according to input assortments
     * all assortments are valid, contain items, only validate retrieved Api response  contains assortments and sharepoint link
     */
    @Test
    public void validatePOSTAssortmentReports_FCRegular_TC001() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePOSTAssortmentReports_FCRegular_TC001 ");
        System.out.println("[Test Scenario] Generate Report FC Regular Report happy path");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/reports/fc-regular");

        List<Integer> assortments =  testUtils.getRandomAssortments(3);
        String body = buildPostAssortmentItemBody(assortments);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("ASSORTMENT_REPORTS_FC_REGULAR");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().post().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_CREATED;

        boolean json = false;
        if(restResponse.jsonPath().get(JSON_PATH_EXPORTS) != null)
            json =validateResponse( restResponse,JSON_PATH_EXPORTS, assortments);
        boolean jsonErrors = restResponse.jsonPath().get(JSON_PATH_ERRORS)==null || restResponse.jsonPath().get(JSON_PATH_ERRORS).toString().equalsIgnoreCase("[]") ? true:false ;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(json ? "[Correct]  Exports were done correctly, Api Response:  " +restResponse.jsonPath().get().toString():
                "[Error] Exports failed,some assortments missing in  Api Response:, Api Response:  " +restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(json, " [Error] Exports failed,some assortments missing in  Api Response:  " +restResponse.jsonPath().get().toString());
        Assert.assertTrue(jsonErrors, " [Error] errors should be [], but is NOT, instead Api Response: " +restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }

    /**
     * This method validates Regular reports are exported according to input assortments
     * all assortments are invalid,  not exist
     */
    @Test
    public void validatePOSTAssortmentReports_FCRegular_InvalidAssortment_TC002() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePOSTAssortmentReports_FCRegular_InvalidAssortment_TC042 ");
        System.out.println("[Test Scenario] Generate  FC Regular Report with invalid Assortments: 0, -1, -2");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/reports/fc-regular");

        List<Integer> assortments = Arrays.asList(0,-1,-2);
        String body = buildPostAssortmentItemBody(assortments);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("ASSORTMENT_REPORTS_FC_REGULAR");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().post().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_CREATED;

        boolean jsonErrors = false;
        if(restResponse.jsonPath().get(JSON_PATH_ERRORS) != null)
            jsonErrors =validateResponseErrors( restResponse,JSON_PATH_ERRORS, assortments);
        boolean json =restResponse.jsonPath().get(JSON_PATH_EXPORTS)==null || restResponse.jsonPath().get(JSON_PATH_EXPORTS).toString().equalsIgnoreCase("[]") ? true:false ;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(jsonErrors ? "[Correct]  Errors Api Response was correctly retrieved:  " +restResponse.jsonPath().get().toString():
                "[Error] Errors failed,some assortments missing in  Api Response: " +restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(jsonErrors, " [Error] Errors failed,some assortments missing in  Api Response:  " +restResponse.jsonPath().get().toString());
        Assert.assertTrue(json, " [Error] exports should be [],but is Not, instead Api Response: " +restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }


    /**
     * This method validates Store Pick List reports are exported according to input assortments
     * all assortments are valid, will validate Api response contains assortments and email sent is true
     */
    @Test
    public void validatePOSTAssortmentReports_StorePickList_TC003() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePOSTAssortmentReports_StorePickList_TC003 ");
        System.out.println("[Test Scenario] Generate Reports Store Pick List happy path");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/reports/store-picks");

        List<Integer> assortments = testUtils.getRandomAssortments(3);
        String body = buildPostAssortmentItemBody(assortments);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("ASSORTMENT_REPORTS_STORE_PICKS");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().post().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_CREATED;

        boolean json = false;
        if(restResponse.jsonPath().get(JSON_PATH_EXPORTS) != null)
            json =validateResponseStorePicksList( restResponse);

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(json ? "[Correct] Report was exported, Api Response:  " +restResponse.jsonPath().get().toString():
                "[Error] Report was not exported correctly, Api Response: " +restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(json, "[Error] Report was not exported correctly, Api Response: " +restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }

    /**
     * This method validates Regular reports are exported according to input assortments
     * all assortments are invalid,  not exist
     */
    @Test
    public void validatePOSTAssortmentReports_StorePickList_Invalid_TC004() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePOSTAssortmentReports_StorePickList_Invalid_TC004 ");
        System.out.println("[Test Scenario] Generate Store Pick List Report with invalid Assortments: 0, -1, -2");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/reports/store-picks");

        List<Integer> assortments = Arrays.asList(0,-1,-2);
        String body = buildPostAssortmentItemBody(assortments);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("ASSORTMENT_REPORTS_STORE_PICKS");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().post().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_CREATED;

        boolean jsonErrors = false;
        if(restResponse.jsonPath().get(JSON_PATH_NOT_INCLUDED) != null)
            jsonErrors =validateResponseErrors( restResponse,JSON_PATH_NOT_INCLUDED, assortments);
        boolean json =restResponse.jsonPath().get(JSON_PATH_EXPORTS)==null || restResponse.jsonPath().get(JSON_PATH_EXPORTS).toString().equalsIgnoreCase("[]") ? true:false ;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(jsonErrors ? "[Correct]  Not Included Api Response was correctly retrieved:  " +restResponse.jsonPath().get().toString():
                "[Error] Not Included failed,some assortments missing in  Api Response: " +restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(jsonErrors, " [Error] Not Included failed,some assortments missing in  Api Response:  " +restResponse.jsonPath().get().toString());
        Assert.assertTrue(json, " [Error] exports should be [],but is Not, instead Api Response: " +restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }



    public String buildPostAssortmentItemBody(List<Integer> assortments){
        StringBuilder body = new StringBuilder();
        body.append("{\n");
        body.append("    \"assortments\": [");
        for(int i =0; i<assortments.size(); i++) {
            body.append(assortments.get(i));
            body.append(i== assortments.size()-1 ? "":",");
        }
        body.append("]\n" );
        body.append("}");

        System.out.println("\n[POST Body] \n" + body.toString());

        return body.toString();
    }

    public boolean validateResponse(Response response,String jsonPath, List<Integer> assortments){
        List<Map<String,Object>> maps =response.jsonPath().getList(jsonPath);
        if(maps== null || maps.isEmpty()) return false;
        StringBuilder message = new StringBuilder();
        boolean exports = false;

        for(Integer assort : assortments){
            for(Map<String,Object> map : maps) {
                if (assort == map.get("assortmentId") && Pattern.matches(".*\\.xlsx",map.get("reportName").toString())
                        && map.get("sharePointLink").toString().length()>0 ) {
                    exports =true; break;
                }
            }if(!exports) message.append("The assortmend id: " +assort+" was not found in the retrieved response data.exports:");
            exports=false;
        }

        if(message.length()>0){
            System.out.println("The Api response is incorrect, next assortments missing in response api: \n"+message.toString());
            return false;
        }
        return true;
    }



    public boolean validateResponseErrors(Response response,String jsonPath, List<Integer> assortments){
        List<Map<String,Object>> maps =response.jsonPath().getList(jsonPath);
        if(maps== null || maps.isEmpty()) return false;
        StringBuilder message = new StringBuilder();
        boolean errors = false;

        for(Integer assort : assortments){
            for(Map<String,Object> map : maps) {
                if (assort == map.get("assortmentId") && map.get("reason").toString().length()>0 ) {
                    errors =true; break;
                }
            }if(!errors) message.append("The assortmend id: " +assort+" was not found in the retrieved response data.errors:");
            errors=false;
        }

        if(message.length()>0){
            System.out.println("The Api response is incorrect, next assortments missing in response api: \n"+message.toString());
            return false;
        }
        return true;
    }


    public boolean validateResponseStorePicksList(Response response){
        String json = response.jsonPath().get(JSON_PATH_EXPORTS_REPORTNAME).toString().replace("[","");
        if(response.jsonPath().get(JSON_PATH_EXPORTS_REPORTNAME)==null || !Pattern.matches("[a-zA-Z0-9]*\\.\\d{2}\\.\\d{2}\\.csv",json.replace("]","")))
           return false;
        if(response.jsonPath().get(JSON_PATH_NOT_INCLUDED)==null || !response.jsonPath().get(JSON_PATH_NOT_INCLUDED).toString().equalsIgnoreCase("[]"))
            return false;

            return true;
    }





}
