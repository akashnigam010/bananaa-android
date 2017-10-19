package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ItemDetailsResponse extends GenericResponse implements Serializable {
    @SerializedName("id")
    private Integer id;

    @SerializedName("merchantId")
    private Integer merchantId;

    @SerializedName("name")
    private String name;

    @SerializedName("merchantName")
    private String merchantName;

    @SerializedName("shortAddress")
    private String shortAddress;

    @SerializedName("rating")
    private String rating;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("ratingClass")
    private String ratingClass;

    @SerializedName("totalRatings")
    private Integer totalRatings;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRatingClass() {
        return ratingClass;
    }

    public void setRatingClass(String ratingClass) {
        this.ratingClass = ratingClass;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }
}
