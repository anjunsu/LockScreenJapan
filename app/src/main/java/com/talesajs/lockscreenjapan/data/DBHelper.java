package com.talesajs.lockscreenjapan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.talesajs.lockscreenjapan.util.Logg;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "talesajs.db";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;
    private boolean mUpgradeFlag;

    // table name
    static final String TABLE_WORDS = "_WORDS";
    //

    // WORDS table Field
    static final String INDEX = "_xi";      // key
    static final String LEVEL = "_lv";      // word level , jlpt2 or my word etc..
    static final String WORD = "_wd";       // word
    static final String KANJI = "_kj";      // chines character
    static final String MEANING = "_mn";    // word meaning

    private static final String CREATE_TABLE_WORDS
            = "CREATE TABLE " + TABLE_WORDS + " ("
            + INDEX + " INTEGER,"
            + LEVEL + " TEXT, "
            + WORD + " TEXT, "
            + KANJI + " TEXT, "
            + MEANING + " TEXT,"
            + " PRIMARY KEY ("+ INDEX + "," + LEVEL +"))";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logg.d("onCreate DBHelper");
        db.execSQL(CREATE_TABLE_WORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version =oldVersion;
        if(version != DATABASE_VERSION){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
            onCreate(db);
        }
    }
}
