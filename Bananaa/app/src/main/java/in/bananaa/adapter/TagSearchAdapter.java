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

public class TagSearchAdapter extends BaseAdapter {
    private static final String TAG = "TAG_SEARCH";
    private List<SearchItem> searchTags;
    private Activity mContext;
    private LayoutInflater infalter;

    public TagSearchAdapter(Activity activity) {
        searchTags = new ArrayList<>();
        infalter = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = activity;
    }

    @Override
    public int getCount() {
        return searchTags.size();
    }

    @Override
    public SearchItem getItem(int position) {
        return searchTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<SearchItem> searchEntities) {
        this.searchTags.clear();
        appendAll(searchEntities);
    }

    public void appendAll(List<SearchItem> searchEntities) {
        try {
            this.searchTags.addAll(searchEntities);
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

        holder.tvName.setText(searchTags.get(position).getName());
        setFont(holder);
        return convertView;
    }

    private void setFont(ViewHolder holder) {
        holder.tvName.setTypeface(Utils.getRegularFont(mContext));
        holder.tvMetaData.setTypeface(Utils.getRegularFont(mContext));
    }
}
