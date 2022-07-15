package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItemStatus;
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

public class GetAssortmentItems2 extends InitAssortment {

    protected static YaapitaReport report;

    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }

    @DataProvider(name = "assortmentItemsFilterByFCBlankSpaces" )
    public Object[] getAssortmentItemsFilterByFCBlankSpaces(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("assortmentItemsFilterByFC"), INPUT_SHEET_NAME_BLANK_SPACES );
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by UPCS (UPC13)
     *     will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     */
     @Test
    public void validateGetAmtItems_filterByUpc_TC009() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_filterByUpc_TC009 ");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_UPC13,Arrays.asList(1000),"upc13").get(0);
        System.out.println("[Test Scenario]  get assortment items by upc filtered with complete upc13 "+param);
        System.out.println("[Endpoint]  " + env.getString("ASSORTMENT_HOST") +"/assortment-items?pageable=false&upcs=" + param);

        String url = env.getString("ASSORTMENT_HOST") +env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        //count how many assortment items are given the UPC13 in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_ASSORTMENT_ITEMS_BY_UPC_13, "%" + param + "%", COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
         //Compare Assortment items return by DB VS the returned by API response
         boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

         //Compare AssortmentItemsID fetched by DB VS AssortmentItemsId fetched by the API response
         boolean assortmentId = false;
         if(assortmentItems == 0 && assortmentItemsDB ==0)
             assortmentId = true;
        else if(restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID) != null) {
             assortmentId = testUtils.compareAndPrint2Lists("Printing  Assortment Items Id from Api:  ", restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID),
                     "Printing  Assortment Items Id from DB:   ", dbUtils.selectQueryParamsReturnInt(SELECT_ID_ASSORTMENT_ITEM_FILTER_BY_UPC_13, "%" + param + "%", Arrays.asList("id")));
         }


        System.out.println(numAssortmentItems ? "\n[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "\n[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println(assortmentId ? "[Correct]  Assortment Items fetched from API matches the Assortment ItemsN ID in DB filter by " + param :
                "[Error] Assortment Items fetched from API NO matches the Assortment Items ID in the DB filter by " + param);


        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        Assert.assertTrue(assortmentId, " [Error] Assortment Items ID fetched from API NO matches the Assortment Items in DB filter by " + param);
        System.out.println("============================================================================================");
    }




    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by multiple UPCS (UPC13)
     *     will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     */
    @Test
    public void validateGetAmtItems_filterByUpcs_TC0010() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_filterByUpcs_TC0010 ");
        List<String> param = dbUtils.getRandomDBRecords(3,SELECT_RANDOM_UPC13,Arrays.asList(100),"upc13");
        System.out.println("[Test Scenario]  get assortment items by upcs filter with complete upc13 "+param);
        System.out.println("[Endpoint]  " + env.getString("ASSORTMENT_HOST") +"/assortment-items?pageable=false&upcs=" +testUtils.formatMultipleUpcs(param) );

        String url = env.getString("ASSORTMENT_HOST") +env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,testUtils.formatMultipleUpcs(param)).
                queryParam(PAGEABLE,"false").

                when().get(url).then().contentType(ContentType.JSON).extract().response();

        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_ASSORTMENT_ITEMS_BY_UPC13,  param , COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        //validate ids retrieved in API VS found in DB
        boolean assortmentId = false;
        List<Object> params = new ArrayList<>(); params.addAll(param);
        if(restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID) != null) {
            assortmentId = testUtils.compareAndPrint2Lists("Printing  Assortment Items Id from Api:  ", restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID),
                    "Printing  Assortment Items Id from DB:   ", dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_ID_ASSORTMENT_ITEMS_BY_3_UPC13, params, "id"));
        }


        System.out.println(numAssortmentItems ? "\n[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "\n[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println(assortmentId ? "[Correct]  Assortment Items ID fetched from API matches the Assortment Items ID found in DB filter by " + param :
                "[Error] Assortment Items ID fetched from API NOT matches the Assortment Items ID found in DB filter by  " + param);

        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        Assert.assertTrue(assortmentId, " [Error] Assortment Items ID fetched from API NO matches the Assortment Items ID in DB filter by " + param);
        System.out.println("============================================================================================");
    }




    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by multiple partial UPCS (UPC13)
     *     will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     */
    @Test
    public void validateGetAmtItems_filterByUpcs_partial_TC0011() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_filterByUpcs_partial_TC0011 ");
        List<String> param = getPartialRandomUPCs(3);
        System.out.println("[Test Scenario]  get assortment items by upcs filter with complete upc13 "+param);
        System.out.println("[Endpoint]  " + env.getString("ASSORTMENT_HOST") +"/assortment-items?pageable=false&upcs=" +testUtils.formatMultipleUpcs(param) );

        String url = env.getString("ASSORTMENT_HOST") +env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,testUtils.formatMultipleUpcs(param)).
                queryParam(PAGEABLE,"false").

                when().get(url).then().contentType(ContentType.JSON).extract().response();


        //count how many assortment items are given the multiple partial UPC13 in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_ASSORTMENT_ITEMS_BY_3_UPC13,  formatUpcsLike(param) , COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        //validate ids retrieved in API VS found in DB
        boolean assortmentId = false;
        List<Object> params = new ArrayList<>(); params.addAll(formatUpcsLike(param));
        if(restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID) != null) {
            assortmentId = testUtils.compareAndPrint2Lists("Printing  Assortment Items Id from Api:  ", restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_ID),
                    "Printing  Assortment Items Id from DB:   ", dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_ID_ASSORTMENT_ITEMS_BY_3_UPC13, params, "id"));
        }


        System.out.println(numAssortmentItems ? "\n[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "\n[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println(assortmentId ? "[Correct]  Assortment Items ID fetched from API matches the Assortment Items ID found in DB filter by " + param :
                "[Error] Assortment Items ID fetched from API NOT matches the Assortment Items ID found in DB filter by  " + param);

        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        Assert.assertTrue(assortmentId, " [Error] Assortment Items ID fetched from API NO matches the Assortment Items ID in DB filter by " + param);
        System.out.println("============================================================================================");
    }



    /**
     *  This method will fetch all assortment items filter by upcs,
     *     Parameters: using blank name, 1 blank space, 3 blank spaces
     *     will validate the status code is 200 OK, will count the assortment Items retrieved VS DB, will validate assortment id fetched
     */
     @Test (dataProvider = "assortmentItemsFilterByFCBlankSpaces" )
    public void validateGetAssortmentItems_ByUPCS_BlankSpaces_TC0012(Map<String, String> dataMap) {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByUPCS_BlankSpaces_TC0012 ");
        String param = !dataMap.get(EXCEL_FILTER__PARAM).isEmpty() && dataMap.get(EXCEL_FILTER__PARAM) != null ? dataMap.get(EXCEL_FILTER__PARAM):"";
        System.out.println("[Test Scenario] GetAssortmentItems filtered by UPCS with "+  dataMap.get(EXCEL_TC_NUMBER) +" - " + dataMap.get(EXCEL_TEST_CASE) + param);
        System.out.println("[Endpoint]  " + env.getString("ASSORTMENT_HOST") + "/assortment-items?pageable=false&upcs=" + param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

         boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        List<String> dbList = dbUtils.fetchRecordSFromDB(SELECT_COUNT_ASSORTMENT_ITEMS_BY_EMPTY_FC, COUNT);
        int   assortmentItemsDB = !dbList.isEmpty() ? Integer.valueOf(dbList.get(0)) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;


         System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                 "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems?"[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB:
                "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);

         Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
         Assert.assertTrue(numAssortmentItems,"[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println("============================================================================================");
    }




    /**
     * This method validate  get assortment items filtering by a single assortmentItemId
     * Validate the status code is 200 and the Api items count  compare to DB assortment items by Assortment ID
     */
    @Test
    public void validateGetAssortmentItems_ByAssortmentId_TC0013() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByAssortmentId_TC0013 ");
        System.out.println("[Test Scenario] Get Assortment Items  filter by a single  Assortment id");
        int param = dbUtils.getRandomIntDBRecord(SELECT_RANDOM_ASSORTMENT_ID,ID);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS")+"?"+ ASSORTMENT_ID +"=" + param );

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENT_ID,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        List<Integer> dblist = dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_COUNT_ASSORTMENTS_ITEMS_BY_ASSORTMENT, Arrays.asList(param)  , COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? dblist.get(0) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        int expectedCode = assortmentItems == 0 && assortmentItemsDB == 0 ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_OK;
        boolean statusCode = restResponse.getStatusCode() == expectedCode;

        System.out.println(numAssortmentItems ? "\n[Correct] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "\n[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println(statusCode ? "[Correct]  Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode() :
                "[Error] Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode());

        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        Assert.assertTrue(statusCode, " [Error] Expected code: " + expectedCode + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println("============================================================================================");
    }







    /**
     * This method validate  get assortment items filtering by a single assortmentItem status
     * Validate the status code is 200 and the Api response retrieve all assortmentItem with status = to status in the request
     */
    @Test
    public void validateGetAssortmentItems_ByStatus_TC0014() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByStatus_TC0014 ");
        System.out.println("[Test Scenario] Get Assortment Items  filter by a single status");
        int param = dbUtils.getRandomIntDBRecord(SELECT_RANDOM_ITEM_STATUS_ID,ID);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS")+"?"+ STATUS +"=" + param );

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(STATUS,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        List<Integer> dblist = dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_COUNT_ASSORTMENTS_ITEMS_BY_STATUS, Arrays.asList(param)  , COUNT);
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
     * This method validate  get assortment items filtering by a single assortmentItem storePickStatus
     * Validate the status code is 200 and the Api response retrieve all assortmentItem with storePickStatus = to storePickStatus in the request
     */
    @Test
    public void validateGetAssortmentItems_ByStorePickStatus_TC0015() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByStorePickStatus_TC0015 ");
        System.out.println("[Test Scenario] Get Assortment Items  filter by a single storePickStatus");
        int param = dbUtils.getRandomIntDBRecord(SELECT_RANDOM_ITEM_STORE_PICK_STATUS_ID,ID);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS")+"?"+ STORE_PICK_STATUS +"=" + param );

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(STORE_PICK_STATUS,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        List<Integer> dblist = dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_COUNT_ASSORTMENTS_ITEMS_BY_STORE_PICK_STATUS, Arrays.asList(param)  , COUNT);
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
     * This method validate  get assortment items filtering by a single locationCode
     * Validate the status code is 200 and the Api response retrieve all assortmentItem with locationCode= to locationCode in the request
     */
    @Test
    public void validateGetAssortmentItems_ByLocationCode_TC0016() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItems_ByLocationCode_TC0016 ");
        System.out.println("[Test Scenario] Get Assortment Items  filter by a single location code");
        String param = dbUtils.getRandomDBRecord(SELECT_RANDOM_LOCATION_CODE,CODE);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_ASSORTMENT_ITEMS")+"?"+ API_LOCATION_CODE +"=" + param );

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_LOCATION_CODE,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        List<Integer> dblist = dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_COUNT_ASSORTMENTS_ITEMS_BY_LOCATION_CODE, Arrays.asList(param)  , COUNT);
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

        Assert.assertTrue(statusCode, "\n [Error] Expected code: " + expectedCode + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems, "[Error] getAssortment Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);
        System.out.println("============================================================================================");
    }








    public List<String> getPartialRandomUPCs(int num){
        List<String> partialUPCS = new ArrayList<>();

        List<String> upcs = dbUtils.getRandomDBRecords(num,SELECT_RANDOM_UPC13,Arrays.asList(2000),"upc13");
        Iterator<String> itr = upcs.iterator();
        while(itr.hasNext())
            partialUPCS.add(itr.next().substring(8,13));

        return partialUPCS;
    }

    public List<String> formatUpcsLike(List<String> upcs){
        List<String> params = new ArrayList<>();
        if(upcs == null || upcs.isEmpty())
            return params;

        Iterator<String> itr = upcs.iterator();
        while(itr.hasNext())
            params.add("%"+itr.next()+"%");

        return params;
    }




}
