package in.bananaa.object.genericSearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.bananaa.object.Tag;

public class MerchantDetailsDto {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("shortAddress")
    private String shortAddress;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("thumbnail")
    private String thumbnail;

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

    @SerializedName("searchTag")
    private Tag searchTag;

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

    public Tag getSearchTag() {
        return searchTag;
    }

    public void setSearchTag(Tag searchTag) {
        this.searchTag = searchTag;
    }
}
