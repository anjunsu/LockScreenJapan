package com.talesajs.lockscreenjapan.lockscreen;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.data.DBHandler;
import com.talesajs.lockscreenjapan.data.WordData;
import com.talesajs.lockscreenjapan.util.Logg;
import com.talesajs.lockscreenjapan.util.Util;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LockScreenActivity extends AppCompatActivity {
    private Context mContext;

    private static final int LOAD_WORD_NUM = 20;
    private static final int LOAD_WORD_MAX = 100;

    @BindView(R.id.textview_lock_screen_word)
    TextView tvWord;
    @BindView(R.id.textview_lock_screen_kanji)
    TextView tvKanji;
    @BindView(R.id.textview_lock_screen_meaning)
    TextView tvMeaning;

    @BindView(R.id.button_prev)
    Button btnPrev;
    private ArrayList<WordData> wordList;
    private int curWordIdx = 0;

    private MutableLiveData<WordData> curWord = new MutableLiveData<>();

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

        loadMoreWord();

        if(wordList == null || wordList.size() == 0)
            finish();


        curWord.setValue(wordList.get(curWordIdx));
        curWord.observe(this, wordData -> {
            runOnUiThread(()->{
                tvWord.setText(wordData.getWord());
                tvKanji.setText(wordData.getKanji());
                tvMeaning.setText(wordData.getMeaning());
            });
        });
    }
    private void loadMoreWord(){
        Logg.d("loadMoreWord");
        DBHandler dbHandler = DBHandler.open(mContext);
        wordList.addAll(dbHandler.getRandomWords(LOAD_WORD_NUM));
        dbHandler.close();

        if(wordList.size() > LOAD_WORD_MAX){
            for(int i=0;i<LOAD_WORD_NUM;i++){
                wordList.remove(0);
            }
            curWordIdx -= LOAD_WORD_NUM;
            Logg.d("remove wordlist : " + wordList.size() + " " +curWordIdx);
        }
    }

    @OnClick({R.id.button_next, R.id.button_prev})
    public void onClickButton(View view){
        switch (view.getId()){
            case R.id.button_next: {
                curWordIdx++;

                btnPrev.setVisibility(View.VISIBLE);

                if(curWordIdx >= wordList.size()){
                    loadMoreWord();
                }
                break;
            }
            case R.id.button_prev: {
                curWordIdx--;

                if(curWordIdx == 0)
                    view.setVisibility(View.INVISIBLE);

                break;
            }
        }
        curWord.setValue(wordList.get(curWordIdx));
        Logg.d(" " + curWordIdx);
    }
}
