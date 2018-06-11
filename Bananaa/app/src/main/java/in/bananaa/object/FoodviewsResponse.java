package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodviewsResponse extends GenericResponse {
    @SerializedName("recommendations")
    List<Foodview> recommendations;

    public List<Foodview> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Foodview> recommendations) {
        this.recommendations = recommendations;
    }
}
