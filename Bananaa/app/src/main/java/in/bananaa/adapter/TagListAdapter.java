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
import in.bananaa.object.RatingColorType;
import in.bananaa.object.Tag;
import in.bananaa.utils.Debug;
import in.bananaa.utils.Utils;

public class TagListAdapter extends BaseAdapter {

    private static final String TAG = "TAG_LIST_ADAPTER";
    List<Tag> tags;
    private Activity mContext;
    private LayoutInflater infalter;
    GradientDrawable background;

    public TagListAdapter(Activity activity) {
        infalter = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = activity;
        tags = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int position) {
        return tags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<Tag> tags) {
        this.tags.clear();
        appendAll(tags);
    }

    public void appendAll(List<Tag> tags) {
        try {
            this.tags.addAll(tags);
        } catch (Exception e) {
            Debug.e(TAG, e.getMessage());
        }

        notifyDataSetChanged();
    }

    public class ViewHolder {
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

        holder.tvName.setText(tags.get(position).getName());
        holder.tvSubString.setText("Spread of " + tags.get(position).getDishCount() + "+ dishes");
        holder.tvRating.setText(tags.get(position).getRating());
        setBackgroundColor(holder, tags.get(position).getRatingClass());

        if (Utils.isNotEmpty(tags.get(position).getThumbnail())) {
            Glide.with(mContext).load(tags.get(position).getThumbnail()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivThumbnail) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.ivThumbnail.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            holder.ivThumbnail.setImageResource(R.color.lightColor);
        }
        setFont(holder);
        return convertView;
    }

    private void setFont(ViewHolder holder) {
        holder.tvName.setTypeface(Utils.getBold(mContext));
        holder.tvSubString.setTypeface(Utils.getRegularFont(mContext));
        holder.tvRating.setTypeface(Utils.getRegularFont(mContext));
    }

    private void setBackgroundColor(ViewHolder holder, String ratingClass) {
        background = (GradientDrawable) holder.tvRating.getBackground();
        RatingColorType colorType = RatingColorType.getCodeByCssClass(ratingClass);
        background.setColor(mContext.getResources().getColor(colorType.getColor()));
    }
}
