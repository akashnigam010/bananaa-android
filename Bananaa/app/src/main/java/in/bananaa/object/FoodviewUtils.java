package in.bananaa.object;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import in.bananaa.fragment.FoodviewModalFragment;

public class FoodviewUtils {

    public void openFoodviewFragment(ItemFoodViewDetails itemFoodViewDetails, AppCompatActivity mContext) {
        android.support.v4.app.FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FoodviewModalFragment foodviewFragment = new FoodviewModalFragment();
        Bundle args = new Bundle();
        args.putParcelable(FoodviewModalFragment.ITEM_DETAILS, itemFoodViewDetails);
        foodviewFragment.setArguments(args);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, foodviewFragment).addToBackStack(null).commit();
    }
}
