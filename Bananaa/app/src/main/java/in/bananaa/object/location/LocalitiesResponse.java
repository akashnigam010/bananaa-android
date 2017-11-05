package in.bananaa.object.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.bananaa.object.GenericResponse;

public class LocalitiesResponse extends GenericResponse {
    @SerializedName("cities")
    private List<City> cities;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
