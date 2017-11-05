package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileResponse extends GenericResponse implements Serializable {
    @SerializedName("profile")
    private Profile profile;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
