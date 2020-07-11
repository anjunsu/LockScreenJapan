package com.talesajs.lockscreenjapan.lockscreen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.data.DBHandler;
import com.talesajs.lockscreenjapan.data.WordData;
import com.talesajs.lockscreenjapan.util.Logg;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LockScreenActivity extends Activity {
    private Context mContext;
    private Random mRandom;

    private int mWordCount;
    @BindView(R.id.textview_lock_screen_word)
    TextView tvWord;
    @BindView(R.id.textview_lock_screen_kanji)
    TextView tvKanji;
    @BindView(R.id.textview_lock_screen_meaning)
    TextView tvMeaning;

    private ArrayList<WordData> wordList;
    private int curWordIdx = 0;
    private WordData curWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(this);
        mContext = this;
        // remove navigation bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        wordList = new ArrayList<>();

        DBHandler dbHandler = DBHandler.open(mContext);
        wordList.addAll(dbHandler.getRandomWords(10));

        dbHandler.close();

        if(wordList != null && wordList.size() != 0)
            finish();

        curWord = wordList.get(curWordIdx);

        tvWord.setText(curWord.getWord());
        tvKanji.setText(curWord.getKanji());
        tvMeaning.setText(curWord.getMeaning());
    }

    @OnClick({R.id.button_next_word, R.id.button_prev_word})
    public void onClickButton(View view){
        switch (view.getId()){
            case R.id.button_next_word : {

                break;
            }
            case R.id.button_prev_word : {

                break;
            }
        }
    }
}
