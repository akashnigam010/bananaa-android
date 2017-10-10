package in.bananaa.adapter;

import android.content.Context;
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
import in.bananaa.object.Foodview;
import in.bananaa.object.RatingColorType;
import in.bananaa.utils.Debug;
import in.bananaa.utils.Utils;

import static in.bananaa.R.layout.foodview;

public class FoodviewsListAdapter extends BaseAdapter {

    private static final String TAG = "FOODVIEW_LIST_ADAPTER";
    List<Foodview> foodviews;
    private AppCompatActivity mContext;
    private LayoutInflater infalter;
    GradientDrawable background;

    public FoodviewsListAdapter(AppCompatActivity activity) {
        infalter = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = activity;
        foodviews = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return foodviews.size();
    }

    @Override
    public Object getItem(int position) {
        return foodviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<Foodview> myFoodviews) {
        this.foodviews.clear();
        appendAll(myFoodviews);
    }

    public void appendAll(List<Foodview> myFoodviews) {
        try {
            this.foodviews.addAll(myFoodviews);
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

        holder.tvName.setText(foodviews.get(position).getUserName());
        setSubheading(holder, foodviews.get(position).getUserRatingCount(), foodviews.get(position).getUserFoodviewCount());
        holder.tvRating.setText(foodviews.get(position).getRating());
        holder.tvYouRated.setVisibility(View.GONE);
        background = (GradientDrawable) holder.tvRating.getBackground();
        RatingColorType colorType = RatingColorType.getCodeByCssClass(foodviews.get(position).getRatingClass());
        if (colorType == null) {
            colorType = RatingColorType.R25;
        }
        background.setColor(mContext.getResources().getColor(colorType.getColor()));
        if (Utils.isEmpty(foodviews.get(position).getDesc())) {
            holder.tvDescription.setVisibility(View.GONE);
        } else {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(foodviews.get(position).getDesc());
        }
        holder.tvTimeDiff.setText(foodviews.get(position).getTimeDiff());

        if (Utils.isNotEmpty(foodviews.get(position).getUserImageUrl())) {
            Glide.with(mContext).load(foodviews.get(position).getUserImageUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivThumbnail) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.ivThumbnail.setImageDrawable(circularBitmapDrawable);
                }
            });
            convertView.setOnClickListener(new CustomUserClickListener(mContext, foodviews.get(position).getUserId()));
        } else {
            holder.ivThumbnail.setImageResource(R.color.lightColor);
        }

        holder.tvName.setOnClickListener(new CustomUserClickListener(mContext, foodviews.get(position).getUserId()));
        holder.tvSubString.setOnClickListener(new CustomUserClickListener(mContext, foodviews.get(position).getUserId()));
        setFont(holder);
        return convertView;
    }

    private class CustomUserClickListener implements View.OnClickListener{
        private Context mContext;
        private Integer userId;

        public CustomUserClickListener(Context context, Integer userId){
            this.mContext = context;
            this.userId = userId;
        }

        @Override
        public void onClick(View v){
            // open user profile using user id
        }
    }

    private void setSubheading(ViewHolder holder, Integer ratingCount, Integer foodviewCount) {
        holder.tvSubString.setText(mContext.getResources().getString(R.string.ratingAndFoodviwes, ratingCount, foodviewCount));
    }

    private void setFont(ViewHolder holder) {
        holder.tvName.setTypeface(Utils.getBold(mContext));
        holder.tvSubString.setTypeface(Utils.getRegularFont(mContext));
        holder.tvRating.setTypeface(Utils.getRegularFont(mContext));
        holder.tvDescription.setTypeface(Utils.getRegularFont(mContext));
        holder.tvTimeDiff.setTypeface(Utils.getRegularFont(mContext));
    }
}
