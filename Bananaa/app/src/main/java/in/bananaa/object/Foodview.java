package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Foodview implements Serializable {
    @SerializedName("id")
    private Integer id;

    @SerializedName("itemId")
    private Integer itemId;

    @SerializedName("name")
    private String name;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("description")
    private String description;

    @SerializedName("timeDiff")
    private String timeDiff;

    @SerializedName("totalRcmdns")
    private Integer totalRcmdns;

    @SerializedName("ratingClass")
    private String ratingClass;

    @SerializedName("rating")
    private String rating;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(String timeDiff) {
        this.timeDiff = timeDiff;
    }

    public Integer getTotalRcmdns() {
        return totalRcmdns;
    }

    public void setTotalRcmdns(Integer totalRcmdns) {
        this.totalRcmdns = totalRcmdns;
    }

    public String getRatingClass() {
        return ratingClass;
    }

    public void setRatingClass(String ratingClass) {
        this.ratingClass = ratingClass;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
