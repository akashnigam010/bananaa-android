package in.bananaa.object;

import com.google.gson.annotations.SerializedName;
import com.plumillonforge.android.chipview.Chip;

import java.io.Serializable;
import java.util.Objects;

import static android.R.attr.type;

public class TagsPreference implements Serializable, Chip {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("selected")
    private Boolean selected;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public String getText() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TagsPreference)) {
            return false;
        }
        TagsPreference tc = (TagsPreference) o;
        return id == tc.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }
}
