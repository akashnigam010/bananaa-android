package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VegnonvegPreferenceResponse extends GenericResponse implements Serializable {
    @SerializedName("id")
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
