package in.bananaa.object.genericSearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.bananaa.object.GenericResponse;

public class GenericSearchResponse extends GenericResponse {
    @SerializedName("merchants")
    private List<MerchantDetailsDto> merchants;

    public List<MerchantDetailsDto> getMerchants() {
        return merchants;
    }

    public void setMerchants(List<MerchantDetailsDto> merchants) {
        this.merchants = merchants;
    }
}
