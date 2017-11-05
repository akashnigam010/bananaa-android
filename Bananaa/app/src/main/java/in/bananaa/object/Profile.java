package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Profile implements Serializable {
    @SerializedName("id")
    private Integer id;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("level")
    private Float level;

    @SerializedName("status")
    private String status;

    @SerializedName("vegnonvegId")
    private Integer vegnonvegId;

    @SerializedName("ratingCount")
    private Integer ratingCount;

    @SerializedName("foodviewCount")
    private Integer foodviewCount;

    @SerializedName("cuisines")
    private List<Tag> cuisines;

    @SerializedName("dishes")
    private List<Tag> dishes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Float getLevel() {
        return level;
    }

    public void setLevel(Float level) {
        this.level = level;
    }

    public Integer getVegnonvegId() {
        return vegnonvegId;
    }

    public void setVegnonvegId(Integer vegnonvegId) {
        this.vegnonvegId = vegnonvegId;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Integer getFoodviewCount() {
        return foodviewCount;
    }

    public void setFoodviewCount(Integer foodviewCount) {
        this.foodviewCount = foodviewCount;
    }

    public List<Tag> getCuisines() {
        return cuisines;
    }

    public void setCuisines(List<Tag> cuisines) {
        this.cuisines = cuisines;
    }

    public List<Tag> getDishes() {
        return dishes;
    }

    public void setDishes(List<Tag> dishes) {
        this.dishes = dishes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if (this.lastName == null) {
            this.lastName = "";
        }
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
