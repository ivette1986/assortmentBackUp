package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItem;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItemStatus;
import com.kroger.assortment.model.assortmentmgmtcore.Result;
import com.kroger.assortment.model.assortmentmgmtcore.StorePickStatus;
import com.org.yaapita.YaapitaReport;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.kroger.assortment.testUtilities.Constants.*;
import static com.kroger.assortment.testUtilities.ConstantsDB.*;
import static io.restassured.RestAssured.given;

public class PostAssortmentItems extends InitAssortment {

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
     *     will create multiple Assortment Items, assigned items to some assortments, for items parameter in body USE UPC14
     *     will validate the status code is 200 OK and now items are assigned to some assortments
     */
    @Test
    public void validatePOSTAssortmentItems_TC001() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePOSTAssortmentItems_TC001 ");
        System.out.println("[Test Scenario] Create assortment items with UPCS that currently are not assigned to any Assortment");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");

        List<String> unassignedItems = getRandomItemsUnassigned(3);
        List<Integer> assortments =  getRandomAssortments(3);
        String body = buildPostAssortmentItemBody(assortments, unassignedItems);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().post().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_CREATED;
        Result result = validateItemsAssigned(restResponse,unassignedItems, assortments);
        boolean allItemsAssigned = result.getListDuplicated().size()>0 && result.getListUnassigned().size()>0
                                    && result.isAssignedCorrectly()? false:true;

        if(!allItemsAssigned){
          validateAllItemsNotAssigned(restResponse,result.getListUnassigned());
            printItemsDuplicated(result.getListDuplicated());
        }


        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(allItemsAssigned ? "[Correct]  Items were correctly assigned, Assortment Items were created for each one " :
                "[Error] Items could not been assigned, Assortment Items were not created" + restResponse.jsonPath().get().toString());
        System.out.println(result.isAssignedCorrectly() ? "[Correct] All assigned Items are retrieved in the Api response assignations "+ restResponse.jsonPath().get().toString() :
                "[Error] Assigned items are not retrieved in the Api response for assignations" + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(allItemsAssigned, "[Error] Items could not been assigned, Assortment Items were not created" + restResponse.jsonPath().get().toString());
        Assert.assertTrue(result.isAssignedCorrectly() ,"[Error] Assigned items are not retrieved in the Api response for assignations" + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will create multiple Assortment Items and then will try to create them again, and shouldn't be possible any more
     *     will validate the status code is 200 OK and now items are not assigned again
     */
    @Test
    public void validatePOSTAssortmentItems_UnassignedItems_TC002() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePOSTAssortmentItems_UnassignedItems_TC002 ");
        System.out.println("[Test Scenario] Create assortment items that are already assigned shouldn't be assigned once again");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");

        List<String> unassignedItems =  getRandomItemsUnassigned(3);
        List<Integer> assortments =  getRandomAssortments(3);
        System.out.println("Assign Items to assortments: ");
        String body = buildPostAssortmentItemBody(assortments, unassignedItems);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().post().then().contentType(ContentType.JSON).extract().response();

        Response response = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().post().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = response.getStatusCode() == HttpStatus.SC_CREATED;
        boolean itemsNotAssigned = validateAllItemsNotAssigned(response,createListItemAssortments(assortments, unassignedItems));

        Result result = validateItemsAssigned(restResponse,unassignedItems,assortments);
        boolean itemsDuplicated = result.getListDuplicated().size()>0? true:false;

        if(itemsDuplicated)
            printItemsDuplicated(result.getListDuplicated());

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " +response.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + response.getStatusCode());
        System.out.println(itemsNotAssigned ? "[Correct]  Any item was assigned to an assortment because they were already assigned " :
                "[Error] Any item should be assigned to an assortment because they were already assigned" +restResponse.jsonPath().get().toString());
        System.out.println( !itemsDuplicated? "[Correct]  Any of the items got duplicated in the Assortment Items table " :
                "[Error] Some of the items got duplicated in the Assortment Items table ");

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + response.getStatusCode());
        Assert.assertTrue(itemsNotAssigned, "[Error] Any item should be assigned to an assortment because they were already assigned" + restResponse.jsonPath().get().toString());
        Assert.assertTrue(!itemsDuplicated, "[Error] Some of the items got duplicated in the Assortment Items table " );
        System.out.println("============================================================================================");
    }


