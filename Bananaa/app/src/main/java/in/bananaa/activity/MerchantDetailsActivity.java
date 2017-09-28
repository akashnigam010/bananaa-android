package in.bananaa.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.bananaa.R;
import in.bananaa.object.MerchantDetails;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Utils;

public class MerchantDetailsActivity extends AppCompatActivity {

    MerchantDetails merchantDetails;
    AlertMessages messages;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_details);
        messages = new AlertMessages(this);
        merchantDetails = (MerchantDetails) getIntent().getSerializableExtra("merchantDetails");
        initializeView();
        getMerchantDetails();
    }

    private void initializeView() {
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

        ivBack.setOnClickListener(onClickBackListener);
        setFont();
    }

    View.OnClickListener onClickBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void getMerchantDetails() {
        setImage();
        tvName.setText(merchantDetails.getName());
        tvShortAddress.setText(merchantDetails.getShortAddress());
        tvHours.setText(Utils.parseListToCommaSeparatedString(merchantDetails.getOpeningHours()));
        tvPhone.setText(merchantDetails.getPhone());
        tvAverageCost.setText(merchantDetails.getAverageCost());
        tvType.setText(Utils.parseListToCommaSeparatedString(merchantDetails.getType()));
        tvLongAddress.setText(merchantDetails.getLongAddress());
    }

    private void setImage() {
        if (Utils.isNotEmpty(merchantDetails.getImageUrl())) {
            Glide
                    .with(MerchantDetailsActivity.this)
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
    }
}
