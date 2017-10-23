package in.bananaa.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.bananaa.R;
import in.bananaa.object.location.Locality;
import in.bananaa.object.location.LocationStore;
import in.bananaa.object.location.LocationType;
import in.bananaa.utils.Debug;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.Utils;

public class LocalityAdapter extends BaseAdapter {
    private static final String TAG = "GLOBAL_SEARCH";
    private List<Locality> localities;
    private Activity mContext;
    private LayoutInflater infalter;

    public LocalityAdapter(Activity activity) {
        localities = new ArrayList<>();
        infalter = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = activity;
    }

    @Override
    public int getCount() {
        return localities.size();
    }

    @Override
    public Locality getItem(int position) {
        return localities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<Locality> localities) {
        this.localities.clear();
        appendAll(localities);
    }

    public void appendAll(List<Locality> localities) {
        try {
            this.localities.addAll(localities);
        } catch (Exception e) {
            Debug.e(TAG, e.getMessage());
        }

        notifyDataSetChanged();
    }

    public class ViewHolder {
        TextView tvName;
        TextView tvMetaData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.row_search_result, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvMetaData = (TextView) convertView.findViewById(R.id.tvMetaData);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Locality locality = localities.get(position);
        holder.tvName.setText(locality.getName());
        holder.tvMetaData.setVisibility(View.GONE);
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationStore locationStore = new LocationStore(locality.getId(), locality.getName(), LocationType.LOCALITY);
                PreferenceManager.setStoredLocation(locationStore);
                finishActivityWithResult();
            }
        });
        setFont(holder);
        return convertView;
    }

    private void finishActivityWithResult() {
        Intent i = mContext.getIntent();
        mContext.setResult(Activity.RESULT_OK, i);
        mContext.finish();
    }

    private void setFont(ViewHolder holder) {
        holder.tvName.setTypeface(Utils.getRegularFont(mContext));
    }
}
