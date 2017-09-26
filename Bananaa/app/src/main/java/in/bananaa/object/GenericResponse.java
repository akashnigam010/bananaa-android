package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenericResponse {

    @SerializedName("result")
    private boolean result;

    @SerializedName("statusCodes")
    private List<StatusCode> statusCodes;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<StatusCode> getStatusCodes() {
        return statusCodes;
    }

    public void setStatusCodes(List<StatusCode> statusCodes) {
        this.statusCodes = statusCodes;
    }
}
