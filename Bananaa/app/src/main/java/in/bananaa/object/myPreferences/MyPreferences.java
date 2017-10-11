package in.bananaa.object.myPreferences;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyPreferences implements Serializable {
    @SerializedName("type")
    private Integer type;

    @SerializedName("cuisines")
    private List<Integer> cuisines;

    @SerializedName("items")
    private List<Integer> items;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Integer> getCuisines() {
        if (this.cuisines == null) {
            this.cuisines = new ArrayList<>();
        }
        return cuisines;
    }

    public void setCuisines(List<Integer> cuisines) {
        this.cuisines = cuisines;
    }

    public List<Integer> getItems() {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<Integer> items) {
        this.items = items;
    }
}
