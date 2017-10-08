package in.bananaa.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import in.bananaa.R;
import in.bananaa.adapter.FoodviewListAdapter;
import in.bananaa.adapter.ItemListAdapter;
import in.bananaa.adapter.TagListAdapter;
import in.bananaa.object.DataGenerator;
import in.bananaa.object.ItemFoodViewDetails;
import in.bananaa.object.MerchantDetails;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.CustomListView;
import in.bananaa.utils.Utils;

import static com.bumptech.glide.Glide.with;

public class MerchantDetailsActivity extends AppCompatActivity {

    MerchantDetails merchantDetails;
    AlertMessages messages;

    ScrollView merchantDetailsView;
    ProgressBar activityLoader;

    ImageView ivImage;
    ImageButton ivShare;
    ImageButton ivBack;
    TextView tvName;
    TextView tvShortAddress;
    TextView tvHours;
    TextView tvHoursTxt;
    TextView tvPhone;
    TextView tvPhoneTxt;
    TextView tvAverageCost;
    TextView tvAverageCostTxt;
    TextView tvType;
    TextView tvTypeTxt;
    TextView tvLongAddress;
    TextView tvLongAddressTxt;
    TextView tvCuisinesAndSpreadTxt;
    TextView tvDelectableDishesTxt;
    CustomListView lvDelectableDishes;
    CustomListView lvCuisinesAndSpread;
    CustomListView lvMyFoodViews;

    AppCompatButton btnSeeMore;
    LinearLayout seeMoreSection;

    TextView tvMyFoodViewsTxt;
    TextView tvFoodviewSubHeading;
    AppCompatButton btnAddFoodview;

