package in.bananaa.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import in.bananaa.object.foodSuggestions.FoodSuggestion;
import in.bananaa.object.Tag;
import in.bananaa.utils.Debug;
import in.bananaa.utils.CustomImageClickListener;
import in.bananaa.utils.OnTagChipClickListener;
import in.bananaa.utils.TagChipView;
import in.bananaa.utils.Utils;

public class FoodSuggestionsRecyclerAdapter extends RecyclerView.Adapter<FoodSuggestionsRecyclerAdapter.SuggestionsViewHolder> {
    private static final String TAG = "FOOD_SUGGESTIONS";
    private List<FoodSuggestion> foodSuggestions;
    private Activity mContext;
    private List<Chip> cvHashtagsChipList;

    public class SuggestionsViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivThumbnail;
        private TextView tvName;
        private TextView tvRestName;
        private TagChipView cvHashtags;

        public SuggestionsViewHolder(View itemView) {
            super(itemView);
            this.ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            this.tvName = (TextView) itemView.findViewById(R.id.tvName);
            this.tvRestName = (TextView) itemView.findViewById(R.id.tvRestName);
            this.cvHashtags = (TagChipView) itemView.findViewById(R.id.cvHashtags);
        }
    }

    public FoodSuggestionsRecyclerAdapter(Activity activity) {
        this.foodSuggestions = new ArrayList<>();
        this.mContext = activity;
    }

    public void clearAll() {
        this.foodSuggestions.clear();
        notifyDataSetChanged();
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

    @Override
    public FoodSuggestionsRecyclerAdapter.SuggestionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.suggestion_card, parent, false);
        return new SuggestionsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FoodSuggestionsRecyclerAdapter.SuggestionsViewHolder holder, int position) {
        FoodSuggestion foodSuggestion = foodSuggestions.get(position);
        holder.tvName.setText(foodSuggestion.getName());
        holder.tvRestName.setText(foodSuggestion.getMerchant().getName() + ", " + foodSuggestion.getMerchant().getAddress().getLocality().getName());
        if (Utils.isNotEmpty(foodSuggestion.getThumbnail())) {
            Glide.with(mContext).load(foodSuggestion.getThumbnail()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivThumbnail) {
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
        setCustomImageViewerData(holder.itemView, foodSuggestions.get(position));
        setFont(holder);
    }

    @Override
    public int getItemCount() {
        return foodSuggestions.size();
    }

    private void setCustomImageViewerData(View view, FoodSuggestion i) {
        view.setOnClickListener(new CustomImageClickListener(mContext, i.getId(),
                i.getName(), i.getRecommendationCount(), i.getRating().toString(), i.getRatingClass(),
                i.getImageUrl(), true, i.getMerchant().getId(), i.getMerchant().getName(),
                i.getMerchant().getAddress().getLocality().getName()));
    }

    private void setFont(FoodSuggestionsRecyclerAdapter.SuggestionsViewHolder holder) {
        holder.tvName.setTypeface(Utils.getBold(mContext));
        holder.tvRestName.setTypeface(Utils.getRegularFont(mContext));
    }
}
