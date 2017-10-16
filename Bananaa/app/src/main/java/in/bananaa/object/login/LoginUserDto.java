package in.bananaa.object.login;

import com.google.gson.annotations.SerializedName;

public class LoginUserDto {
    @SerializedName("id")
    private Integer id;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("isPreferencesSaved")
    private boolean isPreferencesSaved;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPreferencesSaved() {
        return isPreferencesSaved;
    }

    public void setPreferencesSaved(boolean preferencesSaved) {
        isPreferencesSaved = preferencesSaved;
    }
}
