package com.kroger.assortment.testUtilities;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.api.assortmentmgmtcore.BearerToken;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItem;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItemStatus;
import com.kroger.assortment.model.assortmentmgmtcore.Result;
import com.kroger.assortment.model.assortmentmgmtcore.StorePickStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.formula.functions.T;
import org.testng.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.kroger.assortment.testUtilities.Constants.*;
import static com.kroger.assortment.testUtilities.Constants.JSON_PATH_ASSORTMENT_ITEM_FC;
import static com.kroger.assortment.testUtilities.ConstantsDB.ID;
import static com.kroger.assortment.testUtilities.ConstantsDB.SELECT_ASSORTMENTS_ID;
import static io.restassured.RestAssured.given;

public class TestUtils{

    private DBUtils db = new DBUtils();
    protected ResourceBundle env                  = ResourceBundle.getBundle(System.getProperty("env"));
    protected final BearerToken token = new BearerToken();


    public Boolean compareAndPrint2Lists(String message1, List list1,String message2, List list2){
        if((list1 ==null || list2 == null) || (list1.isEmpty() || list2.isEmpty()) )
            return false;
        Collections.sort(list1);
        Collections.sort(list2);

        System.out.println("\n\n" + message1  + list1.toString());
        System.out.println(message2 + list2.toString());

        return list1.equals(list2);
    }




    public Boolean compare2Lists( List list1, List list2){
        if((list1 ==null || list2 == null) || (list1.isEmpty() || list2.isEmpty()) )
            return false;
        Collections.sort(list1);
        Collections.sort(list2);

        return list1.equals(list2);
    }


    public List<Integer> getIntegerArray(List<String> stringArray) {
        List<Integer> result = new ArrayList<Integer>();
        for(String stringValue : stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(Integer.valueOf(stringValue));
            } catch(NumberFormatException nfe) {
                //System.out.println("Could not parse " + nfe);
                System.out.println("NumberFormat Parsing failed! " + stringValue + " can not be an integer");
            }
        }
        return result;
    }

    public Result countRecordsApiVsDB(Response response, String query, List<String> params, String column, String jsonPath){
        Result result = new Result();

        if(response == null || query == null || (params ==null || params.isEmpty()) || (column ==null || column.isEmpty()) || jsonPath == null){
            System.out.println("not all parameters were set to execute countRecordsApiVsDB method in TestUtils.class");
            return null;
        }

        List<String> dblist = db.DBSelectQueryParams(query, params, column);
        result.setDbItems(! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0);

        result.setApiItems(0);
        boolean numAssortmentItems = false;
        if(response.jsonPath().get(jsonPath) != null) {
            result.setApiItems(response.jsonPath().get(JSON_PATH_COUNT));
            result.setCount(result.getDbItems() == result.getApiItems());
        }else{ System.out.println(" [Error] Records retrieved from API: " + result.getApiItems() + "   Records retrieved from DB: " + result.getDbItems());}

        return result;
    }

    public Result countRecordsApiVsDB(Response response, String query, List<String> params, List<String> columns, String jsonPath){
        Result result = new Result();

        if(response == null || query == null || (params ==null || params.isEmpty()) || (columns ==null || columns.isEmpty()) || jsonPath == null){
            System.out.println("not all parameters were set to execute countRecordsApiVsDB method in TestUtils.class");
            return null;
        }


        List<String> dblist = db.DBSelectQueryParams(query, params, columns);
        result.setDbItems(! dblist.isEmpty() ? Integer.valueOf(dblist.get(0)) :0);

        result.setApiItems(0);
        boolean numAssortmentItems = false;
        if(response.jsonPath().get(jsonPath) != null) {
            result.setApiItems(response.jsonPath().get(JSON_PATH_COUNT));
            result.setCount(result.getDbItems() == result.getApiItems());
        }else{ System.out.println(" [Error] Records retrieved from API: " + result.getApiItems() + "   Records retrieved from DB: " + result.getDbItems());}

        return result;
    }

    public Result compareApiListVsDBList(Response response,String jsonPath,String query, List<Object> params,String column){
        Result result = new Result(); result.setListMatches(false);

        if(response.jsonPath().get(jsonPath) != null) {
            result.setApiList(response.jsonPath().get(jsonPath));
            result.setDbList(db.DBSelectQueryObjectParams(query, params, column));

            if((result.getApiList() ==null || result.getDbList() == null) || (result.getApiList().isEmpty() || result.getApiList().isEmpty()) )
                return result;

            result.setListMatches(compareAndPrint2Lists("\nList retrieved from API: ",result.getApiList(),"List retrieved from DB:  ",result.getDbList()));
            System.out.println("Boolean " + result.isListMatches());
            return result;
        }else{
            System.out.println(" [Error] Expected Assortment Items not found, instead getAssortmentItems api retrieved:  " + response.jsonPath().get().toString());
            return result;
        }

    }


    public Integer getRandomNumber(int bound){
        Random rand = new Random();
        return rand.nextInt(bound);
    }

    public String formatMultipleUpcs(List listUpcs){
        StringBuilder upcs = new StringBuilder();

        if(listUpcs == null || listUpcs.isEmpty())
            return upcs.toString();

        int i=0;
        while(i<listUpcs.size()){
            upcs.append(listUpcs.get(i).toString());
            if(i<listUpcs.size()-1)upcs.append(",");
            i++;
        }
        return upcs.toString();
    }

    public List<Integer> getRandomAssortments(int sizeList){
        List<Integer> assortments = new ArrayList<Integer>();
        List<Integer>  listAssortments = db.returnIntegerDBSelectQueryObjectParams(SELECT_ASSORTMENTS_ID, Arrays.asList(100),ID);

        if(listAssortments == null || listAssortments.isEmpty())
            return assortments;

        int i=0;
        while(i<sizeList){
            assortments.add(listAssortments.get( getRandomNumber(listAssortments.size())));
            i++;
        }
        return assortments;
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


    public String formatMultipleElements(List list){
        StringBuilder elements = new StringBuilder();

        if(list == null || list.isEmpty())
            return elements.toString();

        int i=0;
        while(i<list.size()){
            elements.append(list.get(i).toString());
            if(i<list.size()-1)elements.append(",");
            i++;
        }
        return elements.toString();
    }




}
