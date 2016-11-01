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
import java.util.Objects;

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
    private List<Object> trackList = new ArrayList<>();
    private List<String> artistList = new ArrayList<>();

    // Constructor
    public SongsManager(){}


    public List<Object> parseAllAudio(Context context) {
        songList = new ArrayList<Song>();
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
                String lastArtist = new String();

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
                    songList.add(song);
                    if (!artistList.contains(song.getSongArtist())){
                        artistList.add(song.getSongArtist());
                        childList = new ArrayList<>();
                        childList.add(song);
                        trackList.add(childList);
                        if (artistList.size() == 1){
                            lastArtist = song.getSongArtist();
                        }
                    } else {
                        //nalezena skladba s již založeným interpretem
            //            if (artistList.size() == 1 ){
          //
        //                    childList.add(song);
      //                      trackList.add(childList);
    //                        childList = new ArrayList<>();
  //
//
//                        } else {
                            //získám index childListu
                            int i = artistList.indexOf(song.getSongArtist());
                            //trackList.set(i, trackList.get(i));
                            //načtu childlist
                            childList = (ArrayList<Song>) trackList.get(i);
                            childList.add(song);
                            trackList.set(i,childList);
                            childList = new ArrayList<>();
        //                }


                    }
/*
                    if (lastArtist.equals(song.getSongArtist()) || childList.size()== 0){
                        childList.add(song);
                        lastArtist=song.getSongArtist();
                    } else {
                        trackList.add(childList);
                        childList = new ArrayList<>();
                        childList.add(song);
                        lastArtist=song.getSongArtist();
                    }


*/
                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        trackList.add(childList);
        return trackList;
    }

    public List<String> getArtistsList (){
        return artistList;
    }

    public List<Song> getSongList () {return  songList; }





    }

