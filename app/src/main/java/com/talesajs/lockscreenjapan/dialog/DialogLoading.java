package com.talesajs.lockscreenjapan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.util.Logg;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogLoading extends Dialog {
    private Context mContext;
    private int DIALOG_TIMEOUT = 30000;

    @BindView(R.id.loading_dialog_lottie)
    LottieAnimationView lottieLoading;
    @BindView(R.id.loading_dialog_text)
    TextView tvLoading;

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
        setContentView(R.layout.dialog_loading);
        ButterKnife.bind(this);

        timeOutHandler = new Handler();
    }

    public void setLoadingTest(int id){
        tvLoading.setText(mContext.getString(id));
    }
    public void setLoadingTest(String string){
        tvLoading.setText(string);
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
