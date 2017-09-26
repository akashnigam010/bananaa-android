package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

public class StatusCode {
    @SerializedName("code")
    public String code;

    @SerializedName("description")
    public String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
