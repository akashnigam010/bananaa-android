package in.bananaa.object.foodSuggestions;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("locality")
    private Locality locality;

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }
}
