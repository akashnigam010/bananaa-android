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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import in.bananaa.R;
import in.bananaa.activity.ItemDetailsActivity;
import in.bananaa.object.RatingColorType;

public class CustomImageClickListener implements View.OnClickListener {

    private Context mContext;
    private Integer id;
    private String name;
    private Integer recommendationCount;
    private String rating;
    private String ratingClass;
    private String imageUrl;

    public CustomImageClickListener(Context mContext, Integer id, String name,
                                    Integer recommendationCount, String rating,
                                    String ratingClass, String imageUrl){
        this.mContext = mContext;
        this.id = id;
        this.name = name;
        this.recommendationCount = recommendationCount;
        this.rating = rating;
        this.ratingClass = ratingClass;
        this.imageUrl = imageUrl;
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
        TextView tvName = (TextView) imageDialog.findViewById(R.id.tvName);
        TextView tvRating = (TextView) imageDialog.findViewById(R.id.tvRating);
        TextView tvSubString = (TextView) imageDialog.findViewById(R.id.tvSubString);

        GradientDrawable background = (GradientDrawable) tvRating.getBackground();
        RatingColorType colorType = RatingColorType.getCodeByCssClass(ratingClass);
        if (colorType == null) {
            colorType = RatingColorType.R25;
        }
        background.setColor(mContext.getResources().getColor(colorType.getColor()));
        LinearLayout llItemImageViewer = (LinearLayout) imageDialog.findViewById(R.id.llItemImageViewer);
        TextView tvSeeMore = (TextView) imageDialog.findViewById(R.id.tvSeeMore);
        llItemImageViewer.setOnClickListener(new OnSeeMoreClickListener(id, imageDialog));

        tvName.setText(name);
        tvRating.setText(rating);
        tvSubString.setText(mContext.getResources().getString(R.string.peopleRated, recommendationCount));

        tvName.setTypeface(Utils.getBold(mContext));
        tvSubString.setTypeface(Utils.getRegularFont(mContext));
        tvRating.setTypeface(Utils.getRegularFont(mContext));
        tvSeeMore.setTypeface(Utils.getRegularFont(mContext));

        Glide.with(mContext).load(imageUrl).into(image);
        imageDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        imageDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        imageDialog.show();
    }

    private class OnSeeMoreClickListener implements View.OnClickListener {
        private Integer itemId;
        private Dialog dialog;

        OnSeeMoreClickListener(Integer itemId, Dialog dialog) {
            this.itemId = itemId;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            dialog.cancel();
            Intent i = new Intent(mContext, ItemDetailsActivity.class);
            i.putExtra(ItemDetailsActivity.ITEM_ID, this.itemId);
            mContext.startActivity(i);
        }
    }
}
