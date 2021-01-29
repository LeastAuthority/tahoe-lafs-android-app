package org.tahoe.lafs.ui.customview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.tahoe.lafs.R;

public class TahoeToast extends Toast {

    public static final int SUCCESS = 1;
    public static final int INFORMATION = 2;
    public static final int ERROR = 3;
    public static final int WARNING = 4;

    public static final int POSITION_DEFAULT = 1;
    public static final int POSITION_CENTER = 2;
    public static final int POSITION_TOP = 3;

    public TahoeToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, String toastMessage, int duration, int type, int pos) {
        Toast toast = new Toast(context);
        toast.setDuration(duration);

        @SuppressLint("InflateParams") View layout = LayoutInflater.from(context).inflate(R.layout.tahoe_toast_layout, null, false);
        LinearLayout toastRoot = layout.findViewById(R.id.toast_root);

        TextView text = layout.findViewById(R.id.toast_text);

        switch (pos) {
            case 2:
                toast.setGravity(Gravity.CENTER, 0, 0);
                break;
            case 3:
                toast.setGravity(Gravity.TOP, 0, 0);
                break;
        }

        switch (type) {
            case 1:
                toastRoot.setBackgroundResource(R.drawable.success_toast);
                break;
            case 2:
                toastRoot.setBackgroundResource(R.drawable.info_toast);
                break;
            case 3:
                toastRoot.setBackgroundResource(R.drawable.error_toast);
                break;
            case 4:
                toastRoot.setBackgroundResource(R.drawable.warning_toast);
                break;
        }

        text.setText(toastMessage);
        toast.setView(layout);
        return toast;
    }
}
