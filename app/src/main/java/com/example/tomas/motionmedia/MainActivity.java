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

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, MainActivityFragment.GoOnSongListListener, MainActivityFragment.GoOnSettingsListener, MainActivityFragment.GoOnHelpListener, SongListFragment.GoOnMainListener{
    private SongListFragment songListFragment = new SongListFragment();
    private MainActivityFragment mainFragment = new MainActivityFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private HelpFragment helpFragment = new HelpFragment();
    private SongsManager songsManager = new SongsManager();
    private List<Song> actualPlaylist = new ArrayList<>();
    private List<Object> objectSongList = new ArrayList<>();
    private List<String> artistList = new ArrayList<>();
    private List<Song> allSongList = new ArrayList<>();
    private Song currentSong;
    private int currentSongIndex;
    private int xCoordinationSensitivity = 18, yCoordinationSensitivity = 18, zCoordinationSensitivity = 18;
    private int currentArtistIndex;
    private boolean useMotionControl;
    private boolean skippedSong;

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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            goOnSettings();
            return true;
        } else if (id == R.id.action_refresh){
            try {
                refreshSongs();
                Toast.makeText(getApplicationContext(), "List of songs in your device was actualized", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "No songs were found", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.action_help) {
            goOnHelp();
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

    /**
     * Refresh list of all songs in device
     */
    public void refreshSongs () {
        objectSongList = songsManager.getObjectSongList(getApplicationContext());
        artistList = songsManager.getArtistsList();
        allSongList = songsManager.getAllSongList();
    }

    /**
     * transition to another fragment
     */
    public void goOnHelp ()  {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, helpFragment);
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
    public void goOnSettings () {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, settingsFragment );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

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

    /**
     * getters and setters for attributes
     */

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

    public boolean isUseMotionControl() {
        return useMotionControl;
    }

    public void setUseMotionControl(boolean useMotionControl) {
        this.useMotionControl = useMotionControl;
    }

    public int getxCoordinationSensitivity() {
        return xCoordinationSensitivity;
    }

    public void setxCoordinationSensitivity(int xCoordinationSensitivity) {
        this.xCoordinationSensitivity = xCoordinationSensitivity;
    }

    public int getyCoordinationSensitivity() {
        return yCoordinationSensitivity;
    }

    public void setyCoordinationSensitivity(int yCoordinationSensitivity) {
        this.yCoordinationSensitivity = yCoordinationSensitivity;
    }

    public int getzCoordinationSensitivity() {
        return zCoordinationSensitivity;
    }

    public void setzCoordinationSensitivity(int zCoordinationSensitivity) {
        this.zCoordinationSensitivity = zCoordinationSensitivity;
    }

    public boolean isSkippedSong() {
        return skippedSong;
    }

    public void setSkippedSong(boolean skippedSong) {
        this.skippedSong = skippedSong;
    }

    public int getCurrentArtistIndex() {
        return currentArtistIndex;
    }

    public void setCurrentArtistIndex(int currentArtistIndex) {
        this.currentArtistIndex = currentArtistIndex;
    }

    public SongListFragment getSongListFragment() {
        return songListFragment;
    }

}
