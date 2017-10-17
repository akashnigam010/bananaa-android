package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SearchResponse extends GenericResponse implements Serializable {

    @SerializedName("searchItems")
    private List<SearchItem> searchItems;

    public List<SearchItem> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(List<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }
}
