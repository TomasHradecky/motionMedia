package com.example.tomas.motionmedia;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MainActivityFragment.GoOnSongListListener, MainActivityFragment.GoOnSettingsListener, MainActivityFragment.GoOnHelpListener, SongListFragment.GoOnMainListener{
    private SongListFragment songListFragment = new SongListFragment();
    private MainActivityFragment mainFragment = new MainActivityFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private HelpFragment helpFragment = new HelpFragment();
    private Database db = new Database(this);
    private SongsManager songsManager = new SongsManager();
    private List<Song> actualPlaylist = new ArrayList<>();
    private List<Object> objectSongList = new ArrayList<>();
    private List<String> artistList = new ArrayList<>();
    private List<Song> allSongList = new ArrayList<>();
    private List<Song> songForDel = new ArrayList<>();
    private Song currentSong;
    private int currentSongIndex, currentArtistIndex;
    private int xCoordinationSensitivity, yCoordinationSensitivity;
    private int countForDelTotal, countForDelWeek;
    private int defaultSensitivity = 5, defaultSkipWeek = 5, defaultSkipTotal = 10;
    private boolean useMotionControl, skippedSong, songsLoaded = false;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        songsLoaded = preferences.getBoolean("songsLoaded", false);
        xCoordinationSensitivity = preferences.getInt("xSens", defaultSensitivity);
        yCoordinationSensitivity = preferences.getInt("ySens", defaultSensitivity);
        countForDelWeek = preferences.getInt("skipWeek", defaultSkipWeek);
        countForDelTotal = preferences.getInt("skippedTotal", defaultSkipTotal);
        useMotionControl = preferences.getBoolean("useMotion", false);
        skippedSong = preferences.getBoolean("skipped", false);
        setActualPlaylist(db.getPlaylistSongs());

        if (!songsLoaded) {
            new SongLoader().execute();
        }

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
                Toast.makeText(getApplicationContext(), "Wait until refresh will be completed", Toast.LENGTH_LONG).show();
                refreshSongs();
                songListFragment.setActualPlayList(actualPlaylist);
                songListFragment.getActualSongListView().setAdapter(new SongListAdapter(this, actualPlaylist));
                songListFragment.getActualSongListView().invalidateViews();

                songListFragment.setAllSongList(allSongList);
                songListFragment.getAllSongListView().setAdapter(new SongListAdapter(this, allSongList));
                songListFragment.getAllSongListView().invalidateViews();

                songListFragment.setArtistList(artistList);
                songListFragment.setObjectSongList(objectSongList);
                songListFragment.getExpListView().setAdapter(new ExpandableSongListAdapter(this, objectSongList, artistList));
                songListFragment.getExpListView().invalidateViews();

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

    /**
     * Refresh list of all songs in device
     */
    public void refreshSongs () {
        objectSongList = songsManager.getObjectSongList(getApplicationContext());
        artistList = songsManager.getArtistsList();
        allSongList = songsManager.getAllSongList();
        actualPlaylist = db.getPlaylistSongs();
        songForDel = db.getSongsForDel(countForDelTotal, countForDelWeek);
        db.clearSongs();
        db.saveSongs(allSongList);
    }

    private class SongLoader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            objectSongList = songsManager.getObjectSongList(getApplicationContext());
            artistList = songsManager.getArtistsList();
            allSongList = songsManager.getAllSongList();
            actualPlaylist = db.getPlaylistSongs();
            db.saveSongs(allSongList);
            songsLoaded = true;
            editor.putBoolean("songsLoaded", songsLoaded);
            editor.commit();
            return "Executed";
        }
    }

    private class PlaylistSaver extends AsyncTask<List<Song>, Void, String> {

        @Override
        protected String doInBackground(List<Song>... playList) {
            db.clearPlaylistSongs();
            db.markPlaylistSongs(playList[0]);
            return "Executed";
        }
    }

    /**
     * transition to another fragment
     */
    public void goOnHelp ()  {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, helpFragment, "help");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void goOnMain (Song song, List<Song> playList) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mainFragment, "main" );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mainFragment.setPlayList(playList);
        new PlaylistSaver().execute(playList);
        mainFragment.setCurrentSong(song);
        mainFragment.setAnotherSong(song);
        int i = playList.indexOf(song);
        mainFragment.setCurrentSongIndex(i);
    }
    public void goOnSettings () {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, settingsFragment, "settings" );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void goOnSongList() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, songListFragment, "list" );
        if (objectSongList.isEmpty() || artistList.isEmpty() || allSongList.isEmpty()){
            objectSongList = songsManager.getObjectSongList(getApplicationContext());
            artistList = songsManager.getArtistsList();
            allSongList = songsManager.getAllSongList();
            actualPlaylist = db.getPlaylistSongs();
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

    public List<Object> getObjectSongList() {
        return objectSongList;
    }

    public List<Song> getActualPlaylist() {
        return actualPlaylist;
    }

    public void setActualPlaylist(List<Song> actualPlaylist) {
        Collections.sort(actualPlaylist, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getSongName().compareToIgnoreCase(o2.getSongName());
            }
        });
        this.actualPlaylist = actualPlaylist;
    }

    public List<Song> getAllSongList() {
        return allSongList;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    public boolean isUseMotionControl() {
        useMotionControl = preferences.getBoolean("useMotion", false);
        return useMotionControl;
    }

    public void setUseMotionControl(boolean useMotionControl){
        this.useMotionControl = useMotionControl;
        editor.putBoolean("useMotion", useMotionControl);
        editor.commit();
    }

    public int getxCoordinationSensitivity() {
        xCoordinationSensitivity = preferences.getInt("xSens", defaultSensitivity);
        return xCoordinationSensitivity;
    }

    public void setxCoordinationSensitivity(int xCoordinationSensitivity) {
        this.xCoordinationSensitivity = xCoordinationSensitivity;
        editor.putInt("xSens", xCoordinationSensitivity);
        editor.commit();
    }

    public int getyCoordinationSensitivity() {
        xCoordinationSensitivity = preferences.getInt("ySens", defaultSensitivity);
        return yCoordinationSensitivity;
    }

    public void setyCoordinationSensitivity(int yCoordinationSensitivity) {
        this.yCoordinationSensitivity = yCoordinationSensitivity;
        editor.putInt("ySens", yCoordinationSensitivity);
        editor.commit();
    }

    public boolean isSkippedSong() {
        skippedSong = preferences.getBoolean("skipped", false);
        return skippedSong;
    }

    public void setSkippedSong(boolean skippedSong) {
        this.skippedSong = skippedSong;
        editor.putBoolean("skipped", skippedSong);
        editor.commit();
    }

    public int getCurrentArtistIndex() {
        return currentArtistIndex;
    }

    public void setCurrentArtistIndex(int currentArtistIndex) {
        this.currentArtistIndex = currentArtistIndex;
    }

    public Database getDb() {
        return db;
    }

    public SongListFragment getSongListFragment() {
        return songListFragment;
    }

    public boolean isSongsLoaded() {
        songsLoaded = preferences.getBoolean("songsLoaded", false);
        return songsLoaded;
    }

    public int getCountForDelWeek() {
        countForDelTotal = preferences.getInt("skipWeek", defaultSkipWeek);
        return countForDelWeek;
    }

    public void setCountForDelWeek(int countForDelWeek) {
        this.countForDelWeek = countForDelWeek;
        editor = preferences.edit();
        editor.putInt("skipWeek", countForDelWeek);
        editor.commit();
    }

    public int getCountForDelTotal() {
        countForDelTotal = preferences.getInt("skipTotal", defaultSkipTotal);
        return countForDelTotal;
    }

    public void setCountForDelTotal(int countForDelTotal) {
        this.countForDelTotal = countForDelTotal;
        editor = preferences.edit();
        editor.putInt("skipTotal", countForDelTotal);
        editor.commit();
    }

    public MainActivityFragment getMainFragment() {
        return mainFragment;
    }

    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }
}
