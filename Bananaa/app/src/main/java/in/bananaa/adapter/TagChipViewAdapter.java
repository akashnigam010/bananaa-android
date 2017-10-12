package in.bananaa.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.plumillonforge.android.chipview.ChipViewAdapter;

import java.util.concurrent.ThreadLocalRandom;

import in.bananaa.R;
import in.bananaa.object.TagChip;
import in.bananaa.utils.Utils;

public class TagChipViewAdapter extends ChipViewAdapter {

    Context context;
    int[] rainbow;

    public TagChipViewAdapter(Context context) {
        super(context);
        this.context = context;
        rainbow = context.getResources().getIntArray(R.array.rainbow);
    }

    @Override
    public int getLayoutRes(int position) {
        return R.layout.chip_close;
    }

    @Override
    public int getBackgroundColor(int position) {
        return R.drawable.bg_chip;
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
        TagChip tag = (TagChip) getChip(position);
        TextView tv = ((TextView) view.findViewById(android.R.id.text1));
        tv.setTypeface(Utils.getRegularFont(context));
        int randomNum = ThreadLocalRandom.current().nextInt(0, 9 + 1);

        tv.setBackground(context.getResources().getDrawable(R.drawable.bg_chip));
        tv.setBackgroundColor(rainbow[randomNum]);
    }
}
