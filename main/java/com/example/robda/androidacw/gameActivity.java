package com.example.robda.androidacw;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    }

    protected void initaliseMultArray()
    {
        columns = puzzleRow1.length;

        if (puzzleRow4 != null)
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
        if (playerScore != 0)
        {
            playerScore -= elapsedMillis;
        }
        else
        {
            playerScore = 0;
        }
        winner.setVisibility(View.VISIBLE);
        TextView winText = (TextView)findViewById(R.id.winnerText);
        winText.setText(getString(R.string.win) + playerScore + getString(R.string.points));

        SQLiteDatabase db = m_DBHelperRead.getWritableDatabase();


//        String[] projection = {
//                //PuzzleDBContract.PuzzleEntry.COLUMN_PICTURE_SET_DEFINITION,
//                PuzzleDBContract.PuzzleEntry.HIGHSCORE
//        };
//        Cursor c = db.query(
//                PuzzleDBContract.PuzzleEntry.TABLE_NAME,
//                projection,
//                null, null, null, null, null
//        );
//        c.close();
        String highScore = Integer.toString(playerScore);
        values.put(PuzzleDBContract.PuzzleEntry.HIGHSCORE, highScore);
        db.insert(PuzzleDBContract.PuzzleEntry.TABLE_NAME, null, values);
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