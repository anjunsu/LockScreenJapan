package com.talesajs.lockscreenjapan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.util.Logg;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogLoading extends Dialog {
    private Context mContext;
    private int DIALOG_TIMEOUT = 30000;

    @BindView(R.id.lottie_loading)
    LottieAnimationView lottieLoading;

    private Handler timeOutHandler;

    public DialogLoading(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public DialogLoading(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        init();
    }

    private void init() {
        setContentView(R.layout.activity_dialog_loading);
        ButterKnife.bind(this);

        timeOutHandler = new Handler();
    }

    @Override
    public void show() {
        super.show();

        timeOutHandler.postDelayed(() -> {
            Logg.e("dialoag timeout");
            if (this.isShowing())
                this.dismiss();
        }, DIALOG_TIMEOUT);
    }

    @Override
    public void dismiss() {
        timeOutHandler.removeMessages(0);
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        // prevent dismiis with back key press
        super.onBackPressed();
    }
}
