package com.example.tomas.motionmedia;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, MainActivityFragment.GoOnSongListListener, SongListFragment.GoOnMainListener{
    private SongListFragment songListFragment = new SongListFragment();
    private MainActivityFragment mainFragment = new MainActivityFragment();
    private SongsManager songsManager = new SongsManager();
    private List<Song> actualPlaylist = new ArrayList<>();
    private List<Object> objectSongList = new ArrayList<>();
    private List<String> artistList = new ArrayList<>();
    private List<Song> allSongList = new ArrayList<>();
    private Song currentSong;
    private int currentSongIndex;
    private int xCoordinationSensitivity, yCoordinationSensitivity, zCoordinationSensitivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Prepare container for fragments
         */
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh){
            songListFragment.refreshSongs();
            Toast.makeText(getApplicationContext(), "List of songs in your device was actualized", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void goOnSongList() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, songListFragment );
        if (objectSongList.isEmpty() || artistList.isEmpty() || allSongList.isEmpty()){
            objectSongList = songsManager.getObjectSongList(getApplicationContext());
            artistList = songsManager.getArtistsList();
            allSongList = songsManager.getAllSongList();
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void goOnMain (Song song, List<Song> playList) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mainFragment );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mainFragment.setPlayList(playList);
        mainFragment.setAnotherSong(playList.indexOf(song));
        mainFragment.play(song.getSongPath());
    }

    /**
     * Refresh list of all songs in device
     */
    public void refreshSongs () {
        objectSongList = songsManager.getObjectSongList(getApplicationContext());
        artistList = songsManager.getArtistsList();
        allSongList = songsManager.getAllSongList();
    }

    public List<String> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<String> artistList) {
        this.artistList = artistList;
    }

    public List<Object> getObjectSongList() {
        return objectSongList;
    }

    public void setObjectSongList(List<Object> objectSongList) {
        this.objectSongList = objectSongList;
    }

    public List<Song> getActualPlaylist() {
        return actualPlaylist;
    }

    public void setActualPlaylist(List<Song> actualPlaylist) {
        this.actualPlaylist = actualPlaylist;
    }

    public List<Song> getAllSongList() {
        return allSongList;
    }

    public void setAllSongList(List<Song> allSongList) {
        this.allSongList = allSongList;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }
}
