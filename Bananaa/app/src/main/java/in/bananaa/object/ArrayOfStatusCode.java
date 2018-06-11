package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArrayOfStatusCode {

    @SerializedName("statusCode")
    private List<StatusCode> statusCode;

    public List<StatusCode> getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(List<StatusCode> statusCode) {
        this.statusCode = statusCode;
    }
}
