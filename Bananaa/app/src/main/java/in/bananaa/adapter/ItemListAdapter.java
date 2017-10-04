package in.bananaa.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import in.bananaa.R;
import in.bananaa.object.Item;
import in.bananaa.object.RatingColorType;
import in.bananaa.utils.Debug;
import in.bananaa.utils.Utils;

public class ItemListAdapter extends BaseAdapter {

    private static final String TAG = "ITEM_LIST_ADAPTER";
    List<Item> items;
    private Activity mContext;
    private LayoutInflater infalter;
    GradientDrawable background;

    Dialog imageViewer;
    ImageView image;

    public ItemListAdapter(Activity activity) {
        infalter = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = activity;
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
            holder.tvSubString = (TextView) convertView.findViewById(R.id.tvSubString);
            holder.tvRating = (TextView) convertView.findViewById(R.id.tvRating);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(items.get(position).getName());
        holder.tvSubString.setText(items.get(position).getRecommendations() + " people rated");
        holder.tvRating.setText(items.get(position).getRating());
        setBackgroundColor(holder, items.get(position).getRatingClass());

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
            holder.ivThumbnail.setOnClickListener(new CustomImageClickListener(mContext, items.get(position).getThumbnail()));
        } else {
            holder.ivThumbnail.setImageResource(R.color.lightColor);
        }
        setFont(holder);
        return convertView;
    }

    private class CustomImageClickListener implements View.OnClickListener{
        private Context mContext;
        private String imageUrl;
        public CustomImageClickListener(Context context, String imageUrl){
            this.mContext = context;
            this.imageUrl = imageUrl;
        }

        @Override
        public void onClick(View v){
            showImage(this.imageUrl);
        }
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

    private void showImage(String imageUrl) {
        imageViewer = new Dialog(mContext);
        imageViewer.setCancelable(true);
        imageViewer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageViewer.setContentView(R.layout.image_viewer);
        image = (ImageView) imageViewer.findViewById(R.id.dialog_imageView);
        Glide.with(mContext).load(imageUrl).into(image);
        imageViewer.show();
    }
}
