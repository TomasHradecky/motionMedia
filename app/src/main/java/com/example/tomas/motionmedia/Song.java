package com.example.tomas.motionmedia;


/**
 * Created by Tomas on 25.10.2016.
 */

/**
 * Class representing audio file in device storage(internal and external)
 */
public class Song {
    private String songName;
    private String songArtist;
    private int songLength;
    private String songPath;
    private String songAlbum;

    /**
     * constructor
     * @param songName name of song
     * @param songArtist artist of song
     * @param songLength length of song in milliseconds
     * @param songPath location of song in device
     * @param songAlbum album of song
     */
    public Song (String songName,String songArtist,int songLength, String songPath, String songAlbum) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.songLength = songLength;
        this.songPath = songPath;
        this.songAlbum = songAlbum;
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

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }
}
