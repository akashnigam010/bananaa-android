package in.bananaa.object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GlobalSearchItem implements Serializable {
    @SerializedName("id")
    private Integer id;

    @SerializedName("type")
    private SearchResultType type;

    @SerializedName("name")
    private String name;

    @SerializedName("nameId")
    private String nameId;

    @SerializedName("shortAddress")
    private String shortAddress;

    @SerializedName("merchantUrl")
    private String merchantUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SearchResultType getType() {
        return type;
    }

    public void setType(SearchResultType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String nameId) {
        this.nameId = nameId;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getMerchantUrl() {
        return merchantUrl;
    }

    public void setMerchantUrl(String merchantUrl) {
        this.merchantUrl = merchantUrl;
    }
}
