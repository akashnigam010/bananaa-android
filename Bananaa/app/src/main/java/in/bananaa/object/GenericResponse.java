package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GenericResponse implements Serializable {

    @SerializedName("result")
    private boolean result;

    @SerializedName("statusCodes")
    private ArrayOfStatusCode statusCodes;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ArrayOfStatusCode getStatusCodes() {
        return statusCodes;
    }

    public void setStatusCodes(ArrayOfStatusCode statusCodes) {
        this.statusCodes = statusCodes;
    }
}
