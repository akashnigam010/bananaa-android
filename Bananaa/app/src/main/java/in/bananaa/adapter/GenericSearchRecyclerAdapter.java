package in.bananaa.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import in.bananaa.R;
import in.bananaa.activity.MerchantDetailsActivity;
import in.bananaa.object.genericSearch.MerchantDetailsDto;
import in.bananaa.utils.Debug;
import in.bananaa.utils.Utils;

public class GenericSearchRecyclerAdapter extends RecyclerView.Adapter<GenericSearchRecyclerAdapter.MerchantDetailsViewHolder> {
    private static final String TAG = "FOOD_SUGGESTIONS";
    private List<MerchantDetailsDto> merchants;
    private Activity mContext;

    public class MerchantDetailsViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivThumbnail;
        private TextView tvType;
        private TextView tvName;
        private TextView tvAddress;
        private LinearLayout llRatingAndDishDetails;
        private TextView tvRating;
        private TextView tvRatedIn;
        private TextView tvSpread;
        private TextView tvCostTxt;
        private TextView tvCost;
        private TextView tvHoursTxt;
        private TextView tvHours;
        private TextView tvKnownForTxt;
        private TextView tvKnownFor;

        public MerchantDetailsViewHolder(View itemView) {
            super(itemView);
            this.ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            this.tvType = (TextView) itemView.findViewById(R.id.tvType);
            this.tvName = (TextView) itemView.findViewById(R.id.tvName);
            this.tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            llRatingAndDishDetails = (LinearLayout) itemView.findViewById(R.id.llRatingAndDishDetails);
            tvRating = (TextView) itemView.findViewById(R.id.tvRating);
            tvRatedIn = (TextView) itemView.findViewById(R.id.tvRatedIn);
            tvSpread = (TextView) itemView.findViewById(R.id.tvSpread);
            tvCostTxt = (TextView) itemView.findViewById(R.id.tvCostTxt);
            tvCost = (TextView) itemView.findViewById(R.id.tvCost);
            tvHoursTxt = (TextView) itemView.findViewById(R.id.tvHoursTxt);
            tvHours = (TextView) itemView.findViewById(R.id.tvHours);
            tvKnownForTxt = (TextView) itemView.findViewById(R.id.tvKnownForTxt);
            tvKnownFor = (TextView) itemView.findViewById(R.id.tvKnownFor);
        }
    }

    public GenericSearchRecyclerAdapter(Activity activity) {
        this.merchants = new ArrayList<>();
        this.mContext = activity;
    }

    public void clearAll() {
        this.merchants.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<MerchantDetailsDto> merchants) {
        this.merchants.clear();
        appendAll(merchants);
    }

    public void appendAll(List<MerchantDetailsDto> merchants) {
        try {
            this.merchants.addAll(merchants);
        } catch (Exception e) {
            Debug.e(TAG, e.getMessage());
        }
        notifyDataSetChanged();
    }

    @Override
    public GenericSearchRecyclerAdapter.MerchantDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.generic_search_card, parent, false);
        return new MerchantDetailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GenericSearchRecyclerAdapter.MerchantDetailsViewHolder holder, int position) {
        final MerchantDetailsDto merchantDetailsDto = merchants.get(position);
        if (Utils.isNotEmpty(merchantDetailsDto.getThumbnail())) {
            Glide.with(mContext).load(merchantDetailsDto.getThumbnail()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.ivThumbnail) {
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
        holder.tvType.setText(Utils.parseListToCommaSeparatedString(merchantDetailsDto.getType()));
        holder.tvName.setText(merchantDetailsDto.getName());
        holder.tvAddress.setText(merchantDetailsDto.getShortAddress());
        if (merchantDetailsDto.getSearchTag() != null) {
            holder.llRatingAndDishDetails.setVisibility(View.VISIBLE);
            if (merchantDetailsDto.getSearchTag().getRating() != null) {
                holder.tvRating.setText(merchantDetailsDto.getSearchTag().getRating());
                if (Utils.isNotEmpty(merchantDetailsDto.getSearchTag().getName())) {
                    holder.tvRatedIn.setText("Rated in " + merchantDetailsDto.getSearchTag().getName());
                } else {
                    holder.tvRatedIn.setText("Rated by overall food");
                }
            } else if (Utils.isNotEmpty(merchantDetailsDto.getSearchTag().getName())) {
                holder.tvRating.setVisibility(View.GONE);
                holder.tvRatedIn.setText("Dish found: " + merchantDetailsDto.getSearchTag().getName());
            }
            if (merchantDetailsDto.getSearchTag().getDishCount() != null) {
                holder.tvSpread.setText("Spread of " + merchantDetailsDto.getSearchTag().getDishCount() + "+ dishes");
                holder.tvSpread.setVisibility(View.VISIBLE);
            } else {
                holder.tvSpread.setVisibility(View.GONE);
            }
        } else {
            holder.llRatingAndDishDetails.setVisibility(View.GONE);
        }
        holder.tvCost.setText(mContext.getResources().getString(R.string.rupees)+ " " + merchantDetailsDto.getAverageCost() + " for two");
        holder.tvHours.setText(Utils.parseListToCommaSeparatedString(merchantDetailsDto.getOpeningHours()));
        if (merchantDetailsDto.getRatedCuisines().size() > 0) {
            holder.tvKnownFor.setText(Utils.getTagsString(merchantDetailsDto.getRatedCuisines()));
        } else {
            holder.tvKnownForTxt.setVisibility(View.GONE);
            holder.tvKnownFor.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, MerchantDetailsActivity.class);
                i.putExtra(MerchantDetailsActivity.MERCHANT_ID, merchantDetailsDto.getId());
                mContext.startActivity(i);
            }
        });
        setFont(holder);
    }

    @Override
    public int getItemCount() {
        return merchants.size();
    }

    private void setFont(GenericSearchRecyclerAdapter.MerchantDetailsViewHolder holder) {
        holder.tvType.setTypeface(Utils.getRegularFont(mContext));
        holder.tvName.setTypeface(Utils.getBold(mContext));
        holder.tvAddress.setTypeface(Utils.getRegularFont(mContext));
        holder.tvRating.setTypeface(Utils.getBold(mContext));
        holder.tvRatedIn.setTypeface(Utils.getBold(mContext));
        holder.tvSpread.setTypeface(Utils.getBold(mContext));
        holder.tvCost.setTypeface(Utils.getRegularFont(mContext));
        holder.tvHours.setTypeface(Utils.getRegularFont(mContext));
        holder.tvHoursTxt.setTypeface(Utils.getRegularFont(mContext));
        holder.tvCostTxt.setTypeface(Utils.getRegularFont(mContext));
        holder.tvKnownForTxt.setTypeface(Utils.getRegularFont(mContext));
        holder.tvKnownFor.setTypeface(Utils.getRegularFont(mContext));
    }


}
