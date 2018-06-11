package in.bananaa.object;

public class MyItemFoodviewResponse extends GenericResponse {
    private Foodview recommendation;
    private boolean recommended;

    public Foodview getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Foodview recommendation) {
        this.recommendation = recommendation;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }
}
