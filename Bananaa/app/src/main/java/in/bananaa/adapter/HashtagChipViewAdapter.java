package in.bananaa.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.plumillonforge.android.chipview.ChipViewAdapter;

import in.bananaa.R;
import in.bananaa.utils.Utils;

public class HashtagChipViewAdapter extends ChipViewAdapter {

    Context context;

    public HashtagChipViewAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getLayoutRes(int position) {
        return R.layout.hashtag;
    }

    @Override
    public int getBackgroundColor(int position) {
        return 0;
    }

    @Override
    public int getBackgroundColorSelected(int position) {
        return 0;
    }

    @Override
    public int getBackgroundRes(int position) {
        return 0;
    }

    @Override
    public void onLayout(View view, int position) {
        TextView tv = ((TextView) view.findViewById(android.R.id.text1));
        tv.setTypeface(Utils.getRegularFont(context));
    }
}
