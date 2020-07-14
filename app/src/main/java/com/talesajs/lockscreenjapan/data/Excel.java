package com.talesajs.lockscreenjapan.data;

import android.content.Context;
import android.content.res.AssetManager;

import com.talesajs.lockscreenjapan.util.Logg;
import com.talesajs.lockscreenjapan.util.Util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Excel {
    private static final String TAG = "Excel";
    private Context mContext;

    private final int CELL_WORD = 0;
    private final int CELL_KANJI = 1;
    private final int CELL_MEANING = 2;

    public Excel(Context context) {
        super();
        mContext = context;
    }

    public ArrayList<String> getLevels(String filePath) {
        ArrayList<String> result = new ArrayList<>();

        AssetManager assetManager = mContext.getAssets();
        InputStream inputStream = null;
        POIFSFileSystem poifsFileSystem = null;
        HSSFWorkbook workbook = null;

        try {
            inputStream = assetManager.open(filePath);
            poifsFileSystem = new POIFSFileSystem(inputStream);
            workbook = new HSSFWorkbook(poifsFileSystem);

            HSSFSheet curSheet;
            for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
                curSheet = workbook.getSheetAt(sheetIdx);
                String level = curSheet.getSheetName();
                result.add(level);
            }
        } catch (FileNotFoundException e) {
            Logg.e(" excel file not found exception");
        } catch (IOException e) {
            Logg.e(" IOException");
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                Logg.e(" close exceptioin");
            }
        }
        return result;
    }

    public ArrayList<WordData> getWordData(String filePath) {
        Logg.d("xlsReader");
        ArrayList<WordData> wordList = new ArrayList<>();


        ArrayList<WordData> list = new ArrayList<>();
        AssetManager assetManager = mContext.getAssets();
        InputStream inputStream = null;
        POIFSFileSystem poifsFileSystem = null;
        HSSFWorkbook workbook = null;

        try {
            inputStream = assetManager.open(filePath);
            poifsFileSystem = new POIFSFileSystem(inputStream);
            workbook = new HSSFWorkbook(poifsFileSystem);

            HSSFSheet curSheet;
            HSSFRow curRow;
            HSSFCell curCell;

            for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
                curSheet = workbook.getSheetAt(sheetIdx);
                String level = curSheet.getSheetName();
                Logg.d("curSheet : " + curSheet.getSheetName());
                for (int rowIdx = 0; rowIdx < curSheet.getPhysicalNumberOfRows(); rowIdx++) {
                    if (rowIdx != 0) {
                        curRow = curSheet.getRow(rowIdx);
                        if (!Util.isNullOrEmpty(curRow.getCell(0).getStringCellValue())) {
                            String word = "";
                            String kanji = "";
                            String meaning = "";
                            for (int cellIdx = 0; cellIdx < curRow.getPhysicalNumberOfCells(); cellIdx++) {
                                curCell = curRow.getCell(cellIdx);
                                switch (cellIdx) {
                                    case CELL_WORD: {
                                        word = curCell.toString();
                                        break;
                                    }
                                    case CELL_KANJI: {
                                        if (curCell != null)
                                            kanji = curCell.toString();
                                        break;
                                    }
                                    case CELL_MEANING: {
                                        meaning = curCell.toString();
                                        break;
                                    }
                                }
                            }

                            WordData newWord = WordData.builder()
                                    .index(rowIdx)
                                    .level(level)
                                    .word(word)
                                    .kanji(kanji)
                                    .meaning(meaning).build();
                            wordList.add(newWord);
                            Logg.d("row : " + rowIdx + " word : " + word + " kanji : " + kanji + " meaning : " + meaning);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logg.e(" excel file not found exception");
        } catch (IOException e) {
            Logg.e(" IOException");
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                Logg.e(" close exceptioin");
            }
        }
        return wordList;
    }

}
