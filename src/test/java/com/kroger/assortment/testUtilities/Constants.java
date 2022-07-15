package com.kroger.assortment.testUtilities;

import org.apache.http.HttpStatus;

public class Constants {

    //Spreadsheet names
    public static final String INPUT_SHEET_NAME_FUNCTIONAL                  = "Functional";
    public static final String INPUT_SHEET_NAME_NEGATIVE                    = "Negative";
    public static final String INPUT_SHEET_NAME_BLANK_SPACES                = "BlankSpaces";
    public static final String INPUT_SHEET_NAME_FC_AND_UPC13                = "FCandUPC13";
    public static final String INPUT_SHEET_PAGEABLE                         = "PageableFunctional";
    public static final String INPUT_SHEET_PAGEABLE_SIZE                    = "PageSizeFunctional";
    public static final String INPUT_SHEET_PAGE_OFFSET                      = "PageoOffsetFunctional";
    public static final String INPUT_SHEET_ASSORTMENT_ITEM_STATUS2          = "itemStatus2";
    public static final String INPUT_SHEET_DESCRIPTION                      = "Description";
    public static final String INPUT_SHEET_NAME_INVALID_INPUT_DATA          = "InvalidInputData";



    //Excel Columns
    public static final String EXCEL_TC_NUMBER                               = "S.No";
    public static final String EXCEL_TEST_CASE                               = "Test Case";
    public static final String EXCEL_FILTER__PARAM                           = "filterParam";
    public static final String EXCEL_ENDPOINT                                = "Endpoint";
    public static final String EXCEL_FILTER__PARAM_FC                        = "FC";
    public static final String EXCEL_FILTER__PARAM_UPC13                     = "UPC13";
    public static final String EXCEL_STATUS_CODE                             = "StatusCode";
    public static final String EXCEL_EXPECTED                                = "Expected";
    public static final String EXCEL_PAGE_SIZE                               = "paramPageSize";
    public static final String EXCEL_PARAM                                   = "param";
    public static final String EXCEL_ERROR_REASON                            = "errorReason";



    //JSON PATH
    public static final String JSON_PATH_CATALOG_ITEM_STATUS               = "data.assortmentItemStatus";
    public static final String JSON_PATH_CATALOG_PICK_STATUS               = "data.storePickStatus";
    public static final String JSON_PATH_ITEM_STATUS                        = "data.assortmentItemStatus.name";
    public static final String JSON_PATH_STORE_PICK_STATUS                  = "data.storePickStatus.name";
    public static final String JSON_PATH_LOCATIONS                          = "data.assortment";
    public static final String JSON_PATH_ASSORTMENT_ITEM_FC                 = "data.assortmentItem";
    public static final String JSON_PATH_COUNT                              = "data.count";
    public static final String JSON_PATH_ASSORTMENT_ITEM_ID                 = "data.assortmentItem.assortmentItemID";
    public static final String JSON_PATH_ERRORS_REASON                      = "errors.reason";
    public static final String JSON_PATH_ERRORS_CODE                        = "errors.code";
    public static final String JSON_PATH_UPC13                              = "data.assortmentItem.upc13";
    public static final String JSON_PATH_PID_DESCRIPTION                    = "data.assortmentItem.pidDescription";
    public static final String JSON_PATH_PID_CASEUPC                        = "data.assortmentItem.caseUpc";
    public static final String JSON_PATH_SIZE                               = "data.assortmentItem.size";
    public static final String JSON_PATH_SOURCE                             = "data.assortmentItem.source";
    public static final String JSON_PATH_REPLACEMENT_SOURCE                 = "data.assortmentItem.replacementSource";
    public static final String JSON_PATH_SUPPLIER_SITE_NAME                 = "data.assortmentItem.supplierSiteName";
    public static final String JSON_PATH_SUPPLIER_SITE_CODE                 = "data.assortmentItem.supplierSiteCode";
    public static final String JSON_PATH_ASSORTMENT                         = "data.assortmentItem.assortment";
    public static final String JSON_PATH_ASSORTMENT_ITEM_STATUS             = "data.assortmentItem.itemStatus";
    public static final String JSON_PATH_ASSORTMENT_ITEM_PICK_STATUS        = "data.assortmentItem.storePickStatus";
    public static final String JSON_PATH_UPDATED_ITEMS_COUNT                = "data.updatedItemsCount";
    public static final String JSON_PATH_NO_UPDATED_ITEMS                   = "data.notUpdatedItemList";
    public static final String JSON_PATH_ITEMS_NOT_ASSIGNED                 = "data.itemsNotAssigned";
    public static final String JSON_PATH_ITEMS_ASSIGNED                     = "data.assignations";
    public static final String JSON_PATH_UNASSIGNED_ITEMS                   = "data.unassignedItems";
    public static final String JSON_PATH_ERRORS                             = "data.errors";
    public static final String JSON_PATH_EXPORTS                            = "data.exports";
    public static final String JSON_PATH_EXPORTS_REPORTNAME                 = "data.exports.reportName";
    public static final String JSON_PATH_NOT_INCLUDED                       = "data.notIncluded";
    public static final String JSON_PATH_EMAIL_SENT                         = "data.emailSent";
    public static final String JSON_PATH_CHANGES                            = "data.assortmentItemChanges";
    public static final String JSON_PATH_CHANGES_ASSORTMENTITEMID           = "data.assortmentItemID";
    public static final String JSON_PATH_ITEMS_UPC13                        = "data.items.upc13";
    public static final String JSON_PATH_ITEMS_GTIN                         = "data.items.gtin";
    public static final String JSON_PATH_ITEMS_SKUID                        = "data.items.skuId";
    public static final String JSON_PATH_ITEMS_PRODUCT_ID                   = "data.items.productId";











