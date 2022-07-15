package com.kroger.assortment;

import com.kroger.assortment.api.assortmentmgmtcore.BearerToken;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItemStatus;
import com.kroger.assortment.model.assortmentmgmtcore.StorePickStatus;
import com.kroger.assortment.testUtilities.DBUtils;
import com.kroger.assortment.testUtilities.TestUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.kroger.assortment.testUtilities.Constants.*;
import static com.kroger.assortment.testUtilities.Constants.EXCEL_STATUS_CODE;
import static io.restassured.RestAssured.given;

public class InitAssortment {
    protected static final Logger log                = LoggerFactory.getLogger(InitAssortment.class);
    protected ResourceBundle dbQueries                  = ResourceBundle.getBundle(System.getProperty("db"));
    protected ResourceBundle env                  = ResourceBundle.getBundle(System.getProperty("env"));
    protected final DBUtils dbUtils = new DBUtils();
    protected final TestUtils testUtils = new TestUtils();
    protected final BearerToken token = new BearerToken();



    @BeforeSuite
    public void suiteSetup() {

        System.out.println("********************************************************************");
        System.out.println("-------------------------Test Suite Started-------------------------");
        System.out.println("********************************************************************\n\n");

    }



    @AfterSuite(alwaysRun = true)
    public void testFinish()
    {
        System.out.println("\n\n********************************************************************");
        System.out.println("--------------------------Test Suite Ended--------------------------");
        System.out.println("********************************************************************\n\n");
    }



    public Response getCatalogs(){
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_CATALOGS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                when().get(url).then().contentType(ContentType.JSON).extract().response();
        return restResponse;
    }

    public List<StorePickStatus> getCatalogStorePickStatus(){
        List<StorePickStatus> listStorePickStatus = new ArrayList<StorePickStatus>();

        Response response = getCatalogs();
        if(response.jsonPath().get(JSON_PATH_CATALOG_PICK_STATUS) != null)
        listStorePickStatus= response.jsonPath().getList(JSON_PATH_CATALOG_PICK_STATUS,StorePickStatus.class);

        return listStorePickStatus;
    }


    public List<AssortmentItemStatus> getCatalogAssortmentItemStatus(){
        List<AssortmentItemStatus> listItemStatus = new ArrayList<AssortmentItemStatus>();

        Response response = getCatalogs();
        if(response.jsonPath().get(JSON_PATH_CATALOG_ITEM_STATUS) != null)
            listItemStatus= response.jsonPath().getList(JSON_PATH_CATALOG_ITEM_STATUS,AssortmentItemStatus.class);

        return listItemStatus;

    }


    public Integer getCatalogStorePickStatus(String statusLabel){
        List<StorePickStatus> listStorePickStatus = new ArrayList<StorePickStatus>();
        int status = 0;

        Response response = getCatalogs();
        if(response.jsonPath().get(JSON_PATH_CATALOG_PICK_STATUS) != null)
            listStorePickStatus= response.jsonPath().getList(JSON_PATH_CATALOG_PICK_STATUS,StorePickStatus.class);

        for(StorePickStatus pickStatus: listStorePickStatus) {
            if (pickStatus.getName().equalsIgnoreCase(statusLabel))
                return Integer.valueOf(pickStatus.getId());
        }

        return status;
    }


    public Integer getCatalogAssortmentItemStatus(String statusLabel){
        List<AssortmentItemStatus> listItemStatus = new ArrayList<AssortmentItemStatus>();
        int status = 0;

        Response response = getCatalogs();
        if(response.jsonPath().get(JSON_PATH_CATALOG_ITEM_STATUS) != null)
            listItemStatus= response.jsonPath().getList(JSON_PATH_CATALOG_ITEM_STATUS,AssortmentItemStatus.class);

        for(AssortmentItemStatus itemStatus: listItemStatus){
            if (itemStatus.getName().equalsIgnoreCase(statusLabel))
                return Integer.valueOf(itemStatus.getId());
        }

        return status;

    }












}
