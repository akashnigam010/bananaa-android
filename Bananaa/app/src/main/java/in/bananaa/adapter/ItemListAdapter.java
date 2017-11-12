package in.bananaa.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import in.bananaa.object.Item;
import in.bananaa.object.MerchantDetailsResponse;
import in.bananaa.object.RatingColorType;
import in.bananaa.utils.Debug;
import in.bananaa.utils.CustomImageClickListener;
import in.bananaa.utils.Utils;

public class ItemListAdapter extends BaseAdapter {

    private static final String TAG = "ITEM_LIST_ADAPTER";
    MerchantDetailsResponse merchantDetails;
    private List<Item> items;
    private Activity mContext;
    private LayoutInflater infalter;
    GradientDrawable background;

    public ItemListAdapter(Activity activity, MerchantDetailsResponse merchantDetails) {
        infalter = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = activity;
        this.merchantDetails = merchantDetails;
        items = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<Item> items) {
        this.items.clear();
        appendAll(items);
    }

    public void appendAll(List<Item> items) {
        try {
            this.items.addAll(items);
        } catch (Exception e) {
            Debug.e(TAG, e.getMessage());
        }

        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView ivThumbnail;
        TextView tvName, tvSubString, tvRating;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.item_tag, null);
            holder = new ViewHolder();
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvSubString = (TextView) convertView.findViewById(R.id.tvRatingCount);
            holder.tvRating = (TextView) convertView.findViewById(R.id.tvRating);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(items.get(position).getName());
        holder.tvSubString.setText(mContext.getResources().getString(R.string.peopleRated, items.get(position).getRecommendations()));
        holder.tvRating.setText(items.get(position).getRating());
        background = (GradientDrawable) holder.tvRating.getBackground();
        RatingColorType colorType = RatingColorType.getCodeByCssClass(items.get(position).getRatingClass());
        if (colorType == null) {
            colorType = RatingColorType.R25;
        }
        background.setColor(mContext.getResources().getColor(colorType.getColor()));

        if (Utils.isNotEmpty(items.get(position).getThumbnail())) {
            Glide.with(mContext).load(items.get(position).getThumbnail()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivThumbnail) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.ivThumbnail.setImageDrawable(circularBitmapDrawable);
                }
            });
            setCustomImageViewerData(convertView, items.get(position));
        } else {
            holder.ivThumbnail.setImageResource(R.color.lightColor);
        }
        setFont(holder);
        return convertView;
    }

    private void setCustomImageViewerData(View convertView, Item i) {
        convertView.setOnClickListener(new CustomImageClickListener(mContext, i.getId(),
                i.getName(), i.getCost(), i.getRecommendations(), i.getRating(), i.getRatingClass(), i.getImageUrl(),
                false, null, merchantDetails.getName(), null));
    }

    private void setFont(ViewHolder holder) {
        holder.tvName.setTypeface(Utils.getBold(mContext));
        holder.tvSubString.setTypeface(Utils.getRegularFont(mContext));
        holder.tvRating.setTypeface(Utils.getRegularFont(mContext));
    }
}
