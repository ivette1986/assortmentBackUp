package com.kroger.assortment.model.assortmentmgmtcore;

import java.util.Objects;

public class Location {

    private String locationId;
    private String locationDescription;
    private String assortmentId;
    private String assortmentName;
    private String locationCode;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return locationId.equals(location.locationId) && locationDescription.equals(location.locationDescription) && assortmentId.equals(location.assortmentId) && assortmentName.equals(location.assortmentName) && locationCode.equals(location.locationCode);

    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, locationDescription, assortmentId ,assortmentName,locationCode);
    }


   //Setter y Getter
    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getAssortmentId() {
        return assortmentId;
    }

    public void setAssortmentId(String assortmentId) {
        this.assortmentId = assortmentId;
    }

    public String getAssortmentName() {
        return assortmentName;
    }

    public void setAssortmentName(String assortmentName) {
        this.assortmentName = assortmentName;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }
}
