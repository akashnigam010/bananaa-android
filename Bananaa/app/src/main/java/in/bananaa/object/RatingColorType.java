package in.bananaa.object;
import in.bananaa.R;

public enum RatingColorType {
    R10("r10", R.color.r10),
    R15("r15", R.color.r15),
    R20("r20", R.color.r20),
    R25("r25", R.color.r25),
    R30("r30", R.color.r30),
    R35("r35", R.color.r35),
    R40("r40", R.color.r40),
    R45("r45", R.color.r45),
    R50("r50", R.color.r50);

    private String cssClass;
    private int color;

    private RatingColorType(String cssClass, int color) {
        this.cssClass = cssClass;
        this.color = color;
    }

    public String getCssClass() {
        return cssClass;
    }

    public int getColor() {
        return color;
    }

    public static RatingColorType getCodeByCssClass(String cssClass) {
        for (RatingColorType type : RatingColorType.values()) {
            if (type.getCssClass().equalsIgnoreCase(cssClass)) {
                return type;
            }
        }
        return null;
    }
}
