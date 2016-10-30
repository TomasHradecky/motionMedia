package com.example.tomas.motionmedia;


/**
 * Created by Tomas on 25.10.2016.
 */

public class Song {
    private String songName;
    private String songArtist;
    private int songLength;
    private String songPath;

    public Song (String songName,String songArtist,int songLength, String songPath) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.songLength = songLength;
        this.songPath = songPath;
    }


    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public int getSongLength() {
        return songLength;
    }

    public void setSongLength(int songLength) {
        this.songLength = songLength;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }
}