    /**
     *  This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/assortments-items
     *     will try to create Assortment Items with no existing upc14s
     *     will validate the status code is 200 OK and assortment items are not created
     */
    @Test
    public void validatePOSTAssortmentItems_unexistingItems_TC003() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validatePOSTAssortmentItems_unexistingItems_TC003 ");
        System.out.println("[Test Scenario] Create assortment items that are already assigned shouldn't be assigned once again");
        System.out.println("[Endpoint]  "+ env.getString("ASSORTMENT_HOST")+ "/assortment-items");

        List<String> unassignedItems =  getRandomItemsUnassigned(1);
        List<Integer> assortments =  getRandomAssortments(1);
        unassignedItems.add("00000000000000");//adding unexisting upc14
        assortments.add(0);//adding unexisting assortment
        System.out.println("Assign Items to assortments: ");
        String body = buildPostAssortmentItemBody(assortments, unassignedItems);

        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_ASSORTMENT_ITEMS_FILTER_BY_FC");
        Response restResponse = given().baseUri(url).
                headers("Authorization", token.getBearerToken()).
                body(body).
                when().post().then().contentType(ContentType.JSON).extract().response();

        boolean statusCode = restResponse.getStatusCode() == HttpStatus.SC_CREATED;
        //remove invalid assortments and invalid items, since validation will include only valid items and assortments to be assigned
        unassignedItems.remove(1); assortments.remove(1);
        Result result = validateItemsAssigned(restResponse, unassignedItems,assortments);
        boolean itemsAssigned = result.getListUnassigned().size()==0 && result.getListDuplicated().size()==0 ? true: false;

        //create all the maps of item - assortment that should still be unassigned items due to invalid assortments and invalid items
        List<Map<String, Object>> maps= new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("assortment",assortments.get(0)); map.put("upc","00000000000000");  maps.add(map);
        map = new HashMap<>(); map.put("assortment",0); map.put("upc","00000000000000");  maps.add(map);
        map = new HashMap<>(); map.put("assortment",0); map.put("upc",unassignedItems.get(0));  maps.add(map);

        boolean itemsnotAssigned=validateAllItemsNotAssigned(restResponse,maps);

        System.out.println(statusCode ? "\n[Correct]  Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " +restResponse.getStatusCode():
                "[Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        System.out.println(itemsAssigned ? "[Correct]  All the VALID items were assigned to an assortment correctly " :
                "[Error] Not all assortments were correctly assigned to an assortment" + restResponse.jsonPath().get().toString());
        System.out.println(itemsnotAssigned ? "[Correct]  All the INVALID items remains unassigned and are included in the not assigned items API response" :
                "[Error] NOT All the INVALID items remains unassigned  are included in the not assigned items API response" + restResponse.jsonPath().get().toString());

