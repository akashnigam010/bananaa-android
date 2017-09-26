package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GlobalSearchResponse extends GenericResponse {

    @SerializedName("searchItems")
    private List<SearchItem> searchItems;

    public List<SearchItem> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(List<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }
}
