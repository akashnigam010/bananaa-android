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
import in.bananaa.object.location.City;
import in.bananaa.object.location.LocationStore;
import in.bananaa.object.location.LocationType;
import in.bananaa.utils.CustomListView;
import in.bananaa.utils.Debug;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.Utils;

public class CityAdapter extends BaseAdapter {
    private static final String TAG = "CITY_ADAPTER";
    private List<City> cities;
    private Activity mContext;
    private LayoutInflater infalter;

    public CityAdapter(Activity activity) {
        cities = new ArrayList<>();
        infalter = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = activity;
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public City getItem(int position) {
        return cities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<City> cities) {
        this.cities.clear();
        appendAll(cities);
    }

    public void appendAll(List<City> cities) {
        try {
            this.cities.addAll(cities);
        } catch (Exception e) {
            Debug.e(TAG, e.getMessage());
        }

        notifyDataSetChanged();
    }

    public class ViewHolder {
        TextView tvName;
        CustomListView lvLocalities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.row_city_search, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.lvLocalities = (CustomListView) convertView.findViewById(R.id.lvLocalities);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final City city = cities.get(position);
        holder.tvName.setText(city.getName());
        LocalityAdapter localityAdapter = new LocalityAdapter(mContext);
        holder.lvLocalities.setAdapter(localityAdapter);
        localityAdapter.addAll(city.getLocalities());
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationStore locationStore = new LocationStore(city.getId(), city.getName(), LocationType.CITY);
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
