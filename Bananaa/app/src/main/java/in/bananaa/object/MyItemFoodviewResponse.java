package in.bananaa.object;

public class MyItemFoodviewResponse extends GenericResponse {
    MyFoodview recommendation;
    private boolean recommended;

    public MyFoodview getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(MyFoodview recommendation) {
        this.recommendation = recommendation;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }
}
