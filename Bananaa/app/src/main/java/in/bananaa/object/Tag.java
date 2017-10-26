package in.bananaa.object;

import com.google.gson.annotations.SerializedName;
import com.plumillonforge.android.chipview.Chip;

import java.io.Serializable;

public class Tag implements Serializable, Chip {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("dishCount")
    private Integer dishCount;

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

    public Integer getDishCount() {
        return dishCount;
    }

    public void setDishCount(Integer dishCount) {
        this.dishCount = dishCount;
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

    @Override
    public String getText() {
        return "#" + this.name.replace(" ", "").toLowerCase() + "  ";
    }
}
