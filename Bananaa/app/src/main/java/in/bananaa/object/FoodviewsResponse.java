package in.bananaa.object;

import java.util.List;

public class FoodviewsResponse extends GenericResponse {
    List<Foodview> recommendations;

    public List<Foodview> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Foodview> recommendations) {
        this.recommendations = recommendations;
    }
}
