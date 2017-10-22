package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recommendations extends GenericResponse implements Serializable {
    @SerializedName("merchantName")
    private String merchantName;

    @SerializedName("recommendations")
    private List<Foodview> recommendations;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public List<Foodview> getRecommendations() {
        if (this.recommendations == null) {
            this.recommendations = new ArrayList<>();
        }
        return recommendations;
    }

    public void setRecommendations(List<Foodview> recommendations) {
        this.recommendations = recommendations;
    }
}
