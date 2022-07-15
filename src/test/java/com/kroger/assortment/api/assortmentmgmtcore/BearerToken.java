package com.kroger.assortment.api.assortmentmgmtcore;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;

public class BearerToken {
    public  Response restResponse;

    @BeforeClass
    public void testSetup() throws Exception {
        RestAssured.useRelaxedHTTPSValidation();
    }

    public  String getBearerToken(){

        String encodedText = System.getProperty("LOGIN_TENANT_ID");
        String bearerToken = "Bearer " + bearerToken(encodedText,System.getProperty("LOGIN_GET_BEARER_TOKEN_URL"));
        return bearerToken;
    }


    public  String bearerToken(String encodedText, String bearerTokenURL){
        String basicAuth="Bearer " + encodedText;

        restResponse=given().
                headers("Authorization",basicAuth).
                contentType("application/x-www-form-urlencoded").
                formParam("grant_type","client_credentials").
                formParam("client_id",System.getProperty("LOGIN_CLIENT_ID")).
                formParam("client_secret",System.getProperty("LOGIN_CLIENT_SECRET")).
                formParam("resource",System.getProperty("LOGIN_RESOURCE")).
                pathParam("tenant_id",System.getProperty("LOGIN_TENANT_ID")).
                when().post(bearerTokenURL);

        return restResponse.getBody().jsonPath().getString("access_token");

    }


}
