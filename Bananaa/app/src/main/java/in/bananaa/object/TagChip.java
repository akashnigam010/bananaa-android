package in.bananaa.object;

import com.plumillonforge.android.chipview.Chip;

public class TagChip implements Chip {
    private Integer id;
    private String name;
    private SearchResultType type;

    public TagChip(Integer id, String name, SearchResultType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SearchResultType getType() {
        return type;
    }

    public void setType(SearchResultType type) {
        this.type = type;
    }

    @Override
    public String getText() {
        return name;
    }
}
