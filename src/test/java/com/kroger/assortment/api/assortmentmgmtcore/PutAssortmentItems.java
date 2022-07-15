package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.model.assortmentmgmtcore.*;
import com.org.yaapita.YaapitaReport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kroger.assortment.testUtilities.Constants.*;
import static com.kroger.assortment.testUtilities.ConstantsDB.*;
import static com.org.yaapita.libloadinputdata.LoadExcelData.getEnabledExcelTests;
import static io.restassured.RestAssured.given;

public class PutAssortmentItems extends InitAssortment {

    protected final Map<String,Integer> catalogStorePickStatus= new HashMap<>();
    protected final Map<String,Integer> catalogAssortmentItemStatus= new HashMap<>();
    protected static YaapitaReport report;


    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();

        List<AssortmentItemStatus> listItemStatus = getCatalogAssortmentItemStatus();
        for(AssortmentItemStatus itemStatus: listItemStatus)
            catalogAssortmentItemStatus.put(itemStatus.getName(),Integer.valueOf(itemStatus.getId()));

        List<StorePickStatus> listPickStatus = getCatalogStorePickStatus();
        for(StorePickStatus pickStatus: listPickStatus)
            catalogStorePickStatus.put(pickStatus.getName(),Integer.valueOf(pickStatus.getId()));

    }


    @DataProvider(name = "assortmentItemsInvalidUpdate" )
    public Object[] putAssortmentItemsInvalidUpdate(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("putAssortmentItems"), INPUT_SHEET_NAME_NEGATIVE );
    }

    @DataProvider(name = "assortmentItemsStatus2" )
    public Object[] putAssortmentItemStatus2(){
        return getEnabledExcelTests( env.getString("excel_data_path") +
                env.getString("putAssortmentItems"), INPUT_SHEET_ASSORTMENT_ITEM_STATUS2 );
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will update multiple assortment items with all editable fields: notes1, itemStatus, caseUPC, supplierSiteCode, supplierSiteName, source,
     *     replacementSource, storePickStatus and storePickDate
     *     will validate the status code is 200 OK, and all values should be updated
     */
    @Test
    public void validatePUTAssortmentItems_TC001() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePUTAssortmentItems_TC001 ");
        System.out.println("[Test Scenario] Validate multiple existing Assortment Items are updated with all editable fields");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");

        List<String> assortmentItemIds = dbUtils.getRandomDBRecords(3,SELECT_ASSORTMENTS_ITEMS_IDS_BY_ITEMSTATUS,Arrays.asList(1,100),"id");
        List<AssortmentItem> assortItems = getAssortmentItemsByID(assortmentItemIds);
        AssortmentItem item = assortItems.get(0);
        List<AssortmentItem> listAssortItems = new ArrayList<AssortmentItem>();

        item.setVenusNotes("Automation testing VenusNotes " + testUtils.getRandomNumber(5000));
        item.setNotes1("Automation testing notes " + testUtils.getRandomNumber(5000));
        item.setNotes2("Automation testing notes 2 " + testUtils.getRandomNumber(5000));
        item.setInStoreUpc("1234567890123");
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_7_PERMANENT_STORE_PICK));
        item.setItemStatus(ASSORT_ITEM_STATUS_7_PERMANENT_STORE_PICK);
        item.setStorePickStatusValue(catalogStorePickStatus.get(STORE_PICK_STATUS_3_PERMANENT));
        item.setStorePickStatus(STORE_PICK_STATUS_3_PERMANENT);

        String body = buildPutAssortmentItemBody( item, assortItems);
       String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().put().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = HttpStatus.SC_OK == restResponse.getStatusCode();
        boolean responseItemUpdated = restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT) !=null &&
                (Integer)restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT)== 3 ? true : false;
        item.setStorePickDate(null);

        if(statusCode)
        listAssortItems = getAssortmentItemsByID(assortmentItemIds);
        boolean itemsUpdated = validateListAssortItemsUpdated( item, listAssortItems);


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(itemsUpdated && responseItemUpdated? "[Correct]  Assortment Items were correctly updated, resume: " + restResponse.jsonPath().get().toString():
                "[Error] Assortment Items NOT updated, resume: " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(itemsUpdated && responseItemUpdated, "[Error] Assortment Items NOT updated, resume: " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }



    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     according to the next rules update shouldn't be done at all:
     *     1) item status = 4 (active) + store pick status = 2 (yes temp) => [DO NOT UPDATE]
     *     2) item status = 4 (active) + store pick status = 3 (permanent) => [DO NOT UPDATE]
     *     3) item status != 7 (store pick permanent) + store pick status = 3 (permanent) => [DO NOT UPDATE]
     *     4) item status = NULL or invalid + store pick status = 3 (permanent) => [DO NOT UPDATE]
     *     5) item status = 1 + store pick status =  invalid => [DO NOT UPDATE] ,if store pick status = NULL => UPDATE
     *     will validate the status code is 200 OK, and all values should be updated
     */
    @Test(dataProvider = "assortmentItemsInvalidUpdate" )
    public void validatePUTAssortmentItems_InvalidUpdate_TC002(Map<String, String> dataMap) {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePUTAssortmentItems_InvalidUpdate_TC002 ");
        Integer itemStatus = dataMap.get("itemStatus").toString().isEmpty()? null : Integer.valueOf(dataMap.get("itemStatus")) ;
        Integer storePickStatus = dataMap.get("storePickStatus").toString().isEmpty()  ? null :Integer.valueOf(dataMap.get("storePickStatus")) ;
        System.out.println("[Test Scenario] " + dataMap.get(EXCEL_TC_NUMBER) +" " + dataMap.get(EXCEL_TEST_CASE) + "  params: item status " + itemStatus + "  StorePickStatus " + storePickStatus);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");
        boolean updatedItemsCount = true;

        List<String> assortmentItemIds = dbUtils.getRandomDBRecords(3,SELECT_ASSORTMENTS_ITEMS_IDS_BY_ITEMSTATUS,Arrays.asList(1,100),"id");
        List<AssortmentItem> assortItems = getAssortmentItemsByID(assortmentItemIds);
        AssortmentItem item = assortItems.get(0);
        List<AssortmentItem> listAssortItems = new ArrayList<AssortmentItem>();

        //set values according to data driver scenarios
        item.setVenusNotes("Automation testing VenusNotes " + testUtils.getRandomNumber(5000));
        item.setNotes1("Automation testing notes " + testUtils.getRandomNumber(5000));
        item.setNotes2("Automation testing notes 2 " + testUtils.getRandomNumber(5000));
        item.setInStoreUpc("1234567890123");
        item.setItemStatusValue(itemStatus );
        item.setStorePickStatusValue(storePickStatus);

        String body = buildPutAssortmentItemBody( item, assortItems);
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().put().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = HttpStatus.SC_OK == restResponse.getStatusCode();
        //Validate that no updated assortment items id are displayed on the response  for NOT updated items
        if(restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT) !=null && (Integer)restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT)==0
        && !restResponse.jsonPath().get(JSON_PATH_NO_UPDATED_ITEMS).toString().equalsIgnoreCase("null")) {
            updatedItemsCount = false;
            String json= restResponse.jsonPath().get(JSON_PATH_NO_UPDATED_ITEMS).toString();
            for(String itemId : assortmentItemIds){
                if(!json.contains(itemId)){updatedItemsCount = true; System.out.println("[Error] item id " + itemId + "should be in the not updated response: "+ json );}
            }

        }else if(restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT) !=null && (Integer)restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT)>0)
            updatedItemsCount = true;

        if(statusCode)
            listAssortItems =  getAssortmentItemsByID(assortmentItemIds);
        boolean itemsUpdated = validateListAssortItemsUpdated( item, listAssortItems);

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(!itemsUpdated ? "[Correct]  Assortment Items were NOT updated at all": "[Error] Assortment Items  should not be updated,but they were updated " );
        System.out.println( !updatedItemsCount ? "[Correct]  Number of Assortment Items  updated == 0, Api Response: " + restResponse.jsonPath().get().toString():
                "[Error]  Number of Assortment Items  updated should be 0, but instead some Items were updated, Api Response " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(!itemsUpdated, "[Error] Assortment Items  should not be updated,but they were, resume: " +restResponse.jsonPath().get().toString());
        Assert.assertTrue(!updatedItemsCount, "[Error] Number of Assortment Items  updated should be 0, but instead some Items were updated, Api Response: " +restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *      according to the rule update shouldn't be done at all:
     *     1) item status != 4 (active) + store pick status = 2 (yes temp) + store pick date = NULL or invalid => [DO NOT UPDATE]
     */
    @Test
    public void validatePUTAssortmentItems_InvalidUpdate_TC003() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePUTAssortmentItems_InvalidUpdate_TC003 ");
        System.out.println("[Test Scenario] item status != 4 (active) + store pick status = 2 (yes temp) + store pick date = NULL or invalid => [DO NOT UPDATE]");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");

        int numItems = 3;
        List<String> assortmentItemIds = dbUtils.getRandomDBRecords(numItems,SELECT_ASSORTMENTS_ITEMS_IDS_BY_ITEMSTATUS,Arrays.asList(1,100),"id");
        List<AssortmentItem> assortItems = getAssortmentItemsByID(assortmentItemIds);
        List<AssortmentItem> listAssortItems = new ArrayList<>();
        AssortmentItem assortItem =assortItems.get(0);

        assortItem.setVenusNotes("Automation testing VenusNotes " + testUtils.getRandomNumber(5000));
        assortItem.setNotes1("Automation testing notes " + testUtils.getRandomNumber(5000));
        assortItem.setNotes2("Automation testing notes 2 " + testUtils.getRandomNumber(5000));
        assortItem.setInStoreUpc("1234567890123");
        assortItem.setStorePickStatusValue(2);
        assortItem.setStorePickDate(null);

        String body = buildPutAssortmentItemBody( assortItem, assortItems);
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().put().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = HttpStatus.SC_OK == restResponse.getStatusCode();
        boolean responseItemUpdated = restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT) !=null &&
                (Integer)restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT)== 0 ? false : true;

        if(statusCode)
            listAssortItems = getAssortmentItemsByID(assortmentItemIds);
        boolean itemsUpdated = validateListAssortItemsUpdated( assortItem, listAssortItems);


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(!itemsUpdated && !responseItemUpdated? "[Correct]  Assortment Items NOT updated, resume: " + restResponse.jsonPath().get().toString():
                "[Error] Assortment Items were updated and should NOT be, resume: " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(!itemsUpdated && !responseItemUpdated, "[Error] Assortment Items were updated and should NOT be, resume: " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *    item status in the request != 4 (active) + store pick status = 2 (yes temp) AND current item status in DB = 4 (active) => [SET item status = 2 (Setup In Progress)]
     *     will validate the status code is 200 OK, and all values should be updated and item status = 2 (Setup In Progress)], date can't be null and  if blank values is ok not to update
     */
    @Test(dataProvider = "assortmentItemsStatus2")
    public void validatePUTAssortmentItems_Status2_TC004(Map<String, String> dataMap) {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePUTAssortmentItems_Status2_TC004 ");
        Integer itemStatus = dataMap.get("itemStatus").toString().isEmpty()? null : Integer.valueOf(dataMap.get("itemStatus")) ;
        Integer storePickStatus = dataMap.get("storePickStatus").toString().isEmpty()  ? null :Integer.valueOf(dataMap.get("storePickStatus")) ;
        System.out.println("[Test Scenario] " + dataMap.get(EXCEL_TC_NUMBER) +" "+ dataMap.get(EXCEL_TEST_CASE) + "  params: item status " + itemStatus + "  StorePickStatus " + storePickStatus);
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");

        System.out.println("Creating assortment items with item status 4 Active that is required as precondition, items must have Item status !=2 and store pick status = 1");
        int numItems = 3;
        List<String> assortmentItemIds = dbUtils.getRandomDBRecords(numItems,SELECT_ASSORT_ITEMS_IDS_BY_DIFF_ITEMSTATUS_AND_STORE_PICK,
                Arrays.asList(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_2_SETUP_IN_PROGRESS),catalogStorePickStatus.get(STORE_PICK_STATUS_1_NO),100),"id");
        AssortmentItem item =getAssortmentItemsByID(Arrays.asList(assortmentItemIds.get(0))).get(0);
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_4_ACTIVE));
        updateAssortmentItem(item,assortmentItemIds);

        List<AssortmentItem> assortItems = getAssortmentItemsByID(assortmentItemIds);
        List<AssortmentItem> listAssortItems = new ArrayList<>();
        AssortmentItem assortItem =assortItems.get(0);
        assortItem.setVenusNotes("Automation testing VenusNotes " + testUtils.getRandomNumber(5000));
        assortItem.setNotes1("Automation testing notes " + testUtils.getRandomNumber(5000));
        assortItem.setNotes2("Automation testing notes 2 " + testUtils.getRandomNumber(5000));
        assortItem.setInStoreUpc("1234567890123");
        assortItem.setItemStatusValue(itemStatus);
        assortItem.setStorePickStatusValue(storePickStatus);
        assortItem.setStorePickStatus(STORE_PICK_STATUS_2_YES);
        assortItem.setStorePickDate("0005-01-01T00:00:00Z");

        System.out.println("Updating assortment items so item status is set 2");
        String body = buildPutAssortmentItemBody( assortItem, assortItems);
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().put().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = HttpStatus.SC_OK == restResponse.getStatusCode();
        boolean updatedItemsCount = restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT) !=null &&
                (Integer)restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT)== numItems ? true : false;

        if(statusCode)
            listAssortItems = getAssortmentItemsByID(assortmentItemIds);
        //once the update is done the itemStatus should be set 2, set item status=2 into item used to compare update is successfully done
        assortItem.setItemStatus(ASSORT_ITEM_STATUS_2_SETUP_IN_PROGRESS);
        boolean itemsUpdated = validateListAssortItemsUpdated( assortItem, listAssortItems);


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(itemsUpdated && updatedItemsCount ? "[Correct]  Assortment Items were successfully updated item Status  to 2, resume: " + restResponse.jsonPath().get().toString():
                "[Error] Assortment Items were NOT updated item Status  to 2, resume: " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(itemsUpdated && updatedItemsCount, "[Error] Assortment Items were NOT updated item Status  to 2, resume: " +restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }

    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *    the rule: store pick status in the request = 1 (No) => [SET store pick date to NULL]
     *    method will update some assortment items to item status = 2, to make sure store pick date is not Null
     *    then will update store pick status = 1
     *     will validate the status code is 200 OK, and store pick date is null
     */
    @Test
    public void validatePUTAssortmentItems_StorePickDateNull_TC005() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePUTAssortmentItems_StorePickDateNull_TC005 ");
        System.out.println("[Test Scenario] When store pick status in the request = 1 (No) => [SET store pick date to NULL]");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");

        System.out.println("Updating assortment items to with item status 4 Active so whe can update later item status to 2 and store pick date is never null ");
        int numItems = 3;
        List<String> assortmentItemIds = dbUtils.getRandomDBRecords(numItems,SELECT_ASSORT_ITEMS_IDS_BY_DIFF_ITEMSTATUS_AND_STORE_PICK,
                Arrays.asList(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_2_SETUP_IN_PROGRESS),catalogStorePickStatus.get(STORE_PICK_STATUS_1_NO),100),"id");
        AssortmentItem item =getAssortmentItemsByID(Arrays.asList(assortmentItemIds.get(0))).get(0);
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_4_ACTIVE));
        item.setStorePickStatusValue(catalogStorePickStatus.get(item.getStorePickStatus()));
        updateAssortmentItem(item,assortmentItemIds);

        System.out.println("Updating  records with item status 4 Active to 2 Set in progress so all records has a store pick date not null");
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_1_WORK_IN_PROGRESS));
        item.setStorePickStatusValue(catalogStorePickStatus.get(STORE_PICK_STATUS_2_YES));
        item.setStorePickDate("0005-01-01T00:00:00Z");
        updateAssortmentItem(item,assortmentItemIds);

        List<AssortmentItem> assortItems = getAssortmentItemsByID(assortmentItemIds);
        List<AssortmentItem> listAssortItems = new ArrayList<>();
        item =assortItems.get(0);
        item.setVenusNotes("Automation testing VenusNotes " + testUtils.getRandomNumber(5000));
        item.setNotes1("Automation testing notes " + testUtils.getRandomNumber(5000));
        item.setNotes2("Automation testing notes 2 " + testUtils.getRandomNumber(5000));
        item.setInStoreUpc("1234567890123");
        item.setStorePickStatusValue(catalogStorePickStatus.get(STORE_PICK_STATUS_1_NO)); // SET STORE PICK STATUS = 1 AS rule requires
        item.setStorePickStatus(STORE_PICK_STATUS_1_NO);
        item.setItemStatusValue(catalogAssortmentItemStatus.get(item.getItemStatus()));

        System.out.println("Updating  with sotore pick status 1 and store pick date null");
        String body = buildPutAssortmentItemBody( item, assortItems);
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().put().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = HttpStatus.SC_OK == restResponse.getStatusCode();
        boolean updatedItemsCount = restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT) !=null &&
                (Integer)restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT)== numItems ? true : false;

        if(statusCode)
            listAssortItems = getAssortmentItemsByID(assortmentItemIds);
        //once the update is done the store pick status should be set 1 and store pick date should be null
        item.setStorePickDate(null);
        boolean itemsUpdated = validateListAssortItemsUpdated( item, listAssortItems);


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(itemsUpdated && updatedItemsCount ? "[Correct]  Assortment Items were successfully updated: store pick status= 1 and store pick date is null, resume: " + restResponse.jsonPath().get().toString():
                "[Error] Assortment Items were NOT updated as expected store pick status= 1 and store pick date is null, resume: " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(itemsUpdated && updatedItemsCount, "[Error] Assortment Items were NOT updated as expected store pick status= 1 and store pick date is null, resume: " +restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");

    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *    item status in the request = 7 (store pick permanent) + store pick status in the request = 3 (Permanent) => [SET store pick date to NULL]
     *    method will update some assortment items to item status = 2, to make sure store pick date is not Null
     *    then will update item status = 7 and store pick status =3
     *     will validate the status code is 200 OK, and store pick date is null
     */
     @Test
    public void validatePUTAssortmentItems_StorePickDateNull_TC006() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePUTAssortmentItems_StorePickDateNull_TC006 ");
        System.out.println("[Test Scenario] When item status in the request = 7 (store pick permanent) + store pick status in the request = 3 (Permanent) => [SET store pick date to NULL]");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");

        System.out.println("Updating assortment items to with item status 4 Active so whe can update later item status to 2 and store pick date is never null ");
        int numItems = 3;
         List<String> assortmentItemIds = dbUtils.getRandomDBRecords(numItems,SELECT_ASSORT_ITEMS_IDS_BY_DIFF_ITEMSTATUS_AND_STORE_PICK,
                 Arrays.asList(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_2_SETUP_IN_PROGRESS),catalogStorePickStatus.get(STORE_PICK_STATUS_1_NO),100),"id");
        AssortmentItem item =getAssortmentItemsByID(Arrays.asList(assortmentItemIds.get(0))).get(0);
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_4_ACTIVE));
        item.setStorePickStatusValue(catalogStorePickStatus.get(item.getStorePickStatus()));
        updateAssortmentItem(item,assortmentItemIds);

        System.out.println("Updating  records with item status 4 Active to 2 Set in progress so all records has a store pick date not null");
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_1_WORK_IN_PROGRESS));
        item.setStorePickStatusValue(catalogStorePickStatus.get(STORE_PICK_STATUS_2_YES));
        item.setStorePickDate("0005-01-01T00:00:00Z");
        updateAssortmentItem(item,assortmentItemIds);

        List<AssortmentItem> assortItems = getAssortmentItemsByID(assortmentItemIds);
        List<AssortmentItem> listAssortItems = new ArrayList<>();
        item =assortItems.get(0);
        item.setVenusNotes("Automation testing VenusNotes " + testUtils.getRandomNumber(5000));
        item.setNotes1("Automation testing notes " + testUtils.getRandomNumber(5000));
        item.setNotes2("Automation testing notes 2 " + testUtils.getRandomNumber(5000));
        item.setInStoreUpc("1234567890123");
        item.setStorePickStatusValue(catalogStorePickStatus.get(STORE_PICK_STATUS_3_PERMANENT)); // SET STORE PICK STATUS = 3 AS rule requires
        item.setStorePickStatus(STORE_PICK_STATUS_3_PERMANENT);
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_7_PERMANENT_STORE_PICK));// SET ITEM STATUS = 7 AS rule requires
        item.setItemStatus(ASSORT_ITEM_STATUS_7_PERMANENT_STORE_PICK);

        System.out.println("Updating  with item status = 7 store pick status 3 and store pick date null");
        String body = buildPutAssortmentItemBody( item, assortItems);
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().put().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = HttpStatus.SC_OK == restResponse.getStatusCode();
        boolean updatedItemsCount = restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT) !=null &&
                (Integer)restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT)== numItems ? true : false;

        if(statusCode)
            listAssortItems = getAssortmentItemsByID(assortmentItemIds);
        //once the update is done the store pick status should be set 1 and store pick date should be null
        item.setStorePickDate(null);
        boolean itemsUpdated = validateListAssortItemsUpdated( item, listAssortItems);


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(itemsUpdated && updatedItemsCount ? "[Correct]  Assortment Items were successfully updated: store pick status= 3 and store pick date is null, resume: " + restResponse.jsonPath().get().toString():
                "[Error] Assortment Items were NOT updated as expected store pick status= 1 and store pick date is null, resume: " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(itemsUpdated && updatedItemsCount, "[Error] Assortment Items were NOT updated as expected store pick status= 3 and store pick date is null, resume: " +restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");

    }



    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items PUT
     *  This method is focused on validate after an Assortment Item is updated through the PUT Assortment Item endpoint,
     *  all changes are tracked on the changes table, this means each Assortment Item attribute modified through PUT Assortment Item is recorded in this table
     *    For this scenario assortment items will:
     *   1) update assortment item => item status =4, so later can be updated to 2
     *   2) update  assortment items => item status = 2, to make sure store pick date is not Null
     *   3) update  assortment items => item status = 7 (store pick permanent) + store pick status in the request = 3 (Permanent) => [SET store pick date to NULL]
     *     will validate the status code is 200 OK, validate all previous changes were recorded in Changes table from Assortment Manager DB
     */
    @Test
    public void validatePUTAssortmentItems_Changes_TC007() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePUTAssortmentItems_Changes_TC007 ");
        System.out.println("[Test Scenario] Assortment Item is set item status = 2, then is set item status = 7 and store pick status = 3 " +
                "and store pick date = null, all this changes must be recorded on changes table");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");
        boolean changes = false;
        StringBuilder message = new StringBuilder();

        System.out.println("Updating AssortmentItem with item status = 4 Active so whe can update later item status to 2 and store pick date is never null ");
        int numItems = 1;
        List<String> assortmentItemIds = dbUtils.getRandomDBRecords(numItems,SELECT_ASSORT_ITEMS_IDS_BY_DIFF_ITEMSTATUS_AND_STORE_PICK,
                Arrays.asList(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_2_SETUP_IN_PROGRESS),catalogStorePickStatus.get(STORE_PICK_STATUS_1_NO),500),"id");
        AssortmentItem item =setOldValues(getAssortmentItemsByID(Arrays.asList(assortmentItemIds.get(0))).get(0));
        item.setVenusNotes("First Venus Notes");
        item.setNotes1("First Notes 1");
        item.setNotes2("First Notes 2");
        item.setInStoreUpc("1111111111111");
        item.setItemStatus(ASSORT_ITEM_STATUS_4_ACTIVE);
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_4_ACTIVE));
        item.setStorePickStatusValue(catalogStorePickStatus.get(item.getStorePickStatus()));
        updateAssortmentItem(item,assortmentItemIds);
        changes = validateChanges(item); //validate all item attributes were old values is different than new values were inserted in changes table
        if(!changes) message.append("\nChanges were NOT correctly tracked when Updating AssortmentItem with item status = 4 Active");


        System.out.println("Updating AssortmentItem with item status = 2 Set in progress so all records has a store pick date not null");
        item =setOldValues(getAssortmentItemsByID(Arrays.asList(assortmentItemIds.get(0))).get(0));
        item.setVenusNotes("Second Venus Notes");
        item.setNotes1("Second Notes 1");
        item.setNotes2("Second Notes 2");
        item.setInStoreUpc("2222222222222");
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_1_WORK_IN_PROGRESS));
        item.setItemStatus(ASSORT_ITEM_STATUS_1_WORK_IN_PROGRESS);
        item.setStorePickStatusValue(catalogStorePickStatus.get(STORE_PICK_STATUS_2_YES));
        item.setStorePickStatus(STORE_PICK_STATUS_2_YES);
        item.setStorePickDate("0005-01-01T00:00:00Z");
        updateAssortmentItem(item,assortmentItemIds);
        item.setItemStatus(ASSORT_ITEM_STATUS_2_SETUP_IN_PROGRESS);//when item status =1 and pick status = 2 the item status is set to 2
        changes = validateChanges(item); //validate all item attributes were old values is different than new values were inserted in changes table
        if(!changes) message.append("\nChanges were NOT correctly tracked when Updating AssortmentItem with item status = 2 Set In progress");


        System.out.println("Updating AssortmentItem with item status = 7 store pick status 3 and store pick date null");
        List<AssortmentItem> listAssortItems = new ArrayList<>();
        item =setOldValues(getAssortmentItemsByID(Arrays.asList(assortmentItemIds.get(0))).get(0));
        item.setVenusNotes("Third Venus Notes");
        item.setNotes1("Third Notes 1");
        item.setNotes2("Third Notes 2");
        item.setInStoreUpc("3333333333333");
        item.setStorePickStatusValue(catalogStorePickStatus.get(STORE_PICK_STATUS_3_PERMANENT)); // SET STORE PICK STATUS = 3 AS rule requires
        item.setStorePickStatus(STORE_PICK_STATUS_3_PERMANENT);
        item.setItemStatusValue(catalogAssortmentItemStatus.get(ASSORT_ITEM_STATUS_7_PERMANENT_STORE_PICK));// SET ITEM STATUS = 7 AS rule requires
        item.setItemStatus(ASSORT_ITEM_STATUS_7_PERMANENT_STORE_PICK);
        updateAssortmentItem(item,assortmentItemIds);
        item.setStorePickDate(null);//once item status =7 the StorePickDate is set to null
        changes = validateChanges(item); //validate all item attributes were old values is different than new values were inserted in changes table
        if(!changes) message.append("\nChanges were NOT correctly tracked when Updating AssortmentItem with item status = 7 store pick status 3 and store pick date null");

        if(message.length()>0) changes = false; //if not errors were tracked then changes were correctly recorded

        System.out.println(changes? "[Correct]  Changes in Assortment Item were correctly tracked " :
                "[Error] Changes in Assortment Item were not inserted in Changes table properly, error: " + message.toString());
        Assert.assertTrue(changes, "[Error] Changes in Assortment Item were not inserted in Changes table properly, error: " + message.toString());
        System.out.println("============================================================================================");

    }


    /**
     * This method will update assortment items given a list of ids
     * @param assortItem parameters to update the assortment Item:
     *   "notes1": "string",
     *   "itemStatus": 0,
     *   "caseUPC": "string",
     *   "supplierSiteCode": "string",
     *   "supplierSiteName": "string",
     *   "source": "string",
     *   "replacementSource": "string",
     *   "storePickStatus": 0,
     *   "storePickDate": "string"
     * @return
     */
    public boolean updateAssortmentItem(AssortmentItem assortItem,List<String> assortItemIds ){
        List<AssortmentItem> assortItems = getAssortmentItemsByID(assortItemIds);

        String body = buildPutAssortmentItemBody( assortItem, assortItems);
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().put().then().contentType(ContentType.JSON).extract().response();

        boolean updated = restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT) !=null &&
                (Integer)restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT)== assortItemIds.size() ? true : false;
        System.out.println(updated?"":"[Error] updateAssortmentItem method failed, items couldn't be updated response: " + restResponse.jsonPath().get().toString());
        return updated;
    }


    /**
     *  This method will return a list of random list of Assortment Items
     *  Param int numItems, String UPC13, this parameters determines the size of the list in return and the filter by UPC13 and order by UPC13
     */
    public List<AssortmentItem> getAssortmentItemsByID( List<String> ids) {
        List<AssortmentItem> assortItems = new ArrayList<AssortmentItem>();
        if(ids== null || ids.isEmpty())
            return assortItems;

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse;
        for(String id : ids){
            restResponse = given().
                    headers("Authorization", token.getBearerToken()).
                    queryParam(PAGEABLE,"true").
                    queryParam(ASSORTMENT_ITEM_ID_FILTER,id).
                    when().get(url).then().contentType(ContentType.JSON).extract().response();
            if( restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_FC) != null )
                assortItems.add(restResponse.jsonPath().getList(JSON_PATH_ASSORTMENT_ITEM_FC, AssortmentItem.class).get(0));

        }


        return assortItems;
    }



    /**
     *  This method will return a list of random list of Assortment Items
     *  Param int numItems, String UPC13, this parameters determines the size of the list in return and the filter by UPC13 and order by UPC13
     */
    public List<AssortmentItem> getRandomAssortmentItems(int numItems,String upc13){
        Random rand = new Random();

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(PAGEABLE,"true").
                queryParam(PAGE_SIZE,numItems).
                queryParam(UPCS_FILTER,upc13).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        List<AssortmentItem> assortItems = restResponse.jsonPath().get(JSON_PATH_ASSORTMENT_ITEM_FC) != null ?
                restResponse.jsonPath().getList(JSON_PATH_ASSORTMENT_ITEM_FC, AssortmentItem.class):new ArrayList<AssortmentItem>();

        return assortItems;
    }

    /**
     * This method will generate the body for the PUT assortment Items
     * @param assortItem, this object contains the values to update the fields
     * @param listAssortItem , this contains the assortmentId and venusSourceId that determine Assortment Items to update
     * @return a string with the created body ready for the request
     */
    public String buildPutAssortmentItemBody(AssortmentItem assortItem, List<AssortmentItem> listAssortItem){
        StringBuilder body = new StringBuilder();
        body.append("{\n");
        body.append("   \"assortmentItemList\":[\n");
        for(int i =0; i<listAssortItem.size(); i++) {
            body.append("      {\n");
            body.append("         \"assortmentItemId\":" + listAssortItem.get(i).getAssortmentItemID() + ",\n");
            body.append("         \"venusSourceId\":" + listAssortItem.get(i).getVenusSourceId() + "\n");
            body.append(i== listAssortItem.size()-1 ? "      }\n":"      },\n");
        }
        body.append("   ],\n");
        body.append("   \"venusNotes\": \""+assortItem.getVenusNotes()+"\",\n");
        body.append("   \"notes1\": \""+assortItem.getNotes1()+"\",\n");
        body.append("   \"notes2\": \""+assortItem.getNotes2()+"\",\n");
        body.append("   \"itemStatus\":"+assortItem.getItemStatusValue()+",\n");
        body.append("   \"inStoreUpc\": \""+assortItem.getInStoreUpc()+"\",\n");
        body.append("   \"caseUPC\":\""+assortItem.getCaseUpc()+"\",\n");
        body.append("   \"supplierSiteCode\":\""+assortItem.getSupplierSiteCode()+"\",\n");
        body.append("   \"supplierSiteName\":\""+assortItem.getSupplierSiteName()+"\",\n");
        body.append("   \"source\":\""+assortItem.getSource()+"\",\n");
        body.append("   \"replacementSource\":\""+assortItem.getReplacementSource()+"\",\n");
        body.append("   \"storePickStatus\":"+assortItem.getStorePickStatusValue()+",\n");
        if(assortItem.getStorePickDate() == null)
            body.append("   \"storePickDate\":"+assortItem.getStorePickDate()+"\n");
        else body.append("   \"storePickDate\":\""+assortItem.getStorePickDate()+"\"\n");
        body.append("}");

        System.out.println("\n[PUT Body] " + body.toString());

        return body.toString();
    }

    /**
     * this method will validate assortments were updated successfully all its editable fields
     * @param assortItem contains parameter values that are expected to be updated in the assortment items list
     * @param listAssortItem list of assortments expected to be updated
     * @return
     */
    public boolean validateListAssortItemsUpdated(AssortmentItem assortItem, List<AssortmentItem> listAssortItem){
        boolean allUpdated = true;
       if(listAssortItem.isEmpty() || listAssortItem ==null || assortItem == null)
        return allUpdated;
       StringBuilder message = new StringBuilder();

       for(AssortmentItem item: listAssortItem) {
           if (!assortItem.getVenusNotes().isEmpty() && !item.getVenusNotes().equalsIgnoreCase(assortItem.getVenusNotes()))
               message.append(item.getAssortmentItemID() +" venusNotes: " + item.getVenusNotes() + " VS Expected: " + assortItem.getVenusNotes()+ "\n");
           if (!assortItem.getNotes1().isEmpty() && !item.getNotes1().equalsIgnoreCase(assortItem.getNotes1()))
               message.append(item.getAssortmentItemID() +" notes 1: " + item.getNotes1() + " VS Expected: " + assortItem.getNotes1()+ "\n");
           if (!assortItem.getNotes2().isEmpty() && !item.getNotes2().equalsIgnoreCase(assortItem.getNotes2()))
               message.append(item.getAssortmentItemID() +" notes 2: " + item.getNotes2() + " VS Expected: " + assortItem.getNotes2()+ "\n");
           if (!assortItem.getItemStatus().isEmpty() && !item.getItemStatus().equalsIgnoreCase(assortItem.getItemStatus()))
               message.append(item.getAssortmentItemID() +" ItemStatus: " + item.getItemStatus() + " VS Expected: " + assortItem.getItemStatus()+ "\n");
           if (!assortItem.getInStoreUpc().isEmpty() && !item.getInStoreUpc().equalsIgnoreCase(assortItem.getInStoreUpc()))
               message.append(item.getAssortmentItemID() +" InStoreUpc: " + item.getInStoreUpc() + " VS Expected: " + assortItem.getInStoreUpc()+ "\n");
           if (!assortItem.getCaseUpc().isEmpty() && !item.getCaseUpc().equalsIgnoreCase(assortItem.getCaseUpc()) )
               message.append(item.getAssortmentItemID() +" CaseUpc: " + item.getCaseUpc() + " VS Expected: " + assortItem.getCaseUpc()+ "\n");
           if (!assortItem.getSupplierSiteCode().isEmpty() && !item.getSupplierSiteCode().equalsIgnoreCase(assortItem.getSupplierSiteCode()) )
               message.append(item.getAssortmentItemID() +" SupplierSiteCode: " + item.getSupplierSiteCode() + " VS Expected: " + assortItem.getSupplierSiteCode()+ "\n");
           if (!assortItem.getSupplierSiteName().isEmpty() && ! item.getSupplierSiteName().equalsIgnoreCase(assortItem.getSupplierSiteName()))
               message.append(item.getAssortmentItemID() +" SupplierSiteName: " + item.getSupplierSiteName() + " VS Expected: " + assortItem.getSupplierSiteName()+ "\n");
           if (!assortItem.getSource().isEmpty() && !item.getSource().equalsIgnoreCase(assortItem.getSource()))
               message.append(item.getAssortmentItemID() +" Source: " + item.getSource() + " VS Expected: " + assortItem.getSource()+ "\n");
           if (!assortItem.getReplacementSource().isEmpty() && !item.getReplacementSource().equalsIgnoreCase(assortItem.getReplacementSource()) )
               message.append(item.getAssortmentItemID() +" ReplacementSource: " + item.getReplacementSource() + " VS  Expected:" + assortItem.getReplacementSource()+ "\n");
           if (!assortItem.getStorePickStatus().isEmpty() && !item.getStorePickStatus().equalsIgnoreCase(assortItem.getStorePickStatus()) )
               message.append(item.getAssortmentItemID() +" StorePickStatus: " + item.getStorePickStatus() + " VS Expected: " + assortItem.getStorePickStatus()+ "\n");

           if(assortItem.getStorePickDate() != null && item.getStorePickDate()!=null) {
               if (!assortItem.getStorePickDate().isEmpty() && !item.getStorePickDate().substring(0, 19).equalsIgnoreCase(assortItem.getStorePickDate().substring(0, 19)))
                   message.append(item.getAssortmentItemID() + " StorePickDate: " + item.getStorePickDate().substring(0, 19) + " VS Expected: " + assortItem.getStorePickDate().substring(0, 19) + "\n");
           }else if(!(assortItem.getStorePickDate() == null && item.getStorePickDate()==null)) {
               String itemDate = item.getStorePickDate() == null ? "null" : item.getStorePickDate();
               String assortItemDate = assortItem.getStorePickDate() == null ? "null" : assortItem.getStorePickDate();
               message.append(item.getAssortmentItemID() + " StorePickDate: " + itemDate + " VS Expected: " + assortItemDate+ "\n");
           }
       }

       if(message.length()>0) {
           allUpdated = false; System.out.println(" Assortment Items not updated according to AssortItem:"+assortItem.getAssortmentItemID()+" \n" +message.toString());
       }

       return allUpdated;
    }

    /**
     * This method validate changes suffered by an Assortment Items were recorded in the changes table from Assortment Management DB
     * @param item, an Assortment Item with old values and new values after the PUT (old values = ex: oldVenusNotes, new values = regular values ex: venusNotes)
     * @return boolean, true if all changes were successfully recorded, false if at least one of the changes was not tracked
     */
    public Boolean validateChanges(AssortmentItem item){
        StringBuilder message = new StringBuilder();
        if (item == null )  return false;

        Changes change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldVenusNotes(),item.getVenusNotes(),"venus_notes");
        if(item.getOldVenusNotes().equalsIgnoreCase(item.getVenusNotes()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new Venus notes. Old: "+item.getOldVenusNotes()+"  VS New: "+item.getVenusNotes()+"\n");
        else if(!item.getOldVenusNotes().equalsIgnoreCase(item.getVenusNotes()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for Venus Notes Old : "+item.getOldVenusNotes()+"  VS New: "+item.getVenusNotes()+"\n");

        change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldNotes1(),item.getNotes1(),"notes_1");
        if(item.getOldNotes1().equalsIgnoreCase(item.getNotes1()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new Notes 1. Old: "+item.getOldNotes1()+"  VS New: "+item.getNotes1()+"\n");
        else if(!item.getOldNotes1().equalsIgnoreCase(item.getNotes1()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for Notes1 Old : "+item.getOldNotes1()+"  VS New: "+item.getNotes1()+"\n");

        change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldNotes2(),item.getNotes2(),"notes_2");
        if(item.getOldNotes2().equalsIgnoreCase(item.getNotes2()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new Notes2. Old: "+item.getOldNotes2()+"  VS New: "+item.getNotes2()+"\n");
        else if(!item.getOldNotes2().equalsIgnoreCase(item.getNotes2()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for Notes2 Old : "+item.getOldNotes2()+"  VS New: "+item.getNotes2()+"\n");

        change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldItemStatus(),item.getItemStatus(),"status");
        if(item.getOldItemStatus().equalsIgnoreCase(item.getItemStatus()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new ItemStatus. Old: "+item.getOldItemStatus()+"  VS New: "+item.getItemStatus()+"\n");
        else if(!item.getOldItemStatus().equalsIgnoreCase(item.getItemStatus()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for ItemStatus Old : "+item.getOldItemStatus()+"  VS New: "+item.getItemStatus()+"\n");

        change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldInStoreUpc(),item.getInStoreUpc(),"in_store_upc");
        if(item.getOldInStoreUpc().equalsIgnoreCase(item.getInStoreUpc()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new InStoreUpc. Old: "+item.getOldInStoreUpc()+"  VS New: "+item.getInStoreUpc()+"\n");
        else if(!item.getOldInStoreUpc().equalsIgnoreCase(item.getNotes2()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for InStoreUpc Old : "+item.getOldInStoreUpc()+"  VS New: "+item.getInStoreUpc()+"\n");

        change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldCaseUpc(),item.getCaseUpc(),"case_upc");
        if(item.getOldCaseUpc().equalsIgnoreCase(item.getCaseUpc()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new CaseUpc. Old: "+item.getOldCaseUpc()+"  VS New: "+item.getCaseUpc()+"\n");
        else if(!item.getOldCaseUpc().equalsIgnoreCase(item.getCaseUpc()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for CaseUpc Old : "+item.getOldCaseUpc()+"  VS New: "+item.getCaseUpc()+"\n");

        change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldSupplierSiteCode(),item.getSupplierSiteCode(),"supplier_site_code");
        if(item.getOldSupplierSiteCode().equalsIgnoreCase(item.getSupplierSiteCode()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new SupplierSiteCode. Old: "+item.getOldSupplierSiteCode()+"  VS New: "+item.getSupplierSiteCode()+"\n");
        else if(!item.getOldSupplierSiteCode().equalsIgnoreCase(item.getSupplierSiteCode()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for SupplierSiteCode Old : "+item.getOldSupplierSiteCode()+"  VS New: "+item.getSupplierSiteCode()+"\n");

        change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldSupplierSiteName(),item.getSupplierSiteName(),"supplier_site_name");
        if(item.getOldSupplierSiteName().equalsIgnoreCase(item.getSupplierSiteName()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new SupplierSiteName. Old: "+item.getOldSupplierSiteName()+"  VS New: "+item.getSupplierSiteName()+"\n");
        else if(!item.getOldSupplierSiteName().equalsIgnoreCase(item.getSupplierSiteName()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for SupplierSiteName Old : "+item.getOldSupplierSiteName()+"  VS New: "+item.getSupplierSiteName()+"\n");

        change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldSource(),item.getSource(),"source");
        if(item.getOldSource().equalsIgnoreCase(item.getSource()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new Source. Old: "+item.getOldSource()+"  VS New: "+item.getSource()+"\n");
        else if(!item.getOldSource().equalsIgnoreCase(item.getSource()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for Source Old : "+item.getOldSource()+"  VS New: "+item.getSource()+"\n");

        change = new Changes();
        change = getChanges(SELECT_CHANGE,item.getAssortmentItemID(),item.getOldStorePickStatus(),item.getStorePickStatus(),"store_pick_status");
        if(item.getOldStorePickStatus().equalsIgnoreCase(item.getStorePickStatus()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new StorePickStatus. Old: "+item.getOldStorePickStatus()+"  VS New: "+item.getStorePickStatus()+"\n");
        else if(!item.getOldStorePickStatus().equalsIgnoreCase(item.getStorePickStatus()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for StorePickStatus Old : "+item.getOldStorePickStatus()+"  VS New: "+item.getStorePickStatus()+"\n");

        change = new Changes();
        if(item.getStorePickDate()==null)  item.setStorePickDate("");
        else item.setStorePickDate(item.getStorePickDate().substring(0, 19));
        change = getChanges(SELECT_CHANGE_PICKSTATUSDATE,item.getAssortmentItemID(),"%"+item.getOldStorePickDate()+"%","%"+item.getStorePickDate()+"%","store_pick_date");
        if(item.getOldStorePickDate().equalsIgnoreCase(item.getStorePickDate()) && (change !=null && change.getId()>0 ))
            message.append("Error: a change was found for same  old and new StorePickDate. Old: "+item.getOldStorePickDate()+"  VS New: "+item.getStorePickDate()+"\n");
        else if(!item.getOldStorePickDate().equalsIgnoreCase(item.getStorePickDate()) && (change ==null || change.getId()==0 ))
            message.append("Error: a change was NOT found for StorePickDate Old : "+item.getOldStorePickDate()+"  VS New: "+item.getStorePickDate()+"\n");

        if(message.length()>0){
            System.out.println("Error: Changes were not tracked correctly for AssortmentItem: "+item.getAssortmentItemID()+"\n" +message.toString());
            return false;
        }

        return true;
    }


    /**
     * This method will fetch the newest change found according to next parameters
     * @param assortItemId
     * @param oldValue
     * @param newValue
     * @param column
     * @return
     */
    public Changes getChanges(String query,int assortItemId, String oldValue, String newValue, String column){
        List<Object> params = Arrays.asList(assortItemId,column,oldValue,newValue);
        Changes change = new Changes();
        List<Changes> changes = new ArrayList<>();

        try {
            ResultSet rs = dbUtils.DBSelectQueryObjectParams(query,params, dbQueries.getString(DB_DRIVER),
                    dbQueries.getString(DB_URL),dbQueries.getString(DB_USERNAME), dbQueries.getString(DB_PSSWD));

            if(rs == null)return change;

            while ( rs.next()){
                change = new Changes();
                change.setId(rs.getInt("id"));
                change.setAssortmentItemId(rs.getInt("assortment_item_id"));
                change.setColumn(rs.getString("column_name"));
                change.setOldValue(rs.getString("old_value"));
                change.setNewValue(rs.getString("new_value"));
                changes.add(change);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }

        if(changes.isEmpty()) return null;
        else  return changes.get(0) ;
    }



    /**
     * Method to set the current values (before update) as old values
     * @param item,
     * @return an Assortment Item with current values set as old values, all this before the PUT Assortment Items
     */
    public AssortmentItem setOldValues(AssortmentItem item){

        item.setOldVenusNotes(item.getVenusNotes());
        item.setOldNotes1(item.getNotes1());
        item.setOldNotes2(item.getNotes2());
        item.setOldItemStatus(item.getItemStatus());
        item.setOldInStoreUpc(item.getInStoreUpc());
        item.setOldCaseUpc(item.getCaseUpc());
        item.setOldSupplierSiteCode(item.getSupplierSiteCode());
        item.setOldSupplierSiteName(item.getSupplierSiteName());
        item.setOldSource(item.getSource());
        item.setOldStorePickStatus(item.getStorePickStatus());
        if(item.getStorePickDate()==null)  item.setOldStorePickDate("");
        else item.setOldStorePickDate(item.getStorePickDate().substring(0, 19));

        return item;
    }




}
