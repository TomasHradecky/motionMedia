package com.example.tomas.motionmedia;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomas on 25.10.2016.
 * Class for serching mp3 files in device.
 */

public class SongsManager {

    // SDCard Path
    private Song song;
    private List<Song> allSongList = new ArrayList<Song>();
    private List<Object> objectSongList = new ArrayList<>();
    private List<String> artistList = new ArrayList<>();

    // Constructor
    public SongsManager(){}

    public List<Object> getObjectSongList(Context context) {
        allSongList = new ArrayList<Song>();
        objectSongList = new ArrayList<>();
        artistList = new ArrayList<>();
        List<Song> childList= new ArrayList();
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
                    Song song = new Song(cur.getString(titleColumn),cur.getString(artistColumn), cur.getInt(durationColumn), cur.getString(filePathIndex));
                    allSongList.add(song);
                    if (!artistList.contains(song.getSongArtist())){
                        artistList.add(song.getSongArtist());
                        childList = new ArrayList<>();
                        childList.add(song);
                        objectSongList.add(childList);
                    } else {
                            int i = artistList.indexOf(song.getSongArtist());
                            childList = (ArrayList<Song>) objectSongList.get(i);
                            childList.add(song);
                            objectSongList.set(i,childList);
                            childList = new ArrayList<>();
                    }
                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        objectSongList.add(childList);
        return objectSongList;
    }

    public List<String> getArtistsList (){
        return artistList;
    }

    public List<Song> getAllSongList() {return allSongList; }
    }

