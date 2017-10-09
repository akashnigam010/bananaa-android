package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemFoodViewDetails implements Serializable {
    @SerializedName("id")
    private Integer id;

    @SerializedName("itemId")
    private Integer itemId;

    @SerializedName("restName")
    private String restName;

    @SerializedName("locality")
    private String locality;

    @SerializedName("itemName")
    private String itemName;

    @SerializedName("desc")
    private String desc;

    @SerializedName("rating")
    private float rating;

    @SerializedName("isBlankFoodview")
    private Boolean isBlankFoodview;

    public ItemFoodViewDetails(Integer id, Integer itemId, String restName, String locality,
                               String itemName, String desc, float rating, Boolean isBlankFoodview) {
        this.id = id;
        this.itemId = itemId;
        this.restName = restName;
        this.itemName = itemName;
        this.locality = locality;
        this.desc = desc;
        this.rating = rating;
        this.isBlankFoodview = isBlankFoodview;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Boolean isBlankFoodview() {
        return isBlankFoodview;
    }

    public void setBlankFoodview(Boolean blankFoodview) {
        isBlankFoodview = blankFoodview;
    }
}
