package com.example.tomas.motionmedia;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.type;

/**
 * Created by Tomas on 25.10.2016.
 * Class for serching mp3 files in device.
 */

public class SongsManager {

    // SDCard Path
    private Song song;
    private ArrayList<HashMap<String, String>> AsongsList = new ArrayList<HashMap<String, String>>();

    private List<Song> songList = new ArrayList<Song>();

    // Constructor
    public SongsManager(){}


    public List<Song> parseAllAudio(Context context) {
        songList = new ArrayList<Song>();
        try {
            String TAG = "Audio";
            Cursor cur = context.getContentResolver().query( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);


            if (cur == null) {
                // Query failed...
                Log.e(TAG, "Failed to retrieve music: cursor is null :-(");

            }
            else if (!cur.moveToFirst()) {
                // Nothing to query. There is no music on the device. How boring.
                Log.e(TAG, "Failed to move cursor to first row (no query results).");

            }else {
                Log.i(TAG, "Listing...");
                // retrieve the indices of the columns where the ID, title, etc. of the song are

                // add each song to mItems
                do {
                    int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
                    int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
                    int filePathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    Log.i(TAG, "Title column index: " + String.valueOf(titleColumn));
                    Log.i(TAG, "ID column index: " + String.valueOf(titleColumn));

                    Log.i("Final ", "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn) + "Path: " + cur.getString(filePathIndex));
                    songList.add(new Song(cur.getString(titleColumn),cur.getString(artistColumn), cur.getInt(durationColumn), cur.getString(filePathIndex)));
                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return songList;
    }



    }

