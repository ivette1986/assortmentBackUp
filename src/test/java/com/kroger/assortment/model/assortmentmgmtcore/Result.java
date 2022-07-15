package com.kroger.assortment.model.assortmentmgmtcore;

import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Result {

    //RestAssure vars
    private Response restResponse;
    private String jsonPath;
    private Map<String, String> dataMap;
    private int expectedStatusCode;
    private int currentStatusCode;



    //DB vars
    private String query;
    private String param;
    private List<String> params = new ArrayList<>();
    private String column;
    private List<String> columns = new ArrayList<>();
    private List<Map<String,Object>> listDuplicated = new ArrayList<Map<String,Object>>();
    private List<Map<String,Object>> listUnassigned = new ArrayList<Map<String,Object>>();

    //General vars
    private boolean count;
    private boolean listMatches;
    private int apiItems;
    private int dbItems;
    private List<String> apiList;
    private List<String> dbList;
    private boolean assignedCorrectly;

    public boolean isAssignedCorrectly() {
        return assignedCorrectly;
    }

    public void setAssignedCorrectly(boolean assignedCorrectly) {
        this.assignedCorrectly = assignedCorrectly;
    }

    public Response getRestResponse() {
        return restResponse;
    }

    public void setRestResponse(Response restResponse) {
        this.restResponse = restResponse;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(int expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public int getCurrentStatusCode() {
        return currentStatusCode;
    }

    public void setCurrentStatusCode(int currentStatusCode) {
        this.currentStatusCode = currentStatusCode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    public int getApiItems() {
        return apiItems;
    }

    public boolean isListMatches() {
        return listMatches;
    }

    public void setListMatches(boolean listMatches) {
        this.listMatches = listMatches;
    }

    public void setApiItems(int apiItems) {
        this.apiItems = apiItems;
    }

    public int getDbItems() {
        return dbItems;
    }

    public void setDbItems(int dbItems) {
        this.dbItems = dbItems;
    }


    public List<String> getApiList() {
        return apiList;
    }

    public void setApiList(List<String> apiList) {
        this.apiList = apiList;
    }

    public List<String> getDbList() {
        return dbList;
    }

    public void setDbList(List<String> dbList) {
        this.dbList = dbList;
    }

    public List<Map<String, Object>> getListDuplicated() {
        return listDuplicated;
    }

    public void setListDuplicated(List<Map<String, Object>> listDuplicated) {
        this.listDuplicated = listDuplicated;
    }

    public List<Map<String, Object>> getListUnassigned() {
        return listUnassigned;
    }

    public void setListUnassigned(List<Map<String, Object>> listUnassigned) {
        this.listUnassigned = listUnassigned;
    }
}
