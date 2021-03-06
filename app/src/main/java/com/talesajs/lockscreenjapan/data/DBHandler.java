package com.talesajs.lockscreenjapan.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.talesajs.lockscreenjapan.util.Logg;

import java.util.ArrayList;

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

    public long insertWord(int index, String level, String word, String kanji, String meaning) {
        mDB = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(mHelper.INDEX, index);
        values.put(mHelper.LEVEL, level);
        values.put(mHelper.WORD, word);
        values.put(mHelper.KANJI, kanji);
        values.put(mHelper.MEANING, meaning);

         return mDB.insertWithOnConflict(mHelper.TABLE_WORDS,null,values,SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int getWordCount() {
        return getWordCount(null);
    }

    public ArrayList<WordData> getRandomWords(int wordNum) {
        return getRandomWords(null, wordNum);
    }

    public ArrayList<WordData> getRandomWords(ArrayList<String> levels, int wordNum) {
        ArrayList<WordData> wordData = new ArrayList<>();
        mDB = mHelper.getReadableDatabase();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM " + mHelper.TABLE_WORDS);
        if (levels != null && levels.size() != 0) {
            sql.append(" WHERE ");
            for (int i = 0; i < levels.size(); i++) {
                if (i != 0)
                    sql.append(" OR ");
                sql.append(mHelper.LEVEL + " = '" + levels.get(i) + "'");
            }
        }
        sql.append(" ORDER BY RANDOM() LIMIT " + wordNum);
        Logg.d(" getWordCount sql : " + sql.toString());

        Cursor cursor = mDB.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            do {
                WordData d = null;
                int idx = cursor.getInt(cursor.getColumnIndex(mHelper.INDEX));
                String level = cursor.getString(cursor.getColumnIndex(mHelper.LEVEL));
                String word = cursor.getString(cursor.getColumnIndex(mHelper.WORD));
                String kanji = cursor.getString(cursor.getColumnIndex(mHelper.KANJI));
                String meaning = cursor.getString(cursor.getColumnIndex(mHelper.MEANING));
                d = WordData.builder()
                        .index(idx)
                        .level(level)
                        .word(word)
                        .kanji(kanji)
                        .meaning(meaning).build();
                wordData.add(d);
            } while (cursor.moveToNext());
        }
        cursor.close();
        mDB.close();

        return wordData;
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
                sql.append(mHelper.LEVEL + " = '" + levels.get(i) + "'");
            }
        }
        Logg.d(" getWordCount sql : " + sql.toString());

        Cursor cursor = mDB.rawQuery(sql.toString(), null);
        count = cursor.getCount();
        cursor.close();
        return count;
    }

    public WordData getWord(int index) {
        mDB = mHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + mHelper.TABLE_WORDS + " WHERE " + mHelper.INDEX + " = " + index;
        Cursor cursor = mDB.rawQuery(sql, null);
        WordData wordData = null;
        if (cursor.moveToFirst()) {
            int idx = cursor.getInt(cursor.getColumnIndex(mHelper.INDEX));
            String level = cursor.getString(cursor.getColumnIndex(mHelper.LEVEL));
            String word = cursor.getString(cursor.getColumnIndex(mHelper.WORD));
            String kanji = cursor.getString(cursor.getColumnIndex(mHelper.KANJI));
            String meaning = cursor.getString(cursor.getColumnIndex(mHelper.MEANING));
            wordData = WordData.builder()
                    .index(idx)
                    .level(level)
                    .word(word)
                    .kanji(kanji)
                    .meaning(meaning).build();
        }
        return wordData;
    }

    public void deleteWord() {
        mDB = mHelper.getWritableDatabase();
        mDB.delete(mHelper.TABLE_WORDS, null, null);
    }
}
