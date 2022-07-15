package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.model.assortmentmgmtcore.Result;
import com.org.yaapita.YaapitaReport;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.HttpCookie;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kroger.assortment.testUtilities.Constants.*;
import static com.kroger.assortment.testUtilities.ConstantsDB.*;
import static com.org.yaapita.libloadinputdata.LoadExcelData.getEnabledExcelTests;
import static io.restassured.RestAssured.given;

public class GetAssortmentItemsSortBy extends InitAssortment {
    protected static YaapitaReport report;

    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ env.getString("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }



    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy  Default value is upc13.
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test(enabled = false )
    public void validateGetAmtItems_SortBy_TC010() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_TC010 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy default, ordered by UPC13");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"").
                when().get(url).then().contentType(ContentType.JSON).extract().response();


        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_UPC13,SELECT_ASSORTMENT_ITEM_SORTBY,UPC13s,"upc13");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by default UPC13" :
                "[Error] Assortment Items were NOT correctly Sorted by default UPC13 ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by default UPC13");
        System.out.println("============================================================================================");
    }


        /**
         *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
         *     will fetch all assortment items filter by pageable=false and SortBy  pidDescription
         *     will validate the status code is 200 OK, will validate is order by parameter
         */
        @Test( enabled = false)
        public void validateGetAmtItems_SortBy_PIDDescription_TC011() {
            System.out.println("\n\n============================================================================================");
            System.out.println(" Init validateGetAmtItems_SortBy_PIDDescription_TC011 ");
            System.out.println("[Test Scenario]  Test getAssortmentItems SortBy pidDescription");
            List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
            System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                    UPC13s.toString() + "&SortBy=pidDescription");

            String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
            Response restResponse = given().
                    headers("Authorization", token.getBearerToken()).
                    queryParam(PAGEABLE,"false").
                    queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                    queryParam(SORT_BY,"pidDescription").
                    when().get(url).then().contentType(ContentType.JSON).extract().response();

            boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
            boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_PID_DESCRIPTION,SELECT_ASSORTMENT_ITEM_SORTBY_PID_DESCRIPTION,UPC13s,"pid_description");

            System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                    "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
            System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by pidDescription" :
                    "[Error] Assortment Items were NOT correctly Sorted by pidDescription ");

            Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
            Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by pidDescription");
            System.out.println("============================================================================================");
        }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy caseUpc
     *      source, replacementSource, supplierSiteName, supplierSiteCode, assortment, itemStatus, storePickStatus
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( enabled = false)
    public void validateGetAmtItems_SortBy_CaseUPC_TC012() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_CaseUPC_TC012 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy caseUpc");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=caseUpc");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"caseUpc").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_PID_CASEUPC,SELECT_ASSORTMENT_ITEM_SORTBY_CASEUPC,UPC13s,"case_upc");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by caseUpc" :
                "[Error] Assortment Items were NOT correctly Sorted by caseUpc ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by caseUpc");
        System.out.println("============================================================================================");
    }

    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy  size
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( enabled = false)
    public void validateGetAmtItems_SortBy_SIZE_TC013() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_SIZE_TC013 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy Size");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=size");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"size").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_SIZE,SELECT_ASSORTMENT_ITEM_SORTBY_SIZE,UPC13s,"size");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by size" :
                "[Error] Assortment Items were NOT correctly Sorted by size ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by size");
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy source
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( enabled = false)
    public void validateGetAmtItems_SortBy_Source_TC014() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_Source_TC014 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy Source");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=source");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"source").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_SOURCE,SELECT_ASSORTMENT_ITEM_SORTBY_SOURCE,UPC13s,"source");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by source" :
                "[Error] Assortment Items were NOT correctly Sorted by source ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by source");
        System.out.println("============================================================================================");
    }




    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy replacementSource
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( enabled = false)
    public void validateGetAmtItems_SortBy_ReplacementSource_TC015() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_Source_TC015 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy replacementSource");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=replacementSource");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"replacementSource").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_REPLACEMENT_SOURCE,SELECT_ASSORTMENT_ITEM_SORTBY_REPLACEMENT,UPC13s,"replacement_source");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by replacementSource" :
                "[Error] Assortment Items were NOT correctly Sorted by replacementSource ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by replacementSource");
        System.out.println("============================================================================================");
    }



    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy supplierSiteName
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( enabled = false)
    public void validateGetAmtItems_SortBy_SuplierSiteName_TC016() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_SuplierSiteName_TC016 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy supplierSiteName");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=supplierSiteName");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"supplierSiteName").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_SUPPLIER_SITE_NAME,SELECT_ASSORTMENT_ITEM_SORTBY_SUPPLIER_SITE_NAME,UPC13s,"supplier_site_name");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by supplierSiteName" :
                "[Error] Assortment Items were NOT correctly Sorted by supplierSiteName ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by supplierSiteName");
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy supplierSiteCode
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( enabled = false)
    public void validateGetAmtItems_SortBy_supplierSiteCode_TC017() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_supplierSiteCode_TC017 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy supplierSiteCode");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=supplierSiteCode");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"supplierSiteCode").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_SUPPLIER_SITE_CODE,SELECT_ASSORTMENT_ITEM_SORTBY_SUPPLIER_SITE_CODE,UPC13s,"supplier_site_code");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by supplierSiteCode" :
                "[Error] Assortment Items were NOT correctly Sorted by supplierSiteCode ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by supplierSiteCode");
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy assortment
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( enabled = false)
    public void validateGetAmtItems_SortBy_assortment_TC018() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_assortment_TC018 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy assortment");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=assortment");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"assortment").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_ASSORTMENT,SELECT_ASSORTMENT_ITEM_SORTBY_ASSORTMENT,UPC13s,"name");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by assortment" :
                "[Error] Assortment Items were NOT correctly Sorted by assortment ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by assortment");
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy  itemStatus, storePickStatus
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( enabled = false)
    public void validateGetAmtItems_SortBy_itemStatus_TC019() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_itemStatus_TC019 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy itemStatus");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=itemStatus");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"itemStatus").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_ASSORTMENT_ITEM_STATUS,SELECT_ASSORTMENT_ITEM_SORTBY_ITEM_STATUS,UPC13s,"name");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by itemStatus" :
                "[Error] Assortment Items were NOT correctly Sorted by itemStatus ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by itemStatus");
        System.out.println("============================================================================================");
    }



    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will fetch all assortment items filter by pageable=false and SortBy storePickStatus
     *     will validate the status code is 200 OK, will validate is order by parameter
     */
    @Test( enabled = false)
    public void validateGetAmtItems_SortBy_storePickStatus_TC020() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAmtItems_SortBy_storePickStatus_TC020 ");
        System.out.println("[Test Scenario]  Test getAssortmentItems SortBy storePickStatus");
        List<String> UPC13s = getRandomAssortmentItemsUPC13(5);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?pageable=false&UPC13.filter=" +
                UPC13s.toString() + "&SortBy=storePickStatus");

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"false").
                queryParam(UPCS_FILTER,formatMultipleUpcs(UPC13s)).
                queryParam(SORT_BY,"storePickStatus").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == Integer.valueOf(HttpStatus.SC_OK);
        boolean listsMatches = compareApiListVsDBList(restResponse, JSON_PATH_ASSORTMENT_ITEM_PICK_STATUS,SELECT_ASSORTMENT_ITEM_SORTBY_PICK_STATUS,UPC13s,"name");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(listsMatches ? "[Correct]  Assortment Items were correctly Sorted by storePickStatus" :
                "[Error] Assortment Items were NOT correctly Sorted by storePickStatus ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(listsMatches, " [Error] Assortment Items were NOT correctly Sorted by storePickStatus");
        System.out.println("============================================================================================");
    }

    public List<String> getRandomAssortmentItemsUPC13(int sizeList){
        List<String> assortItems = new ArrayList<>();
        List<String>  listassortItems = dbUtils.DBSelectQueryObjectParams(SELECT_RANDOM_UPC13, Arrays.asList(100),UPC13);

        if(listassortItems == null || listassortItems.isEmpty())
            return assortItems;

        int i=0;
        while(i<sizeList){
            assortItems.add(listassortItems.get( testUtils.getRandomNumber(listassortItems.size())));
            i++;
        }
        return assortItems;
    }


    public String formatMultipleUpcs(List<String> listUpcs){
        StringBuilder upcs = new StringBuilder();

        if(listUpcs == null || listUpcs.isEmpty())
            return upcs.toString();

        int i=0;
        while(i<listUpcs.size()){
            upcs.append(listUpcs.get(i));
            if(i<listUpcs.size()-1)upcs.append(",");
            i++;
        }
        return upcs.toString();
    }






    public boolean compareApiListVsDBList(Response response,String jsonPath,String query, List<String> params,String column){
        if(response.jsonPath().get(jsonPath) != null) {

            List apiList =response.jsonPath().get(jsonPath);
            List<String> dbList = DBSelectQueryParams(query, params, column);

            if((apiList ==null || dbList == null) || (apiList.isEmpty() || dbList.isEmpty()) )
                return false;

            System.out.println("\nList retrieved from API: "  + apiList.toString());
            System.out.println("List retrieved from DB:  " + dbList.toString());

            return apiList.equals(dbList);
        }else{
            System.out.println(" [Error] Expected Assortment Items not found, instead getAssortmentItems api retrieved:  " + response.jsonPath().get().toString());
            return false;
        }

    }

    public List<String> DBSelectQueryParams(String query,List<String> param,String column)
    {
        List<String> list = new ArrayList<>();
        Connection assortmentDB = null;
        ResultSet rs = null;

        try {
            rs= dbUtils.DBSelectQueryParams(query, param, dbQueries.getString("dbDriver"), dbQueries.getString("urlDB"), dbQueries.getString("userNameDB"),
                    dbQueries.getString("passwordDB"));

            if(rs == null)return list;
            while ( rs.next())
                list.add(rs.getString(column)!=null?rs.getString(column):"");
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }
        return list;
    }
















}
