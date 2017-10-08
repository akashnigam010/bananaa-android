package in.bananaa.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.bananaa.R;
import in.bananaa.object.DishSearchItem;
import in.bananaa.utils.Debug;
import in.bananaa.utils.Utils;

public class DishSearchAdapter extends BaseAdapter {
    private static final String TAG = "GLOBAL_SEARCH";
    private List<DishSearchItem> searchEntities;
    private Activity mContext;
    private LayoutInflater infalter;

    public DishSearchAdapter(Activity activity) {
        searchEntities = new ArrayList<>();
        infalter = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = activity;
    }

    @Override
    public int getCount() {
        return searchEntities.size();
    }

    @Override
    public DishSearchItem getItem(int position) {
        return searchEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<DishSearchItem> searchEntities) {
        this.searchEntities.clear();
        appendAll(searchEntities);
    }

    public void appendAll(List<DishSearchItem> searchEntities) {
        try {
            this.searchEntities.addAll(searchEntities);
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
            convertView = infalter.inflate(R.layout.row_global_search_result, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvMetaData = (TextView) convertView.findViewById(R.id.tvMetaData);
            holder.tvMetaData.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(searchEntities.get(position).getName());
        setFont(holder);
        return convertView;
    }

    private void setFont(ViewHolder holder) {
        holder.tvName.setTypeface(Utils.getRegularFont(mContext));
    }
}
