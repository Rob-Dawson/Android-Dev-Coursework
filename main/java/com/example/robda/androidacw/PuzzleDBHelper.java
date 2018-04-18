package com.example.robda.androidacw;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by robda on 14/03/2018.
 */

public class PuzzleDBHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Puzzles.db";

    public PuzzleDBHelper(Context pContext)
    {
        super(pContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase pDb)
    {
        pDb.execSQL(PuzzleDBContract.SQL_CREATE_PUZZLE_TABLE);
        pDb.execSQL(PuzzleDBContract.SQL_CREATE_LAYOUT_TABLE);
    }

    public void onUpgrade(SQLiteDatabase pDb, int pOldVersion, int pNewVersion)
    {
    }
}
