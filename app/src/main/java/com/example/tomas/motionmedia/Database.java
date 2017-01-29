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

    /**
     * save all songs to database
     * @param songList
     */
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
            contentValues.put("IN_PLAYLIST", 0);
            getWritableDatabase().insert("SONGS", null, contentValues);
        }
    }

    /**
     * mark all songs in playlist
     * @param songList
     */
    public void markPlaylistSongs (List<Song> songList) {
        for (int i = 0; i < songList.size(); i++){
            int songId = songList.get(i).getId();
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("UPDATE SONGS SET IN_PLAYLIST = 1 WHERE SONG_ID =" + songId + ";");

        }
    }

    /**
     * mark individual song added to playlist
     * @param song
     */
    public void markPlaylistSong (Song song) {
        int songId = song.getId();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SONGS SET IN_PLAYLIST = 1 WHERE SONG_ID =" + songId + ";");
    }

    /**
     * clear songs marked for playlist
     */
    public void clearPlaylistSongs () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SONGS SET IN_PLAYLIST = 0;");
    }

    public void delSong (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SONGS WHERE SONG_ID = " + id +";");
    }

    public List<Song> getAllSongs () {
        List<Song> songList = new ArrayList<>();
        Song song;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM SONGS;", null);
        if (cursor.moveToFirst()) {
            do {
                song = new Song();
                song.setId(cursor.getInt(0));
                song.setSongName(cursor.getString(1));
                song.setSongArtist(cursor.getString(2));
                song.setSongLength(cursor.getInt(3));
                song.setSongAlbum(cursor.getString(4));
                song.setSongPath(cursor.getString(5));
                song.setSkipped(cursor.getInt(6));
                songList.add(song);
            } while (cursor.moveToNext());
        }
        return songList;
    }

    /**
     * get songs marked for playlist
     * @return list of songs in playlist
     */
    public List<Song> getPlaylistSongs () {
        List<Song> songList = new ArrayList<>();
        Song song;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM SONGS WHERE IN_PLAYLIST = 1;", null);
        if (cursor.moveToFirst()) {
            do {
                song = new Song();
                song.setId(cursor.getInt(0));
                song.setSongName(cursor.getString(1));
                song.setSongArtist(cursor.getString(2));
                song.setSongLength(cursor.getInt(3));
                song.setSongAlbum(cursor.getString(4));
                song.setSongPath(cursor.getString(5));
                song.setSkipped(cursor.getInt(6));
                songList.add(song);
            } while (cursor.moveToNext());
        }
        return songList;
    }

    /**
     * return song from database by id
     * @param id od song
     * @return
     */
    public Song getSong (int id){
        Song song = new Song();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM SONGS WHERE SONG_ID = " + id + ";", null);
        if (cursor.moveToFirst()){
            song.setId(cursor.getInt(0));
            song.setSongName(cursor.getString(1));
            song.setSongArtist(cursor.getString(2));
            song.setSongLength(cursor.getInt(3));
            song.setSongAlbum(cursor.getString(4));
            song.setSongPath(cursor.getString(5));
            song.setSkipped(cursor.getInt(6));
            return song;
        }
        return null;
    }

    /**
     * return songs with enough skipped to delete them
     * @return
     */
    public List<Song> getSongsForDel (int totalSkip, int weekSkip){
        List<Song> songList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM SONGS WHERE SKIPPED_TOTAL > " + totalSkip + "  OR SKIPPED_IN_WEEK >" + weekSkip + " ;", null);
        if (cursor.moveToFirst()){
            do {
                Song song = new Song();
                song.setId(cursor.getInt(0));
                song.setSongName(cursor.getString(1));
                song.setSongArtist(cursor.getString(2));
                song.setSongLength(cursor.getInt(3));
                song.setSongAlbum(cursor.getString(4));
                song.setSongPath(cursor.getString(5));
                song.setSkipped(cursor.getInt(6));
                songList.add(song);
            } while (cursor.moveToNext());
        }
        return songList;
    }

    /**
     * add mark to skipped song
     * @param id
     */
    public void markSkippedSong (int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SONGS SET SKIPPED_TOTAL = SKIPPED_TOTAL + 1 WHERE SONG_ID =" + id + ";");
    }

    /**
     * clear marks about total count of skipping songs
     */
    public void clearSkippedTotal (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SONGS SET SKIPPED_TOTAL = 0;");
    }

    /**
     * clear marks about week count of skipping songs
     */
    public void clearSkippedWeek () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE SONGS SET SKIPPED_WEEK = 0;");
    }

    /**
     * clear whole SONGS table
     */
    public void clearSongs () {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM SONGS;");
    }

    /**
     * create table SONGS
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE SONGS (SONG_ID INTEGER PRIMARY KEY NOT NULL,NAME TEXT, ARTIST TEXT, LENGTH INTEGER,ALBUM TEXT, PATH TEXT, SKIPPED_TOTAL INTEGER, SKIPPED_IN_WEEK INTEGER, IN_PLAYLIST INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE SONGS;");
        onCreate(db);
    }
}
