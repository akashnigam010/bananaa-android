package in.bananaa.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipViewAdapter;

import java.util.ArrayList;
import java.util.List;

import in.bananaa.R;
import in.bananaa.object.FoodSuggestions.FoodSuggestion;
import in.bananaa.object.Tag;
import in.bananaa.utils.CustomImageClickListener;
import in.bananaa.utils.Debug;
import in.bananaa.utils.OnTagChipClickListener;
import in.bananaa.utils.TagChipView;
import in.bananaa.utils.Utils;

public class FoodSuggestionsAdapter extends BaseAdapter {
    private static final String TAG = "GLOBAL_SEARCH";
    private List<FoodSuggestion> foodSuggestions;
    private Activity mContext;
    private LayoutInflater infalter;
    private List<Chip> cvHashtagsChipList;

    public FoodSuggestionsAdapter(Activity activity) {
        foodSuggestions = new ArrayList<>();
        infalter = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = activity;
    }

    @Override
    public int getCount() {
        return foodSuggestions.size();
    }

    @Override
    public FoodSuggestion getItem(int position) {
        return foodSuggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<FoodSuggestion> foodSuggestions) {
        this.foodSuggestions.clear();
        appendAll(foodSuggestions);
    }

    public void appendAll(List<FoodSuggestion> foodSuggestions) {
        try {
            this.foodSuggestions.addAll(foodSuggestions);
        } catch (Exception e) {
            Debug.e(TAG, e.getMessage());
        }

        notifyDataSetChanged();
    }

    public class ViewHolder {
        ImageView ivThumbnail;
        TextView tvName;
        TextView tvRestName, tvHashtags;
        TagChipView cvHashtags;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.suggestion_card, null);
            holder = new ViewHolder();
            holder.ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvRestName = (TextView) convertView.findViewById(R.id.tvRestName);
            holder.cvHashtags = (TagChipView) convertView.findViewById(R.id.cvHashtags);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(foodSuggestions.get(position).getName());
        holder.tvRestName.setText(foodSuggestions.get(position).getMerchant().getName() + ", " + foodSuggestions.get(position).getMerchant().getAddress().getLocality().getName());
        if (Utils.isNotEmpty(foodSuggestions.get(position).getThumbnail())) {
            Glide.with(mContext).load(foodSuggestions.get(position).getThumbnail()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivThumbnail) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable dr =
                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);

                    dr.setCornerRadius(10);
                    holder.ivThumbnail.setImageDrawable(dr);
                }
            });
        } else {
            holder.ivThumbnail.setImageResource(R.color.lightColor);
        }
        holder.cvHashtags.setChipLayoutRes(R.layout.chip);
        ChipViewAdapter cvCuisineChipViewAdapter = new HashtagChipViewAdapter(mContext);
        holder.cvHashtags.setAdapter(cvCuisineChipViewAdapter);
        cvHashtagsChipList = new ArrayList<>();
        cvHashtagsChipList.addAll(foodSuggestions.get(position).getSuggestions());
        holder.cvHashtags.setChipList(cvHashtagsChipList);
        holder.cvHashtags.setOnChipClickListener(new OnTagChipClickListener() {
            @Override
            public void onChipClick(Chip chip, View view, int i) {
                Tag hashtag = (Tag) chip;
                Toast.makeText(mContext, hashtag.getName(), Toast.LENGTH_LONG).show();
            }
        });
        setCustomImageViewerData(convertView, foodSuggestions.get(position));
        setFont(holder);
        return convertView;
    }

    private void setCustomImageViewerData(View convertView, FoodSuggestion i) {
        convertView.setOnClickListener(new CustomImageClickListener(mContext, i.getId(),
                i.getName(), i.getRecommendationCount(), i.getRating().toString(), i.getRatingClass(), i.getImageUrl()));
    }

    private void setFont(ViewHolder holder) {
        holder.tvName.setTypeface(Utils.getBold(mContext));
        holder.tvRestName.setTypeface(Utils.getRegularFont(mContext));
    }
}
