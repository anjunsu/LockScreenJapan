package com.talesajs.lockscreenjapan.lockscreen;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.airbnb.lottie.LottieAnimationView;
import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.config.ConfigPreference;
import com.talesajs.lockscreenjapan.data.DBHandler;
import com.talesajs.lockscreenjapan.data.WordData;
import com.talesajs.lockscreenjapan.util.Logg;
import com.talesajs.lockscreenjapan.util.Util;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class LockScreenActivity extends AppCompatActivity {
    private Context mContext;

    private static final int LOAD_WORD_NUM = 20;
    private static final int LOAD_WORD_MAX = 100;

    @BindView(R.id.textview_lock_screen_up_word)
    TextView tvUpWord;
    @BindView(R.id.textview_lock_screen_down_word)
    TextView tvDownWord;
    @BindView(R.id.textview_lock_screen_meaning)
    TextView tvMeaning;
    @BindView(R.id.textview_lock_screen_level)
    TextView tvLevel;

    @BindView(R.id.view_show_meaning)
    View viewShowMeaning;

    @BindView(R.id.button_prev)
    Button btnPrev;
    private ArrayList<WordData> mWordList = new ArrayList<>();
    private int curWordIdx = 0;

    private MutableLiveData<WordData> curWord = new MutableLiveData<>();
    private KeyguardManager mKeyguardManager;

    private Set<String> selectedLevels;
    private boolean showMeaning = true; // true : show meaning , false : hide meaning
    private boolean showKanji = true;    // true : show hiragana at upWord , false : show kanji at upWord

    private TextToSpeech tts;
    private boolean nowSpeak = false;

    @BindView(R.id.lottie_speaker)
    LottieAnimationView lottieSpeaker;

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

        selectedLevels = ConfigPreference.getInstance(mContext).getConfigSelectedLevels();

        loadMoreWord();
        Logg.d("word size " + mWordList.size());
        if (mWordList == null || mWordList.size() == 0) {
            finish();
            //todo stop service
        } else {
            init();
        }
    }

    private void init() {
        showMeaning = ConfigPreference.getInstance(mContext).getConfigMeaning();
        if (showMeaning)
            viewShowMeaning.setVisibility(View.VISIBLE); // don't need when showMeaning

        showKanji = ConfigPreference.getInstance(mContext).getConfigWord();
        curWord.setValue(mWordList.get(curWordIdx));
        curWord.observe(this, wordData -> {
            runOnUiThread(() -> {
                if (!showKanji || Util.isNullOrEmpty(wordData.getKanji())) {
                    tvUpWord.setText(wordData.getWord());
                    tvDownWord.setText(wordData.getKanji());
                } else {
                    tvUpWord.setText(wordData.getKanji());
                    tvDownWord.setText(wordData.getWord());
                }
                tvDownWord.setVisibility(Util.getVisibleBooler(showMeaning));

                tvMeaning.setText(wordData.getMeaning());
                tvMeaning.setVisibility(Util.getVisibleBooler(showMeaning));
                tvLevel.setText(wordData.getLevel());
            });
        });


        tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            nowSpeak = true;
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            nowSpeak = false;
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Logg.e("tts has onError");
                            nowSpeak = false;
                        }
                    });

                    tts.setLanguage(Locale.JAPANESE);
                }
            }
        });


//        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//            @Override
//            public void onStart(String s) {
//                nowSpeak = true;
//            }
//
//            @Override
//            public void onDone(String s) {
//                nowSpeak = false;
//            }
//
//            @Override
//            public void onError(String s) {
//
//            }
//        });

//        float posX =ConfigPreference.getInstance(mContext).getSpeakerIconPositionX();
//        float posY = ConfigPreference.getInstance(mContext).getSpeakerIconPositionY();
//        if(posX != -1 && posY != -1) {
//            Logg.d("pos x,y : " + posX + "," + posY);
//            lottieSpeaker.setX(posX);
//            lottieSpeaker.setY(posY);
//        }
    }

    @OnClick(R.id.lottie_speaker)
    public void onClickSpeaker(View view) {
        if (nowSpeak)
            return;
        lottieSpeaker.setProgress(0);
        lottieSpeaker.playAnimation();
        if (tts != null) {
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
            TextView targetTextView;
            if (!showKanji || Util.isNullOrEmpty(tvDownWord.getText().toString())) {
                targetTextView = tvUpWord;
            } else {
                targetTextView = tvDownWord;
            }
            tts.speak(targetTextView.getText().toString(), TextToSpeech.QUEUE_FLUSH, params, "UniqueID");
        }
    }

