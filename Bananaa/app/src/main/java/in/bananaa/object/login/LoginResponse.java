package in.bananaa.object.login;

import com.google.gson.annotations.SerializedName;

import in.bananaa.object.GenericResponse;

public class LoginResponse extends GenericResponse {

    @SerializedName("user")
    private LoginUserDto user;

    @SerializedName("accessToken")
    private String accessToken;

    public LoginUserDto getUser() {
        return user;
    }

    public void setUser(LoginUserDto user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
