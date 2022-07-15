package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItem;
import com.kroger.assortment.model.assortmentmgmtcore.Result;
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
import java.util.*;

import static com.kroger.assortment.testUtilities.Constants.*;
import static com.kroger.assortment.testUtilities.ConstantsDB.*;
import static io.restassured.RestAssured.given;

public class PatchAssortmentItems extends InitAssortment {

    protected static YaapitaReport report;


    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
       report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will unassign all assortment items for the upcs for each one of the assortments
     *     will validate the status code is 200 OK and now all upcs with each one of the assortments has been unassigned and no longer exist in assortmentItems
     */
    @Test
    public void validatePatchAssortmentItems_TC001() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePatchAssortmentItems_TC001 ");
        System.out.println("[Test Scenario] Unassign assortment items with UPCS, that currently are  assigned ");

        List<AssortmentItem> assignedItems = getRandomAssortmentItems();
        List<Integer> assortments = getAssortments(assignedItems);
        List<String> upcs= getUpcs(assignedItems);

        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?"+UPCS+"="+testUtils.formatMultipleUpcs(upcs)+"&"+
                ASSORTMENT_IDS+"="+testUtils.formatMultipleUpcs(assortments) );
        String url = env.getString("ASSORTMENT_HOST") +env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,testUtils.formatMultipleUpcs(upcs)).
                queryParam(ASSORTMENT_IDS,testUtils.formatMultipleUpcs(assortments)).
                queryParam(PAGEABLE,"false").
                when().patch(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;
        boolean unassigned = validateItemsUnassigned(assignedItems);
        boolean response = false;
        if(restResponse.jsonPath().get(JSON_PATH_UNASSIGNED_ITEMS) != null)
            response =validateUnassignedInResponse( restResponse,JSON_PATH_UNASSIGNED_ITEMS, assignedItems);

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(unassigned ? "[Correct]  Items were correctly unassigned " :
                "[Error] Items were NOT unassigned, remains on Assortment Item table ");
        System.out.println(response ? "[Correct]  Unassigned Items were correctly retrieved in the Api response  "+restResponse.jsonPath().get().toString() :
                "[Error] Unassigned Items were NOT retrieved in the Api response  " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
       Assert.assertTrue(unassigned,  "[Error] Items were NOT unassigned, remains on Assortment Item table ");
        Assert.assertTrue(response,  "[Error] Unassigned Items were NOT retrieved in the Api response  " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate items has been already unassigned can't be unassigned anymore
     * first some items will be unassined, then a second unassigned to the same items will be try
     * Validate status code is 200, validate items were not unassigned again in the API response
     */
    @Test
    public void validatePatchAssortmentItems_NOT_Unassigned_TC002() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePatchAssortmentItems_NOT_Unassigned_TC002 ");
        System.out.println("[Test Scenario] Validate items not assigned can't be unassigned");

        List<AssortmentItem> assignedItems = getRandomAssortmentItems();
        List<Integer> assortments = getAssortments(assignedItems);
        List<String> upcs= getUpcs(assignedItems);

        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?"+UPCS+"="+testUtils.formatMultipleUpcs(upcs)+"&"+
                ASSORTMENT_IDS+"="+testUtils.formatMultipleUpcs(assortments) );
        String url = env.getString("ASSORTMENT_HOST") +env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,testUtils.formatMultipleUpcs(upcs)).
                queryParam(ASSORTMENT_IDS,testUtils.formatMultipleUpcs(assortments)).
                queryParam(PAGEABLE,"false").
                when().patch(url).then().contentType(ContentType.JSON).extract().response();

        Response response = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,testUtils.formatMultipleUpcs(upcs)).
                queryParam(ASSORTMENT_IDS,testUtils.formatMultipleUpcs(assortments)).
                queryParam(PAGEABLE,"false").
                when().patch(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = response.getStatusCode() == HttpStatus.SC_OK;
        boolean unassigned = validateItemsUnassigned(assignedItems);
        boolean json = false;
        if(response.jsonPath().get(JSON_PATH_ERRORS) != null)
            json =validateUnassignedErrors( response,JSON_PATH_ERRORS, assignedItems);

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(unassigned ? "[Correct]  Items were correctly unassigned " :
                "[Error] Items were NOT unassigned, remains on Assortment Item table ");
        System.out.println(json ? "[Correct] Items not unassigned were  retrieved in the Api response errors  "+response.jsonPath().get().toString() :
                "[Error] Items not unassigned were NOT retrieved in the Api response errors  " + response.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(unassigned,  "[Error] Items were NOT unassigned, remains on Assortment Item table ");
        Assert.assertTrue(json,  "[Error] [Error] Items not unassigned were NOT retrieved in the Api response errors  " + response.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }

    /**
     * This method will validate invalid items are not unassigned
     * Validate status code is 200, validate items were not unassigned and are on the errors in the api response
     */
    @Test
    public void validatePatchAssortmentItems_InvalidItems_TC003() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePatchAssortmentItems_InvalidItems_TC003 ");
        System.out.println("[Test Scenario] Validate invalid items are not assigned and are retrieved in the errors response");

        List<AssortmentItem> assignedItems = getRandomAssortmentItems();
        List<Integer> assortments = getAssortments(assignedItems);
        List<String> upcs= getUpcs(assignedItems); upcs.add("00000000000000");

        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?"+UPCS+"="+testUtils.formatMultipleUpcs(upcs)+"&"+
                ASSORTMENT_IDS+"="+testUtils.formatMultipleUpcs(assortments) );
        String url = env.getString("ASSORTMENT_HOST") +env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,testUtils.formatMultipleUpcs(upcs)).
                queryParam(ASSORTMENT_IDS,testUtils.formatMultipleUpcs(assortments)).
                queryParam(PAGEABLE,"false").
                when().patch(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_OK;
        boolean unassigned = validateItemsUnassigned(assignedItems);

        boolean json = false;
        if(restResponse.jsonPath().get(JSON_PATH_UNASSIGNED_ITEMS) != null)
            json =validateUnassignedInResponse( restResponse,JSON_PATH_UNASSIGNED_ITEMS, assignedItems);

        //add invalid UPC, to validate this invalid UPC wasnt unasiggned because is invalid
        for(AssortmentItem item : assignedItems)
            item.setUpc13("00000000000000");

        boolean jsonErrors = false;
        if(restResponse.jsonPath().get(JSON_PATH_ERRORS) != null)
            jsonErrors =validateUnassignedErrors( restResponse,JSON_PATH_ERRORS, assignedItems);

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(unassigned ? "[Correct]  Items were correctly unassigned " :
                "[Error] Items were NOT unassigned, remains on Assortment Item table ");
        System.out.println(json ? "[Correct]  Unassigned Items were correctly retrieved in the Api response  "+restResponse.jsonPath().get().toString() :
                "[Error] Unassigned Items were NOT retrieved in the Api response  " + restResponse.jsonPath().get().toString());
        System.out.println(jsonErrors ? "[Correct] Items not unassigned were  retrieved in the Api response errors  "+restResponse.jsonPath().get().toString() :
                "[Error] Items not unassigned were NOT retrieved in the Api response errors  " + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(unassigned,  "[Error] Items were NOT unassigned, remains on Assortment Item table ");
        Assert.assertTrue(json,  "[Error] Unassigned Items were NOT retrieved in the Api response  " + restResponse.jsonPath().get().toString());
        Assert.assertTrue(jsonErrors,  "[Error] [Error] Items not unassigned were NOT retrieved in the Api response errors  " + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }


    /**
     * This method will validate an item not assigned, assign it and then unassign it
     * Validate status code is 200, validate items were not unassigned and are on the errors in the api response
     */
    @Test
    public void validateIntegration_Assign_Unassign_Items_TC004() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateIntegration_Assign_Unassign_Items_TC004 ");
        System.out.println("[Test Scenario] Validate valid items unassigned  are assigned and then unassigned");

        //Random Items unassigned will be assign to random assortments
        List<String> unassignedItems = getRandomItemsUnassigned(3);
        List<Integer> listAssort =  getRandomAssortments(3);
        System.out.println("Assinging unassigned items: \n");
        String body = buildPostAssortmentItemBody(listAssort, unassignedItems);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().post().then().contentType(ContentType.JSON).extract().response();

        //get assortment items once unassigned items has been assign to an assortment
        List<AssortmentItem> assignedItems = getAssortmentItems(unassignedItems);

        //Unassign the previous assigned items
        System.out.println("Unassign the previous assigned items: \n");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items?"+UPCS+"="+testUtils.formatMultipleUpcs(unassignedItems)+"&"+
                ASSORTMENT_IDS+"="+testUtils.formatMultipleUpcs(listAssort) );
        Response responsee = given().
                headers("Authorization", token.getBearerToken()).
                queryParam(UPCS,testUtils.formatMultipleUpcs(unassignedItems)).
                queryParam(ASSORTMENT_IDS,testUtils.formatMultipleUpcs(listAssort)).
                queryParam(PAGEABLE,"false").
                when().patch(url).then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = responsee.getStatusCode() == HttpStatus.SC_OK;
        boolean unassigned = validateItemsUnassigned(assignedItems);

        boolean json = false;
        if(responsee.jsonPath().get(JSON_PATH_UNASSIGNED_ITEMS) != null)
            json =validateUnassignedInResponse( responsee,JSON_PATH_UNASSIGNED_ITEMS, assignedItems);

        boolean jsonErrors = responsee.jsonPath().get(JSON_PATH_ERRORS).toString().length()>0? true:false;

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " +responsee.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + responsee.getStatusCode());
        System.out.println(unassigned ? "[Correct]  Items were correctly unassigned " :
                "[Error] Items were NOT unassigned, remains on Assortment Item table ");
        System.out.println(json ? "[Correct]  Unassigned Items were correctly retrieved in the Api response  "+responsee.jsonPath().get().toString() :
                "[Error] Unassigned Items were NOT retrieved in the Api response  " + responsee.jsonPath().get().toString());
        System.out.println(jsonErrors ? "[Correct] Items not unassigned were  retrieved in the Api response errors  "+responsee.jsonPath().get().toString() :
                "[Error] Items not unassigned were NOT retrieved in the Api response errors  " + responsee.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_OK + " VS " + " Obtained Status Code: " + responsee.getStatusCode());
        Assert.assertTrue(unassigned,  "[Error] Items were NOT unassigned, remains on Assortment Item table ");
        Assert.assertTrue(json,  "[Error] Unassigned Items were NOT retrieved in the Api response  " + responsee.jsonPath().get().toString());
        Assert.assertTrue(jsonErrors,  "[Error] [Error] Items not unassigned were NOT retrieved in the Api response errors  " + responsee.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }

    public boolean validateUnassignedInResponse(Response response,String jsonPath, List<AssortmentItem> items){
        List<Map<String,Object>> maps =response.jsonPath().getList(jsonPath);
        if(maps== null || maps.isEmpty()) return false;
        StringBuilder message = new StringBuilder();
        boolean unassigned = false;

        for(AssortmentItem item : items){
            for(Map<String,Object> map : maps) {
                if (item.getAssortmentId() == map.get("assortmentId") && map.get("upcs").toString().contains(item.getUpc13())) {
                    unassigned =true; break;
                }
            }if(!unassigned) message.append("The unassignment for upc: " +item.getUpc13()+" to assortment: " +item.getAssortmentId() +" is not in the response\n");
        unassigned=false;
        }

        if(message.length()>0){
            System.out.println("The Api response is incorrect, next items should be but were not retrieved in response api: \n"+message.toString());
            return false;
        }
        return true;
    }



    public boolean validateUnassignedErrors(Response response,String jsonPath, List<AssortmentItem> items){
        List<Map<String,Object>> maps =response.jsonPath().getList(jsonPath);
        if(maps== null || maps.isEmpty()) return false;
        StringBuilder message = new StringBuilder();
        boolean unassigned = false;

        for(AssortmentItem item : items){
            for(Map<String,Object> map : maps) {
                if (item.getAssortmentId() == map.get("assortmentId") && map.get("upcs").toString().contains(item.getUpc13())) {
                    unassigned =true; break;
                }
            }if(!unassigned) message.append("The error for upc: " +item.getUpc13()+" to assortment: " +item.getAssortmentId() +" is not in the response\n");
            unassigned=false;
        }

        if(message.length()>0){
            System.out.println("The unassigned failed, next items not retrieved in response api: \n"+message.toString());
            return false;
        }
        return true;
    }


    public boolean validateItemsUnassigned(List<AssortmentItem> assignedItems){
        StringBuilder message = new StringBuilder();
        if(assignedItems==null || assignedItems.size()==0)
            return false;

        for(AssortmentItem item : assignedItems){
            List<Integer> unassignedItems = dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_ASSORTMENT_ITEM_ID_BY_ASSORTITEMID,
                    Arrays.asList(item.getAssortmentItemID()), "id");

            if(unassignedItems!=null && unassignedItems.size()>0)
                message.append("Item " +item.getUpc13()+ " was NOT unassigned to  assortment "+ item.getAssortmentId()+"\n");
        }

        if(message.length()>0){
            System.out.println("The next items remains assigned to an assortment: \n"+message.toString());
            return false;
        }
         return true;
    }


    public List<AssortmentItem> getRandomAssortmentItems(){
        List<String> randomUpcs =  dbUtils.getRandomDBRecords(3,SELECT_RANDOM_UPC13, Arrays.asList(1000),"upc13");
        List<AssortmentItem> assortItems = new ArrayList<AssortmentItem>();

        try {
            ResultSet rs = dbUtils.DBSelectQueryParams(SELECT_UPC_ASSORTMENTID_BY_UPC13,randomUpcs, dbQueries.getString(DB_DRIVER),
                    dbQueries.getString(DB_URL),dbQueries.getString(DB_USERNAME), dbQueries.getString(DB_PSSWD));

            if(rs == null)return assortItems;

            AssortmentItem assortItem ;
            while ( rs.next()){
                    assortItem = new AssortmentItem();
                    assortItem.setAssortmentItemID(rs.getInt("id"));
                    assortItem.setUpc13(rs.getString("upc13"));
                    assortItem.setAssortmentId(rs.getInt("assortment_id"));
                    assortItems.add(assortItem);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }

        return assortItems;
    }


    public List<AssortmentItem> getAssortmentItems(List<String> items){
        List<AssortmentItem> assortItems = new ArrayList<AssortmentItem>();

        try {
            ResultSet rs = dbUtils.DBSelectQueryParams(SELECT_UPC_ASSORTMENTID_BY_UPC13,items, dbQueries.getString(DB_DRIVER),
                    dbQueries.getString(DB_URL),dbQueries.getString(DB_USERNAME), dbQueries.getString(DB_PSSWD));

            if(rs == null)return assortItems;

            AssortmentItem assortItem ;
            while ( rs.next()){
                assortItem = new AssortmentItem();
                assortItem.setAssortmentItemID(rs.getInt("id"));
                assortItem.setUpc13(rs.getString("upc13"));
                assortItem.setAssortmentId(rs.getInt("assortment_id"));
                assortItems.add(assortItem);
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(" SQL Exception Message :" + e.getMessage());
        }

        return assortItems;
    }


    List<Integer> getAssortments(List<AssortmentItem> assortItems){
        Set<Integer> assortmentsId = new LinkedHashSet<>();
        List<Integer> ids = new ArrayList<>();

        if(assortItems==null || assortItems.size()==0)return ids;

        for(AssortmentItem item : assortItems)
            assortmentsId.add(item.getAssortmentId());

        ids.addAll(assortmentsId);
        return ids;
    }


    List<String> getUpcs(List<AssortmentItem> assortItems){
        List<String> upcs = new ArrayList<>();
        Set<String>  items = new LinkedHashSet<>();

        if(assortItems==null || assortItems.size()==0)return upcs;

        for(AssortmentItem item : assortItems)
            items.add(item.getUpc13());

        upcs.addAll(items);
        return upcs;
    }


    public List<String> getRandomItemsUnassigned(int sizeList){
        List<String> unassignedItems = new ArrayList<String>();
        List<String>  listItemsUnassigned = dbUtils.DBSelectQueryObjectParams(SELECT_UNASSIGNED_ITEMS, Arrays.asList(100),UPC13);

        if(listItemsUnassigned == null || listItemsUnassigned.isEmpty())
            return unassignedItems;

        int i=0;
        while(i<sizeList){
            unassignedItems.add(listItemsUnassigned.get( testUtils.getRandomNumber(listItemsUnassigned.size())));
            i++;
        }
        return unassignedItems;
    }

    public List<Integer> getRandomAssortments(int sizeList){
        List<Integer> assortments = new ArrayList<Integer>();
        List<Integer>  listAssortments = dbUtils.returnIntegerDBSelectQueryObjectParams(SELECT_ASSORTMENTS_ID, Arrays.asList(100),ID);

        if(listAssortments == null || listAssortments.isEmpty())
            return assortments;

        int i=0;
        while(i<sizeList){
            assortments.add(listAssortments.get( testUtils.getRandomNumber(listAssortments.size())));
            i++;
        }
        return assortments;
    }

    public String buildPostAssortmentItemBody(List<Integer> assortments, List<String> items){
        StringBuilder body = new StringBuilder();
        body.append("{\n");
        body.append("   \"upcs\":[\n");
        for(int i =0; i<items.size(); i++) {
            body.append("        \""+items.get(i) +"\"");
            body.append(i== items.size()-1 ? "\n":",\n");
        }
        body.append("   ],\n");
        body.append("    \"assortments\": [\n");
        for(int i =0; i<assortments.size(); i++) {
            body.append("       "+assortments.get(i));
            body.append(i== assortments.size()-1 ? "\n":",\n");
        }
        body.append("   ]\n" );
        body.append("}");

        System.out.println("\n[PUT Body] \n" + body.toString());

        return body.toString();
    }

}
