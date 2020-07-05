package com.talesajs.lockscreenjapan.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.talesajs.lockscreenjapan.util.Logg;
import com.talesajs.lockscreenjapan.util.Util;

import java.util.ArrayList;
import java.util.Currency;

public class DBHandler {
    private DBHelper mHelper;
    private SQLiteDatabase mDB;
    private Context mContext;

    public DBHandler(Context context) {
        mHelper = new DBHelper(context);
        mContext = context;
    }

    public static DBHandler open(Context context) {
        return new DBHandler(context);
    }

    public void close() {
        if (mDB != null) {
            mDB.close();
            mDB = null;
        }

        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }

    public long insertWord(String level, String word, String kanji, String meaning) {
        mDB = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(mHelper.LEVEL, level);
        values.put(mHelper.WORD, word);
        values.put(mHelper.KANJI, kanji);
        values.put(mHelper.MEANING, meaning);

        return mDB.insert(mHelper.TABLE_WORDS, null, values);
    }

    public long insertLevel(String level, String fileName) {
        mDB = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(mHelper.LEVEL, level);
        values.put(mHelper.FILENAME, fileName);

        return mDB.insert(mHelper.TABLE_LEVEL, null, values);
    }

    public int getWordCount() {
        return getWordCount(null);
    }

    public int getWordCount(ArrayList<String> levels) {
        int count = 0;
        mDB = mHelper.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM " + mHelper.TABLE_WORDS);
        if (levels != null) {
            sql.append(" WHERE ");
            for (int i = 0; i < levels.size(); i++) {
                if (i != 0)
                    sql.append(" OR ");
                sql.append(mHelper.LEVEL + " = " + levels.get(i));
            }
        }
        Logg.d(" getWordCount sql : " + sql.toString());

        Cursor cursor = mDB.rawQuery(sql.toString(), null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public RespWordData selectWord(int index) {
        mDB = mHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + mHelper.TABLE_WORDS + " WHERE " + mHelper.INDEX + " = " + index;
        Cursor cursor = mDB.rawQuery(sql, null);
        RespWordData wordData = null;
        if(cursor.moveToFirst()){
            int idx = cursor.getInt(cursor.getColumnIndex(mHelper.INDEX));
            String level = cursor.getString(cursor.getColumnIndex(mHelper.LEVEL));
            String word = cursor.getString(cursor.getColumnIndex(mHelper.WORD));
            String kanji = cursor.getString(cursor.getColumnIndex(mHelper.KANJI));
            String meaning = cursor.getString(cursor.getColumnIndex(mHelper.MEANING));
            wordData = RespWordData.builder()
                    .level(level)
                    .word(word)
                    .kanji(kanji)
                    .meaning(meaning).build();
        }
        return wordData;
    }
}
