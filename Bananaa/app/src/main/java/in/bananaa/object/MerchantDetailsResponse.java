package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MerchantDetailsResponse extends GenericResponse implements Serializable {

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("nameId")
    private String nameId;

    @SerializedName("shortAddress")
    private String shortAddress;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("merchantUrlAbsolute")
    private String merchantUrlAbsolute;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("phone")
    private String phone;

    @SerializedName("openingHours")
    private List<String> openingHours;

    @SerializedName("type")
    private List<String> type;

    @SerializedName("averageCost")
    private String averageCost;

    @SerializedName("longAddress")
    private String longAddress;

    @SerializedName("ratedCuisines")
    private List<Tag> ratedCuisines;

    @SerializedName("items")
    private List<Item> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String nameId) {
        this.nameId = nameId;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(List<String> openingHours) {
        this.openingHours = openingHours;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public String getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(String averageCost) {
        this.averageCost = averageCost;
    }

    public String getLongAddress() {
        return longAddress;
    }

    public void setLongAddress(String longAddress) {
        this.longAddress = longAddress;
    }

    public List<Tag> getRatedCuisines() {
        return ratedCuisines;
    }

    public void setRatedCuisines(List<Tag> ratedCuisines) {
        this.ratedCuisines = ratedCuisines;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getMerchantUrlAbsolute() {
        return merchantUrlAbsolute;
    }

    public void setMerchantUrlAbsolute(String merchantUrlAbsolute) {
        this.merchantUrlAbsolute = merchantUrlAbsolute;
    }
}
