package com.example.robda.androidacw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Highscores extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);
        SQLiteDatabase db = new PuzzleDBHelper(this).getReadableDatabase();
        String[] projection = {
                PuzzleDBContract.PuzzleEntry._ID,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME,
                PuzzleDBContract.PuzzleEntry.HIGHSCORE
        };

        Cursor c = db.query(
                PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList puzzleList123 = new ArrayList<Puzzle>();

        c.moveToFirst();

        String name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));
        int highScore = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.HIGHSCORE));
        int id = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry._ID));

        String highScoreText = Integer.toString(highScore);
        Puzzle puzzle = new Puzzle(name, null, null, highScoreText, id);

        String formattedList = "Puzzle: " + puzzle.Name() + "\nScore: " + puzzle.Highscore();
        puzzleList123.add(formattedList);

        while (c.moveToNext())
        {
            name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));
            highScore = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.HIGHSCORE));
            id = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry._ID));
            if(highScore == 0)
            {
                continue;
            }
            highScoreText = Integer.toString(highScore);
            puzzle = new Puzzle(name, null, null, highScoreText, id);
            formattedList = "Puzzle: " + puzzle.Name() + "\nScore: " + puzzle.Highscore();

            puzzleList123.add(formattedList);
        }
        c.close();
        PuzzleAdapter adapter = new PuzzleAdapter(this, android.R.layout.simple_list_item_1, puzzleList123);
        final ListView listView = (ListView) findViewById(R.id.highscore);
        listView.setAdapter(adapter);


    }
}
