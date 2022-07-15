package com.kroger.assortment.model.assortmentmgmtcore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssortmentItem {

    private int assortmentItemID;
    private String upc14;
    private String itemStatus;
    private String oldItemStatus;
    private Integer itemStatusValue;
    private String statusReason;
    private String storePickStatus;
    private String oldStorePickStatus;
    private Integer storePickStatusValue;
    private String storePickDate;
    private String oldStorePickDate;
    private int venusSourceId;
    private String venusNotes;
    private String oldVenusNotes;
    private String notes1;
    private String oldNotes1;
    private String notes2;
    private String oldNotes2;
    private String supplierSiteCode;
    private String oldSupplierSiteCode;
    private String supplierSiteName;
    private String oldSupplierSiteName;
    private String source;
    private String oldSource;
    private String replacementSource;
    private String oldReplacementSource;
    private String inStoreUpc;
    private String oldInStoreUpc;
    private String orderableCaseUpc;
    private int orderableSourceWhs;
    private String keywords;
    private int ospBoh;
    private String lastBohCheckTime;
    private String ospOcadoTradeId;
    private String createdAt;
    private String updatedAt;
    private String assortment;
    private String locationDescription;
    private String level1Upc;
    private String level2Upc;
    private String caseUpc;
    private String oldCaseUpc;
    private String pack;
    private String upc13;
    private String SkuId;
    private String ProductId;
    private String size;
    private String hazardous;
    private String pidDescription;
    private String primaryDeptName;
    private String recapDeptName;
    private String deptName;
    private String commodityName;
    private String subCommodityName;
    private Integer assortmentId;



    public int getAssortmentItemID() {
        return assortmentItemID;
    }

    public void setAssortmentItemID(int assortmentItemID) {
        this.assortmentItemID = assortmentItemID;
    }

    public String getUpc14() {
        return upc14;
    }

    public void setUpc14(String upc14) {
        this.upc14 = upc14;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public String getStorePickStatus() {
        return storePickStatus;
    }

    public void setStorePickStatus(String storePickStatus) {
        this.storePickStatus = storePickStatus;
    }

    public String getStorePickDate() {
        return storePickDate;
    }

    public void setStorePickDate(String storePickDate) {
        this.storePickDate = storePickDate;
    }

    public int getVenusSourceId() {
        return venusSourceId;
    }

    public void setVenusSourceId(int venusSourceId) {
        this.venusSourceId = venusSourceId;
    }

    public String getVenusNotes() {
        return venusNotes;
    }

    public void setVenusNotes(String venusNotes) {
        this.venusNotes = venusNotes;
    }

    public String getNotes1() {
        return notes1;
    }

    public void setNotes1(String notes1) {
        this.notes1 = notes1;
    }

    public String getNotes2() {
        return notes2;
    }

    public void setNotes2(String notes2) {
        this.notes2 = notes2;
    }

    public String getSupplierSiteCode() {
        return supplierSiteCode;
    }

    public void setSupplierSiteCode(String supplierSiteCode) {
        this.supplierSiteCode = supplierSiteCode;
    }

    public String getSupplierSiteName() {
        return supplierSiteName;
    }

    public void setSupplierSiteName(String supplierSiteName) {
        this.supplierSiteName = supplierSiteName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReplacementSource() {
        return replacementSource;
    }

    public void setReplacementSource(String replacementSource) {
        this.replacementSource = replacementSource;
    }

    public String getInStoreUpc() {
        return inStoreUpc;
    }

    public void setInStoreUpc(String inStoreUpc) {
        this.inStoreUpc = inStoreUpc;
    }

    public String getOrderableCaseUpc() {
        return orderableCaseUpc;
    }

    public void setOrderableCaseUpc(String orderableCaseUpc) {
        this.orderableCaseUpc = orderableCaseUpc;
    }

    public int getOrderableSourceWhs() {
        return orderableSourceWhs;
    }

    public void setOrderableSourceWhs(int orderableSourceWhs) {
        this.orderableSourceWhs = orderableSourceWhs;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getOspBoh() {
        return ospBoh;
    }

    public void setOspBoh(int ospBoh) {
        this.ospBoh = ospBoh;
    }

    public String getLastBohCheckTime() {
        return lastBohCheckTime;
    }

    public void setLastBohCheckTime(String lastBohCheckTime) {
        this.lastBohCheckTime = lastBohCheckTime;
    }

    public String getOspOcadoTradeId() {
        return ospOcadoTradeId;
    }

    public void setOspOcadoTradeId(String ospOcadoTradeId) {
        this.ospOcadoTradeId = ospOcadoTradeId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAssortment() {
        return assortment;
    }

    public void setAssortment(String assortment) {
        this.assortment = assortment;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getLevel1Upc() {
        return level1Upc;
    }

    public void setLevel1Upc(String level1Upc) {
        this.level1Upc = level1Upc;
    }

    public String getLevel2Upc() {
        return level2Upc;
    }

    public void setLevel2Upc(String level2Upc) {
        this.level2Upc = level2Upc;
    }

    public String getCaseUpc() {
        return caseUpc;
    }

    public void setCaseUpc(String caseUpc) {
        this.caseUpc = caseUpc;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getUpc13() {
        return upc13;
    }

    public void setUpc13(String upc13) {
        this.upc13 = upc13;
    }

    public String getSkuId() {
        return SkuId;
    }

    public void setSkuId(String skuId) {
        SkuId = skuId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getHazardous() {
        return hazardous;
    }

    public void setHazardous(String hazardous) {
        this.hazardous = hazardous;
    }

    public String getPidDescription() {
        return pidDescription;
    }

    public void setPidDescription(String pidDescription) {
        this.pidDescription = pidDescription;
    }

    public String getPrimaryDeptName() {
        return primaryDeptName;
    }

    public void setPrimaryDeptName(String primaryDeptName) {
        this.primaryDeptName = primaryDeptName;
    }

    public String getRecapDeptName() {
        return recapDeptName;
    }

    public void setRecapDeptName(String recapDeptName) {
        this.recapDeptName = recapDeptName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getSubCommodityName() {
        return subCommodityName;
    }

    public void setSubCommodityName(String subCommodityName) {
        this.subCommodityName = subCommodityName;
    }


    public Integer getItemStatusValue() {
        return itemStatusValue;
    }

    public void setItemStatusValue(Integer itemStatusValue) {
        this.itemStatusValue = itemStatusValue;
    }

    public Integer getStorePickStatusValue() {
        return storePickStatusValue;
    }

    public void setStorePickStatusValue(Integer storePickStatusValue) {
        this.storePickStatusValue = storePickStatusValue;
    }

    public Integer getAssortmentId() {
        return assortmentId;
    }

    public void setAssortmentId(Integer assortmentId) {
        this.assortmentId = assortmentId;
    }

    public String getOldItemStatus() {
        return oldItemStatus;
    }

    public void setOldItemStatus(String oldItemStatus) {
        this.oldItemStatus = oldItemStatus;
    }

    public String getOldStorePickStatus() {
        return oldStorePickStatus;
    }

    public void setOldStorePickStatus(String oldStorePickStatus) {
        this.oldStorePickStatus = oldStorePickStatus;
    }

    public String getOldStorePickDate() {
        return oldStorePickDate;
    }

    public void setOldStorePickDate(String oldStorePickDate) {
        this.oldStorePickDate = oldStorePickDate;
    }

    public String getOldVenusNotes() {
        return oldVenusNotes;
    }

    public void setOldVenusNotes(String oldVenusNotes) {
        this.oldVenusNotes = oldVenusNotes;
    }

    public String getOldNotes1() {
        return oldNotes1;
    }

    public void setOldNotes1(String oldNotes1) {
        this.oldNotes1 = oldNotes1;
    }

    public String getOldNotes2() {
        return oldNotes2;
    }

    public void setOldNotes2(String oldNotes2) {
        this.oldNotes2 = oldNotes2;
    }

    public String getOldInStoreUpc() {
        return oldInStoreUpc;
    }

    public void setOldInStoreUpc(String oldInStoreUpc) {
        this.oldInStoreUpc = oldInStoreUpc;
    }


    public String getOldSupplierSiteCode() {
        return oldSupplierSiteCode;
    }

    public void setOldSupplierSiteCode(String oldSupplierSiteCode) {
        this.oldSupplierSiteCode = oldSupplierSiteCode;
    }

    public String getOldSupplierSiteName() {
        return oldSupplierSiteName;
    }

    public void setOldSupplierSiteName(String oldSupplierSiteName) {
        this.oldSupplierSiteName = oldSupplierSiteName;
    }

    public String getOldSource() {
        return oldSource;
    }

    public void setOldSource(String oldSource) {
        this.oldSource = oldSource;
    }

    public String getOldReplacementSource() {
        return oldReplacementSource;
    }

    public void setOldReplacementSource(String oldReplacementSource) {
        this.oldReplacementSource = oldReplacementSource;
    }

    public String getOldCaseUpc() {
        return oldCaseUpc;
    }

    public void setOldCaseUpc(String oldCaseUpc) {
        this.oldCaseUpc = oldCaseUpc;
    }
}
