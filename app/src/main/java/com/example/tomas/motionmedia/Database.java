package com.example.tomas.motionmedia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < songList.size() -1; i++){
            String name = songList.get(i).getSongName();
            name = DatabaseUtils.sqlEscapeString(name);
            String path = songList.get(i).getSongPath();
            path = DatabaseUtils.sqlEscapeString(path);
            contentValues.put("SONG_ID", songList.get(i).getId());
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

    public Song getSong (int id){
        Song song = new Song();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM SONGS WHERE SONG_ID = " + id + ";", null);
        if (cursor.moveToFirst()){
            song.setId(cursor.getInt(1));
            song.setSongName(cursor.getString(2));
            song.setSongArtist(cursor.getString(3));
            song.setSongLength(cursor.getInt(4));
            song.setSongAlbum(cursor.getString(5));
            song.setSongPath(cursor.getString(6));
            song.setSkipped(cursor.getInt(7));
            return song;
        }
        return null;
    }

    public List<Song> getSongsForDel (){
        List<Song> songList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM SONGS WHERE SKIPPED_TOTAL > 10 OR SKIPPED_IN_WEEK > 5;", null);
        if (cursor.moveToFirst()){
            do {
                Song song = new Song();
                song.setId(cursor.getInt(1));
                song.setSongName(cursor.getString(2));
                song.setSongArtist(cursor.getString(3));
                song.setSongLength(cursor.getInt(4));
                song.setSongAlbum(cursor.getString(5));
                song.setSongPath(cursor.getString(6));
                song.setSkipped(cursor.getInt(7));
                songList.add(song);
            } while (cursor.moveToNext());
        }
        return songList;
    }

    public void markSkippedSong (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //path = DatabaseUtils.sqlEscapeString(path);
        db.execSQL("UPDATE SONGS SET SKIPPED_TOTAL = SKIPPED_TOTAL + 1 WHERE SONG_ID =" + id + ";");
    }

    public void clearSkippedTotal (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SONGS SET SKIPPED_TOTAL = 0;");
    }

    public void clearSkippedWeek () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SONGS SET SKIPPED_WEEK = 0;");
    }

    public void clearSongs () {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM SONGS;");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE SONGS (ID INTEGER PRIMARY KEY NOT NULL, SONG_ID INTEGER,NAME TEXT, ARTIST TEXT, LENGTH INTEGER,ALBUM TEXT, PATH TEXT, SKIPPED_TOTAL INTEGER, SKIPPED_IN_WEEK INTEGER );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE SONGS;");
        //db.execSQL("DROP TABLE SONGS_FOR_DEL;");
        onCreate(db);
    }
}
