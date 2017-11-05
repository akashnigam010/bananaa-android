package in.bananaa.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import in.bananaa.R;

public class AlertMessages {
    Context context;

    public AlertMessages(Context context) {
        this.context = context;
    }

    private static Dialog alertDialog;

    public static void showError(Context mContext, String title, String message, String btnText) {
        alertDialog = new Dialog(mContext);
        alertDialog.setCancelable(true);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_error);
        alertDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        TextView tvTitle = (TextView) alertDialog.findViewById(R.id.tvTitle);
        TextView tvMessage = (TextView) alertDialog.findViewById(R.id.tvMessage);
        AppCompatButton btnDismiss = (AppCompatButton) alertDialog.findViewById(R.id.btnDismiss);

        tvTitle.setText(title);
        tvMessage.setText(message);
        btnDismiss.setText(btnText);

        tvTitle.setTypeface(Utils.getBold(mContext));
        tvMessage.setTypeface(Utils.getRegularFont(mContext));
        btnDismiss.setTypeface(Utils.getRegularFont(mContext));

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }
}
