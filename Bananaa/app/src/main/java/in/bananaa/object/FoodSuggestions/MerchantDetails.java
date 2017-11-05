package in.bananaa.object.foodSuggestions;

import com.google.gson.annotations.SerializedName;

public class MerchantDetails {

    @SerializedName("id")
    private Integer id;

    @SerializedName("nameId")
    private String nameId;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private Address address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String nameId) {
        this.nameId = nameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
