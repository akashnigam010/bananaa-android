package in.bananaa.object.foodSuggestions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.bananaa.object.Tag;

public class FoodSuggestion {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("nameId")
    private String nameId;

    @SerializedName("rating")
    private Float rating;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("recommendationCount")
    private Integer recommendationCount;

    @SerializedName("ratingClass")
    private String ratingClass;

    @SerializedName("merchant")
    private MerchantDetails merchant;

    @SerializedName("suggestions")
    private List<Tag> suggestions;

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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
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

    public Integer getRecommendationCount() {
        return recommendationCount;
    }

    public void setRecommendationCount(Integer recommendationCount) {
        this.recommendationCount = recommendationCount;
    }

    public String getRatingClass() {
        return ratingClass;
    }

    public void setRatingClass(String ratingClass) {
        this.ratingClass = ratingClass;
    }

    public MerchantDetails getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantDetails merchant) {
        this.merchant = merchant;
    }

    public List<Tag> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<Tag> suggestions) {
        this.suggestions = suggestions;
    }
}