        Assert.assertTrue(statusCode, " [Error] Expected code: " + HttpStatus.SC_CREATED + " VS " + " Obtained Status Code: " + restResponse.getStatusCode());
        Assert.assertTrue(itemsAssigned, "[Error] Not all VALID items were correctly assigned to an assortment" +restResponse.jsonPath().get().toString());
        Assert.assertTrue(itemsnotAssigned, "[Error] NOT All the INVALID items remains unassigned  are included in the not assigned items API response" +restResponse.jsonPath().get().toString());
        Assert.assertTrue(result.isAssignedCorrectly() ,"[Error] Assigned items are not retrieved in the Api response for assignations" + restResponse.jsonPath().get().toString());
        System.out.println("============================================================================================");
    }



    public boolean validateAllItemsNotAssigned(Response response,List<Map<String,Object>> listmaps) {
        boolean notAssigned = false;
        StringBuilder message = new StringBuilder();
        List<Map<String, Object>> itemsNotAssigned = response.jsonPath().getList(JSON_PATH_ITEMS_NOT_ASSIGNED) != null ?
                response.jsonPath().getList(JSON_PATH_ITEMS_NOT_ASSIGNED) : new ArrayList<Map<String, Object>>();

        if (listmaps == null || listmaps.isEmpty() )
            return false;

        //This validate the json itemsNotAssigned contains all assortments and each assortment with all the upcs, meaning any upc was assigned to any assortment
        System.out.println("\nItems not assigned: \n");
        for(Map<String,Object> mapUnassigned:listmaps){
            for(Map<String,Object> mapJson:itemsNotAssigned){
                if(mapUnassigned.get("assortment") == mapJson.get("assortment")){
                    if(! mapJson.get("failedUpcs").toString().contains(mapUnassigned.get("upc").toString()))
                        message.append("The Item " +mapUnassigned.get("upc")+ "was not assigned to assortment "+mapUnassigned.get("assortment") + "but still is not in the Api response of itemsNotAssigned\n" );
                    else {
                        System.out.println("Unassigned Item: " + mapUnassigned.get("upc") + " could NOT be assigned to Assortment " + mapUnassigned.get("assortment"));
                        notAssigned = true; break;
                    }
                }else notAssigned = false;
            }
        }
        if(message.length()>0){
            notAssigned = false;
            System.out.println("Some items not assigned to any assortment are not included in the api response: \n" + message.toString());
        }

        return notAssigned;
    }

    public void printItemsDuplicated(List<Map<String,Object>> listDuplicated){
        System.out.println("List of Items assigned duplicated");
        for(Map<String,Object> map:listDuplicated){
            System.out.println("Item "+ map.get("upc") + " assigned to assortment " +map.get("assortment")+ "was inserted in the DB more than once");
        }

    }




    public Result validateItemsAssigned(Response response, List<String> listItems, List<Integer> assortments){
        List<String>  itemsAssigned = new ArrayList<>();
        List<Map<String,Object>> maps= new ArrayList<Map<String, Object>>();
        List<Object> params = new ArrayList<>();
        Map<String,Object> map;
        Result result = new Result();

       // will create a list for duplicated assortment items, a list for unassigned items and a list for all items correctly assigned
        for(int i = 0; i < assortments.size(); i++){
            for(String item : listItems) {
                params = new ArrayList<Object>();
                params.add(0, item);
                params.add(1, assortments.get(i));
                map = new HashMap<>();
                map.put("upc", item); map.put("assortment", assortments.get(i));
                itemsAssigned = dbUtils.DBSelectQueryObjectParams(SELECT_ASSORTMENT_ITEM_UPC13_AND_ASSORTMENT_ID, params, UPC13);

                if (!itemsAssigned.isEmpty() && itemsAssigned.size()>1)
                    result.getListDuplicated().add(map);
                else if (!itemsAssigned.isEmpty() && itemsAssigned.get(0).equalsIgnoreCase(item)) {
                    maps.add(map);
                    System.out.println("Item: " + item + " has been assigned to Assortment " + assortments.get(i));
                } else result.getListUnassigned().add(map);
            }
        }

        //Validate if not all assortment items were assigned to return false that all assortments were correctly assigned
        if(maps.size()!= (listItems.size()*assortments.size())){ result.setAssignedCorrectly(false); return result;}

        List<Map<String, Object>> jsonAssigned = response.jsonPath().getList(JSON_PATH_ITEMS_ASSIGNED) != null ?
                response.jsonPath().getList(JSON_PATH_ITEMS_ASSIGNED) : new ArrayList<Map<String, Object>>();


        //This validate the json items Assigned contains all assortments and each assortment with all the valid upcs
        StringBuilder message= new StringBuilder();
        for(Map<String,Object> mapAssigned:maps){
            for(Map<String,Object> mapJson:jsonAssigned){
                if(mapAssigned.get("assortment") == mapJson.get("assortment")){
                    if(! mapJson.get("upcs").toString().contains(mapAssigned.get("upc").toString()))
                        message.append("The Item " +mapAssigned.get("upc")+ "was  assigned to assortment "+mapAssigned.get("assortment") + "but still is not in the Api response Assignations\n" );
                    else {
                        result.setAssignedCorrectly(true); break;
                    }
                }else result.setAssignedCorrectly(false);
            }
        }
        if(message.length()>0){
            result.setAssignedCorrectly(false);
            System.out.println("Some assigned items are not included in the api response: \n" + message.toString());
        }

        return result;
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


    public List<Map<String,Object>> createListItemAssortments(List<Integer> assortments, List<String> items){
        List<Map<String,Object>> maps = new ArrayList<Map<String,Object>>();
        Map<String,Object> map ;

        if(assortments == null || assortments.isEmpty()  || items == null || items.isEmpty())
            return maps;

        for(int assortment : assortments){
            for(String item : items) {
                map = new HashMap<>();
                map.put("assortment", assortment); map.put("upc", item);
                maps.add(map);
            }
        }
        return maps;
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
