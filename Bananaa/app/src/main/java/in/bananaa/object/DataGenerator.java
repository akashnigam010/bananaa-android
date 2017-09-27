package in.bananaa.object;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {

    public static MerchantDetails getMerchantDetails() {
        MerchantDetails details = new MerchantDetails();

        details.setId(1);
        details.setName("Chinese Pavilion");
        details.setNameId("chinese-pavilion-gachibowli");
        details.setShortAddress("Gachibowli, Hyderabad");
        details.setImageUrl("https://bna-s3.s3.amazonaws.com/rest-img/chinese-pavilion-gachibowli.jpg");
        details.setThumbnail("https://bna-s3.s3.amazonaws.com/rest-img/t/chinese-pavilion-gachibowli.jpg");
        details.setPhone("04068888831");
        details.setOpeningHours(getOpeningHours());
        details.setType(getType());
        details.setAverageCost("1000");
        details.setLongAddress("Plot 3 & 5, First Floor, Venkata Sai Ganapathi's Gold, Above Cafe Coffee Day, Near Flyover, Gachibowli, Hyderabad");
        details.setRatedCuisines(getRatedCuisines());
        details.setItems(getPopularItems());
        return details;
    }

    private static List<String> getOpeningHours() {
        List<String> openingHours = new ArrayList<>();
        openingHours.add("12:00 PM to 03:30 PM");
        openingHours.add("07:00 PM to 11:00 PM");
        return openingHours;
    }

    private static List<String> getType() {
        List<String> type = new ArrayList<>();
        type.add("Casual Dining");
        type.add("Cafe");
        return type;
    }

    private static List<Item> getPopularItems() {
        List<Item> items = new ArrayList<>();
        items.add(getItem(1, "Pan Fried Noodles", "https://bna-s3.s3.amazonaws.com/d/77/t/pan-fried-noodles.jpg", 10, "r50", "4.9"));
        items.add(getItem(2, "Bamboo Rice", "https://bna-s3.s3.amazonaws.com/d/77/t/bamboo-rice.jpg", 9, "r50", "4.7"));
        items.add(getItem(3, "Manchow Soup", "https://bna-s3.s3.amazonaws.com/d/77/t/manchow-soup.jpg", 11, "r45", "4.5"));
        items.add(getItem(4, "Potatoes In Chilli Plum Sauce", "https://bna-s3.s3.amazonaws.com/d/77/t/potatoes-in-chilli-plum-sauce.jpg", 5, "r40", "3.6"));
        items.add(getItem(5, "Chicken Bananaa Leaves", "https://bna-s3.s3.amazonaws.com/d/77/t/chicken-banana-leaves.jpg", 4, "r35", "3.5"));
        return items;
    }

    private static List<Tag> getRatedCuisines() {
        List<Tag> tags = new ArrayList<>();
        tags.add(getTag(1, "Chinese", "https://s3.ap-south-1.amazonaws.com/bna-s3/item-img/cuisine/t/chinese.jpg", 112, "r35", "3.8"));
        tags.add(getTag(2, "Sea Food", "https://s3.ap-south-1.amazonaws.com/bna-s3/item-img/cuisine/t/sea-food.jpg", 17, "r35", "3.7"));
        tags.add(getTag(3, "Desserts", "https://s3.ap-south-1.amazonaws.com/bna-s3/item-img/cuisine/t/desserts.jpg", 9, "r30", "3.4"));
        tags.add(getTag(4, "Thai", "https://s3.ap-south-1.amazonaws.com/bna-s3/item-img/cuisine/t/thai.jpg", 6, "r25", "2.7"));
        return tags;
    }

    private static Item getItem(Integer id, String name, String thumbnail, Integer recommendations, String ratingClass, String rating) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setThumbnail(thumbnail);
        item.setRecommendations(recommendations);
        item.setRatingClass(ratingClass);
        item.setRating(rating);
        return item;
    }

    private static Tag getTag(Integer id, String name, String thumbnail, Integer dishCount, String ratingClass, String rating) {
        Tag item = new Tag();
        item.setId(id);
        item.setName(name);
        item.setThumbnail(thumbnail);
        item.setDishCount(dishCount);
        item.setRatingClass(ratingClass);
        item.setRating(rating);
        return item;
    }
}