    ItemListAdapter itemListAdapter;
    TagListAdapter cuisinesListAdapter;
    FoodviewListAdapter foodviewListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_details);
        messages = new AlertMessages(this);
        merchantDetails = (MerchantDetails) getIntent().getSerializableExtra("merchantDetails");
        initializeView();
    }

    private void initializeView() {
        merchantDetailsView = (ScrollView) findViewById(R.id.merchantDetailsView);
        activityLoader = (ProgressBar) findViewById(R.id.activityLoader);

        startAsync();
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                endAsync();
                setComponents();
                setFont();
                setMerchantDetails();
            }
        }.start();
    }

    private void setComponents() {
        ivBack = (ImageButton) findViewById(R.id.ivBack);
        ivShare = (ImageButton) findViewById(R.id.ivShare);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvShortAddress = (TextView) findViewById(R.id.tvShortAddress);
        tvHours = (TextView) findViewById(R.id.tvHours);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvAverageCost = (TextView) findViewById(R.id.tvAverageCost);
        tvType = (TextView) findViewById(R.id.tvType);
        tvLongAddress = (TextView) findViewById(R.id.tvLongAddress);
        tvHoursTxt = (TextView) findViewById(R.id.tvHoursTxt);
        tvPhoneTxt = (TextView) findViewById(R.id.tvPhoneTxt);
        tvAverageCostTxt = (TextView) findViewById(R.id.tvAverageCostTxt);
        tvTypeTxt = (TextView) findViewById(R.id.tvTypeTxt);
        tvLongAddressTxt = (TextView) findViewById(R.id.tvLongAddressTxt);
        tvCuisinesAndSpreadTxt = (TextView) findViewById(R.id.tvCuisinesAndSpreadTxt);
        tvDelectableDishesTxt = (TextView) findViewById(R.id.tvDelectableDishesTxt);
        lvCuisinesAndSpread = (CustomListView) findViewById(R.id.lvCuisinesAndSpread);
        lvDelectableDishes = (CustomListView) findViewById(R.id.lvDelectableDishes);
        seeMoreSection = (LinearLayout) findViewById(R.id.seeMoreSection);
        btnSeeMore = (AppCompatButton) findViewById(R.id.btnSeeMore);
        tvMyFoodViewsTxt = (TextView) findViewById(R.id.tvMyFoodViewsTxt);
        tvFoodviewSubHeading = (TextView) findViewById(R.id.tvFoodviewSubHeading);
        lvMyFoodViews = (CustomListView) findViewById(R.id.lvMyFoodviews);
        btnAddFoodview = (AppCompatButton) findViewById(R.id.btnAddFoodview);

        itemListAdapter = new ItemListAdapter(this);
        itemListAdapter.addAll(merchantDetails.getItems());
        cuisinesListAdapter = new TagListAdapter(this);
        cuisinesListAdapter.addAll(merchantDetails.getRatedCuisines());
        foodviewListAdapter = new FoodviewListAdapter(this, merchantDetails.getName(), merchantDetails.getShortAddress());
        foodviewListAdapter.addAll(DataGenerator.getMyRecommendations());

        lvCuisinesAndSpread.setAdapter(cuisinesListAdapter);
        lvDelectableDishes.setAdapter(itemListAdapter);
        lvMyFoodViews.setAdapter(foodviewListAdapter);
        btnSeeMore.setOnClickListener(onClickSeeMoreListner);
        ivBack.setOnClickListener(onClickBackListener);
        btnAddFoodview.setOnClickListener(onClickRateAndFoodViewListener);
        setToastMessages();
    }

    View.OnClickListener onClickSeeMoreListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            itemListAdapter.appendAll(merchantDetails.getItems());
            seeMoreSection.setVisibility(View.GONE);
        }
    };
    View.OnClickListener onClickBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener onClickRateAndFoodViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ItemFoodViewDetails itemFoodViewDetails =
                    new ItemFoodViewDetails(null,null, merchantDetails.getName(),
                            merchantDetails.getShortAddress(), null, null, null, true);
            foodviewListAdapter.openFoodviewFragment(itemFoodViewDetails);
        }
    };

    private void setMerchantDetails() {
        setImage();
        tvName.setText(merchantDetails.getName());
        tvShortAddress.setText(merchantDetails.getShortAddress());
        tvHours.setText(Utils.parseListToCommaSeparatedString(merchantDetails.getOpeningHours()));
        tvPhone.setText(merchantDetails.getPhone());
        tvAverageCost.setText(getResources().getString(R.string.rupees)+ " " + merchantDetails.getAverageCost());
        tvType.setText(Utils.parseListToCommaSeparatedString(merchantDetails.getType()));
        tvLongAddress.setText(merchantDetails.getLongAddress());
        tvFoodviewSubHeading.setText(getResources().getString(R.string.foodViewSubHeading) + merchantDetails.getName());
    }

    private void setImage() {
        if (Utils.isNotEmpty(merchantDetails.getImageUrl())) {
            with(MerchantDetailsActivity.this)
                    .load(merchantDetails.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.grey)
                    .crossFade()
                    .into(ivImage);
        } else {
            ivImage.setImageResource(R.color.lightColor);
        }
    }

    private void setFont() {
        tvName.setTypeface(Utils.getBold(this));
        tvShortAddress.setTypeface(Utils.getRegularFont(this));
        tvHours.setTypeface(Utils.getRegularFont(this));
        tvPhone.setTypeface(Utils.getRegularFont(this));
        tvAverageCost.setTypeface(Utils.getRegularFont(this));
        tvType.setTypeface(Utils.getRegularFont(this));
        tvLongAddress.setTypeface(Utils.getRegularFont(this));
        tvHoursTxt.setTypeface(Utils.getRegularFont(this));
        tvPhoneTxt.setTypeface(Utils.getRegularFont(this));
        tvAverageCostTxt.setTypeface(Utils.getRegularFont(this));
        tvTypeTxt.setTypeface(Utils.getRegularFont(this));
        tvLongAddressTxt.setTypeface(Utils.getRegularFont(this));
        tvDelectableDishesTxt.setTypeface(Utils.getBold(this));
        tvCuisinesAndSpreadTxt.setTypeface(Utils.getBold(this));
        tvMyFoodViewsTxt.setTypeface(Utils.getBold(this));
        btnSeeMore.setTypeface(Utils.getRegularFont(this));
        tvFoodviewSubHeading.setTypeface(Utils.getRegularFont(this));
        btnAddFoodview.setTypeface(Utils.getRegularFont(this));
    }

    private void startAsync() {
        merchantDetailsView.setVisibility(View.GONE);
        activityLoader.setVisibility(View.VISIBLE);
    }

    private void endAsync() {
        merchantDetailsView.setVisibility(View.VISIBLE);
        activityLoader.setVisibility(View.GONE);
    }

    private void setToastMessages() {
        tvDelectableDishesTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= tvDelectableDishesTxt.getRight() - tvDelectableDishesTxt.getTotalPaddingRight()) {
                        showToast(false);
                    }
                }
                return true;
            }
        });

        tvCuisinesAndSpreadTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= tvCuisinesAndSpreadTxt.getRight() - tvCuisinesAndSpreadTxt.getTotalPaddingRight()) {
                        showToast(true);
                    }
                }
                return true;
            }
        });
    }

    private void showToast(boolean isCuisineToast) {
        if (isCuisineToast) {
            Toast.makeText(this,
                    "Rated based on weighted average of most recent dish ratings and the number of dishes served per cuisine.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
                    "Rated b" +
                            "ased on weighted average of most recent ratings, user credibility and number of times people have tried a dish.", Toast.LENGTH_LONG).show();
        }
    }
}
