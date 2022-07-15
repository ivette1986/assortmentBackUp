package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.testUtilities.Constants;
import com.kroger.assortment.testUtilities.ConstantsDB;
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

public class GetProducts1 extends InitAssortment {

    protected static YaapitaReport report;

    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by a singular assortment(assortmentId)
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byAssortment_TC001() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byAssortment_TC001 ");
        System.out.println("[Test Scenario] Validate total of products fetch by a single Assortment");
        int param = dbUtils.getRandomIntDBRecord(SELECT_RANDOM_ASSORTMENT_ID,ID);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?assortments="+ param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENTS,param).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(assortmentItems>0 ? "[Correct] getProducts filtered by assortments: " +param+ " retrieved  " + assortmentItems + " products ":
                "[Error] getProducts filtered by assortments: " +param+ " should be higher than 0 instead it retrieved: "  + assortmentItems);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(assortmentItems>0 ,"[Error] getProducts filtered by assortments: " +param+ " should be higher than 0 instead it retrieved: "  + assortmentItems);

        System.out.println("============================================================================================");
    }



    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by multiple assortments(assortmentId)
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byMultipleAssortments_TC002() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byMultipleAssortments_TC002 ");
        System.out.println("[Test Scenario] Validate total of products fetch by multiple Assortments");
        List<Integer> params = dbUtils.getRandomIntDBRecords(3,SELECT_RANDOM_ASSORTMENTS_ID,Arrays.asList(100),ID);
        String formatParams= testUtils.formatMultipleElements(params);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?assortments="+formatParams);
        int products = 0;
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");


        for(int param:params){
            Response restResponse = given().
                    headers("Authorization", token.getBearerToken()).
                    queryParam(ASSORTMENTS,param).
                    queryParam(PAGE_SIZE,100).
                    when().get(url).then().contentType(ContentType.JSON).extract().response();

            if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null) {
                products = products + Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
                System.out.println("Assortment: "+ param + " has at least on the first page: "+
                        Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString())+" products");
            }
        }

        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(ASSORTMENTS,formatParams).
                queryParam(PAGE_SIZE,300).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        int apiProducts = restResponse.jsonPath().get(JSON_PATH_COUNT) != null? Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString()) : 0;
        boolean correctProducts = apiProducts >= products? true: false;



        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(correctProducts? "[Correct] getProducts filtered by assortments: " +formatParams+ " retrieved  " + apiProducts + " products ":
                "[Error] getProducts filtered by assortments: " +formatParams+ " should be higher than "+products+" instead it retrieved: "  + apiProducts);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(correctProducts ,"[Error] getProducts filtered by assortments: " +formatParams+ " should be higher than "+products+" instead it retrieved: "  + apiProducts);

        System.out.println("============================================================================================");
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by a singular upc(upc13)
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byUPC_TC003() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byUPC_TC003 ");
        System.out.println("[Test Scenario] Validate total of products fetch by a single UPC");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_PRODUCT_UPC13,Arrays.asList(1000),"upc13").get(0);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?upcs="+ param);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,param).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the multiple partial UPC13 in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_UPC13,param, COUNT);
        int productsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        int products = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            products = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numProducts = products == productsDB;

        //validate ids retrieved in API VS found in DB
        boolean matchProducts = false;
        List<Object> params = new ArrayList<>(); params.add(param);
        if(restResponse.jsonPath().get(JSON_PATH_ITEMS_UPC13) != null) {
            matchProducts = testUtils.compareAndPrint2Lists("Printing  upc13 products from Api:  ", restResponse.jsonPath().get(JSON_PATH_ITEMS_UPC13),
                    "Printing  upc13 products from DB:   ", dbUtils.DBSelectQueryObjectParams(SELECT_PRODUCT_UPC13_BY_UPC13, params, UPC13));
        }


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numProducts ? "[Correct] getProducts retrieved " + products + " VS DB that retrieved " + productsDB :
                "[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        System.out.println(matchProducts ? "[Correct] Product upc13 fetched from API matches the Product upc13 found in DB filter by " + param :
                "[Error] Product fetched from API NOT matches the Product API found in DB filter by  " + param);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numProducts ,"[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        Assert.assertTrue(numProducts ,"[Error] Product fetched from API NOT matches the Product API found in DB filter by  " + param);
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by a multiple upc(upc13)
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byUPCs_TC004() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byUPCs_TC004 ");
        System.out.println("[Test Scenario] Validate total of products fetch by multiple Upcs");
        List<String> params = dbUtils.getRandomDBRecords(3,SELECT_RANDOM_PRODUCT_UPC13,Arrays.asList(1000),"upc13");
        String formatParams= testUtils.formatMultipleElements(params);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?upcs="+ formatParams);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,formatParams).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the multiple partial UPC13 in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_3_UPC13,params, COUNT);
        int productsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        int products = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            products = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numProducts = products == productsDB;

        //validate ids retrieved in API VS found in DB
        boolean matchProducts = false;
        List<Object> queryParams = new ArrayList<>(); queryParams.addAll(params);
        if(restResponse.jsonPath().get(JSON_PATH_ITEMS_UPC13) != null) {
            matchProducts = testUtils.compareAndPrint2Lists("Printing  upc13 products from Api:  ", restResponse.jsonPath().get(JSON_PATH_ITEMS_UPC13),
                    "Printing  upc13 products from DB:   ", dbUtils.DBSelectQueryObjectParams(SELECT_PRODUCT_UPC13_BY_3_UPC13, queryParams, UPC13));
        }


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numProducts ? "[Correct] getProducts retrieved " + products + " VS DB that retrieved " + productsDB :
                "[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        System.out.println(matchProducts ? "[Correct] Product upc13 fetched from API matches the Product upc13 found in DB filter by " + params.toString() :
                "[Error] Product fetched from API NOT matches the Product API found in DB filter by  " + params.toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numProducts ,"[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        Assert.assertTrue(matchProducts ,"[Error] Product fetched from API NOT matches the Product API found in DB filter by  " + params.toString());
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by multiplbe partial upc(upc13)
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byPartialUPC13s_TC005() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byPartialUPC13s_TC005 ");
        System.out.println("[Test Scenario] Validate total of products fetch by multiple partial UPC13s");
        List<String> params = getPartialRandomProductsUPC13s(3);
        String formatParams = testUtils.formatMultipleElements(params) ;
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?upcs="+ formatParams);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        for(String param:params){
            Response restResponse = given().
                    headers("Authorization", token.getBearerToken()).
                    queryParam(UPCS,param).
                    when().get(url).then().contentType(ContentType.JSON).extract().response();

            if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null) {
                products = products + Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
                System.out.println("Products: "+ param + " has at least on the first page: "+
                        Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString())+" products");
            }
        }

        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,formatParams).
                queryParam(PAGE_SIZE,60).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        int apiProducts = restResponse.jsonPath().get(JSON_PATH_COUNT) != null? Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString()) : 0;


        //due to big amount of data, pagination must be used in request, so if DB contains more than 60 records, validate request retrieve at least 200 records
        boolean numProducts = false;
        if(products>200)
            numProducts = apiProducts == 60? true: false;
        else
            numProducts = apiProducts == products;


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numProducts? "[Correct] getProducts filtered by upcs: (" +formatParams+ ") retrieved  " + apiProducts + " products ":
                "[Error] getProducts filtered by assortments: " +formatParams+ " should be higher than "+products+" instead it retrieved: "  + apiProducts);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numProducts ,"[Error] getProducts filtered by upcs: (" +formatParams+ ") should be higher than "+products+" instead it retrieved: "  + apiProducts);
        System.out.println("============================================================================================");
    }




    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by single gtin(upc14)
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byGTIN_TC006() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byGTIN_TC006 ");
        System.out.println("[Test Scenario] Validate GetProducts filtered by GTIN");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_PRODUCT_GTIN,Arrays.asList(2000), ConstantsDB.GTIN).get(0);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?gtin="+ param);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_GTIN,param).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the multiple partial UPC13 in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_GTIN,param, COUNT);
        int productsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            products = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numProducts = products == productsDB;

        //validate ids retrieved in API VS found in DB
        boolean matchProducts = false;
        List<Object> params = new ArrayList<>(); params.add(param);
        if(restResponse.jsonPath().get(JSON_PATH_ITEMS_GTIN) != null) {
            matchProducts = testUtils.compareAndPrint2Lists("Printing  gtin products from Api:  ", restResponse.jsonPath().get(JSON_PATH_ITEMS_GTIN),
                    "Printing  gtin products from DB:   ", dbUtils.DBSelectQueryObjectParams(SELECT_PRODUCT_GTIN_BY_GTIN, params, GTIN));
        }


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numProducts ? "[Correct] getProducts retrieved " + products + " VS DB that retrieved " + productsDB :
                "[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        System.out.println(matchProducts ? "[Correct] Product gtin fetched from API matches the Product gtin found in DB filter by " + param :
                "[Error] Product fetched from API NOT matches the Product API found in DB filter by  " + param);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numProducts ,"[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        Assert.assertTrue(matchProducts ,"[Error] Product fetched from API NOT matches the Product API found in DB filter by  " + param);
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by single skuId
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_bySkuId_TC007() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_bySkuId_TC007 ");
        System.out.println("[Test Scenario] Validate GetProducts filtered by skuId");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_SKUID_PRODUCT_BY_SKUID,Arrays.asList(1000), SKUID).get(0);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?skuId="+ param);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_SKUID,param).
                queryParam(PAGE_SIZE,100).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the multiple partial UPC13 in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_SKUID,param, COUNT);
        int productsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            products = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numProducts = products == productsDB;

        //validate ids retrieved in API VS found in DB
        boolean matchProducts = false;
        List<Object> params = new ArrayList<>(); params.add(param);
        if(restResponse.jsonPath().get(JSON_PATH_ITEMS_SKUID) != null) {
            matchProducts = testUtils.compareAndPrint2Lists("Printing  skuId products from Api:  ", restResponse.jsonPath().get(JSON_PATH_ITEMS_SKUID),
                    "Printing  skuId products from DB:   ", dbUtils.DBSelectQueryObjectParams(SELECT_PRODUCT_SKUID_BY_SKUID, params, SKUID));
        }


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numProducts ? "[Correct] getProducts retrieved " + products + " VS DB that retrieved " + productsDB :
                "[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        System.out.println(matchProducts ? "[Correct] Product skuId fetched from API matches the Product skuId found in DB filter by " + param :
                "[Error] Product fetched from API NOT matches the Product API found in DB filter by  skuId " + param);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numProducts ,"[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        Assert.assertTrue(matchProducts ,"[Error] Product fetched from API NOT matches the Product API found in DB filter by skuId " + param);
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by single productId
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byProductId_TC008() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byProductId_TC008 ");
        System.out.println("[Test Scenario] Validate GetProducts filtered by productId");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_PRODUCTID_PRODUCT,Arrays.asList(1000), PRODUCT_ID).get(0);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?productId="+ param);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_PRODUCT_ID,param).
                queryParam(PAGE_SIZE,100).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the multiple partial UPC13 in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_PRODUCTID,param, COUNT);
        int productsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            products = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numProducts = products == productsDB;

        //validate ids retrieved in API VS found in DB
        boolean matchProducts = false;
        List<Object> params = new ArrayList<>(); params.add(param);
        if(restResponse.jsonPath().get(JSON_PATH_ITEMS_PRODUCT_ID) != null) {
            matchProducts = testUtils.compareAndPrint2Lists("Printing  skuId products from Api:  ", restResponse.jsonPath().get(JSON_PATH_ITEMS_PRODUCT_ID),
                    "Printing  skuId products from DB:   ", dbUtils.DBSelectQueryObjectParams(SELECT_PRODUCT_PRODUCTID_BY_PRODUCTID, params, PRODUCT_ID));
        }


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numProducts ? "[Correct] getProducts retrieved " + products + " VS DB that retrieved " + productsDB :
                "[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        System.out.println(matchProducts ? "[Correct] Product productId fetched from API matches the Product productId found in DB filter by " + param :
                "[Error] Product fetched from API NOT matches the Product API found in DB filter by  skuId " + param);

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numProducts ,"[Error] getProducts retrieved " + products + " VS DB that retrieved " + productsDB);
        Assert.assertTrue(matchProducts ,"[Error] Product fetched from API NOT matches the Product API found in DB filter by productId " + param);
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by single primaryDeptCode
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byPrimaryDeptCode_TC009() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byPrimaryDeptCode_TC009 ");
        System.out.println("[Test Scenario] Validate GetProducts filtered by primaryDeptCode");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_PRODUCT_BY_PRIMARY_DEPT_CODE,Arrays.asList(1000), PRIMARY_DEPT_CODE).get(0);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?primaryDeptCode="+ param);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_PRIMARY_DEPT_CODE,param).
                queryParam(PAGE_SIZE,200).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the single Product primary dept code in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_PRIMARY_DEPT_CODE,param, COUNT);
        int productsDB = ! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            products = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());

        //due to big amount of data, pagination must be used in request, so if DB contains more than 200 records, validate request retrieve at least 200 records
        boolean numProducts = false;
        if(productsDB>200)
            numProducts = products == 200? true: false;
        else
            numProducts = products == productsDB;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(numProducts ? "[Correct] getProducts with page.size=200 retrieved " + products + " while DB  retrieved " + productsDB :
                "[Error] getProducts should have at least 200 records or "+productsDB+ " instead it retrieved " + products + " products " );


        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numProducts ,"[Error] getProducts should have at least 200 records or "+productsDB+ " instead it retrieved " + products + " products " );
        System.out.println("============================================================================================");
    }



    public List<String> getPartialRandomProductsUPC13s(int num){
        List<String> partialUPCS = new ArrayList<>();

        List<String> upcs = dbUtils.getRandomDBRecords(num,SELECT_RANDOM_PRODUCT_UPC13,Arrays.asList(2000),UPC13);
        Iterator<String> itr = upcs.iterator();
        while(itr.hasNext())
            partialUPCS.add(itr.next().substring(6,13));

        return partialUPCS;
    }














}
