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

public class GetAssortmentItems1 extends InitAssortment {

    protected static YaapitaReport report;

    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ env.getString("env")+ "_"+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }


    @DataProvider(name = "assortmentItemsFilterByFCBlankSpaces" )
    public Object[] getAssortmentItemsFilterByFCBlankSpaces(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("assortmentItemsFilterByFC"), INPUT_SHEET_NAME_BLANK_SPACES );
    }

    @DataProvider(name = "assortmentItemsFilterByFCInvalidInputData" )
    public Object[] getAssortmentItemsFilterByFCInvalidInputData(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("assortmentItemsFilterByFC"), INPUT_SHEET_NAME_NEGATIVE );
    }


    @DataProvider(name = "assortmentItemsFilterByUPC13InvalidInputData" )
    public Object[] getAssortmentItemsFilterByUPC13InvalidInputDat(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("getAssortmentItems"), INPUT_SHEET_NAME_NEGATIVE );
    }

    @DataProvider(name = "assortmentItemsFilterByUPC13BlankSpaces" )
    public Object[] getAssortmentItemsFilterByUPC13BlankSpaces(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("assortmentItemsFilterByFC"), INPUT_SHEET_NAME_BLANK_SPACES );
    }

    @DataProvider(name = "assortmentItemsFilterByUPC13andFC" )
    public Object[] getAssortmentItemsFilterByUPC13andFC(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("getAssortmentItems"), INPUT_SHEET_NAME_FC_AND_UPC13 );
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     * will fetch all assortment items filter by assortmentName, using complete assortment name
     * will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     */
    @Test
    public void validateGetAssortmentItems_byAssortmentName_TC001() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_byAssortmentName_TC001 ");
        System.out.println("[Test Scenario] Validate total of assortment items fetched by Assortment Name ");
        String param = dbUtils.getRandomDBRecord(SELECT_RANDOM_ASSORTMENT_NAME,NAME);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS") +"?assortmentName="+ param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENT_NAME,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();


        //DB - Count how many assortment items are assigned to the given Assortment
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_ASSORTMENT_ITEMS_BY_FC, "%" + param + "%", COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        //Compare Assortment items return by DB VS the returned by API response
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        int expectedCode = assortmentItems == 0 && assortmentItemsDB == 0 ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_OK;
        boolean statusCode = restResponse.getStatusCode() == expectedCode;

        //Compare AssortmentItemsID fetched by DB VS AssortmentItemsId fetched by the API response
        boolean assortmentId = false;
        if(assortmentItems == 0 && assortmentItemsDB ==0)
             assortmentId = true;
        else if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null) {
            assortmentId = testUtils.compareAndPrint2Lists("Printing  Assortment Items Id from Api:  ", restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID),
                    "Printing  Assortment Items Id from DB:   ", dbUtils.selectQueryParamsReturnInt(SELECT_ID_ASSORTMENT_ITEM_FILTER_BY_FC, "%" + param + "%", new ArrayList<>(Collections.singleton("id"))));
        }

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + expectedCode + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + expectedCode+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems ? "\n[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                    "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println(assortmentId ? "[Correct]  Assortment Items fetched from Assortment Management Core matches the Assortment Items in the DataBase filter by " + param :
                    "[Error] Assortment Items fetched from Assortment Management Core NO matches the Assortment Items in the DataBase filter by " + param +", getAssortmentItems api retrieved:" + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + expectedCode + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        Assert.assertTrue(assortmentId, " [Error] Assortment Items fetched from Assortment Management Core NO matches the Assortment Items in the DataBase filter by " + param+ ", getAssortmentItems api retrieved: " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }

    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by AssortmentName,
     *     Parameters: using blank name, 1 blank space, 3 blank spaces
     *     will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     * @param dataMap
     */
    @Test (dataProvider = "assortmentItemsFilterByFCBlankSpaces" )
    public void validateGetAssortmentItems_ByAssortmentName_BlankSpaces_TC002(Map<String, String> dataMap) {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByAssortmentName_BlankSpaces_TC002 ");
        String param = !dataMap.get(EXCEL_FILTER__PARAM).isEmpty() && dataMap.get(EXCEL_FILTER__PARAM) != null ? dataMap.get(EXCEL_FILTER__PARAM):"";
        System.out.println("[Test Scenario]  Assortment Items filtered by AssortmentName: "+  dataMap.get(EXCEL_TC_NUMBER) +" - " + dataMap.get(EXCEL_TEST_CASE) + param);
        System.out.println("[Endpoint]  " + env.getString("ASSORTMENT_HOST") + dataMap.get(EXCEL_ENDPOINT) + param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENT_NAME,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;


        //Fetch from DB count of every single existing Assortmen Item, empty/ blank AssortmentName parameter must return all existing assortment items
        List<String> dbList = dbUtils.fetchRecordSFromDB(SELECT_COUNT_ASSORTMENT_ITEMS_BY_EMPTY_FC, COUNT);
        int   assortmentItemsDB = !dbList.isEmpty() ? Integer.valueOf(dbList.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB; // compare DB VS api return the same number of assortment Items

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems?"[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB:
                "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems,"[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println("============================================================================================");
    }


    /**
     *     This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by AssortmentName,
     *     Parameters: Assortment name not existing in DB, escaped special characters: / %2F ,? %3F , : %3A ,@ %40
     *     will validate the status code is 404 Not Found, will validate errors: "assortment items were not found during search"
     * @param dataMap
     */
     @Test (dataProvider = "assortmentItemsFilterByFCInvalidInputData")
    public void validateGetAssortmentItems_ByAssortmentName_InvalidInputData_TC003(Map<String, String> dataMap) {
         System.out.println("\n\n============================================================================================");
         System.out.println(" Init validateGetAssortmentItems_ByAssortmentName_InvalidInputData_TC003 ");
         String param = dataMap.get(EXCEL_FILTER__PARAM) != null  &&  !dataMap.get(EXCEL_FILTER__PARAM).isEmpty()? dataMap.get(EXCEL_FILTER__PARAM):"";
         System.out.println("[Test Scenario]  "+  dataMap.get(EXCEL_TC_NUMBER) +" - " + dataMap.get(EXCEL_TEST_CASE) + " Parameter " + param);
         System.out.println("[Endpoint]  " + env.getString("ASSORTMENT_HOST") +"/assortment-items?pageable=false&assortmentName=" + param);

         String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
         Response restResponse = given().urlEncodingEnabled(false).
                 headers("Authorization", token.getBearerToken()).
                 queryParam(ASSORTMENT_NAME,param).
                 queryParam(PAGEABLE,"false").
                 when().get(url).then().contentType(ContentType.JSON).extract().response();

         boolean errorReason = restResponse.jsonPath().get(JSON_PATH_ERRORS_REASON) != null?
                    restResponse.jsonPath().get(JSON_PATH_ERRORS_REASON).toString().equalsIgnoreCase(GET_ASSORTMENT_ITEMS_ERRORS_REASON):false;
            boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_NOT_FOUND;

            System.out.println(errorReason ? "\n[Correct]  Assortment Items were not found because special character " + param + " is invalid data, api error reason " + GET_ASSORTMENT_ITEMS_ERRORS_REASON :
                    "[Error] Assortment Items  should NOT be found because special character " + param + " is invalid input data , expected Message: '"
                            + GET_ASSORTMENT_ITEMS_ERRORS_REASON + "' message, instead api retrieved answer was: "+ restResponse.jsonPath().get().toString());
            System.out.println(statusCode ? "[Correct]  Expected code: " + HttpStatus.SC_NOT_FOUND + " VS " + " Obteined Status Code: " + restResponse.getStatusCode() :
                    "[Error] Expected code: " + HttpStatus.SC_NOT_FOUND + " VS " + " Obteined Status Code: " + restResponse.getStatusCode());


            Assert.assertTrue(errorReason, "[Error] Assortment Items  should NOT be found because special character " + param + " is invalid input data , expected Message: '"
                    + GET_ASSORTMENT_ITEMS_ERRORS_REASON + "' message, instead api retrieved answer was: "+ restResponse.jsonPath().get().toString());
            Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_NOT_FOUND + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     * will fetch all assortment items filter by  assortmentName, using PARTIAL assortment name
     * will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     */
    @Test
    public void validateGetAssortmentItems_byPartialAssortmentName_TC004() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_byPartialAssortmentName_TC004 ");
        System.out.println("[Test Scenario] Validate total of assortment items fetched by PARTIAL Assortment Name ");
        //Remove Assort word, to have only partial name
        String param = dbUtils.getRandomDBRecord(SELECT_RANDOM_ASSORTMENT_NAME,NAME).replace("Assort","");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS") +"?assortmentName="+ param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENT_NAME,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();


        //DB - Count how many assortment items are assigned to the given Assortment
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_ASSORTMENT_ITEMS_BY_FC, "%" + param + "%", COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        //Compare Assortment items return by DB VS the returned by API response
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        int expectedCode = assortmentItems == 0 && assortmentItemsDB == 0 ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_OK;
        boolean statusCode = restResponse.getStatusCode() == expectedCode;

        //Compare AssortmentItemsID fetched by DB VS AssortmentItemsId fetched by the API response
        boolean assortmentId = false;
        if(assortmentItems == 0 && assortmentItemsDB ==0)
            assortmentId = true;
        else if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null) {
            assortmentId = testUtils.compareAndPrint2Lists("Printing  Assortment Items Id from Api:  ", restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID),
                    "Printing  Assortment Items Id from DB:   ", dbUtils.selectQueryParamsReturnInt(SELECT_ID_ASSORTMENT_ITEM_FILTER_BY_FC, "%" + param + "%", new ArrayList<>(Collections.singleton("id"))));
        }

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + expectedCode + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + expectedCode+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems ? "\n[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println(assortmentId ? "[Correct]  Assortment Items fetched from Assortment Management Core matches the Assortment Items in the DataBase filter by " + param :
                "[Error] Assortment Items fetched from Assortment Management Core NO matches the Assortment Items in the DataBase filter by " + param +", getAssortmentItems api retrieved:" + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + expectedCode + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        Assert.assertTrue(assortmentId, " [Error] Assortment Items fetched from Assortment Management Core NO matches the Assortment Items in the DataBase filter by " + param+ ", getAssortmentItems api retrieved: " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     * will fetch all assortment items filter by  assortmentName, using PARTIAL assortment name
     * will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     */
    @Test
    public void validateGetAssortmentItems_byPartialAssortmentName2_TC005() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_byPartialAssortmentName2_TC005 ");
        System.out.println("[Test Scenario] Validate total of assortment items fetched by PARTIAL Assortment Name ");
        //get a random Assortment Name, get as parameter only the first word of the name, example: Detroit Assort, param = Detroit
        String param = dbUtils.getRandomDBRecord(SELECT_RANDOM_ASSORTMENT_NAME,NAME).split(" ")[0];
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS") +"?assortmentName="+ param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENT_NAME,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();


        //DB - Count how many assortment items are assigned to the given Assortment
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_ASSORTMENT_ITEMS_BY_FC, "%" + param + "%", COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        //Compare Assortment items return by DB VS the returned by API response
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        int expectedCode = assortmentItems == 0 && assortmentItemsDB == 0 ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_OK;
        boolean statusCode = restResponse.getStatusCode() == expectedCode;

        //Compare AssortmentItemsID fetched by DB VS AssortmentItemsId fetched by the API response
        boolean assortmentId = false;
        if(assortmentItems == 0 && assortmentItemsDB ==0)
            assortmentId = true;
        else if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null) {
            assortmentId = testUtils.compareAndPrint2Lists("Printing  Assortment Items Id from Api:  ", restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID),
                    "Printing  Assortment Items Id from DB:   ", dbUtils.selectQueryParamsReturnInt(SELECT_ID_ASSORTMENT_ITEM_FILTER_BY_FC, "%" + param + "%", new ArrayList<>(Collections.singleton("id"))));
        }

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + expectedCode + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + expectedCode+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems ? "\n[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println(assortmentId ? "[Correct]  Assortment Items fetched from Assortment Management Core matches the Assortment Items in the DataBase filter by " + param :
                "[Error] Assortment Items fetched from Assortment Management Core NO matches the Assortment Items in the DataBase filter by " + param +", getAssortmentItems api retrieved:" + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + expectedCode + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        Assert.assertTrue(assortmentId, " [Error] Assortment Items fetched from Assortment Management Core NO matches the Assortment Items in the DataBase filter by " + param+ ", getAssortmentItems api retrieved: " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     * will fetch all assortment items filter by  assortmentId, single id
     * will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     */
    @Test
    public void validateGetAssortmentItems_byAssortmentID_TC006() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_byAssortmentID_TC006 ");
        System.out.println("[Test Scenario] Validate total of assortment items fetched by Assortment Item ID ");
        //get a random Assortment ID
        int param = dbUtils.getRandomIntDBRecord(SELECT_RANDOM_ASSORTMENT_ID,ID);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS") +"?assortmentId="+ param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENT_ID,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();


        //DB - Count how many assortment items are assigned to the given Assortment ID
        List<String> dblist = dbUtils.DBSelectQueryObjectParams(SELECT_COUNT_ASSORTMENTS_ITEMS_BY_ASSORTMENT, Arrays.asList(param), COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        //Compare Assortment items return by DB VS the returned by API response
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        int expectedCode = assortmentItems == 0 && assortmentItemsDB == 0 ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_OK;
        boolean statusCode = restResponse.getStatusCode() == expectedCode;

        //Compare AssortmentItemsID fetched by DB VS AssortmentItemsId fetched by the API response
        boolean assortmentId = false;
        if(assortmentItems == 0 && assortmentItemsDB ==0)
            assortmentId = true;
        else if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null) {
            assortmentId = testUtils.compareAndPrint2Lists("Printing  Assortment Items Id from Api:  ", restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID),
                    "Printing  Assortment Items Id from DB:   ", dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_ID_ASSORTMENT_ITEM_BY_ASSORTMENT_ID,Arrays.asList(param),"id"));
        }

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + expectedCode + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + expectedCode + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems ? "\n[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println(assortmentId ? "[Correct]  Assortment Items fetched from Assortment Management Core matches the Assortment Items in the DataBase filter by " + param :
                "[Error] Assortment Items fetched from Assortment Management Core NO matches the Assortment Items in the DataBase filter by " + param +", getAssortmentItems api retrieved:" + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + expectedCode + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        Assert.assertTrue(assortmentId, " [Error] Assortment Items fetched from Assortment Management Core NO matches the Assortment Items in the DataBase filter by " + param+ ", getAssortmentItems api retrieved: " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }



    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by AssortmentID,
     *     Parameters: using blank name, 1 blank space, 3 blank spaces
     *     will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     * @param dataMap
     */
    @Test (dataProvider = "assortmentItemsFilterByFCBlankSpaces" )
    public void validateGetAssortmentItems_ByAssortmentID_BlankSpaces_TC007(Map<String, String> dataMap) {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByAssortmentID_BlankSpaces_TC007 ");
        String param = !dataMap.get(EXCEL_FILTER__PARAM).isEmpty() && dataMap.get(EXCEL_FILTER__PARAM) != null ? dataMap.get(EXCEL_FILTER__PARAM):"";
        System.out.println("[Test Scenario]  Assortment Items filtered by AssortmentID: "+  dataMap.get(EXCEL_TC_NUMBER) +" - " + dataMap.get(EXCEL_TEST_CASE) + param);
        System.out.println("[Endpoint]  " + env.getString("ASSORTMENT_HOST") + dataMap.get(EXCEL_ENDPOINT) + param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENT_ID,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //Fetch from DB count of every single existing Assortmen Item, empty/ blank AssortmentName parameter must return all existing assortment items
        List<String> dbList = dbUtils.fetchRecordSFromDB(SELECT_COUNT_ASSORTMENT_ITEMS_BY_EMPTY_FC, COUNT);
        int   assortmentItemsDB = !dbList.isEmpty() ? Integer.valueOf(dbList.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB; // compare DB VS api return the same number of assortment Items

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems?"[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB:
                "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems,"[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println("============================================================================================");
    }





    /**
     *     This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will validate invalid assortment id parameter is not found results
     *     will validate the status code is 404 Not Found, will validate errors: "assortment items were not found during search"
     */
    @Test
    public void validateGetAssortmentItems_ByAssortmentID_InvalidInputData_TC008() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByAssortmentID_InvalidInputData_TC008 ");
        int param = -1;
        System.out.println("[Test Scenario] Validate Api response when Assortment Id is invalid = -1");
        System.out.println("[Endpoint]  " + env.getString("ASSORTMENT_HOST") +"/assortment-items?pageable=false&assortmentId=" + param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().urlEncodingEnabled(false).
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENT_ID,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean errorReason = restResponse.jsonPath().get(JSON_PATH_ERRORS_REASON) != null?
                restResponse.jsonPath().get(JSON_PATH_ERRORS_REASON).toString().equalsIgnoreCase(GET_ASSORTMENT_ITEMS_ERRORS_REASON):false;
        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_NOT_FOUND;

        System.out.println(errorReason ? "\n[Correct]  Assortment Items were not found because " + param + " is an invalid assortmentId, api error reason " + GET_ASSORTMENT_ITEMS_ERRORS_REASON :
                "[Error] Assortment Items  should NOT be found because " + param + " is an invalid assortmentId , expected Message: '"
                        + GET_ASSORTMENT_ITEMS_ERRORS_REASON + ", instead api retrieved answer was: "+ restResponse.jsonPath().get().toString());
        System.out.println(statusCode ? "[Correct]  Expected code: " + HttpStatus.SC_NOT_FOUND + " VS " + " Obtained Status Code: " + restResponse.getStatusCode() :
                "[Error] Expected code: " + HttpStatus.SC_NOT_FOUND + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());


        Assert.assertTrue(errorReason, "[Error] Assortment Items  should NOT be found because " + param + " is invalid assortmentId , expected Message: '"
                + GET_ASSORTMENT_ITEMS_ERRORS_REASON + "' message, instead api retrieved answer was: "+ restResponse.jsonPath().get().toString());
        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_NOT_FOUND + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println("============================================================================================");
    }































}
