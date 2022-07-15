package com.kroger.assortment.testUtilities;

public class ConstantsDB {

    //DB Properties
    public static final String DB_DRIVER                                 = "dbDriver";
    public static final String DB_URL                                    = "urlDB";
    public static final String DB_USERNAME                               = "userNameDB";
    public static final String DB_PSSWD                                  = "passwordDB";



    //SQL Queries
    public static final String SELECT_ALL_ITEM_STATUS                       = "SELECT NAME FROM public.item_status ORDER BY ID ASC";
    public static final String SELECT_ALL_STORE_PICK_STATUS                 = "SELECT NAME FROM public.store_pick_status ORDER BY id ASC";
    public static final String SELECT_ALL_LOCATIONS_CATALOGS                = "select l.id as locationId, l.description as locationDescription, a.id as assortmentId, a.name as assortmentName, l.code as code from public.assortments as a inner join public.locations as l ON a.id = l.id ORDER BY locationId";
    public static final String SELECT_COUNT_ASSORTMENT_ITEMS_BY_FC          = "select count(*) from public.assortment_items as ai inner join public.assortments as a ON ai.assortment_id = a.id  where a.name like ?";
    public static final String SELECT_ID_ASSORTMENT_ITEM_FILTER_BY_FC       = "select ai.id from public.assortment_items as ai inner join public.assortments as a ON ai.assortment_id = a.id  where a.name like ? ORDER BY ai.id ASC";
    public static final String SELECT_COUNT_ASSORTMENT_ITEMS_BY_EMPTY_FC    = "select count(*) from public.assortment_items as ai inner join public.assortments as a ON ai.assortment_id = a.id";
    public static final String SELECT_ID_ASSORTMENT_ITEM_FILTER_BY_EMPTY_FC = "select ai.id from public.assortment_items as ai inner join public.assortments as a ON ai.assortment_id = a.id ORDER BY ai.id ASC";
    public static final String SELECT_ID_ASSORTMENT_ITEM_FILTER_BY_UPC_13   = "SELECT id FROM public.assortment_items  WHERE upc13 like ? ORDER BY id ASC";
    public static final String SELECT_COUNT_ASSORTMENT_ITEMS_BY_UPC_13      = "SELECT COUNT(*) FROM public.assortment_items  WHERE upc13 LIKE ?";
    public static final String SELECT_COUNT_ASSORTMENT_ITEMS_BY_UPC13_FC    = "SELECT COUNT(*) FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 INNER JOIN public.assortments as a ON  ai.assortment_id = a.id WHERE i.upc13 like ? and  a.name like ? ";
    public static final String SELECT_RANDOM_UPC13                          = "SELECT upc13 FROM  public.assortment_items  LIMIT ?";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY                = "SELECT  i.upc13 FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 WHERE i.upc13 in(?,?,?,?,?) ORDER BY i.upc13 ASC, ai.id ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_PID_DESCRIPTION    = "SELECT  i.pid_description FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 WHERE i.upc13 in(?,?,?,?,?) ORDER BY pid_description ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_CASEUPC         = "SELECT  i.upc13,v.case_upc FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 Inner Join public.venus_source as v ON ai.venus_source_id=v.id WHERE i.upc13 in(?,?,?,?,?) ORDER BY v.case_upc ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_SIZE            = "SELECT i.size FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 WHERE i.upc13 in(?,?,?,?,?) ORDER BY i.size ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_SOURCE          = "SELECT  ai.source FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 WHERE i.upc13 in(?,?,?,?,?) ORDER BY ai.source ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_REPLACEMENT     = "SELECT  ai.replacement_source FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 WHERE i.upc13 in(?,?,?,?,?) ORDER BY ai.replacement_source ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_SUPPLIER_SITE_NAME     = "SELECT  ai.supplier_site_name FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 WHERE i.upc13 in(?,?,?,?,?) ORDER BY ai.supplier_site_name ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_SUPPLIER_SITE_CODE     = "SELECT  ai.supplier_site_code FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 WHERE i.upc13 in(?,?,?,?,?) ORDER BY ai.supplier_site_code ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_ASSORTMENT       = "SELECT  a.name FROM  public.assortment_items AS ai Inner Join public.items AS i ON ai.upc14 = i.upc14 Inner Join public.assortments AS a ON ai.assortment_id = a.id WHERE i.upc13 in(?,?,?,?,?) ORDER BY a.name ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_ITEM_STATUS      = "SELECT  status.name FROM  public.assortment_items AS ai Inner Join public.items AS i ON ai.upc14 = i.upc14 Inner Join public.item_status AS status ON ai.status = status.id WHERE i.upc13 in(?,?,?,?,?) ORDER BY status.name ASC";
    public static final String SELECT_ASSORTMENT_ITEM_SORTBY_PICK_STATUS      = "SELECT  status.name FROM  public.assortment_items AS ai Inner Join public.items AS i ON ai.upc14 = i.upc14 Inner Join public.store_pick_status AS status ON ai.store_pick_status = status.id WHERE i.upc13 in(?,?,?,?,?) ORDER BY status.name ASC";
    public static final String SELECT_COUNT_ALL_ASSORTMENT_ITEMS              = "select count(*) from public.assortment_items ";
    public static final String SELECT_ASSORTMENT_ITEM_OFFSET                  = "SELECT  id FROM  public.assortment_items  ORDER BY upc13 ASC, id ASC LIMIT ?";
    public static final String SELECT_UNASSIGNED_ITEMS                        = "SELECT pr.upc13 FROM public.products AS pr LEFT JOIN public.assortment_items as ai ON ai.upc13 = pr.upc13 WHERE ai.upc13 IS NULL LIMIT ?";
    public static final String SELECT_ASSORTMENTS_ID                          = "SELECT id FROM public.assortments LIMIT ?";
    public static final String SELECT_COUNT_ASSORTMENTS_ITEMS_BY_ASSORTMENT   = "SELECT COUNT(*) FROM public.assortment_items WHERE assortment_id = ? ";
    public static final String SELECT_ASSORTMENT_ITEM_UPC13_AND_ASSORTMENT_ID = "SELECT upc13 FROM public.assortment_items WHERE upc13 like ? AND assortment_id = ? ";
    public static final String SELECT_ASSORTMENTS_ITEMS_IDS_BY_ITEMSTATUS     = "SELECT id FROM public.assortment_items WHERE status=? limit ?  ";
    public static final String SELECT_ASSORTMENTS_ITEMS_IDS_BY_STORE_PICK_STATUS     = "SELECT id FROM public.assortment_items WHERE store_pick_status=? limit ?  ";
    public static final String SELECT_ASSORT_ITEMS_IDS_BY_DIFF_ITEMSTATUS_AND_STORE_PICK     = "SELECT id FROM public.assortment_items WHERE status !=? AND store_pick_status =? limit ?  ";
    public static final String SELECT_RANDOM_ASSORTMENT                       = "SELECT name FROM public.assortments LIMIT ?";
    public static final String SELECT_RANDOM_ASSORTMENT_ITEM_ID              = "SELECT id FROM public.assortment_items LIMIT ? ";
    public static final String SELECT_RANDOM_UPC14                           = "SELECT upc14 FROM  public.assortment_items  LIMIT ?";
    public static final String SELECT_UPC_ASSORTMENTID_BY_UPC13              = "SELECT id, upc13, assortment_id FROM public.assortment_items WHERE upc13 in (?,?,?)";
    public static final String SELECT_ASSORTMENT_ITEM_ID_BY_ASSORTITEMID     = "SELECT id FROM  public.assortment_items  WHERE id = ?";
    public static final String SELECT_CHANGE                                 = "SELECT id,assortment_item_id,column_name,old_value,new_value,date FROM CHANGES WHERE assortment_item_id = ? AND column_name = ? AND old_value= ? AND new_value = ? AND date>CURRENT_DATE ORDER BY date DESC";
    public static final String SELECT_CHANGE_PICKSTATUSDATE                  = "SELECT id,assortment_item_id,column_name,old_value,new_value,date FROM CHANGES WHERE assortment_item_id = ? AND column_name = ? AND old_value like ? AND new_value like ? AND date>CURRENT_DATE ORDER BY date DESC";
    public static final String SELECT_CHANGE_BY_ASSORTMENTID                 = "SELECT id,assortment_item_id,column_name,old_value,new_value,date FROM CHANGES WHERE assortment_item_id = ? ORDER BY date DESC";
    public static final String SELECT_RANDOM_ASSORTMENT_NAME                 = "SELECT name FROM public.assortments LIMIT 100";
    public static final String SELECT_RANDOM_ASSORTMENT_ID                   = "SELECT id FROM public.assortments LIMIT 100";
    public static final String SELECT_RANDOM_ASSORTMENTS_ID                   = "SELECT id FROM public.assortments LIMIT ?";