//    float touchedX, touchedY;
//    float oldX, oldY;
//
//    @OnTouch(R.id.lottie_speaker)
//    public boolean onTouchSpeaker(View v, MotionEvent event) {
//        float eventX = event.getRawX();
//        float eventY = event.getRawY();
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            touchedX = event.getX();
//            touchedY = event.getY();
//            oldX = eventX;
//            oldY = eventY;
//
//        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            v.setX(eventX - touchedX);
//            v.setY(eventY - (touchedY + v.getHeight()));
//        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//            ConfigPreference.getInstance(mContext).setSpeakerIconPositionX(eventX - touchedX);
//            ConfigPreference.getInstance(mContext).setSpeakerIconPositionY(eventY - (touchedY + v.getHeight()));
//        }
//        return true;
//    }


    private void loadMoreWord() {
        Logg.d("loadMoreWord");
        DBHandler dbHandler = DBHandler.open(mContext);
        mWordList.addAll(dbHandler.getRandomWords(new ArrayList<>(selectedLevels), LOAD_WORD_NUM));
        dbHandler.close();

        if (mWordList.size() > LOAD_WORD_MAX) {
            for (int i = 0; i < LOAD_WORD_NUM; i++) {
                mWordList.remove(0);
            }
            curWordIdx -= LOAD_WORD_NUM;
        }
    }

    @OnClick({R.id.button_next, R.id.button_prev})
    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.view_next_word:
            case R.id.button_next: {
                curWordIdx++;
                btnPrev.setVisibility(View.VISIBLE);
                if (curWordIdx >= mWordList.size()) {
                    loadMoreWord();
                }
                break;
            }
            case R.id.view_prev_word:
            case R.id.button_prev: {
                if (curWordIdx == 0) {
                    Logg.e("curwordIdx : 0");
                    return;
                }
                curWordIdx--;
                break;
            }
        }
        curWord.setValue(mWordList.get(curWordIdx));
        Logg.d(" " + curWordIdx);

    }

    @OnTouch(R.id.button_finger_print)
    public boolean onTouchFingerPrint(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            finish();
        return true;
    }

    @OnTouch(R.id.view_show_meaning)
    public boolean onTouchShowMeaning(View view, MotionEvent motionEvent) {
        if (showMeaning)
            return false;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                tvDownWord.setVisibility(View.VISIBLE);
                tvMeaning.setVisibility(View.VISIBLE);
                break;
            }
            case MotionEvent.ACTION_UP: {
                tvDownWord.setVisibility(View.GONE);
                tvMeaning.setVisibility(View.GONE);
                break;
            }
        }
        return true;
    }

    private Handler meaningShowHandler = new Handler();

    @OnTouch({R.id.view_prev_word, R.id.view_next_word})
    public boolean onTouchPrevNextView(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (!showMeaning)
                    meaningShowHandler.postDelayed(() -> {
                        tvDownWord.setVisibility(View.VISIBLE);
                        tvMeaning.setVisibility(View.VISIBLE);
                    }, 500);
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!showMeaning) {
                    meaningShowHandler.removeMessages(0);
                    tvDownWord.setVisibility(View.GONE);
                    tvMeaning.setVisibility(View.GONE);
                }
                switch (view.getId()) {
                    case R.id.view_next_word: {
                        curWordIdx++;
                        if (curWordIdx >= mWordList.size()) {
                            loadMoreWord();
                        }

                        break;
                    }
                    case R.id.view_prev_word: {
                        if (curWordIdx == 0) {
                            Logg.e("curwordIdx : 0");
                            return true;
                        }
                        curWordIdx--;
                        break;
                    }
                }
                curWord.setValue(mWordList.get(curWordIdx));
                Logg.d(" " + curWordIdx);
                break;
            }
        }


        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mKeyguardManager.isDeviceLocked() && mKeyguardManager.isKeyguardLocked()) {
            mKeyguardManager.requestDismissKeyguard(this, null);
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
