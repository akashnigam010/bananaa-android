package in.bananaa.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.bananaa.R;
import in.bananaa.activity.ItemDetailsActivity;
import in.bananaa.activity.MerchantDetailsActivity;
import in.bananaa.object.RatingColorType;

public class CustomImageClickListener implements View.OnClickListener {

    private Context mContext;
    private Integer id;
    private String name;
    private Integer recommendationCount;
    private String rating;
    private String ratingClass;
    private String imageUrl;
    private boolean navigateToMerchantDetails;
    private Integer merchantId;
    private String merchantName;
    private String locality;
    private boolean isImageLoaded = false;

    public CustomImageClickListener(Context mContext, Integer id, String name,
                                    Integer recommendationCount, String rating,
                                    String ratingClass, String imageUrl, boolean navigateToMerchantDetails, Integer merchantId,
                                    String merchantName, String locality){
        this.mContext = mContext;
        this.id = id;
        this.name = name;
        this.recommendationCount = recommendationCount;
        this.rating = rating;
        this.ratingClass = ratingClass;
        this.imageUrl = imageUrl;
        this.navigateToMerchantDetails = navigateToMerchantDetails;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.locality = locality;
    }

    @Override
    public void onClick(View v){
        showPreview();
    }

    private void showPreview() {
        Dialog imageDialog = new Dialog(mContext);

        imageDialog.setCancelable(true);
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.setContentView(R.layout.image_viewer);
        ImageView image = (ImageView) imageDialog.findViewById(R.id.dialog_imageView);
        LinearLayout rlItemView = (LinearLayout) imageDialog.findViewById(R.id.llItemView);
        RelativeLayout rlMerchantView = (RelativeLayout) imageDialog.findViewById(R.id.rlMerchantView);
        TextView tvName = (TextView) imageDialog.findViewById(R.id.tvName);
        TextView tvRating = (TextView) imageDialog.findViewById(R.id.tvRating);
        TextView tvSubString = (TextView) imageDialog.findViewById(R.id.tvSubString);
        TextView tvRestName = (TextView) imageDialog.findViewById(R.id.tvRestName);

        GradientDrawable background = (GradientDrawable) tvRating.getBackground();
        RatingColorType colorType = RatingColorType.getCodeByCssClass(ratingClass);
        if (colorType == null) {
            colorType = RatingColorType.R25;
        }
        background.setColor(mContext.getResources().getColor(colorType.getColor()));
        TextView tvSeeMore = (TextView) imageDialog.findViewById(R.id.tvSeeMore);

        OnClickShowDetailsListener onItemDetailsListener = new OnClickShowDetailsListener(id, null, false, imageDialog);
        OnClickShowDetailsListener onMerchantDetailsListener = new OnClickShowDetailsListener(null, merchantId, true, imageDialog);

        image.setOnClickListener(onItemDetailsListener);
        rlItemView.setOnClickListener(onItemDetailsListener);
        if (navigateToMerchantDetails) {
            rlMerchantView.setOnClickListener(onMerchantDetailsListener);
        } else {
            rlMerchantView.setOnClickListener(onItemDetailsListener);
        }

        tvName.setText(name);
        tvRating.setText(rating);
        tvSubString.setText(mContext.getResources().getString(R.string.peopleRated, recommendationCount));
        if (locality != null) {
            tvRestName.setText("At " + merchantName + ", " + locality);
        } else {
            tvRestName.setText("At " + merchantName);
        }

        tvName.setTypeface(Utils.getBold(mContext));
        tvSubString.setTypeface(Utils.getRegularFont(mContext));
        tvRating.setTypeface(Utils.getRegularFont(mContext));
        tvRestName.setTypeface(Utils.getRegularFont(mContext));
        tvSeeMore.setTypeface(Utils.getRegularFont(mContext));

        loadImage(imageUrl, image);
        imageDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        imageDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        imageDialog.show();
    }

    private void loadImage(String imageUrl, ImageView image) {
        Glide.with(mContext).load(imageUrl).into(image);
    }

    private class OnClickShowDetailsListener implements View.OnClickListener {
        private Integer itemId;
        private Integer merchantId;
        boolean isMerchantDetails;
        private Dialog dialog;

        OnClickShowDetailsListener(Integer itemId, Integer merchantId, boolean isMerchantDetails, Dialog dialog) {
            this.itemId = itemId;
            this.merchantId = merchantId;
            this.isMerchantDetails = isMerchantDetails;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            dialog.cancel();
            Intent i = null;
            if (isMerchantDetails) {
                i = new Intent(mContext, MerchantDetailsActivity.class);
                i.putExtra(MerchantDetailsActivity.MERCHANT_ID, this.merchantId);
            } else {
                i = new Intent(mContext, ItemDetailsActivity.class);
                i.putExtra(ItemDetailsActivity.ITEM_ID, this.itemId);
            }
            mContext.startActivity(i);
        }
    }
}
