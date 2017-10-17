package in.bananaa.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.plumillonforge.android.chipview.ChipViewAdapter;

import in.bananaa.R;
import in.bananaa.object.SearchItem;
import in.bananaa.utils.Utils;

import static in.bananaa.utils.Utils.chipsBackgrounds;

public class TagChipViewAdapter extends ChipViewAdapter {

    Context context;

    public TagChipViewAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getLayoutRes(int position) {
        return R.layout.chip;
    }

    @Override
    public int getBackgroundColor(int position) {
        return R.drawable.bg_chip1;
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
        SearchItem tag = (SearchItem) getChip(position);
        TextView tv = ((TextView) view.findViewById(android.R.id.text1));
        tv.setTypeface(Utils.getRegularFont(context));
//        tv.setBackground(context.getResources().getDrawable(chipsBackgrounds[position%10], null));
        if (tag.getSelected()) {
            tv.setBackground(context.getResources().getDrawable(chipsBackgrounds[tag.getId()%10], null));
            tv.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }
}