    //General constants
    public static final String GET_ASSORTMENT_ITEMS_ERRORS_REASON            = "[assortment items were not found during search]";
    public static final String GET_ASSORTMENT_ITEMS_INTERNAL_SERVER_ERROR    = "[Internal Server Error]";
    public static final String GET_ASSORTMENT_ITEMS_BY_ASSORTMENTID_ERROR_REASON    = "param value id: 'test' must be integer";



    //Assortment Item Status catalog
    public static final String ASSORT_ITEM_STATUS_1_WORK_IN_PROGRESS            = "Work in Progress";
    public static final String ASSORT_ITEM_STATUS_2_SETUP_IN_PROGRESS           = "Setup in Progress";
    public static final String ASSORT_ITEM_STATUS_3_PENDING                     = "Pending";
    public static final String ASSORT_ITEM_STATUS_4_ACTIVE                      = "Active";
    public static final String ASSORT_ITEM_STATUS_5_DISCONTINUE_WHEN_OUT        = "Discontinue when Out";
    public static final String ASSORT_ITEM_STATUS_6_HISTORIC                    = "Historic";
    public static final String ASSORT_ITEM_STATUS_7_PERMANENT_STORE_PICK        = "Permanent store pick";
    public static final Integer ASSORT_ITEM_STATUS_1                            = 1;
    public static final Integer ASSORT_ITEM_STATUS_2                            = 2;
    public static final Integer ASSORT_ITEM_STATUS_3                            = 3;
    public static final Integer ASSORT_ITEM_STATUS_4                            = 4;
    public static final Integer ASSORT_ITEM_STATUS_5                            = 5;
    public static final Integer ASSORT_ITEM_STATUS_6                            = 6;
    public static final Integer ASSORT_ITEM_STATUS_7                            = 7;
    public static final String STORE_PICK_STATUS_1_NO                           = "No";
    public static final String STORE_PICK_STATUS_2_YES                          = "Yes";
    public static final String STORE_PICK_STATUS_3_PERMANENT                    = "Permanent";
    public static final Integer STORE_PICK_STATUS_1                             = 1;
    public static final Integer STORE_PICK_STATUS_2                             = 2;
    public static final Integer STORE_PICK_STATUS_3                             = 3;


    //Assortment Item filter params
    public static final String ASSORTMENT_NAME                                  = "assortmentName";
    public static final String ASSORTMENT_ID                                    = "assortmentId";
    public static final String DESCRIPTION_FILTER                               = "description.filter";
    public static final String UPCS_FILTER                                      = "upcs.filter";
    public static final String ASSORTMENT_ITEM_ID_FILTER                        = "assortmentItemId.filter";
    public static final String SORT_BY                                          = "sortBy";
    public static final String PAGE_SIZE                                        = "page.size";
    public static final String PAGE_OFFSET                                      = "page.offset";
    public static final String PAGEABLE                                         = "pageable";
    public static final String UPCS                                             = "upcs";
    public static final String ASSORTMENT_IDS                                   = "assortmentIds";
    public static final String API_ID                                           = "id";
    public static final String STATUS                                           = "status";
    public static final String STORE_PICK_STATUS                                = "storePickStatus";
    public static final String API_LOCATION_CODE                                = "locationCode";
    public static final String IMFTYPE                                          = "imfType";

    public static final String IMFSTATUS                                        = "imfStatus";
    public static final String PID_DESCRIPTION                                  = "pidDescription";
    public static final String ASSORTMENTS                                      = "assortments";
    public static final String API_GTIN                                         = "gtin";
    public static final String API_SKUID                                        = "skuId";
    public static final String API_PRODUCT_ID                                   = "productId";
    public static final String API_PRIMARY_DEPT_CODE                            = "primaryDeptCode";
    public static final String API_RECAP_DEPT_CODE                              = "recapDeptCode";
    public static final String API_DEPT_CODE                                    = "deptCode";
    public static final String API_COMMODITY_CODE                               = "commodityCode";
    public static final String API_SUB_COMMODITY_CODE                           = "subCommodityCode";











}
