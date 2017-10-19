package in.bananaa.object;

import java.util.List;

public class FoodviewsResponse extends GenericResponse {
    List<MyFoodview> recommendations;

    public List<MyFoodview> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<MyFoodview> recommendations) {
        this.recommendations = recommendations;
    }
}
