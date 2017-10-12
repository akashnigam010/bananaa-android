package in.bananaa.object;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {

    public static MerchantDetailsResponse getMerchantDetails() {
        MerchantDetailsResponse details = new MerchantDetailsResponse();

        details.setId(1);
        details.setName("SodaBottleOpenerWala");
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

    public static List<MyFoodview> getMyRecommendations() {
        Recommendations recommendations = new Recommendations();
        recommendations.setMerchantName("SodaBottleOpenerWala");
        recommendations.getRecommendations().add(getFoodView(4, "Pan Fried Noodles", "2.5", "r25", "https://bna-s3.s3.amazonaws.com/d/77/t/pan-fried-noodles.jpg", "5 hours ago", 1, "Not good at all. It was pathetic and undercooked"));
        recommendations.getRecommendations().add(getFoodView(3, "Bamboo Rice", "3.5", "r35", "https://bna-s3.s3.amazonaws.com/d/77/t/bamboo-rice.jpg", "28 days ago", 1, "Too good. I ate such rice the first time and it was exqisite"));
        recommendations.getRecommendations().add(getFoodView(2, "Whiteboard Special Pizza", "5.0", "r50", "https://bna-s3.s3.amazonaws.com/d/23/t/wb-special-pizza.jpg", "2 months ago", 2, "Very very special pizza. Didn't expect Whiteboard Cafe to be serving this beautiful piece of food. Yummy!"));
        recommendations.getRecommendations().add(getFoodView(1, "Quinoa Craving Salad", "4.5", "r45", "https://bna-s3.s3.amazonaws.com/item-img/t/green-leaf.png", "4 months ago", 4, null));
        return recommendations.getRecommendations();
    }

    public static ItemDetailsResponse getItemDetailsResponse() {
        ItemDetailsResponse response = new ItemDetailsResponse();
        response.setMerchantName("SodaBottlOpenerWala");
        response.setName("Pan Fried Noodles");
        response.setRating("4.0");
        response.setRatingClass("r40");
        response.setId(1);
        response.setImageUrl("https://bna-s3.s3.amazonaws.com/d/12/pasta-donatella.jpg");
        response.setNameId("pan-fried-noodles");
        response.setShortAddress("Hitech City, Hyderabad");
        response.setTotalRatings(13);
        response.getFoodviews().add(getFoodview());
        return response;
    }

    public static List<TagChip> getSuggestionTags() {
        List<TagChip> items = new ArrayList<>();
        items.add(getSuggestionTag(1, "Noodles Noodles"));
        items.add(getSuggestionTag(2, "Noodles zxc"));
        items.add(getSuggestionTag(3, "Noodlesa s"));
        items.add(getSuggestionTag(4, "Noodlesasasd asd"));
        items.add(getSuggestionTag(5, "Noodles3"));
        items.add(getSuggestionTag(6, "Noodlessd3 3"));
        items.add(getSuggestionTag(7, "Noodles Noodles Noodles"));
        items.add(getSuggestionTag(8, "Noodles"));
        items.add(getSuggestionTag(9, "Noodles Nood"));
        items.add(getSuggestionTag(10, "Noodles"));
        items.add(getSuggestionTag(11, "Noodlesnasdasd"));
        items.add(getSuggestionTag(12, "Noodles asd asd"));
        items.add(getSuggestionTag(13, "Noodles asd"));
        items.add(getSuggestionTag(14, "Noodlesasd"));
        items.add(getSuggestionTag(15, "Noodles asd"));
        items.add(getSuggestionTag(16, "Noodles asda asdd"));
        items.add(getSuggestionTag(17, "Noodlesasd"));
        items.add(getSuggestionTag(18, "Noodles asd"));
        items.add(getSuggestionTag(19, "Noodles"));
        items.add(getSuggestionTag(20, "Noodlesasd"));
        items.add(getSuggestionTag(21, "Noodles"));
        items.add(getSuggestionTag(22, "Noodlesasdas"));
        items.add(getSuggestionTag(23, "Noodles asd"));
        items.add(getSuggestionTag(24, "Noodlesasd asd asd"));
        items.add(getSuggestionTag(25, "Noodles asdsdas"));
        items.add(getSuggestionTag(26, "Noodlesads a"));
        items.add(getSuggestionTag(27, "Nood"));
        items.add(getSuggestionTag(28, "Noodles"));
        items.add(getSuggestionTag(29, "Noodlessdd"));
        items.add(getSuggestionTag(30, "Nood"));
        items.add(getSuggestionTag(31, "Noodlesas sd"));
        items.add(getSuggestionTag(32, "Noodles"));
        items.add(getSuggestionTag(33, "Noodlesa "));
        items.add(getSuggestionTag(34, "Nood"));
        items.add(getSuggestionTag(35, "Noodlesasd asd asd"));
        items.add(getSuggestionTag(36, "Nood"));
        items.add(getSuggestionTag(37, "Noodl sdsd"));
        items.add(getSuggestionTag(38, "Noodles ads"));
        items.add(getSuggestionTag(39, "Noodles"));
        items.add(getSuggestionTag(40, "Noodlesasd"));
        return items;
    }

    private static TagChip getSuggestionTag(Integer id, String name) {
        return new TagChip(id, name, SearchResultType.DISH);
    }

    private static final Foodview getFoodview() {
        Foodview foodview = new Foodview();
        foodview.setId(1);
        foodview.setRating("3.0");
        foodview.setRatingClass("r30");
        foodview.setTimeDiff("1 month ago");
        foodview.setUserId(1);
        foodview.setUserName("Yogesh Sadula");
        foodview.setUserImageUrl("https://bna-usr.s3.amazonaws.com/yogesh-sadula-1492921443270.jpg");
        foodview.setUserFoodviewCount(2);
        foodview.setUserRatingCount(2);
        foodview.setDesc("Pasta, in the the real italian style in hyderbad, then there is only one address.. little italy.. the fresh veggies, herbs, cream and cheese are unforgettable.. served with love");
        return foodview;
    }

    private static MyFoodview getFoodView(Integer id, String name, String rating, String ratingClass, String thumbnail, String timeDiff, Integer totalRcmds, String description) {
        MyFoodview myFoodview = new MyFoodview();
        myFoodview.setId(id);
        myFoodview.setItemId(id);
        myFoodview.setName(name);
        myFoodview.setRating(rating);
        myFoodview.setRatingClass(ratingClass);
        myFoodview.setThumbnail(thumbnail);
        myFoodview.setTimeDiff(timeDiff);
        myFoodview.setTotalRcmdns(totalRcmds);
        myFoodview.setDescription(description == null ? "" : description);
        return myFoodview;
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
