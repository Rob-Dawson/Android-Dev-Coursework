package com.example.robda.androidacw;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class SelectPuzzleActivity extends AppCompatActivity
{

    PuzzleDBHelper m_DBHelper = new PuzzleDBHelper(this);
    PuzzleDBHelper m_DBHelperRead = new PuzzleDBHelper(this);
    Boolean downloadingInProgress = false;
    String layout = "";
    String picture = "";
    String[] formattedLayoutRow1 = null, formattedLayoutRow2 = null,
            formattedLayoutRow3 = null, formattedLayoutRow4 = null;
    String[] fullLayoutArray = null;

    private class downloadPuzzleDefinition extends AsyncTask<String, String, String>
    {

        protected String doInBackground(String... args)
        {
            downloadingInProgress = true;
            try
            {
                ContentValues values = new ContentValues();
                SQLiteDatabase db = m_DBHelper.getWritableDatabase();
                SQLiteDatabase dbRead = m_DBHelperRead.getReadableDatabase();

                InputStream stream = (InputStream) new URL(args[0]).getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String puzzle = args[0].substring(args[0].lastIndexOf('/') + 1);

                String line = "";
                String result = "";
                while (line != null)
                {
                    line = reader.readLine();
                    result += line;
                }

                JSONObject json = new JSONObject(result);
                picture = json.getString("PictureSet");
                Log.i("picture", picture);
                layout = json.getString("layout");
                Log.i("Layout", layout);

                String[] projection = {
                        //PuzzleDBContract.PuzzleEntry.COLUMN_PICTURE_SET_DEFINITION,
                        PuzzleDBContract.PuzzleEntry.COLUMN_LAYOUT_DEFINITION
                };
                Cursor c = dbRead.query(
                        PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                        projection,
                        null, null, null, null, null
                );
                c.close();

                values.put(PuzzleDBContract.PuzzleEntry.COLUMN_LAYOUT_DEFINITION, layout);
                values.put(PuzzleDBContract.PuzzleEntry.COLUMN_PICTURE_SET_DEFINITION, picture);
                db.update(PuzzleDBContract.PuzzleEntry.TABLE_NAME, values, "Name =\"" + puzzle + "\"", null);

                ///Downloads the layout and stores it in a new table in database.
                ///Downloads the pictures and stores them on the device.
                String url = "http://www.simongrey.net/08027/slidingPuzzleAcw/layouts/" + layout;
                stream = (InputStream) new URL(url).getContent();
                reader = new BufferedReader(new InputStreamReader(stream));
                line = "";
                String resultLayout = "";
                while (line != null)
                {
                    line = reader.readLine();
                    resultLayout += line;
                }
                Log.i("layout", "" + resultLayout);


                JSONObject jsonLayout = new JSONObject(resultLayout);
                JSONArray layoutArray = jsonLayout.getJSONArray("layout");

                formattedLayoutRow1 = null;
                formattedLayoutRow2 = null;
                formattedLayoutRow3 = null;
                formattedLayoutRow4 = null;
                String fullLayout = "";

                for (int i = 0; i < layoutArray.length(); ++i)
                {

                    String completeLayout = layoutArray.getString(i);

                    completeLayout = completeLayout.replaceAll("[^empty 1-9\\],]", "");
                    //completeLayout = completeLayout.substring(0, completeLayout.length() - 1);
                    completeLayout = completeLayout.replace("]", ",");
                    fullLayout += completeLayout;
                    switch (i)
                    {
                        case 0:
                            formattedLayoutRow1 = completeLayout.split(",");
                            break;

                        case 1:
                            formattedLayoutRow2 = completeLayout.split(",");
                            break;

                        case 2:
                            formattedLayoutRow3 = completeLayout.split(",");
                            break;

                        case 3:
                            formattedLayoutRow4 = completeLayout.split(",");
                            break;
                    }
                }
                fullLayoutArray = fullLayout.split(",");

                values.clear();
                values.put(PuzzleDBContract.LayoutEntry.COLUMN_NAME_NAME, layout);
                db.insert(PuzzleDBContract.LayoutEntry.TABLE_NAME, null, values);

                for (int i = 0; i < formattedLayoutRow1.length; ++i)
                {
                    switch (i)
                    {
                        case 0:
                            values.put(PuzzleDBContract.LayoutEntry.row1Col1, formattedLayoutRow1[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 1:
                            values.put(PuzzleDBContract.LayoutEntry.row1Col2, formattedLayoutRow1[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 2:
                            values.put(PuzzleDBContract.LayoutEntry.row1Col3, formattedLayoutRow1[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 3:
                            values.put(PuzzleDBContract.LayoutEntry.row1Col4, formattedLayoutRow1[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;
                    }
                }
                for (int i = 0; i < formattedLayoutRow2.length; ++i)
                {
                    switch (i)
                    {
                        case 0:
                            values.put(PuzzleDBContract.LayoutEntry.row2Col1, formattedLayoutRow2[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 1:
                            values.put(PuzzleDBContract.LayoutEntry.row2Col2, formattedLayoutRow2[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 2:
                            values.put(PuzzleDBContract.LayoutEntry.row2Col3, formattedLayoutRow2[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 3:
                            values.put(PuzzleDBContract.LayoutEntry.row2Col4, formattedLayoutRow2[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;
                    }
                }
                for (int i = 0; i < formattedLayoutRow3.length; ++i)
                {
                    switch (i)
                    {
                        case 0:
                            values.put(PuzzleDBContract.LayoutEntry.row3Col1, formattedLayoutRow3[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 1:
                            values.put(PuzzleDBContract.LayoutEntry.row3Col2, formattedLayoutRow3[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 2:
                            values.put(PuzzleDBContract.LayoutEntry.row3Col3, formattedLayoutRow3[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;

                        case 3:
                            values.put(PuzzleDBContract.LayoutEntry.row3Col4, formattedLayoutRow3[i]);
                            db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                            break;
                    }
                }
                if (formattedLayoutRow4 != null)
                {
                    for (int i = 0; i < formattedLayoutRow4.length; ++i)
                    {
                        switch (i)
                        {
                            case 0:
                                values.put(PuzzleDBContract.LayoutEntry.row4Col1, formattedLayoutRow4[i]);
                                db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                                break;

                            case 1:
                                values.put(PuzzleDBContract.LayoutEntry.row4Col2, formattedLayoutRow4[i]);
                                db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                                break;

                            case 2:
                                values.put(PuzzleDBContract.LayoutEntry.row4Col3, formattedLayoutRow4[i]);
                                db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                                break;

                            case 3:
                                values.put(PuzzleDBContract.LayoutEntry.row4Col4, formattedLayoutRow4[i]);
                                db.update(PuzzleDBContract.LayoutEntry.TABLE_NAME, values, "Name=\"" + layout + "\"", null);
                                break;
                        }
                    }
                }

                Bitmap bitmap;
                for (int i = 0; i < fullLayoutArray.length; ++i)
                {
                    try
                    {
                        FileInputStream read = getApplicationContext().openFileInput(picture + "" + fullLayoutArray[i] + ".JPEG");
                        bitmap = BitmapFactory.decodeStream(read);
                        Log.i("Image", "Image Exists");
                    } catch (FileNotFoundException fileNotFound)
                    {
                        try
                        {
                            Log.i("Image", "Image does not exist");
                            String image = "http://www.simongrey.net/08027/slidingPuzzleAcw/images/" + picture + "/" + fullLayoutArray[i];

                            if (fullLayoutArray[i].equals("empty"))
                            {
                                continue;
                            }
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(image).getContent());
                            FileOutputStream writer = null;
                            try
                            {
                                writer = getApplicationContext().openFileOutput(picture + "" + fullLayoutArray[i] + ".JPEG", Context.MODE_PRIVATE);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, writer);
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            } finally
                            {
                                writer.close();
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            downloadingInProgress = false;
            return "";
        }

        @Override
        protected void onPostExecute(String s)
        {
            Log.i("Downloading", "Complete");
            Intent intent = new Intent(getApplicationContext(), gameActivity.class);
            intent.putExtra("puzzleLayout1", formattedLayoutRow1);
            intent.putExtra("puzzleLayout2", formattedLayoutRow2);
            intent.putExtra("puzzleLayout3", formattedLayoutRow3);
            intent.putExtra("puzzleLayout4", formattedLayoutRow4);
            intent.putExtra("fullPuzzleLayout", fullLayoutArray);
            intent.putExtra("puzzleImage", picture);
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_puzzle);

        SQLiteDatabase db = new PuzzleDBHelper(this).getReadableDatabase();
        String[] projection = {
                PuzzleDBContract.PuzzleEntry._ID,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME
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

        ArrayList puzzleList = new ArrayList<Puzzle>();
        c.moveToFirst();

        String name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));
        int id = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry._ID));
        puzzleList.add(new Puzzle(name, null, null, id));
        while (c.moveToNext())
        {
            name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME));
            id = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry._ID));
            puzzleList.add(new Puzzle(name, null, null, id));
        }
        c.close();

        PuzzleAdapter adapter = new PuzzleAdapter(this, android.R.layout.simple_list_item_1, puzzleList);
        final ListView puzzleListView = (ListView) findViewById(R.id.PirateListView);
        puzzleListView.setAdapter(adapter);


        puzzleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {

                position++;
                new downloadPuzzleDefinition().execute("http://www.simongrey.net/08027/slidingPuzzleAcw/puzzles/puzzle" + position + ".json");
            }
        });
    }
}

