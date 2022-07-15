package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.org.yaapita.YaapitaReport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.kroger.assortment.testUtilities.Constants.*;
import static com.kroger.assortment.testUtilities.ConstantsDB.*;
import static com.org.yaapita.libloadinputdata.LoadExcelData.getEnabledExcelTests;
import static io.restassured.RestAssured.given;

public class GetAssortmentItems3 extends InitAssortment {

    protected static YaapitaReport report;

    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }


    @DataProvider(name = "assortmentItemsFilterByInvalidInputData" )
    public Object[] getAssortmentItemsFilterByInvalidInputData(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("assortmentItemsFilterByFC"), INPUT_SHEET_NAME_INVALID_INPUT_DATA );
    }

    /**
     * This method validate  get assortment items filtering by a single imfType
     * Validate the status code is 200 and the Api response retrieve all assortmentItem with imfType= to imfType in the request
     */
    @Test
    public void validateGetAssortmentItems_ByImfType_TC0017() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByImfType_TC0017 ");
        System.out.println("[Test Scenario] Get Assortment Items  filter by a imfType");
        int param = dbUtils.getRandomIntDBRecord(SELECT_RANDOM_IMFTYPE,ID);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS")+"?"+ IMFTYPE +"=" + param );

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(IMFTYPE,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        List<Integer> dblist = dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_COUNT_ASSORTMENTS_ITEMS_BY_IMFTYPE, Arrays.asList(param)  , COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? dblist.get(0) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        int expectedCode = assortmentItems == 0 && assortmentItemsDB == 0 ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_OK;
        boolean statusCode = restResponse.getStatusCode() == expectedCode;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode() :
                "\n[Error] Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems ? "[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);

        Assert.assertTrue(statusCode, "\n [Error] Expected code: " + expectedCode+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println("============================================================================================");
    }



    /**
     * This method validate  get assortment items filtering by a single imfStatus
     * Validate the status code is 200 and the Api response retrieve all assortmentItem with imfStatus= to imfStatus in the request
     */
    @Test
    public void validateGetAssortmentItems_ByImfStatus_TC0018() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByImfStatus_TC0018 ");
        System.out.println("[Test Scenario] Get Assortment Items  filter by a imfStatus");
        int param = dbUtils.getRandomIntDBRecord(SELECT_RANDOM_IMFSTATUS,ID);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS")+"?"+ IMFSTATUS +"=" + param );

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(IMFSTATUS,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();


        List<Integer> dblist = dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_COUNT_ASSORTMENTS_ITEMS_BY_IMFSTATUS, Arrays.asList(param)  , COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? dblist.get(0) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        int expectedCode = assortmentItems == 0 && assortmentItemsDB == 0 ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_OK;
        boolean statusCode = restResponse.getStatusCode() == expectedCode;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode() :
                "\n[Error] Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems ? "[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);

        Assert.assertTrue(statusCode, "\n [Error] Expected code: " + expectedCode+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println("============================================================================================");
    }



    /**
     * This method validate  get assortment items filtering by a single pidDescription
     * Validate the status code is 200 and the Api response retrieve all assortmentItem with pidDescription= to pidDescription in the request
     */
    @Test
    public void validateGetAssortmentItems_ByPidDescription_TC0019() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByPidDescription_TC0019 ");
        System.out.println("[Test Scenario] Get Assortment Items  filter by a pidDescription");
        String param = "banana";
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS")+"?"+ PID_DESCRIPTION +"=" + param );

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PID_DESCRIPTION,param).
                queryParam(PAGEABLE,"true").
                when().get(url).then().contentType(ContentType.JSON).extract().response();


        List<Integer> dblist = dbUtils.returnIntegerDBSelectQueryObjectParams
                (SELECT_COUNT_ASSORTMENTS_ITEMS_BY_DESCRIPTION, Arrays.asList("%"+param+"%","%"+param+"%")  , COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? dblist.get(0) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        int expectedCode = assortmentItems == 0 && assortmentItemsDB == 0 ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_OK;
        boolean statusCode = restResponse.getStatusCode() == expectedCode;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode() :
                "\n[Error] Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems ? "[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);

        Assert.assertTrue(statusCode, "\n [Error] Expected code: " + expectedCode+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println("============================================================================================");
    }




    /**
     *  This method will fetch assortmentItems with invalid input data for filter params:
     *  pidDescription
     * assortmentId
     * status
     * storePickStatus
     * locationCode
     * imfType
     * imfStatus
     *     will validate the status code is 400/404 , will validate the error reason
     */
    @Test (dataProvider = "assortmentItemsFilterByInvalidInputData" )
    public void validateGetAssortmentItems_ByInvalidInputData_TC0020(Map<String, String> dataMap) {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByInvalidInputData_TC0020 ");
        String param = dataMap.get(EXCEL_PARAM);
        String filterParam = dataMap.get(EXCEL_FILTER__PARAM);
        String error = dataMap.get(EXCEL_ERROR_REASON);
        System.out.println("[Test Scenario] GetAssortmentItems "+  dataMap.get(EXCEL_TC_NUMBER) +" - " + dataMap.get(EXCEL_TEST_CASE) + param);
        System.out.println("[Endpoint]  " + env.getString("ASSORTMENT_HOST") + "/assortment-items?"+filterParam+ "=" + param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(filterParam,param).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        int excelStatusCode = Integer.valueOf(dataMap.get(EXCEL_STATUS_CODE));
        boolean statusCode = restResponse.getStatusCode() == excelStatusCode;

        boolean errorReason = restResponse.jsonPath().get(JSON_PATH_ERRORS_REASON) != null?
                restResponse.jsonPath().get(JSON_PATH_ERRORS_REASON).toString().contains(error):false;


        System.out.println(statusCode ? "\n[Correct]  Expected code: " +excelStatusCode + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + excelStatusCode+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(errorReason ? "\n[Correct]  Assortment Items were not found because " + param + " is an invalid "+filterParam+", api error reason " + GET_ASSORTMENT_ITEMS_ERRORS_REASON :
                "[Error] Assortment Items  should NOT be found because " + param + " is an invalid "+filterParam+" , expected Message: '"
                        + GET_ASSORTMENT_ITEMS_ERRORS_REASON + ", instead api retrieved answer was: "+ restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + excelStatusCode + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(errorReason, "[Error] Assortment Items  should NOT be found because " + param + " is invalid assortmentId , expected Message: '"
                + GET_ASSORTMENT_ITEMS_ERRORS_REASON + "' message, instead api retrieved answer was: "+ restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }













}
