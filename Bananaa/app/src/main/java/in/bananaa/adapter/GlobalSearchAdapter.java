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
import in.bananaa.object.SearchItem;
import in.bananaa.utils.Debug;
import in.bananaa.utils.Utils;

import static in.bananaa.object.SearchResultType.CUISINE;
import static in.bananaa.object.SearchResultType.DISH;

public class GlobalSearchAdapter extends BaseAdapter {
    private static final String TAG = "GLOBAL_SEARCH";
    private List<SearchItem> searchEntities;
    private Activity mContext;
    private LayoutInflater infalter;

    public GlobalSearchAdapter(Activity activity) {
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
    public SearchItem getItem(int position) {
        return searchEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<SearchItem> searchEntities) {
        this.searchEntities.clear();
        appendAll(searchEntities);
    }

    public void appendAll(List<SearchItem> searchEntities) {
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvName.setText(searchEntities.get(position).getName());
        SearchItem current = searchEntities.get(position);
        switch(current.getType()) {
            case RESTAURANT:
                holder.tvMetaData.setText(current.getShortAddress());
                break;
            case CUISINE:
                holder.tvMetaData.setText(CUISINE.name());
                break;
            case DISH:
                holder.tvMetaData.setText(DISH.name());
                break;
            default:
                holder.tvMetaData.setText("");
                break;
        }
        setFont(holder);
        return convertView;
    }

    private void setFont(ViewHolder holder) {
        holder.tvName.setTypeface(Utils.getRegularFont(mContext));
        holder.tvMetaData.setTypeface(Utils.getRegularFont(mContext));
    }
}
