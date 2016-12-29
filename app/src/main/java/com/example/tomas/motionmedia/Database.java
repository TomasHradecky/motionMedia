package com.example.tomas.motionmedia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomas Hradecky on 29.12.2016.
 */

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MotionMedia";
    private static final int DATABASE_VERSION = 1;


    public Database (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void saveSongs (List<Song> songList) {
        //String name, String artist, String album, int length, String path, int skippedTotal, int skippedInWeek
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < songList.size() -1; i++){
            String name = songList.get(i).getSongName();
            name = name.replaceAll("'","\'");
            String path = songList.get(i).getSongPath();
            path = path.replaceAll("'","\'");
            contentValues.put("NAME", name);
            contentValues.put("ARTIST", songList.get(i).getSongArtist());
            contentValues.put("ALBUM", songList.get(i).getSongAlbum());
            contentValues.put("LENGTH", songList.get(i).getSongLength());
            contentValues.put("PATH", path);
            contentValues.put("SKIPPED_TOTAL", 0);
            contentValues.put("SKIPPED_IN_WEEK", 0);
            getWritableDatabase().insert("SONGS", null, contentValues);
        }


    }

    public Song getSongs (){
        Song song = new Song();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM SONGS", null);
        return song;
    }

    public Song getSong (String path){
        Song song = new Song();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM SONGS WHERE PATH LIKE?", new String[]{path});
        return song;
    }

    public List<Song> getSongsForDel (){
        List<Song> songList = new ArrayList<>();
        Song song = new Song();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM SONGS WHERE SKIPPED_TOTAL > 5;", null);
        if (cursor.moveToFirst()){
            do {
                cursor.moveToFirst();
                song.setSongName(cursor.getString(1));
                song.setSongArtist(cursor.getString(2));
                song.setSongLength(cursor.getInt(3));
                song.setSongAlbum(cursor.getString(4));
                song.setSongPath(cursor.getString(5));
                songList.add(song);
            } while (cursor.moveToNext());
        }
        return songList;
    }

    public void markSkippedSong (String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        //difficulty=difficulty.replaceAll("'","\'");
        ///storage/sdcard1/Hudba/The Offspring/14.(Can't Get My) Head Around You.mp3
        path = path.replaceAll("'","\'");

        int i = 0;
        path = path.replace("'","\'");
        db.execSQL("UPDATE SONGS SET SKIPPED_TOTAL = SKIPPED_TOTAL + 1 WHERE PATH LIKE '"  + path + "'");
    }

    public void clearSongs () {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM SONGS;");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE SONGS (ID INTEGER PRIMARY KEY NOT NULL,NAME TEXT, ARTIST TEXT, LENGTH INTEGER,ALBUM TEXT, PATH TEXT, SKIPPED_TOTAL INTEGER, SKIPPED_IN_WEEK integer );");
       // db.execSQL("CREATE TABLE SONGS_FOR_DEL (ID INTEGER PRIMARY KEY NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE SONGS;");
        db.execSQL("DROP TABLE SONGS_FOR_DEL;");
        onCreate(db);
    }
}
