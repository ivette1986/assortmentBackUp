package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItem;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItemStatus;
import com.kroger.assortment.model.assortmentmgmtcore.Changes;
import com.kroger.assortment.model.assortmentmgmtcore.StorePickStatus;
import com.org.yaapita.YaapitaReport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.kroger.assortment.testUtilities.Constants.*;

import static com.kroger.assortment.testUtilities.Constants.JSON_PATH_UPDATED_ITEMS_COUNT;
import static com.kroger.assortment.testUtilities.ConstantsDB.*;
import static com.kroger.assortment.testUtilities.ConstantsDB.DB_PSSWD;
import static io.restassured.RestAssured.given;

public class GetAssortmentItemsChanges extends InitAssortment {
    protected static YaapitaReport report;


    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }

    /**
     * This method validate all changes made to an Assortment Item  done through the PutAssortmentItem services must be recorded in the Changes table
     * example https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortment-items/changes?id=103199
     *
     */
    @Test
    public void validateGetAssortmentItemChanges_TC001() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItemChanges_TC001 ");
        System.out.println("[Test Scenario] Validate Assortment Items changes through Put Assortment Items service are recorded in Changes table");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items/changes");

        System.out.println("\nUpdating AssortmentItem with item status = 4 Active to validate changes");
        List<String> assortmentItemIds = dbUtils.getRandomDBRecords(1,SELECT_ASSORT_ITEMS_IDS_BY_DIFF_ITEMSTATUS_AND_STORE_PICK,
                Arrays.asList(ASSORT_ITEM_STATUS_2,STORE_PICK_STATUS_1,500),"id");
        AssortmentItem item =setOldValues(testUtils.getAssortmentItemsByID(Arrays.asList(assortmentItemIds.get(0))).get(0));
        item.setVenusNotes("First Venus Notes");
        item.setNotes1("First Notes 1");
        item.setNotes2("First Notes 2");
        item.setInStoreUpc("1111111111111");
        item.setItemStatus(ASSORT_ITEM_STATUS_4_ACTIVE);
        item.setItemStatusValue(ASSORT_ITEM_STATUS_4);
        item.setStorePickStatusValue(getCatalogStorePickStatus(item.getStorePickStatus()));
        List<AssortmentItem> assortItems = testUtils.getAssortmentItemsByID(assortmentItemIds);

        String body = buildPutAssortmentItemBody( item, assortItems);
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().put().then().contentType(ContentType.JSON).extract().response();

        boolean updated = restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT) !=null &&
                (Integer)restResponse.jsonPath().get(JSON_PATH_UPDATED_ITEMS_COUNT)== assortmentItemIds.size() ? true : false;
        System.out.println(updated?"":"[Error] updateAssortmentItem method failed, items couldn't be updated response: " + restResponse.jsonPath().get().toString());


        System.out.println("Validating previous changes were tracked throught the GetAssortmentItemChanges Endpoint");
         url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_CHANGES");
         restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_ID,item.getAssortmentItemID()).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = HttpStatus.SC_OK == restResponse.getStatusCode();
        boolean changes = false;

        if(restResponse.jsonPath().get(JSON_PATH_CHANGES_ASSORTMENTITEMID)!=null &&
                (Integer)restResponse.jsonPath().get(JSON_PATH_CHANGES_ASSORTMENTITEMID)==item.getAssortmentItemID()) {
            List<Changes> apiChanges = restResponse.jsonPath().getList(JSON_PATH_CHANGES, Changes.class);
            changes = validateChanges(item,apiChanges);

        }else System.out.println("\nError: Assorment Item id dont match, Api response id: "+
                restResponse.jsonPath().get(JSON_PATH_CHANGES_ASSORTMENTITEMID)+" Assortment Item modified "+assortmentItemIds.get(0));

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(changes? "[Correct]  Assortment Items changes were correctly tracked, resume: " + restResponse.jsonPath().get().toString():
                "[Error] Assortment Items changes were  NOT correctly tracked, resume: " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(changes, "[Error] Assortment Items changes were  NOT correctly tracked, resume: " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }





    /**
     * This method validates the GetAssortmentItemChanges with not existing AssortmentItem
     * example https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortment-items/changes?id=103199
     *
     */
    @Test
    public void validateGetAssortmentItemChanges_InvalidId_TC002() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetAssortmentItemChanges_InvalidId_TC002 ");
        System.out.println("[Test Scenario] Validate GetAssortmentItemChanges with a not existing AssortmentItem id");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items/changes");

        System.out.println("Validating previous changes were tracked throught the GetAssortmentItemChanges Endpoint");
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_CHANGES");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(API_ID,0).
                when().get(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = HttpStatus.SC_OK == restResponse.getStatusCode();
        boolean changes = true;

        if(restResponse.jsonPath().get(JSON_PATH_CHANGES_ASSORTMENTITEMID)!=null &&
                (Integer)restResponse.jsonPath().get(JSON_PATH_CHANGES_ASSORTMENTITEMID)==0) {
            changes = restResponse.jsonPath().get(JSON_PATH_CHANGES)!=null &&
                    restResponse.jsonPath().get(JSON_PATH_CHANGES).toString().equalsIgnoreCase("[]")?true:false;

        }else System.out.println("\nError: Assorment Item should not have changes, because assortment item don't exist ");

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "\n[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(changes? "[Correct]  Not existing Assortment Item Id don't have any changes tracked: ":
                "[Error] Not existing Assortment Item Id should NOT have any changes tracked, BUT they have: " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(changes, "[Error] Not existing Assortment Item Id should NOT have any changes tracked, BUT they have: " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }


    public Changes getChanges(List<Changes> changesList,String oldValue,String newValue,String column){
        List<Changes> changes = new ArrayList<>();
        for(Changes change:changesList){
            if(change.getColumnName().equalsIgnoreCase(column) && change.getOldValue().contains(oldValue) && change.getNewValue().contains(newValue)){
                return change;
            }
        }
        return null;
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



    /**
     * This method validate changes suffered by an Assortment Items are retrieved by the GetAssortmentItemsChanges api
     * @param item, an Assortment Item with old values and new values after the PUT (old values = ex: oldVenusNotes, new values = regular values ex: venusNotes)
     * @param changes, list of changes retrieved by the GetAssortmentItemsChanges api
     * @return boolean, true if all changes were successfully recorded, false if at least one of the changes was not tracked
     */
    public Boolean validateChanges(AssortmentItem item,List<Changes> changes){
        StringBuilder message = new StringBuilder();
        if (item == null || changes.isEmpty())  return false;

        Changes change = null;
        change = getChanges(changes,item.getOldVenusNotes(),item.getVenusNotes(),"venus_notes");
        if(item.getOldVenusNotes().equalsIgnoreCase(item.getVenusNotes()) && (change !=null ))
            message.append("Error: a change was found for same  old and new Venus notes. Old: "+item.getOldVenusNotes()+"  VS New: "+item.getVenusNotes()+"\n");
        else if(!item.getOldVenusNotes().equalsIgnoreCase(item.getVenusNotes()) && (change ==null || !change.getColumnName().equalsIgnoreCase("venus_notes") ))
            message.append("Error: a change was NOT found for Venus Notes Old : "+item.getOldVenusNotes()+"  VS New: "+item.getVenusNotes()+"\n");

        change = null;
        change = getChanges(changes,item.getOldNotes1(),item.getNotes1(),"notes_1");
        if(item.getOldNotes1().equalsIgnoreCase(item.getNotes1()) && (change !=null  ))
            message.append("Error: a change was found for same  old and new Notes 1. Old: "+item.getOldNotes1()+"  VS New: "+item.getNotes1()+"\n");
        else if(!item.getOldNotes1().equalsIgnoreCase(item.getNotes1()) && (change ==null || !change.getColumnName().equalsIgnoreCase("notes_1") ))
            message.append("Error: a change was NOT found for Notes1 Old : "+item.getOldNotes1()+"  VS New: "+item.getNotes1()+"\n");

        change = null;
        change = getChanges(changes,item.getOldNotes2(),item.getNotes2(),"notes_2");
        if(item.getOldNotes2().equalsIgnoreCase(item.getNotes2()) && (change !=null ))
            message.append("Error: a change was found for same  old and new Notes2. Old: "+item.getOldNotes2()+"  VS New: "+item.getNotes2()+"\n");
        else if(!item.getOldNotes2().equalsIgnoreCase(item.getNotes2()) && (change ==null || !change.getColumnName().equalsIgnoreCase("notes_2")))
            message.append("Error: a change was NOT found for Notes2 Old : "+item.getOldNotes2()+"  VS New: "+item.getNotes2()+"\n");

        change = null;
        change = getChanges(changes,item.getOldItemStatus(),item.getItemStatus(),"status");
        if(item.getOldItemStatus().equalsIgnoreCase(item.getItemStatus()) && (change !=null  ))
            message.append("Error: a change was found for same  old and new ItemStatus. Old: "+item.getOldItemStatus()+"  VS New: "+item.getItemStatus()+"\n");
        else if(!item.getOldItemStatus().equalsIgnoreCase(item.getItemStatus()) && (change ==null || !change.getColumnName().equalsIgnoreCase("status") ))
            message.append("Error: a change was NOT found for ItemStatus Old : "+item.getOldItemStatus()+"  VS New: "+item.getItemStatus()+"\n");

        change = null;
        change = getChanges(changes,item.getOldInStoreUpc(),item.getInStoreUpc(),"in_store_upc");
        if(item.getOldInStoreUpc().equalsIgnoreCase(item.getInStoreUpc()) && (change !=null ))
            message.append("Error: a change was found for same  old and new InStoreUpc. Old: "+item.getOldInStoreUpc()+"  VS New: "+item.getInStoreUpc()+"\n");
        else if(!item.getOldInStoreUpc().equalsIgnoreCase(item.getNotes2()) && (change ==null || !change.getColumnName().equalsIgnoreCase("in_store_upc") ))
            message.append("Error: a change was NOT found for InStoreUpc Old : "+item.getOldInStoreUpc()+"  VS New: "+item.getInStoreUpc()+"\n");

        change = null;
        change = getChanges(changes,item.getOldCaseUpc(),item.getCaseUpc(),"case_upc");
        if(item.getOldCaseUpc().equalsIgnoreCase(item.getCaseUpc()) && (change !=null ))
            message.append("Error: a change was found for same  old and new CaseUpc. Old: "+item.getOldCaseUpc()+"  VS New: "+item.getCaseUpc()+"\n");
        else if(!item.getOldCaseUpc().equalsIgnoreCase(item.getCaseUpc()) && (change ==null || !change.getColumnName().equalsIgnoreCase("case_upc")))
            message.append("Error: a change was NOT found for CaseUpc Old : "+item.getOldCaseUpc()+"  VS New: "+item.getCaseUpc()+"\n");

        change = null;
        change = getChanges(changes,item.getOldSupplierSiteCode(),item.getSupplierSiteCode(),"supplier_site_code");
        if(item.getOldSupplierSiteCode().equalsIgnoreCase(item.getSupplierSiteCode()) && (change !=null  ))
            message.append("Error: a change was found for same  old and new SupplierSiteCode. Old: "+item.getOldSupplierSiteCode()+"  VS New: "+item.getSupplierSiteCode()+"\n");
        else if(!item.getOldSupplierSiteCode().equalsIgnoreCase(item.getSupplierSiteCode()) && (change ==null || !change.getColumnName().equalsIgnoreCase("supplier_site_code") ))
            message.append("Error: a change was NOT found for SupplierSiteCode Old : "+item.getOldSupplierSiteCode()+"  VS New: "+item.getSupplierSiteCode()+"\n");

        change = null;
        change = getChanges(changes,item.getOldSupplierSiteName(),item.getSupplierSiteName(),"supplier_site_name");
        if(item.getOldSupplierSiteName().equalsIgnoreCase(item.getSupplierSiteName()) && (change !=null ))
            message.append("Error: a change was found for same  old and new SupplierSiteName. Old: "+item.getOldSupplierSiteName()+"  VS New: "+item.getSupplierSiteName()+"\n");
        else if(!item.getOldSupplierSiteName().equalsIgnoreCase(item.getSupplierSiteName()) && (change ==null || !change.getColumnName().equalsIgnoreCase("supplier_site_name") ))
            message.append("Error: a change was NOT found for SupplierSiteName Old : "+item.getOldSupplierSiteName()+"  VS New: "+item.getSupplierSiteName()+"\n");

        change = null;
        change = getChanges(changes,item.getOldSource(),item.getSource(),"source");
        if(item.getOldSource().equalsIgnoreCase(item.getSource()) && (change !=null  ))
            message.append("Error: a change was found for same  old and new Source. Old: "+item.getOldSource()+"  VS New: "+item.getSource()+"\n");
        else if(!item.getOldSource().equalsIgnoreCase(item.getSource()) && (change ==null || !change.getColumnName().equalsIgnoreCase("source")))
            message.append("Error: a change was NOT found for Source Old : "+item.getOldSource()+"  VS New: "+item.getSource()+"\n");

        change = null;
        change = getChanges(changes,item.getOldStorePickStatus(),item.getStorePickStatus(),"store_pick_status");
        if(item.getOldStorePickStatus().equalsIgnoreCase(item.getStorePickStatus()) && (change !=null  ))
            message.append("Error: a change was found for same  old and new StorePickStatus. Old: "+item.getOldStorePickStatus()+"  VS New: "+item.getStorePickStatus()+"\n");
        else if(!item.getOldStorePickStatus().equalsIgnoreCase(item.getStorePickStatus()) && (change ==null || !change.getColumnName().equalsIgnoreCase("store_pick_status") ))
            message.append("Error: a change was NOT found for StorePickStatus Old : "+item.getOldStorePickStatus()+"  VS New: "+item.getStorePickStatus()+"\n");

        change = null;
        if(item.getStorePickDate()==null) item.setStorePickDate("");
            else item.setStorePickDate(item.getStorePickDate().substring(0, 19));
        change = getChanges(changes,item.getOldStorePickDate(),item.getStorePickDate(),"store_pick_date");
        if(item.getOldStorePickDate().equalsIgnoreCase(item.getStorePickDate()) && (change !=null ))
            message.append("Error: a change was found for same  old and new StorePickDate. Old: "+item.getOldStorePickDate()+"  VS New: "+item.getStorePickDate()+"\n");
        else if(!item.getOldStorePickDate().equalsIgnoreCase(item.getStorePickDate()) && (change ==null || !change.getColumnName().equalsIgnoreCase("store_pick_date") ))
            message.append("Error: a change was NOT found for StorePickDate Old : "+item.getOldStorePickDate()+"  VS New: "+item.getStorePickDate()+"\n");

        if(message.length()>0){
            System.out.println("Error: Changes were not tracked correctly for AssortmentItem: "+item.getAssortmentItemID()+"\n" +message.toString());
            return false;
        }

        return true;
    }









}
