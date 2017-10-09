package in.bananaa.object;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemFoodViewDetails implements Parcelable {
    Integer id, itemId;
    String restName, locality, itemName, desc, rating;
    Boolean isAddNewFoodview;
    Boolean isDetailsAddNew;

    public ItemFoodViewDetails(Integer id, Integer itemId, String restName, String locality,
                               String itemName, String desc, String rating, Boolean isAddNewFoodview, Boolean isDetailsAddNew) {
        this.id = id;
        this.itemId = itemId;
        this.restName = restName;
        this.itemName = itemName;
        this.locality = locality;
        this.desc = desc;
        this.rating = rating;
        this.isAddNewFoodview = isAddNewFoodview;
        this.isDetailsAddNew = isDetailsAddNew;
    }

    public ItemFoodViewDetails(Parcel in) {
        restName = in.readString();
        locality = in.readString();
        itemName = in.readString();
        desc = in.readString();
        id = in.readInt();
        itemId = in.readInt();
        rating = in.readString();
        isAddNewFoodview  = (in.readInt() == 0) ? false : true;
        isDetailsAddNew  = (in.readInt() == 0) ? false : true;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(restName);
        dest.writeString(locality);
        dest.writeString(itemName);
        dest.writeString(desc);
        dest.writeInt(id);
        dest.writeInt(itemId);
        dest.writeString(rating);
        dest.writeInt(isAddNewFoodview ? 1 : 0);
        dest.writeInt(isDetailsAddNew ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemFoodViewDetails> CREATOR = new Creator<ItemFoodViewDetails>() {
        @Override
        public ItemFoodViewDetails createFromParcel(Parcel in) {
            return new ItemFoodViewDetails(in);
        }

        @Override
        public ItemFoodViewDetails[] newArray(int size) {
            return new ItemFoodViewDetails[size];
        }
    };

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Boolean getAddNewFoodview() {
        return isAddNewFoodview;
    }

    public void setAddNewFoodview(Boolean addNewFoodview) {
        isAddNewFoodview = addNewFoodview;
    }

    public Boolean getDetailsAddNew() {
        return isDetailsAddNew;
    }

    public void setDetailsAddNew(Boolean detailsAddNew) {
        isDetailsAddNew = detailsAddNew;
    }
}
