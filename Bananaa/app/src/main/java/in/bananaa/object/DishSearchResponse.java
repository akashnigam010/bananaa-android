package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DishSearchResponse extends GenericResponse {

    @SerializedName("items")
    private List<DishSearchItem> items;

    public List<DishSearchItem> getItems() {
        return items;
    }

    public void setItems(List<DishSearchItem> items) {
        this.items = items;
    }
}
