package in.bananaa.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
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
import in.bananaa.activity.ItemDetailsActivity;
import in.bananaa.activity.MerchantDetailsActivity;
import in.bananaa.object.Foodview;
import in.bananaa.object.RatingColorType;
import in.bananaa.utils.Debug;
import in.bananaa.utils.Utils;

import static in.bananaa.R.id.tvName;
import static in.bananaa.R.layout.foodview;

public class RatingsAndFoodviewsListAdapter extends BaseAdapter {
    private static final String TAG = "RATING_AND_FOODVIEWS_LIST_ADAPTER";
    List<Foodview> foodviews;
    private AppCompatActivity mContext;
    private LayoutInflater infalter;
    GradientDrawable background;

    public RatingsAndFoodviewsListAdapter(AppCompatActivity activity) {
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

    public void addAll(List<Foodview> foodviews) {
        this.foodviews.clear();
        appendAll(foodviews);
    }

    public void appendAll(List<Foodview> foodviews) {
        try {
            this.foodviews.addAll(foodviews);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(foodview, null);
            holder = new ViewHolder();
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
            holder.tvName = (TextView) convertView.findViewById(tvName);
            holder.tvSubString = (TextView) convertView.findViewById(R.id.tvSubString);
            holder.tvRating = (TextView) convertView.findViewById(R.id.tvRating);
            holder.tvYouRated = (TextView) convertView.findViewById(R.id.tvYouRated);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            holder.tvTimeDiff = (TextView) convertView.findViewById(R.id.tvTimeDiff);
            holder.tvYouRated.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(foodviews.get(position).getName());
        holder.tvSubString.setText("@ " + foodviews.get(position).getMerchantName());
        holder.tvRating.setText(foodviews.get(position).getRating());
        background = (GradientDrawable) holder.tvRating.getBackground();
        RatingColorType colorType = RatingColorType.getCodeByCssClass(foodviews.get(position).getRatingClass());
        if (colorType == null) {
            colorType = RatingColorType.R25;
        }
        background.setColor(ContextCompat.getColor(mContext, colorType.getColor()));
        if (Utils.isEmpty(foodviews.get(position).getDescription())) {
            holder.tvDescription.setVisibility(View.GONE);
        } else {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(foodviews.get(position).getDescription());
        }
        holder.tvTimeDiff.setText(foodviews.get(position).getTimeDiff());
        CustomImageClickListener listener = new CustomImageClickListener(mContext, foodviews.get(position));
        if (Utils.isNotEmpty(foodviews.get(position).getThumbnail())) {
            Glide.with(mContext).load(foodviews.get(position).getThumbnail()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivThumbnail) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.ivThumbnail.setImageDrawable(circularBitmapDrawable);
                }
            });
            convertView.setOnClickListener(listener);
        } else {
            holder.ivThumbnail.setImageResource(R.color.lightColor);
        }
        holder.tvName.setOnClickListener(listener);
        holder.tvSubString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, MerchantDetailsActivity.class);
                i.putExtra(MerchantDetailsActivity.MERCHANT_ID, foodviews.get(position).getMerchantId());
                mContext.startActivity(i);
            }
        });
        setFont(holder);
        return convertView;
    }

    private class CustomImageClickListener implements View.OnClickListener{
        private Context mContext;
        private Foodview foodview;

        public CustomImageClickListener(Context context, Foodview foodview){
            this.mContext = context;
            this.foodview = foodview;
        }

        @Override
        public void onClick(View v){
            Intent i = new Intent(mContext, ItemDetailsActivity.class);
            i.putExtra(ItemDetailsActivity.ITEM_ID, foodview.getItemId());
            mContext.startActivity(i);
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
}
