package com.example.robda.androidacw;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class gameActivity extends AppCompatActivity
{
    int playerScore = 100;
    int rows = 3;
    int columns = 3;

    String path = "/data/data/com.example.robda.androidacw/files/";
    String puzzlePicture = "";
    String[] puzzleRow1 = {};
    String[] puzzleRow2 = {};
    String[] puzzleRow3 = {};
    String[] puzzleRow4 = {};
    String[][] puzzle;
    String[] fullLayout = {};
    Bitmap tiles[];
    ImageView winner;
    String[] winCondition3x3 = {"empty", "21", "31",
            "12", "22", "32",
            "13", "23", "33"};

    String[] winCondition4x3 = {"empty", "21", "31",
            "12", "22", "32",
            "13", "23", "33",
            "14", "24", "34"};

    String[] winCondition3x4 = {"empty", "21", "31", "41",
            "12", "22", "32", "42",
            "13", "23", "33", "43"};

    String[] winCondition4x4 = {"empty", "21", "31", "41",
            "12", "22", "32", "42",
            "13", "23", "33", "43",
            "14", "24", "34", "44"};

    GridView gridView;
    Bitmap bitmap;
    Chronometer simpleChronometer;
    GridAdapter adapter = new GridAdapter(gameActivity.this, tiles);

    public static final String Shared_Pref = "timer";
    public static final String currentTime = "currentTime";




    private long resumeTime;
    String puzzle1;

    @Override
    protected void onPause()
    {
        super.onPause();
//        long timeWhenStopped = simpleChronometer.getBase() - SystemClock.elapsedRealtime();
//        SharedPreferences preferences = getSharedPreferences(Shared_Pref, MODE_PRIVATE);
//        SharedPreferences.Editor editor;
//        editor = preferences.edit();
//        editor.putLong(currentTime, timeWhenStopped);
//        editor.apply();
        simpleChronometer.stop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        SharedPreferences preferences = getSharedPreferences(Shared_Pref, MODE_PRIVATE);
//        resumeTime = preferences.getLong(currentTime, 0);
//        Toast.makeText(this, "" + resumeTime, Toast.LENGTH_SHORT).show();
//        simpleChronometer.setBase(SystemClock.elapsedRealtime() + resumeTime);
        simpleChronometer.start();
    }

//    @Override
//    protected void onDestroy()
//    {
//        super.onDestroy();
//        SharedPreferences preferences = getSharedPreferences(Shared_Pref, MODE_PRIVATE);
//        SharedPreferences.Editor editor;
//        editor = preferences.edit();
//        editor.clear();
//        editor.commit();
//
//
//
//        simpleChronometer.stop();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        winner = (ImageView) findViewById(R.id.winner);
        winner.setVisibility(View.INVISIBLE);
        getStringIntent();
        initaliseMultArray(); //Initialises a Multidimensional Array to be used to call each bitmap easily.
        
        //Creates the grid by using the multidimensional array
        if (puzzleRow4 != null)
        {
            tiles = new Bitmap[puzzleRow1.length + puzzleRow2.length +
                    puzzleRow3.length + puzzleRow4.length];
        }
        else
        {
            tiles = new Bitmap[puzzleRow1.length + puzzleRow2.length + puzzleRow3.length];
        }
        drawGrid();

        simpleChronometer = findViewById(R.id.simpleChronometer); // initiate a chronometer
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        simpleChronometer.start();

        adapter = new GridAdapter(gameActivity.this, tiles);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                checkMove(position);
                initaliseMultArray();
                checkWin();
                drawGrid();
                gridView.setAdapter(adapter);
            }
        });
    }

    protected void getStringIntent()
    {
        Intent intent = getIntent();
        puzzleRow1 = intent.getStringArrayExtra((getString(R.string.puzzleLayout1)));
        puzzleRow2 = intent.getStringArrayExtra((getString(R.string.puzzleLayout2)));
        puzzleRow3 = intent.getStringArrayExtra((getString(R.string.puzzleLayout3)));
        puzzleRow4 = intent.getStringArrayExtra((getString(R.string.puzzleLayout4)));

        fullLayout = intent.getStringArrayExtra((getString(R.string.fullPuzzleLayout)));
        puzzlePicture = intent.getStringExtra((getString(R.string.puzzleImage)));
        puzzle1 = intent.getStringExtra("puzzle");

        List<String> list = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        List<String> list3 = new ArrayList<String>();
        List<String> list4 = new ArrayList<String>();
        List<String> listFull = new ArrayList<String>();

        for(String s : puzzleRow1) {
            if(s != null && s.length() > 0) {
                list.add(s);
            }
        }
        puzzleRow1 = list.toArray(new String[list.size()]);

        for(String s : puzzleRow2) {
            if(s != null && s.length() > 0) {
                list2.add(s);
            }
        }
        puzzleRow2 = list2.toArray(new String[list2.size()]);

        for(String s : puzzleRow3) {
            if(s != null && s.length() > 0) {
                list3.add(s);
            }
        }
        puzzleRow3 = list3.toArray(new String[list3.size()]);
        if(puzzleRow4 != null)
        {
            for (String s : puzzleRow4)
            {
                if (s != null && s.length() > 0)
                {
                    list4.add(s);
                }
            }
            puzzleRow4 = list4.toArray(new String[list4.size()]);
        }

        for(String s : fullLayout) {
            if(s != null && s.length() > 0) {
                listFull.add(s);
            }
        }
        fullLayout = listFull.toArray(new String[list.size()]);

    }

    protected void initaliseMultArray()
    {
        columns = puzzleRow1.length;

        if (puzzleRow4 != null && puzzleRow4.length > 1)
        {
            rows = 4;
        }

        puzzle = new String[rows][columns];
        for (int i = 0; i < rows; ++i)
        {
            for (int j = 0; j < columns; ++j)
            {
                if (i == 0)
                {
                    puzzle[i][j] = fullLayout[j];
                }
                else if (i == 1)
                {
                    if (columns == 3)
                    {
                        puzzle[i][j] = fullLayout[j + (i + 2)];
                    }
                    else if (columns == 4)
                    {
                        puzzle[i][j] = fullLayout[j + (i + 3)];
                    }
                }
                else if (i == 2)
                {
                    if (columns == 3)
                    {
                        puzzle[i][j] = fullLayout[j + (i + 4)];
                    }
                    else if (columns == 4)
                    {
                        puzzle[i][j] = fullLayout[j + (i + 6)];
                    }
                }
                else if (i == 3)
                {
                    puzzle[i][j] = fullLayout[j + (i + 6)];
                }
            }
        }

    }
    PuzzleDBHelper m_DBHelperRead = new PuzzleDBHelper(this);


    private void showElapsedTime()
    {    ContentValues values = new ContentValues();
        values.clear();
        simpleChronometer.stop();
        long elapsedMillis = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
        elapsedMillis = elapsedMillis / 1000;
        playerScore -= elapsedMillis;
        if (playerScore <= 0)
        {
            playerScore = 0;
        }
        winner.setVisibility(View.VISIBLE);
        TextView winText = (TextView)findViewById(R.id.winnerText);
        winText.setText(getString(R.string.win) + playerScore + getString(R.string.points));

        SQLiteDatabase db = m_DBHelperRead.getWritableDatabase();

        values.put(PuzzleDBContract.PuzzleEntry.HIGHSCORE, playerScore);
        db.update(PuzzleDBContract.PuzzleEntry.TABLE_NAME, values, "Name=\"" + puzzle1 + "\"", null);

        Log.i("Insert", "Highscore " + values);
    }

    public void checkMove(int position)
    {

        for (int i = 0; i < fullLayout.length; ++i)
        {
            if (fullLayout[i].equals("empty"))
            {

                if (fullLayout.length > position - 1 && position - 1 >= 0 &&
                        fullLayout[position - 1] == fullLayout[i])
                {
                    String temp = fullLayout[position];
                    fullLayout[position] = fullLayout[i];
                    fullLayout[i] = temp;
                }
                else if (fullLayout.length > position + 1 && position + 1 >= 0 &&
                        fullLayout[position + 1] == fullLayout[i])
                {
                    String temp = fullLayout[position];
                    fullLayout[position] = fullLayout[i];
                    fullLayout[i] = temp;
                }

                if (columns == 3)
                {
                    if (fullLayout.length > position + 3 && position + 3 >= 0 &&
                            fullLayout[position + 3] == fullLayout[i])
                    {
                        String temp = fullLayout[position];
                        fullLayout[position] = fullLayout[i];
                        fullLayout[i] = temp;
                    }
                    else if (fullLayout.length > position - 3 && position - 3 >= 0 &&
                            fullLayout[position - 3] == fullLayout[i])
                    {
                        String temp = fullLayout[position];
                        fullLayout[position] = fullLayout[i];
                        fullLayout[i] = temp;
                    }
                }
                if (columns == 4)
                {
                    if (fullLayout.length > position + 4 && position + 4 >= 0 &&
                            fullLayout[position + 4] == fullLayout[i])
                    {
                        String temp = fullLayout[position];
                        fullLayout[position] = fullLayout[i];
                        fullLayout[i] = temp;
                    }
                    else if (fullLayout.length > position - 4 && position - 4 >= 0 &&
                            fullLayout[position - 4] == fullLayout[i])
                    {
                        String temp = fullLayout[position];
                        fullLayout[position] = fullLayout[i];
                        fullLayout[i] = temp;
                    }
                }
            }
        }
    }

    protected void drawGrid()
    {
        gridView = (GridView) findViewById(R.id.puzzleGrid);
        if (puzzleRow1.length == 3)
        {
            gridView.setNumColumns(3);
        }
        else if (puzzleRow1.length == 4)
        {
            gridView.setNumColumns(4);
        }

        int pic = 0;
        for (int i = 0; i < rows; ++i)
        {
            for (int j = 0; j < columns; ++j)
            {

                bitmap = BitmapFactory.decodeFile(path + puzzlePicture + puzzle[i][j] + ".JPEG");
                tiles[pic] = bitmap;
                pic++;
            }
        }

    }

    protected void checkWin()
    {
        if (rows == 3 && columns == 3)
        {
            int numberOfCorrectTiles = 0;
            for (int i = 0; i < fullLayout.length; ++i)
            {
                if (fullLayout[i].equals(winCondition3x3[i]))
                {
                    numberOfCorrectTiles++;
                    if (numberOfCorrectTiles == 8)
                    {
                        showElapsedTime();
                    }
                }
            }
        }
        if (rows == 4 && columns == 3)
        {
            int numberOfCorrectTiles = 0;
            for (int i = 0; i < fullLayout.length; ++i)
            {
                if (fullLayout[i].equals(winCondition4x3[i]))
                {
                    numberOfCorrectTiles++;
                    if (numberOfCorrectTiles == 11)
                    {
                        showElapsedTime();
                    }
                }
            }
        }
        if (rows == 3 && columns == 4)
        {
            int numberOfCorrectTiles = 0;
            for (int i = 0; i < fullLayout.length; ++i)
            {
                if (fullLayout[i].equals(winCondition3x4[i]))
                {
                    numberOfCorrectTiles++;
                    if (numberOfCorrectTiles == 11)
                    {
                        showElapsedTime();
                    }
                }
            }
        }
        if (rows == 4 && columns == 4)
        {
            int numberOfCorrectTiles = 0;
            for (int i = 0; i < fullLayout.length; ++i)
            {
                if (fullLayout[i].equals(winCondition4x4[i]))
                {
                    numberOfCorrectTiles++;
                    if (numberOfCorrectTiles == 15)
                    {
                        showElapsedTime();
                    }
                }
            }
        }
    }

}