package com.talesajs.lockscreenjapan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.dialog.callback.OneButtonDialogCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;
import lombok.Setter;


public class OneButtonDialog extends Dialog {
    @Getter @Setter
    private OneButtonDialogCallBack oneButtonDialogCallBack;

    @BindView(R.id.one_button_dialog_title)     TextView one_button_dialog_title;
    @BindView(R.id.one_button_dialog_content)   TextView one_button_dialog_content;
    @BindView(R.id.one_button_dialog_confirm)   Button one_button_dialog_confirm;


    public OneButtonDialog(Context context) {
        super(context);
        init();
    }

    public OneButtonDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_one_button);
        ButterKnife.bind(this);
        //BusProvider.getInstance().register(this);
    }

    public void setTitle(String title){
        one_button_dialog_title.setVisibility(View.VISIBLE);
        one_button_dialog_title.setText(title);
    }
    public void setTitle(int title){
        one_button_dialog_title.setVisibility(View.VISIBLE);
        one_button_dialog_title.setText(title);
    }

    public void setContent(String content){
        one_button_dialog_content.setVisibility(View.VISIBLE);
        one_button_dialog_content.setText(content);
    }
    public void setContent(int content){
        one_button_dialog_content.setVisibility(View.VISIBLE);
        one_button_dialog_content.setText(content);
    }

    public void setConfirmText(String confirmText){
        one_button_dialog_confirm.setVisibility(View.VISIBLE);
        one_button_dialog_confirm.setText(confirmText);
    }
    public void setConfirmText(int confirmText){
        one_button_dialog_confirm.setVisibility(View.VISIBLE);
        one_button_dialog_confirm.setText(confirmText);
    }

    @OnClick(R.id.one_button_dialog_confirm)
    public void onClickOneButtonDialogConfirm(View view){
        if(oneButtonDialogCallBack != null){
            oneButtonDialogCallBack.onClickConfirm();
        }
        else{
            dismiss();
        }
    }
}
