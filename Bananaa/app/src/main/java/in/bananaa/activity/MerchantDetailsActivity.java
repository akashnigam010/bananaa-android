package in.bananaa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.bananaa.R;
import in.bananaa.object.MerchantDetails;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Utils;

public class MerchantDetailsActivity extends AppCompatActivity {

    MerchantDetails details;
    AlertMessages messages;

    ImageView ivImage;
    ImageView ivShare;
    Toolbar toolbar;
    TextView tvName;
    TextView tvShortAddress;
    TextView tvHours;
    TextView tvPhone;
    TextView tvAverageCost;
    TextView tvType;
    TextView tvLongAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_details);

        messages = new AlertMessages(this);
        initializeView();
    }

    private void initializeView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivShare = (ImageView) findViewById(R.id.ivShare);
        tvName = (TextView) findViewById(R.id.tvName);

        tvShortAddress = (TextView) findViewById(R.id.tvShortAddress);
        tvHours = (TextView) findViewById(R.id.tvHours);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvAverageCost = (TextView) findViewById(R.id.tvAverageCost);
        tvType = (TextView) findViewById(R.id.tvType);
        tvLongAddress = (TextView) findViewById(R.id.tvLongAddress);
        setFont();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                getMerchantDetails();
                break;
            default:
                messages.showCustomMessage("Some thing is fishy! Please try after some time.");
        }
    }

    private void getMerchantDetails() {
        setImage();
        tvName.setText(details.getName());
        tvShortAddress.setText(details.getShortAddress());
        tvHours.setText(Utils.parseListToCommaSeparatedString(details.getOpeningHours()));
        tvPhone.setText(details.getPhone());
        tvAverageCost.setText(details.getAverageCost());
        tvType.setText(Utils.parseListToCommaSeparatedString(details.getType()));
        tvLongAddress.setText(details.getLongAddress());
    }

    private void setImage() {
        if (Utils.isNotEmpty(details.getImageUrl())) {
            Glide
                    .with(MerchantDetailsActivity.this)
                    .load(details.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.grey)
                    .crossFade()
                    .into(ivImage);
        } else {
            ivImage.setImageResource(R.color.lightColor);
        }
    }

    private void setFont() {
        tvName.setTypeface(Utils.getRegularFont(this));
        tvShortAddress.setTypeface(Utils.getRegularFont(this));
        tvHours.setTypeface(Utils.getRegularFont(this));
        tvPhone.setTypeface(Utils.getRegularFont(this));
        tvAverageCost.setTypeface(Utils.getRegularFont(this));
        tvType.setTypeface(Utils.getRegularFont(this));
        tvLongAddress.setTypeface(Utils.getRegularFont(this));
    }
}
