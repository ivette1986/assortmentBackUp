package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.model.assortmentmgmtcore.Result;
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

public class GetAssortmentItemsPageable extends InitAssortment {
    protected static YaapitaReport report;

    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }

    @DataProvider(name = "assortmentItemsPageable" )
    public Object[] getAssortmentItemsPageable(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("getAssortmentItems"), INPUT_SHEET_PAGEABLE );
    }

    @DataProvider(name = "assortmentItemsPageableSize" )
    public Object[] getAssortmentItemsPageableSize(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("getAssortmentItems"), INPUT_SHEET_PAGEABLE_SIZE );
    }

    @DataProvider(name = "assortmentItemsPageOffset" )
    public Object[] getAssortmentItemsPageOffset(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("getAssortmentItems"), INPUT_SHEET_PAGE_OFFSET );
    }

    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items pageable= true, pageable = false and pageable by default true
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( dataProvider = "assortmentItemsPageable")
    public void validateGetAmtItems_pageable_TC001(Map<String, String> dataMap) {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_pageable_TC001 ");
        String pageable = dataMap.get(EXCEL_FILTER__PARAM)!= null? dataMap.get(EXCEL_FILTER__PARAM).toString():"";
        System.out.println("[Test Scenario] "+dataMap.get(EXCEL_TC_NUMBER) +" " + dataMap.get(EXCEL_TEST_CASE) + " Parameter Pageable = " + pageable);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=" + pageable);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(!pageable.isEmpty()?"pageable":"",!pageable.isEmpty()? pageable:"").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean isPageable = pageable.isEmpty() || pageable.equalsIgnoreCase("true")? true:false;
        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(dataMap.get(EXCEL_STATUS_CODE).toString());
        int apiCount = restResponse.jsonPath().get(JSON_PATH_COUNT) != null ? Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString()):0;
        int dbCount = isPageable?20:Integer.valueOf(dbUtils.fetchRecordSFromDB(SELECT_COUNT_ALL_ASSORTMENT_ITEMS,"count").get(0));
        boolean result = apiCount == dbCount;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(result ? "[Correct]  Assortment Items according to Pageable = " + isPageable + ", Expected number of a items " + apiCount + " Current number of a items from API " + dbCount:
                "[Error] Assortment Items according to Pageable = " + isPageable + ", Expected number of a items " + apiCount + " Current number of a items from API " + dbCount);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(result, " [Error] Assortment Items according to Pageable = " + isPageable + ", Expected number of a items " + apiCount + " Current number of a items from API " + dbCount);
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items page.size= 30,50, empty, blank spaces, 0
     *     will validate the status code is 200 OK, and for parameter 0 404
     */
    @Test( dataProvider = "assortmentItemsPageableSize")
    public void validateGetAmtItems_pageSize_TC002(Map<String, String> dataMap) {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_pageSize_TC002 ");
        String size = dataMap.get(EXCEL_FILTER__PARAM)!= null? dataMap.get(EXCEL_FILTER__PARAM).toString():"";
        System.out.println("[Test Scenario] "+dataMap.get(EXCEL_TC_NUMBER) +" " +  dataMap.get(EXCEL_TEST_CASE) + " Parameter page.size = " + size);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=true&page.size=" + size);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"true").
                queryParam(PAGE_SIZE,size).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(dataMap.get(EXCEL_STATUS_CODE).toString());
        String apiCount = restResponse.jsonPath().get(JSON_PATH_COUNT) != null?restResponse.jsonPath().get(JSON_PATH_COUNT).toString(): GET_ASSORTMENT_ITEMS_ERRORS_REASON;
        boolean pageSize = apiCount.equalsIgnoreCase(dataMap.get(EXCEL_EXPECTED).toString());


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + dataMap.get(EXCEL_STATUS_CODE).toString() + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + dataMap.get(EXCEL_STATUS_CODE).toString() + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(pageSize ? "[Correct]  Assortment Items according to page.Size = " + size + ", Expected number of a items: " + dataMap.get(EXCEL_EXPECTED).toString()+ " ,Current number of a items from API: " + apiCount:
                "[Error] Assortment Items according to page.Size = " + size + ", Expected number of a items: " + dataMap.get(EXCEL_EXPECTED).toString()+ ", Current number of a items from API: " + apiCount);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + dataMap.get(EXCEL_STATUS_CODE).toString() + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(pageSize, " [Error] Assortment Items according to page.Size = " + size + ", Expected number of a items: " + dataMap.get(EXCEL_EXPECTED).toString()+ " ,Current number of a items from API: " + apiCount);
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items  pageable = true and page.offset = 5, page.offset = 6, page.offset = 35, page.offset = 72, page.offset = 0
     *     will validate the status code is 200 OK, and should return all existing assortment items
     */
    @Test(dataProvider = "assortmentItemsPageOffset")
    public void validateGetAmtItems_pageOffset_TC003(Map<String, String> dataMap) {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_pageOffset_TC003 ");
        System.out.println("[Test Scenario] "+dataMap.get(EXCEL_TC_NUMBER) + " " + dataMap.get(EXCEL_TEST_CASE));
        int offset = dataMap.get(EXCEL_FILTER__PARAM) != null? Integer.valueOf(dataMap.get(EXCEL_FILTER__PARAM).toString()): 0 ;
        int pageSize = dataMap.get(EXCEL_PAGE_SIZE)!= null? Integer.valueOf(dataMap.get(EXCEL_PAGE_SIZE).toString()): 0;
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=true&page.size="+pageSize+"&page.offset=" + offset);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"true").
                queryParam(PAGE_OFFSET,offset>0? offset-1:offset).
                queryParam(PAGE_SIZE,pageSize).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(dataMap.get(EXCEL_STATUS_CODE));
        int limit = offset>0?offset+(pageSize-1):offset+pageSize;// as we count as 0 our first position, when offset is already 0 we wont decrease pagesize
        List<Integer> listdbOffset = getDBoffset(offset>0? offset-1:offset,SELECT_ASSORTMENT_ITEM_OFFSET,Arrays.asList(limit),"id" );
        boolean offsetMatches = offsetMatches(JSON_PATH_ASSORTMENT_ITEM_ID,restResponse,listdbOffset);


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + dataMap.get(EXCEL_STATUS_CODE) + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + dataMap.get(EXCEL_STATUS_CODE) + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(offsetMatches ? "[Correct]  Assortment Items according to offset " + offset + " and page.size " +pageSize + " were correctly fetched":
                "[Error] Assortment Items according to offset " + offset + " and page.size " +pageSize + " were NOT correctly fetched");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + dataMap.get(EXCEL_STATUS_CODE) + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(offsetMatches, " [Error] Assortment Items according to offset " + offset + " and page.size " +pageSize + " were NOT correctly fetched");
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items  pageable = true, page.size and page.offset = -1,
     *     will validate the status code is 200 OK, and should return all existing assortment items
     */
    @Test
    public void validateGetAmtItems_pageOffset_negativeNumber_TC004() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_pageOffset_negativeNumber_TC004 ");
        System.out.println("[Test Scenario] Test getAssortmentItems with Pageable=true, page.size=10 and page.offset=negative number");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=true&page.size=10&page.offset=-1");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"true").
                queryParam(PAGE_OFFSET,-1).
                queryParam(PAGE_SIZE,10).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR;
        String errorCode = restResponse.jsonPath().get(JSON_PATH_ERRORS_CODE)!= null?restResponse.jsonPath().get(JSON_PATH_ERRORS_CODE).toString():restResponse.jsonPath().get();
        boolean internalError = errorCode.equalsIgnoreCase(GET_ASSORTMENT_ITEMS_INTERNAL_SERVER_ERROR);

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_INTERNAL_SERVER_ERROR + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_INTERNAL_SERVER_ERROR + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(internalError ? "[Correct]  Assortment Items can't be filter with page.offset negative numbers so API responsed: "+ errorCode:
                "[Error] Assortment Items can't be filter with page.offset negative numbers but error should be: "+ GET_ASSORTMENT_ITEMS_INTERNAL_SERVER_ERROR + " instead of " +errorCode);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_INTERNAL_SERVER_ERROR + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(internalError, " [Error] Assortment Items can't be filter with page.offset negative numbers but error should be: "+ GET_ASSORTMENT_ITEMS_INTERNAL_SERVER_ERROR + " instead of " +errorCode);
        System.out.println("============================================================================================");
    }

    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items  pageable = true and  page.offset = blank spaces
     *     will validate the status code is 200 OK, and should return all existing assortment items
     */
    @Test
    public void validateGetAmtItems_pageOffset_blankspaces_TC005() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_pageOffset_blankspaces_TC005 ");
        System.out.println("[Test Scenario] GetAssormentItems filter by pageable=true and page.offset= blank space");
        int offset = 0 ;
        int pageSize = 70;
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=true&page.size="+pageSize+"&page.offset=");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"true").
                queryParam(PAGE_OFFSET," ").
                queryParam(PAGE_SIZE,pageSize).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;
        List<Integer> listdbOffset = getDBoffset(offset,SELECT_ASSORTMENT_ITEM_OFFSET,Arrays.asList(offset+pageSize),"id" );
        boolean offsetMatches = offsetMatches(JSON_PATH_ASSORTMENT_ITEM_ID,restResponse,listdbOffset);


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(offsetMatches ? "[Correct]  Assortment Items according to offset " + offset + " and page.size " +pageSize + " were correctly fetched":
                "[Error] Assortment Items according to offset " + offset + " and page.size " +pageSize + " were NOT correctly fetched");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(offsetMatches, " [Error] Assortment Items according to offset " + offset + " and page.size " +pageSize + " were NOT correctly fetched");
        System.out.println("============================================================================================");
    }

    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items  pageable = true and  page.offset = empty
     *     will validate the status code is 200 OK, and should return all existing assortment items
     */
    @Test
    public void validateGetAmtItems_pageOffset_empty_TC006() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_pageOffset_empty_TC006 ");
        System.out.println("[Test Scenario] GetAssormentItems filter by pageable=true and page.offset= empty");
        int offset = 0 ;
        int pageSize = 60;
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=true&page.size="+pageSize+"&page.offset=");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"true").
                queryParam(PAGE_OFFSET,"").
                queryParam(PAGE_SIZE,pageSize).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;
        List<Integer> listdbOffset = getDBoffset(offset,SELECT_ASSORTMENT_ITEM_OFFSET,Arrays.asList(offset+pageSize),"id" );
        boolean offsetMatches = offsetMatches(JSON_PATH_ASSORTMENT_ITEM_ID,restResponse,listdbOffset);


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(offsetMatches ? "[Correct]  Assortment Items according to offset " + offset + " and page.size " +pageSize + " were correctly fetched":
                "[Error] Assortment Items according to offset " + offset + " and page.size " +pageSize + " were NOT correctly fetched");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(offsetMatches, " [Error] Assortment Items according to offset " + offset + " and page.size " +pageSize + " were NOT correctly fetched");
        System.out.println("============================================================================================");
    }





    private List<Integer> getDBoffset(int offset, String query,List<Object> params,String column ){
        List<Integer> listdbOffset = new ArrayList<>();

        List<String> listAssortmentIds = dbUtils.DBSelectQueryObjectParams(SELECT_ASSORTMENT_ITEM_OFFSET, params, column);
        if(listAssortmentIds== null || listAssortmentIds.isEmpty())
            return listdbOffset;
        for(int i= offset; i<listAssortmentIds.size();i++)
            listdbOffset.add(Integer.valueOf(listAssortmentIds.get(i)));

        return listdbOffset;
    }

    private boolean offsetMatches(String jsonPath,Response response,List listdbOffset){
        List listApiOffset = response.jsonPath().get(jsonPath)!=null? response.jsonPath().get(jsonPath) :new ArrayList<String>();

        if(listApiOffset == null || listdbOffset == null || listApiOffset.isEmpty() || listdbOffset.isEmpty())
            return false;

        System.out.println("\n\nAssortment Items by offset retrieved by API: "  + listApiOffset.toString());
        System.out.println("Assortment Items by offset retrieved by DB:  " + listdbOffset.toString());

        return listApiOffset.equals(listdbOffset);

    }




}