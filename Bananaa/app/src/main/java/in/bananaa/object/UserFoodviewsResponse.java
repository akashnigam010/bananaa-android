package in.bananaa.object;

import java.util.List;

public class UserFoodviewsResponse extends GenericResponse {
    List<UserFoodview> foodviews;

    public List<UserFoodview> getFoodviews() {
        return foodviews;
    }

    public void setFoodviews(List<UserFoodview> foodviews) {
        this.foodviews = foodviews;
    }
}
