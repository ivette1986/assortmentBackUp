package com.kroger.assortment.api.assortmentmgmtcore;

import com.kroger.assortment.InitAssortment;
import com.kroger.assortment.model.assortmentmgmtcore.AssortmentItemStatus;
import com.kroger.assortment.model.assortmentmgmtcore.Location;
import com.kroger.assortment.model.assortmentmgmtcore.StorePickStatus;
import com.kroger.assortment.testUtilities.DBUtils;
import com.org.yaapita.YaapitaReport;
import com.org.yaapita.libapiresthelper.RestResponse;
import com.org.yaapita.libapiresthelper.builder.RequestBuilder;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.formula.functions.T;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.collections.CollectionUtils;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kroger.assortment.testUtilities.Constants.*;
import static com.kroger.assortment.testUtilities.ConstantsDB.*;
import static io.restassured.RestAssured.given;

public class GetCatalogs extends InitAssortment {
    protected static  YaapitaReport report;
    @BeforeClass
    public void classSetup() throws Exception {
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        report = new YaapitaReport( this.getClass().getSimpleName() + "_"+ System.getProperty("env")+ formatter.format(date));
        RestAssured.useRelaxedHTTPSValidation();
    }
    /**
     * This method will request all catalogs without params and
     * will validate the status code retrieved is equal to 200 ok
     */
    @Test
    public void validateGetCatalogs_TC001() {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetCatalogs_TC001 ");
        //This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/catalogs
        //will fetch all catalog items: locations, item_status and store_pick_status
        //will validate the status code is 200 OK
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_CATALOGS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                when().get(url).then().contentType(ContentType.JSON).extract().response();
        System.out.println("[EndPoint] " + url);
        if (Objects.equals(restResponse.getStatusCode(), HttpStatus.SC_OK)){
            report.putData("Comment(s)",
                    "Assortment management core catalogs were fetched successfully, Current code: "+ restResponse.getStatusCode() + " - Expected code: " + HttpStatus.SC_OK );
            Assert.assertEquals(restResponse.getStatusCode(), HttpStatus.SC_OK, "Current Status: " + restResponse.getStatusCode() + "Expected Status: " + HttpStatus.SC_OK);
        }else{
            report.putData("Comment(s)",
                    "Assortment management core couldn't fetch the catalogs, Current code: "+ restResponse.getStatusCode() + " - Expected code: " + HttpStatus.SC_OK );
            Assert.fail(" [Error] Result Mismatch\n" +
                    "Current Status: " + restResponse.getStatusCode() + " Expected Status: " + HttpStatus.SC_OK);
        }
        report.setTestCase("getCatalogs [ no parameters ]");
        System.out.println("[Correct] Assortment management core catalogs were fetched successfully, Current code: "+ restResponse.getStatusCode() + " - Expected code: " +  HttpStatus.SC_OK);
        System.out.println("============================================================================================");
    }
    /**
     * This method will request all catalogs without params and
     * will compare all catalogs retreived by the API against all existing catalogs in DB
     */
    @Test
    public void validateGetCatalogs_vs_DBCatalogs_TC002() throws SQLException {
        System.out.println("\n\n============================================================================================");
        System.out.println(" Init validateGetCatalogs_vs_DBCatalogs_TC002 ");
        //This method will validate the endpoint https://assortment-manager-core.amtapp-test.kpsazc.dgtl.kroger.com/catalogs
        //will fetch all catalog items: locations, item_status and store_pick_status
        //will validate the catalogs fetched by the api against the database
        String url = env.getString("ASSORTMENT_HOST") + env.getString("GET_CATALOGS");
        Response restResponse = given().
                headers("Authorization", token.getBearerToken()).
                when().get(url).then().contentType(ContentType.JSON).extract().response();
        System.out.println("[EndPoint] " + url);

        //Fetch catalogs from assortment management core api and from DB and compare them
        boolean itemStatus = testUtils.compareAndPrint2Lists("Printing  List from Item Status Api:  ",new ArrayList<>(restResponse.jsonPath().getList(JSON_PATH_ITEM_STATUS)),
                "Printing  List from Item Status DB:   ",dbUtils.fetchRecordSFromDB(SELECT_ALL_ITEM_STATUS,"name"));
        boolean storePickStatus = testUtils.compareAndPrint2Lists("Printing  List from Pick Status Api:  ", new ArrayList<>(restResponse.jsonPath().getList(JSON_PATH_STORE_PICK_STATUS)),
                "Printing  List from Pick Status DB:   ",dbUtils.fetchRecordSFromDB(SELECT_ALL_STORE_PICK_STATUS,"name"));
        boolean fcLocations = compare2ListsLocations(new ArrayList<Location>(restResponse.jsonPath().getList(JSON_PATH_LOCATIONS,Location.class)),
                new ArrayList<Location>(fetchLocationsCatalog(SELECT_ALL_LOCATIONS_CATALOGS)));

        System.out.println(itemStatus?"\n\n[Correct] Assortment Item Status fetch from Assortment Management Core match the Assortment Items in the DataBase":
                "[Error] Assortment Item Status fetch from Assortment Management Core  are not the same than the DataBase");
        System.out.println(storePickStatus?"[Correct] Assortment Store Pick Status fetch from Assortment Management Core match the Store Pick Status in the DataBase":
                "[Error] Store Pick Status fetch from Assortment Management Core  are not the same than the DataBase");
        System.out.println(fcLocations?"[Correct] Assortment Locations  fetch from Assortment Management Core match the Assortment Locations in the DataBase":
                "[Error] Locations fetch from Assortment Management Core  are not the same than the DataBase");
        Assert.assertTrue(itemStatus,"[Error] Assortment Item Status fetch from Assortment Management Core  are not the same than the DataBase");
        Assert.assertTrue(storePickStatus, " [Error] Store Pick Status fetch from Assortment Management Core  are not the same than the DataBase");
        Assert.assertTrue(fcLocations, "[Error] Locations fetch from Assortment Management Core  are not the same than the DataBase");
        System.out.println("============================================================================================");
    }
    public Boolean compare2ListsLocations(List<Location> list1, List<Location> list2){
        if((list1.isEmpty() || list2.isEmpty()) || (list1.size() != list2.size()))
            return false;
        for(int i=0; i<list1.size(); i++){
            System.out.println( "\nPrinting Location Catalog from API: " + list1.get(i).getLocationId() + ", " + list1.get(i).getAssortmentId() + ", " + list1.get(i).getLocationDescription() + ", " + list1.get(i).getAssortmentName());
            System.out.println( "Printing Location Catalog from DB:  " +list2.get(i).getLocationId() + ", " + list2.get(i).getAssortmentId() + ", " + list2.get(i).getLocationDescription() + ", " + list2.get(i).getAssortmentName());
        }
        return Objects.equals(list1,list2);
    }
    public List<Location> fetchLocationsCatalog(String query)
    {
        List<Location> list = new ArrayList<>();
        try {
            // String selectAllItemStatusQuery = dbQueries.getString("SELECT_ALL_ITEM_STATUS");
            ResultSet rs = dbUtils.DBSelectQuery(query, dbQueries.getString("dbDriver"), dbQueries.getString("urlDB"), dbQueries.getString("userNameDB"),
                    dbQueries.getString("passwordDB"));
            if(rs == null)return list;
            Location location;
            while ( rs.next()){
                location = new Location();
                location.setLocationId(rs.getString(LOCATION_ID));
                location.setLocationDescription(rs.getString(LOCATION_DESCRIPTION));
                location.setAssortmentId(rs.getString(LOCATION_ASSORTMENT_ID));
                location.setAssortmentName(rs.getString(LOCATION_ASSORTMENT_NAME));
                location.setLocationCode(rs.getString(LOCATION_CODE));
                list.add(location);
            }
        }catch(SQLException e){System.out.println("Error: Data Base error: "+ e.getMessage());}
        return list;
    }
    @AfterClass(alwaysRun = true)
    private void report()throws IOException {
        report.setTest("assortmentManagementCore.getCatalogs");
    }
}