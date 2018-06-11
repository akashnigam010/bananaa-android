package in.bananaa.object.foodSuggestions;

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
