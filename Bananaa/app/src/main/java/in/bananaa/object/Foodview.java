package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Foodview implements Serializable {
    @SerializedName("id")
    private Integer id;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userImageUrl")
    private String userImageUrl;

    @SerializedName("userRatingCount")
    private Integer userRatingCount;

    @SerializedName("userFoodviewCount")
    private Integer userFoodviewCount;

    @SerializedName("rating")
    private String rating;

    @SerializedName("ratingClass")
    private String ratingClass;

    @SerializedName("desc")
    private String desc;

    @SerializedName("timeDiff")
    private String timeDiff;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public Integer getUserRatingCount() {
        return userRatingCount;
    }

    public void setUserRatingCount(Integer userRatingCount) {
        this.userRatingCount = userRatingCount;
    }

    public Integer getUserFoodviewCount() {
        return userFoodviewCount;
    }

    public void setUserFoodviewCount(Integer userFoodviewCount) {
        this.userFoodviewCount = userFoodviewCount;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRatingClass() {
        return ratingClass;
    }

    public void setRatingClass(String ratingClass) {
        this.ratingClass = ratingClass;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
    }
}
