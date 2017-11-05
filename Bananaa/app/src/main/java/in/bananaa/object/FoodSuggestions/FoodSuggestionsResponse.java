package in.bananaa.object.foodSuggestions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.bananaa.object.GenericResponse;

public class FoodSuggestionsResponse extends GenericResponse {
    @SerializedName("dishes")
    private List<FoodSuggestion> dishes;

    public List<FoodSuggestion> getDishes() {
        return dishes;
    }

    public void setDishes(List<FoodSuggestion> dishes) {
        this.dishes = dishes;
    }
}
