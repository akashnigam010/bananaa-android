package in.bananaa.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import in.bananaa.R;
import in.bananaa.activity.FoodviewActivity;
import in.bananaa.object.ItemFoodViewDetails;
import in.bananaa.object.MyFoodview;
import in.bananaa.object.RatingColorType;
import in.bananaa.utils.Debug;
import in.bananaa.utils.Utils;

import static in.bananaa.R.layout.foodview;

public class MyFoodviewsListAdapter extends BaseAdapter {

    private static final String TAG = "MY_FOODVIEW_LIST_ADAPTER";
    List<MyFoodview> myFoodviews;
    private AppCompatActivity mContext;
    private LayoutInflater infalter;
    GradientDrawable background;
    Integer merchantId;
    String merchantName;
    String locality;

    public MyFoodviewsListAdapter(AppCompatActivity activity, Integer merchantId, String merchantName, String locality) {
        infalter = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = activity;
        myFoodviews = new ArrayList<>();
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.locality = locality;
    }

    @Override
    public int getCount() {
        return myFoodviews.size();
    }

    @Override
    public Object getItem(int position) {
        return myFoodviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<MyFoodview> myFoodviews) {
        this.myFoodviews.clear();
        appendAll(myFoodviews);
    }

    public void appendAll(List<MyFoodview> myFoodviews) {
        try {
            this.myFoodviews.addAll(myFoodviews);
        } catch (Exception e) {
            Debug.e(TAG, e.getMessage());
        }

        notifyDataSetChanged();
    }

    public class ViewHolder {
        ImageView ivThumbnail;
        TextView tvName, tvSubString, tvRating, tvYouRated, tvDescription, tvTimeDiff;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(foodview, null);
            holder = new ViewHolder();
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvSubString = (TextView) convertView.findViewById(R.id.tvSubString);
            holder.tvRating = (TextView) convertView.findViewById(R.id.tvRating);
            holder.tvYouRated = (TextView) convertView.findViewById(R.id.tvYouRated);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            holder.tvTimeDiff = (TextView) convertView.findViewById(R.id.tvTimeDiff);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(myFoodviews.get(position).getName());
        setSubheading(holder, myFoodviews.get(position).getTotalRcmdns());
        holder.tvRating.setText(myFoodviews.get(position).getRating());
        background = (GradientDrawable) holder.tvRating.getBackground();
        RatingColorType colorType = RatingColorType.getCodeByCssClass(myFoodviews.get(position).getRatingClass());
        if (colorType == null) {
            colorType = RatingColorType.R25;
        }
        background.setColor(mContext.getResources().getColor(colorType.getColor()));
        if (Utils.isEmpty(myFoodviews.get(position).getDescription())) {
            holder.tvDescription.setVisibility(View.GONE);
        } else {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(myFoodviews.get(position).getDescription());
        }
        holder.tvTimeDiff.setText(myFoodviews.get(position).getTimeDiff());

        if (Utils.isNotEmpty(myFoodviews.get(position).getThumbnail())) {
            Glide.with(mContext).load(myFoodviews.get(position).getThumbnail()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivThumbnail) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.ivThumbnail.setImageDrawable(circularBitmapDrawable);
                }
            });
            convertView.setOnClickListener(new CustomImageClickListener(mContext, myFoodviews.get(position)));
        } else {
            holder.ivThumbnail.setImageResource(R.color.lightColor);
        }
        setFont(holder);
        return convertView;
    }

    private class CustomImageClickListener implements View.OnClickListener{
        private Context mContext;
        private MyFoodview myFoodview;

        public CustomImageClickListener(Context context, MyFoodview myFoodview){
            this.mContext = context;
            this.myFoodview = myFoodview;
        }

        @Override
        public void onClick(View v){
            openRecommendationModal(this.myFoodview);
        }
    }

    private void setSubheading(ViewHolder holder, Integer totalRcmds) {
        if (totalRcmds == 1) {
            holder.tvSubString.setText(mContext.getResources().getString(R.string.foodviewCountStr1));
        } else {
            holder.tvSubString.setText(mContext.getResources().getString(R.string.foodviewCountStr2, (totalRcmds-1)));
        }
    }

    private void setFont(ViewHolder holder) {
        holder.tvName.setTypeface(Utils.getBold(mContext));
        holder.tvSubString.setTypeface(Utils.getRegularFont(mContext));
        holder.tvRating.setTypeface(Utils.getRegularFont(mContext));
        holder.tvYouRated.setTypeface(Utils.getRegularFont(mContext));
        holder.tvDescription.setTypeface(Utils.getRegularFont(mContext));
        holder.tvTimeDiff.setTypeface(Utils.getRegularFont(mContext));
    }

    private void openRecommendationModal(MyFoodview myFoodview) {
        ItemFoodViewDetails itemFoodViewDetails =
                new ItemFoodViewDetails(myFoodview.getItemId(),
                        merchantId, merchantName, locality, myFoodview.getName(),
                        false);
        Intent i = new Intent(mContext, FoodviewActivity.class);
        i.putExtra(FoodviewActivity.FOODVIEW_DETAILS, itemFoodViewDetails);
        mContext.startActivity(i);
    }
}
