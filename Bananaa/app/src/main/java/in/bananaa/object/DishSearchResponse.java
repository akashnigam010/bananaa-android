package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DishSearchResponse extends GenericResponse implements Serializable {

    @SerializedName("items")
    private List<DishSearchItem> items;

    public List<DishSearchItem> getItems() {
        return items;
    }

    public void setItems(List<DishSearchItem> items) {
        this.items = items;
    }
}
