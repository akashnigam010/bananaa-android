//package in.bananaa.object;
//
//import com.plumillonforge.android.chipview.Chip;
//
//import java.util.Objects;
//
//public class TagChip implements Chip {
//    private Integer id;
//    private String name;
//    private SearchResultType type;
//    private boolean isSelected;
//
//    public TagChip(Integer id, String name, SearchResultType type, boolean isSelected) {
//        this.id = id;
//        this.name = name;
//        this.type = type;
//        this.isSelected = isSelected;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public SearchResultType getType() {
//        return type;
//    }
//
//    public void setType(SearchResultType type) {
//        this.type = type;
//    }
//
//    public boolean isSelected() {
//        return isSelected;
//    }
//
//    public void setSelected(boolean selected) {
//        isSelected = selected;
//    }
//
//    @Override
//    public String getText() {
//        return name;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (o == this) return true;
//        if (!(o instanceof TagChip)) {
//            return false;
//        }
//        TagChip tc = (TagChip) o;
//        return id == tc.getId();
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name, type);
//    }
//}
