package com.talesajs.lockscreenjapan.lockscreen;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

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
    @BindView(R.id.textview_lock_screen_level)
    TextView tvLevel;

    @BindView(R.id.button_prev)
    Button btnPrev;
    private ArrayList<WordData> mWordList = new ArrayList<>();;
    private int curWordIdx = 0;

    private MutableLiveData<WordData> curWord = new MutableLiveData<>();
    private KeyguardManager mKeyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(this);
        mContext = this;
        // remove navigation bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        loadMoreWord();

        if(mWordList == null || mWordList.size() == 0)
            finish();


        curWord.setValue(mWordList.get(curWordIdx));
        curWord.observe(this, wordData -> {
            runOnUiThread(()->{
                tvWord.setText(wordData.getWord());
                tvKanji.setText(wordData.getKanji());
                tvMeaning.setText(wordData.getMeaning());
                tvLevel.setText(wordData.getLevel());
            });
        });
    }
    private void loadMoreWord(){
        Logg.d("loadMoreWord");
        DBHandler dbHandler = DBHandler.open(mContext);
        mWordList.addAll(dbHandler.getRandomWords(LOAD_WORD_NUM));
        dbHandler.close();

        if(mWordList.size() > LOAD_WORD_MAX){
            for(int i=0;i<LOAD_WORD_NUM;i++){
                mWordList.remove(0);
            }
            curWordIdx -= LOAD_WORD_NUM;
        }
    }

    @OnClick({R.id.button_next, R.id.button_prev})
    public void onClickButton(View view){
        switch (view.getId()){
            case R.id.button_next: {
                curWordIdx++;
                btnPrev.setVisibility(View.VISIBLE);
                if(curWordIdx >= mWordList.size()){
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
        curWord.setValue(mWordList.get(curWordIdx));
        Logg.d(" " + curWordIdx);

    }

    @OnTouch(R.id.button_exit)
    public void onTouchFingerPrint(View view, MotionEvent motionEvent){
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mKeyguardManager.isDeviceLocked() && mKeyguardManager.isKeyguardLocked()) {
            mKeyguardManager.requestDismissKeyguard(this, null);
        }

    }
}