    public static final String SELECT_ID_ASSORTMENT_ITEM_BY_ASSORTMENT_ID    = "SELECT id FROM  public.assortment_items  WHERE assortment_id = ?";
    public static final String SELECT_COUNT_ASSORTMENT_ITEMS_BY_UPC13        = "SELECT COUNT(*) FROM public.assortment_items WHERE upc13 in (?,?,?)";
    public static final String SELECT_ID_ASSORTMENT_ITEMS_BY_3_UPC13         = "SELECT id FROM public.assortment_items WHERE  upc13 like ? OR upc13 like ? OR upc13 like ?";
    public static final String SELECT_COUNT_ASSORTMENT_ITEMS_BY_3_UPC13      = "SELECT COUNT(*) FROM public.assortment_items WHERE upc13 like ? OR upc13 like ? OR upc13 like ?";
    public static final String SELECT_RANDOM_ITEM_STATUS_ID                  = "SELECT id FROM public.item_status LIMIT 100";
    public static final String SELECT_COUNT_ASSORTMENTS_ITEMS_BY_STATUS      = "SELECT COUNT(*) FROM public.assortment_items WHERE status = ? ";
    public static final String SELECT_RANDOM_ITEM_STORE_PICK_STATUS_ID       = "SELECT id FROM public.store_pick_status LIMIT 100";
    public static final String SELECT_COUNT_ASSORTMENTS_ITEMS_BY_STORE_PICK_STATUS      = "SELECT COUNT(*) FROM public.assortment_items WHERE store_pick_status = ? ";
    public static final String SELECT_RANDOM_LOCATION_CODE                   = "SELECT code FROM public.locations LIMIT 100";
    public static final String SELECT_COUNT_ASSORTMENTS_ITEMS_BY_LOCATION_CODE      = "SELECT COUNT(*) FROM public.assortment_items AS ai INNER JOIN public.assortments AS a ON ai.assortment_id=a.id INNER JOIN public.locations AS l ON a.location_id = l.id WHERE l.code = ? ";
    public static final String SELECT_RANDOM_IMFTYPE                         = "SELECT id FROM public.imf_type LIMIT 100";
    public static final String SELECT_COUNT_ASSORTMENTS_ITEMS_BY_IMFTYPE     = "SELECT count(distinct assortment_item_id) FROM public.imf_source   WHERE type_id = ? ";
    public static final String SELECT_RANDOM_IMFSTATUS                       = "SELECT id FROM public.imf_status LIMIT 100";
    public static final String SELECT_COUNT_ASSORTMENTS_ITEMS_BY_IMFSTATUS   = "SELECT count(distinct assortment_item_id) FROM public.imf_source   WHERE status = ? ";
    public static final String SELECT_COUNT_ASSORTMENTS_ITEMS_BY_DESCRIPTION   = "SELECT count(distinct ai.id) FROM public.assortment_items AS ai INNER JOIN public.products AS p ON ai.upc13=p.upc13  WHERE LOWER(p.pid_description) LIKE LOWER( ? ) OR LOWER(p.ecomm_description) LIKE LOWER( ? ) ";
    public static final String SELECT_COUNT_PRODUCTS_BY_ASSORTMENTID           = "SELECT count(*) FROM public.assortments as ai inner join public.assortments as a ON ai.assortment_id = a.id  where a.name like ?";
    public static final String SELECT_RANDOM_PRODUCT_UPC13                     = "SELECT upc13 FROM  public.products  LIMIT ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_UPC13                  = "SELECT COUNT(*) FROM public.products WHERE upc13 like ? ";
    public static final String SELECT_PRODUCT_UPC13_BY_UPC13                   = "SELECT upc13 FROM  public.products  WHERE upc13 like ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_3_UPC13                = "SELECT COUNT(*) FROM public.products WHERE upc13 in (?,?,?)";
    public static final String SELECT_PRODUCT_UPC13_BY_3_UPC13                  = "SELECT upc13 FROM  public.products  WHERE upc13 like ? OR upc13 like ? OR upc13 like ? ";
    public static final String SELECT_COUNT_PRODUCTS_BY_3_PARTIAL_UPC13         = "SELECT COUNT(*) FROM public.products WHERE upc13 like ? OR upc13 like ? OR upc13 like ?";
    public static final String SELECT_RANDOM_PRODUCT_GTIN                      = "SELECT gtin FROM  public.products  LIMIT ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_GTIN                    = "SELECT COUNT(*) FROM public.products WHERE gtin like ? ";
    public static final String SELECT_PRODUCT_GTIN_BY_GTIN                      = "SELECT gtin FROM  public.products  WHERE gtin like ?";
    public static final String SELECT_RANDOM_SKUID_PRODUCT_BY_SKUID             = "select sku_id from public.products where sku_id is not null and sku_id <> '' limit ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_SKUID                   = "SELECT COUNT(*) FROM public.products WHERE sku_id like ? ";
    public static final String SELECT_PRODUCT_SKUID_BY_SKUID                    = "SELECT sku_id FROM  public.products  WHERE sku_id = ?";
    public static final String SELECT_RANDOM_PRODUCTID_PRODUCT                  = "select product_id from public.products where product_id is not null and product_id <> '' limit ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_PRODUCTID               = "SELECT COUNT(*) FROM public.products WHERE product_id like ? ";
    public static final String SELECT_PRODUCT_PRODUCTID_BY_PRODUCTID            = "SELECT product_id FROM  public.products  WHERE product_id = ?";
    public static final String SELECT_RANDOM_PRODUCT_BY_PRIMARY_DEPT_CODE       = "select primary_dept_code from public.products where primary_dept_code is not null and primary_dept_code <> '' limit ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_PRIMARY_DEPT_CODE       = "SELECT COUNT(*) FROM public.products WHERE primary_dept_code = ? ";
    public static final String SELECT_RANDOM_PRODUCT_BY_RECAP_DEPT_CODE         = "select recap_dept_code from public.products where recap_dept_code is not null and recap_dept_code <> '' limit ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_RECAP_DEPT_CODE         = "SELECT COUNT(*) FROM public.products WHERE recap_dept_code = ?  ";
    public static final String SELECT_RANDOM_PRODUCT_BY_DEPT_CODE               = "select dept_code from public.products where dept_code is not null and recap_dept_code <> '' limit ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_DEPT_CODE               = "SELECT COUNT(*) FROM public.products WHERE dept_code = ?  ";
    public static final String SELECT_RANDOM_PRODUCT_BY_COMMODITY_CODE          = "select commodity_code from public.products where commodity_code is not null and commodity_code <> '' limit ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_COMMODITY_CODE          = "SELECT COUNT(*) FROM public.products WHERE commodity_code = ?  ";
    public static final String SELECT_RANDOM_PRODUCT_BY_SUB_COMMODITY_CODE      = "select sub_commodity_code from public.products where sub_commodity_code is not null and sub_commodity_code <> '' limit ?";
    public static final String SELECT_COUNT_PRODUCTS_BY_SUB_COMMODITY_CODE      = "SELECT COUNT(*) FROM public.products WHERE sub_commodity_code = ?  ";
    public static final String SELECT_COUNT_PRODUCT_BY_PID_DESCRIPTION          = "SELECT count(distinct upc13) FROM  public.products  WHERE LOWER(pid_description) LIKE LOWER( ? ) OR LOWER(ecomm_description) LIKE LOWER( ? ) ";
    public static final String SELECT_PRODUCTS_ITEM_OFFSET                  = "SELECT  ai.id FROM  public.assortment_items AS ai Inner Join public.items as i ON ai.upc14 = i.upc14 ORDER BY i.upc13 ASC, ai.id ASC LIMIT ?";




















    //DB Columns
    //FC Locations DB Column names
    public static final String LOCATION_ID                                   = "locationId";
    public static final String LOCATION_DESCRIPTION                          = "locationDescription";
    public static final String LOCATION_ASSORTMENT_ID                        = "assortmentId";
    public static final String LOCATION_ASSORTMENT_NAME                      = "assortmentName";
    public static final String LOCATION_CODE                                 = "code";
    public static final String UPC14                                         = "upc14";
    public static final String ID                                            = "id";
    public static final String UPC13                                         = "upc13";
    public static final String NAME                                          = "name";
    public static final String CODE                                          = "code";
    public static final String COUNT                                         = "count";
    public static final String GTIN                                          = "gtin";
    public static final String SKUID                                         = "sku_id";
    public static final String PRODUCT_ID                                    = "product_id";
    public static final String PRIMARY_DEPT_CODE                             = "primary_dept_code";
    public static final String RECAP_DEPT_CODE                               = "recap_dept_code";
    public static final String DEPT_CODE                                     = "dept_code";
    public static final String COMMODITY_CODE                                = "commodity_code";
    public static final String SUB_COMMODITY_CODE                            = "sub_commodity_code";
















}
