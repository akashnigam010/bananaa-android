package in.bananaa.object;

import java.io.Serializable;

public class ItemFoodViewDetails implements Serializable {
    private Integer itemId;
    private Integer merchantId;
    private String restName;
    private String locality;
    private String itemName;
    private boolean isNewFoodview;

    public ItemFoodViewDetails(Integer itemId, Integer merchantId, String restName, String locality,
                               String itemName, boolean isNewFoodview) {
        this.itemId = itemId;
        this.merchantId = merchantId;
        this.restName = restName;
        this.itemName = itemName;
        this.locality = locality;
        this.isNewFoodview = isNewFoodview;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public boolean isNewFoodview() {
        return isNewFoodview;
    }

    public void setNewFoodview(boolean newFoodview) {
        isNewFoodview = newFoodview;
    }
}
