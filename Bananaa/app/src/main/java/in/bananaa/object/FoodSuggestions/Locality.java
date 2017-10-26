package in.bananaa.object.FoodSuggestions;

import com.google.gson.annotations.SerializedName;

public class Locality {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
