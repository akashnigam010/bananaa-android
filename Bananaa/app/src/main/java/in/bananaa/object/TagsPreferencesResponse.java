package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TagsPreferencesResponse extends GenericResponse {
    @SerializedName("searchItems")
    List<TagsPreference> searchItems;

    public List<TagsPreference> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(List<TagsPreference> searchItems) {
        this.searchItems = searchItems;
    }
}
