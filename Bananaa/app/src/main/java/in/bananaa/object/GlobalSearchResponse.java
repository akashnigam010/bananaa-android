package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GlobalSearchResponse extends GenericResponse implements Serializable {

    @SerializedName("searchItems")
    private List<GlobalSearchItem> searchItems;

    public List<GlobalSearchItem> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(List<GlobalSearchItem> searchItems) {
        this.searchItems = searchItems;
    }
}
