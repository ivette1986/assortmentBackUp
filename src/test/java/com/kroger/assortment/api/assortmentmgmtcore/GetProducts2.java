package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
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

public class GetProducts2 extends InitAssortment {

    protected static YaapitaReport report;

    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }


    @DataProvider(name = "productsFilterByInvalidInputData" )
    public Object[] getProductsFilterByInvalidInputData(){
        return getEnabledExcelTests( env.getString("excel_data_path_products") +
                env.getString("getProducts"), INPUT_SHEET_NAME_INVALID_INPUT_DATA );
    }


    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by single recapDeptCode
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byRecapDeptCode_TC0010() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byRecapDeptCode_TC0010 ");
        System.out.println("[Test Scenario] Validate GetProducts filtered by recapDeptCode");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_PRODUCT_BY_RECAP_DEPT_CODE,Arrays.asList(1000), RECAP_DEPT_CODE).get(0);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?recapDeptCode="+ param);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_RECAP_DEPT_CODE,param).
                queryParam(PAGE_SIZE,200).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the single Product primary dept code in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_RECAP_DEPT_CODE,param, COUNT);
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



    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by single deptCode
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byDeptCode_TC0011() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byDeptCode_TC0011 ");
        System.out.println("[Test Scenario] Validate GetProducts filtered by deptCode");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_PRODUCT_BY_DEPT_CODE,Arrays.asList(1000), DEPT_CODE).get(0);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"?deptCode="+ param);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_DEPT_CODE,param).
                queryParam(PAGE_SIZE,200).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the single Product primary dept code in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_DEPT_CODE,param, COUNT);
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



    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by single commodityCode
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byCommodityCode_TC0012() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byCommodityCode_TC0012 ");
        System.out.println("[Test Scenario] Validate GetProducts filtered by commodityCode");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_PRODUCT_BY_COMMODITY_CODE,Arrays.asList(1000), COMMODITY_CODE).get(0);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"? commodityCode="+ param);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_COMMODITY_CODE,param).
                queryParam(PAGE_SIZE,200).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the single Product primary dept code in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_COMMODITY_CODE,param, COUNT);
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




    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by single subCommodityCode
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_bySubCommodityCode_TC0013() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_bySubCommodityCode_TC0013 ");
        System.out.println("[Test Scenario] Validate GetProducts filtered by subCommodityCode");
        String param = dbUtils.getRandomDBRecords(1,SELECT_RANDOM_PRODUCT_BY_SUB_COMMODITY_CODE,Arrays.asList(1000), SUB_COMMODITY_CODE).get(0);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"? subCommodityCode="+ param);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_SUB_COMMODITY_CODE,param).
                queryParam(PAGE_SIZE,200).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;

        //count how many assortment items are given the single Product primary dept code in DB
        List<String> dblist = dbUtils.DBSelectQueryParams(SELECT_COUNT_PRODUCTS_BY_SUB_COMMODITY_CODE,param, COUNT);
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




    /**
     * This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/product
     * will fetch all products filter by single pidDescription
     * will validate the status code is 200 OK, will count the products by API retrieved VS DB
     */
    @Test
    public void validateGetProducts_byPidDescription_TC0014() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetProducts_byPidDescription_TC0014 ");
        System.out.println("[Test Scenario] Validate GetProducts filtered by pidDescription");
        String param = "banana";
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ env.getString("GET_PRODUCTS") +"? pidDescription="+ param);
        int products = 0;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_PRODUCTS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PID_DESCRIPTION,param).
                queryParam(PAGEABLE,"false").
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        List<Integer> dblist = dbUtils.returnIntegerDBSelectQueryObjectParams
                (SELECT_COUNT_PRODUCT_BY_PID_DESCRIPTION, Arrays.asList("%"+param+"%","%"+param+"%")  , COUNT);
        int assortmentItemsDB = ! dblist.isEmpty() ? dblist.get(0) :0;
        int assortmentItems = 0;
        if(restResponse.jsonPath().get(JSON_PATH_COUNT) != null)
            assortmentItems = Integer.valueOf(restResponse.jsonPath().get(JSON_PATH_COUNT).toString());
        boolean numAssortmentItems = assortmentItems == assortmentItemsDB;

        int expectedCode = assortmentItems == 0 && assortmentItemsDB == 0 ? HttpStatus.SC_NOT_FOUND: HttpStatus.SC_OK;
        boolean statusCode = restResponse.getStatusCode() == expectedCode;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode() :
                "\n[Error] Expected code: " + expectedCode + " VS " + " Obteined Status Code: " + restResponse.getStatusCode());
        System.out.println(numAssortmentItems ? "[Correct] getProducts Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB :
                "[Error] getProducts Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);

        Assert.assertTrue(statusCode, "\n [Error] Expected code: " + expectedCode+ " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(numAssortmentItems, "[Error] getProducts Items retrieved " + assortmentItems + " VS DB that retrieved " + assortmentItemsDB);


        System.out.println("============================================================================================");
    }



























}
