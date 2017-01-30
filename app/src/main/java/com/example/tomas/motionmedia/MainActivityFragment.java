package com.example.tomas.motionmedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.content.Context.SENSOR_SERVICE;


/**
 * Created by Tomas Hradecky
 * Main fragment with music player features
 */
public class MainActivityFragment extends Fragment implements SensorEventListener {
    private GoOnSongListListener goOnSongListListener;
    private GoOnSettingsListener goOnSettingsListener;
    private GoOnHelpListener goOnHelpListener;
    private Song currentSong;
    private int currentSongIndex;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Button playButton, pauseButton, randomButton, repeatButton, previousButton, nextButton, trackListButton;
    private TextView songTimeCurent, songName, songArtist, songAlbum, songTimeEnd;
    private Boolean isRandom = false;
    private Boolean isRepeat = false;
    private List<Song> playList = new ArrayList<Song>();
    private Random random = new Random();
    private List<Integer> previousRandomValues = new ArrayList<Integer>();
    private int previousRandomValuesListCounter;
    private List<Integer> playedSongIndexList = new ArrayList<>();
    private SeekBar songSeekBar;
    private ImageView songImageView;
    private Handler handler=new Handler();
    private SensorManager sensorManager;
    private float ax, ay, dAx, dAy, lAx, lAy, absTmpAx, absAx, absAy, absTmpAy;
    private float[] xDataList = new float[4];
    private float[] yDataList = new float[4];
    private float[] xDeltaList = new float[2];
    private float[] yDeltaList = new float[2];
    private boolean counter, isLinearAccell = false, isPaused = false, useMotion = false, newData = false, newDelta = false;
    private int intCounter, intDeltaCounter;
    private long timeNow;
    private long lastUpdateTime;
    private long lastActionTime;
    /**
     * nonparametric constructor
     */
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layout;
        layout =  inflater.inflate(R.layout.fragment_main, container, false);
        lastUpdateTime = System.currentTimeMillis();
        prepareSensors();

        repeatButton = (Button) layout.findViewById(R.id.repeatButton);
        randomButton = (Button) layout.findViewById(R.id.randomButton);
        playButton = (Button) layout.findViewById(R.id.playButton);
        pauseButton = (Button) layout.findViewById(R.id.pauseButton);
        nextButton = (Button) layout.findViewById(R.id.nextButton);
        previousButton = (Button) layout.findViewById(R.id.prevButton);
        trackListButton = (Button) layout.findViewById(R.id.trackListButton);
        songName = (TextView) layout.findViewById(R.id.songNameText);
        songTimeCurent = (TextView) layout.findViewById(R.id.timeText1);
        songTimeEnd = (TextView) layout.findViewById(R.id.timeText2);
        songArtist = (TextView) layout.findViewById(R.id.artistTextView);
        songAlbum = (TextView) layout.findViewById(R.id.albumTextView);
        songSeekBar = (SeekBar) layout.findViewById(R.id.songSeekBar);
        songImageView = (ImageView) layout.findViewById(R.id.imageView);

        trackListButton.setEnabled(false);
        playButton.setEnabled(false);
        previousButton.setEnabled(false);
        nextButton.setEnabled(false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        currentSongIndex = preferences.getInt("currentSongIndex", currentSongIndex);
        setPlayList(((MainActivity)getActivity()).getDb().getPlaylistSongs());
        randomButton.setActivated(preferences.getBoolean("random", false));
        isRandom = preferences.getBoolean("random", false);
        repeatButton.setActivated(preferences.getBoolean("repeat", false));
        isRepeat = preferences.getBoolean("repeat", false);
        isPaused = preferences.getBoolean("paused", false);
        setPlayList(((MainActivity)getActivity()).getDb().getPlaylistSongs());

        if (isRandom){
            randomButton.setShadowLayer(8,-1,2,Color.BLUE);
        } else {
            randomButton.setShadowLayer(8,-1,2,Color.WHITE);
        }
        if (isRepeat){
            repeatButton.setShadowLayer(8,-1,2,Color.BLUE);
        } else {
            repeatButton.setShadowLayer(8,-1,2,Color.WHITE);
        }

        if (currentSong != null){
            setSongDescription(currentSong);
            setSongTime(currentSong);
            songSeekBar.setClickable(true);
        } else if (!playList.isEmpty() & playList.size() >= currentSongIndex){
            setCurrentSong(playList.get(currentSongIndex));
            setSongDescription(currentSong);
            setSongTime(currentSong);
            songSeekBar.setClickable(true);
        }

        trackListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    goOnSongListListener.goOnSongList();
            }
        });

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandom(!getRandom());
                if (isRandom){
                    randomButton.setShadowLayer(8,-1,2,Color.BLUE);
                    Toast.makeText(getContext(), "Random play was activated", Toast.LENGTH_SHORT);
                } else {
                    randomButton.setShadowLayer(8,-1,2,Color.WHITE);
                    Toast.makeText(getContext(), "Random play was deactivated", Toast.LENGTH_SHORT);
                }
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("random", getRandom());
                editor.commit();
                previousButton.setEnabled(true);
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeat(!getRepeat());
                if (isRepeat){
                    repeatButton.setShadowLayer(8,-1,2,Color.BLUE);
                    Toast.makeText(getContext(), "Repeating was activated", Toast.LENGTH_SHORT).show();
                } else {
                    repeatButton.setShadowLayer(8,-1,2,Color.WHITE);
                    Toast.makeText(getContext(), "Repeating was deactivated", Toast.LENGTH_SHORT).show();
                }
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("repeat", getRepeat());
                editor.commit();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousSongButtonAction();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSongButtonAction();
                previousButton.setEnabled(true);
            }
        });

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(songSeekBar.getProgress()*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setSharedPreferences();
        waitUntilSongsLoaded();
        setPlayButton();
        setPlayList(((MainActivity)getActivity()).getDb().getPlaylistSongs());
        return layout;
    }

    public void waitUntilSongsLoaded (){
        int i = 0;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        i = preferences.getInt("iterator", 0);
        if(((MainActivity)getActivity()).isSongsLoaded()){
            trackListButton.setEnabled(true);
            playButton.setEnabled(true);
            previousButton.setEnabled(true);
            nextButton.setEnabled(true);
            if (i == 0){
                Toast.makeText(getContext(), "Songs are loaded and ready for play", Toast.LENGTH_SHORT).show();
                i++;
                editor.putInt("iterator", i);
                editor.commit();
            }

        } else {
            try {
                trackListButton.setEnabled(false);
                playButton.setEnabled(false);
                previousButton.setEnabled(false);
                nextButton.setEnabled(false);
                Toast.makeText(getContext(), "Wait until songs will be loaded and ready for play", Toast.LENGTH_LONG).show();
                Thread.sleep(200);
                waitUntilSongsLoaded();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save data to sharedPreferences for next open
     */
    public void setSharedPreferences (){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("artist", songArtist.getText().toString());
        editor.putString("name", songName.getText().toString());
        editor.putString("album", songAlbum.getText().toString());
        editor.putBoolean("random", getRandom());
        editor.putBoolean("repeat", getRepeat());
        editor.commit();
    }

    /**
     * Set play button preferences by status
     */
    public void setPlayButton () {
        if (mediaPlayer.isPlaying()){
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer.pause();
                    setPlayButton();
                }
            });
        } else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (playList.isEmpty()){
                        ((MainActivity)getActivity()).refreshSongs();
                        setPlayList(((MainActivity)getActivity()).getDb().getPlaylistSongs());
                        nextSongButtonAction();
                        setSongTime(getCurrentSong());
                    } else {
                        if (isPaused & mediaPlayer.getCurrentPosition() != 1) {
                            continuePlay();
                        } else {
                            setAnotherSong(playList.get(currentSongIndex));
                            setCurrentSong(playList.get(currentSongIndex));
                        }
                    }
                    setSongDescription(getCurrentSong());
                    songSeekBar.setClickable(true);
                    setPlayButton();
                }
            });
        }
    }

    /**
     * Refreshing list contains previously generated random number which represent songs index in playlist
     * @param nextVal next random value
     */
    public void refreshPreviousRandomValuesList(int nextVal) {
        if (previousRandomValues.size() < 20) {
           previousRandomValues.add(nextVal);
            previousRandomValuesListCounter++;
        } else {
            if (previousRandomValuesListCounter < 20) {
                previousRandomValuesListCounter++;
                previousRandomValues.set(previousRandomValuesListCounter,nextVal);
            } else {
                previousRandomValuesListCounter = 0;
                previousRandomValues.set(previousRandomValuesListCounter,nextVal);
            }
        }
    }


    /**
     *Continuously setting currentSong time in text views and progress in seek bar
     * @param song currently playing currentSong
     */
    public void setSongTime (Song song){
        if (handler == null){
            handler = new Handler();
        }
        handler.post(new Runnable(){
            @Override
            public void run() {
                songTimeCurent.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition()),
                        TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition()))

                ));
                songSeekBar.setProgress(mediaPlayer.getCurrentPosition()/1000);
                handler.postDelayed(this,900); // set time here to refresh textView
            }
        });

    }

    /**
     *Set artist, ablum and name of currently playing currentSong
     * @param song currently playing currentSong
     */
    public void setSongDescription (Song song){
        songName.setText("Song: " + song.getSongName());
        songAlbum.setText( "Album: " + song.getSongAlbum());
        songArtist.setText("Artist: " + song.getSongArtist());
        songSeekBar.setMax(mediaPlayer.getDuration()/1000);
        songTimeEnd.setText( String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(song.getSongLength()),
                TimeUnit.MILLISECONDS.toSeconds(song.getSongLength()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(song.getSongLength()))
        ));
    }

    /**
     * Set next currentSong for play
     */
    public void setAnotherSong (Song song) {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        ((MainActivity)getActivity()).setCurrentSong(getCurrentSong());
        ((MainActivity)getActivity()).setCurrentSongIndex(getCurrentSongIndex());
        setSharedPreferences();
        play(song.getSongPath());
    }

    public void continuePlay () {
            mediaPlayer.start();
    }

    /**
     *Start play set currentSong
     * @param path path to currentSong location
     */
    public void play (String path) {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try{
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if(path.startsWith("'")){
                path = path.substring(1, path.length()-1);
            }
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    nextSongButtonAction();
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (!isRepeat){
            playedSongIndexList.add(getCurrentSongIndex());
        }
    }

    public interface GoOnSongListListener {
        public void goOnSongList();
    }

    public interface GoOnSettingsListener {
        public void goOnSettings ();
    }

    public interface GoOnHelpListener {
        public void goOnHelp ();
    }

    /**
     *Choose which next currentSong is chosen (random and repeat or not)
     */
    public void nextSongButtonAction() {
        if (currentSong != null) {
            Database db = ((MainActivity)getActivity()).getDb();
            db.markSkippedSong(getCurrentSong().getId());
        }
        if (isRandom && isRepeat) {
            int low = 0;
            int high = playList.size();
            int result = random.nextInt(high - low) + low;
            Song nextSong = playList.get(result);
            setAnotherSong(nextSong);
            setCurrentSong(nextSong);
            setSongDescription(getCurrentSong());
            refreshPreviousRandomValuesList(result);
        } else if (!isRandom && isRepeat) {
            int i = getCurrentSongIndex();
            i++;
            if (i < playList.size() ){
                prepareNextSong(i);
                Song nextSong = playList.get(i);
                setAnotherSong(nextSong);
                setCurrentSong(nextSong);
                setSongDescription(getCurrentSong());
                setCurrentSongIndex(i);
            } else {
                i = 0;
                prepareNextSong(i);
                Song nextSong = playList.get(i);
                setAnotherSong(nextSong);
                setCurrentSong(nextSong);
                setSongDescription(getCurrentSong());
                setCurrentSongIndex(i);
            }
        } else if (!isRandom && !isRepeat) {
            int i = getCurrentSongIndex();
            i ++;
            if (i < playList.size() && !playedSongIndexList.contains(i) ){
                Song nextSong = playList.get(i);
                setAnotherSong(nextSong);
                setCurrentSong(nextSong);
                setSongDescription(getCurrentSong());
                setCurrentSongIndex(i);
            }
            else if (i == playList.size() && !playedSongIndexList.contains(i)) {
                i=0;
                Song nextSong = playList.get(i);
                setAnotherSong(nextSong);
                setCurrentSong(nextSong);
                setSongDescription(getCurrentSong());
                setCurrentSongIndex(i);
            } else {
                mediaPlayer.stop();
                mediaPlayer.reset();
                songTimeEnd.setText("0:00");
                songTimeCurent.setText("0:00");
                setPlayButton();
            }
        } else if (isRandom && !isRepeat) {
            int low = 0;
            int high = playList.size();
            int result = random.nextInt(high - low) + low;
            while (playedSongIndexList.contains(result)){
                result = random.nextInt(high - low) + low;
            }
            if (!previousRandomValues.contains(result)){
                Song nextSong = playList.get(result);
                setAnotherSong(nextSong);
                setCurrentSong(nextSong);
                setSongDescription(getCurrentSong());
                refreshPreviousRandomValuesList(result);
            } else {
                mediaPlayer.stop();
                mediaPlayer.reset();
                songTimeEnd.setText("0:00");
                songTimeCurent.setText("0:00");
                setPlayButton();
            }
        }
        setPlayButton();
    }

    public void prepareNextSong (int i) {
        if (playList.isEmpty()){
            setPlayList(((MainActivity)getActivity()).getDb().getPlaylistSongs());
        }
        Song s = playList.get(i);
        if(s.getSongPath().startsWith("'")){
            String path = s.getSongPath();
            path = path.substring(1, path.length()-1);
            s.setSongPath(path);

        }
        if(s.getSongName().startsWith("'")){
            String name = s.getSongName();
            name = name.substring(1, name.length()-1);
            s.setSongName(name);
        }
        playList.set(i,s);
    }

    public void nextRandomSong () {
        int low = 0;
        int high = playList.size();
        int result = random.nextInt(high - low) + low;
        Song nextSong = playList.get(result);
        setAnotherSong(nextSong);
        setCurrentSong(nextSong);
        setSongDescription(getCurrentSong());
    }

    public void previousSongButtonAction () {
        if (isRandom && previousRandomValues.size() != 0) {
            if (previousRandomValuesListCounter - 1 > (- 1)){
                Song nextSong = playList.get(previousRandomValues.get(previousRandomValuesListCounter-1));
                setAnotherSong(nextSong);
                setCurrentSong(nextSong);
                setSongDescription(getCurrentSong());
                previousRandomValuesListCounter--;
            } else {
                previousRandomValuesListCounter = previousRandomValues.size() - 1;
                Song nextSong = playList.get(previousRandomValues.get(previousRandomValuesListCounter));
                setAnotherSong(nextSong);
                setCurrentSong(nextSong);
                setSongDescription(getCurrentSong());
                previousRandomValuesListCounter--;
            }
        } else if (isRandom && previousRandomValues.size() == 0) {
            previousButton.setEnabled(false);
        }
        else if (!isRandom) {
            int i = getCurrentSongIndex();
            i--;
            if (-1 < i){
                Song nextSong = playList.get(i);
                setAnotherSong(nextSong);
                setCurrentSong(nextSong);
                setSongDescription(getCurrentSong());
                setCurrentSongIndex(i);
            } else {
                i = playList.size() - 1;
                Song nextSong = playList.get(i);
                setAnotherSong(nextSong);
                setCurrentSong(nextSong);
                setSongDescription(getCurrentSong());
                setCurrentSongIndex(i);
            }
        }
    }

    public void prepareSensors () {
        sensorManager=(SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
            isLinearAccell = true;
        } else {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void nextRandomArtist () {
        int nextArtistIndex = ((MainActivity)getActivity()).getCurrentArtistIndex() + 1;
        SongListFragment songListFragment = ((MainActivity)getActivity()).getSongListFragment();
        songListFragment.setNextArtistSonglist(nextArtistIndex);
    }

    //motion control code block
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (((MainActivity)getActivity()).isUseMotionControl()){
            if (isLinearAccell) {
                if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
                    timeNow = System.currentTimeMillis();
                    lAx=event.values[0];
                    lAy=event.values[1];
                    if (lastActionTime + 1000 < System.currentTimeMillis()) {
                        if (lAx > ((MainActivity)getActivity()).getxCoordinationSensitivity() && mediaPlayer.isPlaying()){
                            lastActionTime = System.currentTimeMillis();
                            clearSensorData();
                            nextSongButtonAction();
                        }
                        if (lAx < -((MainActivity)getActivity()).getxCoordinationSensitivity() && mediaPlayer.isPlaying()) {
                            lastActionTime = System.currentTimeMillis();
                            clearSensorData();
                            previousSongButtonAction();
                        }

                        if (lAy > ((MainActivity)getActivity()).getyCoordinationSensitivity() && mediaPlayer.isPlaying()) {
                            lastActionTime = System.currentTimeMillis();
                            clearSensorData();
                            nextRandomSong();
                        }
                        if (lAy < -((MainActivity)getActivity()).getyCoordinationSensitivity() && mediaPlayer.isPlaying()) {
                            lastActionTime = System.currentTimeMillis();
                            clearSensorData();
                            nextRandomArtist();
                        }
                    }
                }
            } else {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    float avgX = 0;
                    float avgDeltaX = 0;
                    float avgY = 0;
                    float avgDeltaY = 0;

                    timeNow = System.currentTimeMillis();
                    newDelta = false;
                    newData = false;
                    //get data minimally after 25 ms
                    if (counter & timeNow > lastUpdateTime + 25) {
                        ax=event.values[0];
                        ay=event.values[1];
                        lastUpdateTime = System.currentTimeMillis();
                        newData = true;
                        counter = !counter;
                    } else if (!counter & timeNow > lastUpdateTime + 25){
                        ax=event.values[0];
                        ay=event.values[1];
                        lastUpdateTime = System.currentTimeMillis();
                        counter = !counter;
                        newData = true;
                        newDelta = true;
                    }
                    //new sensor data are ready
                    if (newData) {
                        xDataList[intCounter] = ax;
                        yDataList[intCounter] = ay;
                        intCounter++;
                    }
                    //delta values are calculated in absolute value;
                    if (newDelta) {
                        absAx = Math.abs(xDataList[intCounter]);
                        absTmpAx = Math.abs(xDataList[intCounter - 1]);
                        if (absTmpAx > absAx){
                            dAx = absTmpAx - absAx;
                            xDeltaList[intDeltaCounter] = dAx;
                        } else {
                            dAx = absAx - absTmpAx;
                            xDeltaList[intDeltaCounter] = dAx;
                        }

                        absAy = Math.abs(yDataList[intCounter]);
                        absTmpAy = Math.abs(yDataList[intCounter - 1]);
                        if (absTmpAy > absAy){
                            dAy = absTmpAy - absAy;
                            yDeltaList[intDeltaCounter] = dAy;
                        } else {
                            dAy = absAy - absTmpAy;
                            yDeltaList[intDeltaCounter] = dAy;
                        }
                        intDeltaCounter++;
                    }
                    //set counter for fill fields with data
                    if (intCounter == xDataList.length){
                        intCounter = 0;
                        intDeltaCounter = 0;
                    }

                    /**
                     * if average is positive then all values in field are declared as positive and average is recalculate, similarly for negative average
                     */
                    //calculate average value of x speed
                    for (int i = 0; i < xDataList.length; i++){
                        avgX = avgX + xDataList[i];
                    }
                    avgX = avgX / xDataList.length;
                    if (avgX > 0){
                        avgX = 0;
                        for (int i = 0; i < xDataList.length; i++){
                            float tmp = Math.abs(xDataList[i]);
                            avgX = avgX + tmp;
                        }
                        avgX = avgX / 4;
                    } else {
                        avgX = 0;
                        for (int i = 0; i < xDataList.length; i++){
                            float tmp = Math.abs(xDataList[i]);
                            avgX = avgX - tmp;
                        }
                        avgX = avgX / 4;
                    }
                    //calculate average value of y speed
                    for (int i = 0; i < yDataList.length; i++){
                        avgY = avgY + yDataList[i];
                    }
                    avgY = avgY / yDataList.length;
                    if (avgY > 0){
                        avgY = 0;
                        for (int i = 0; i < yDataList.length; i++){
                            float tmp = Math.abs(yDataList[i]);
                            avgY = avgY + tmp;
                        }
                        avgY = avgY / 4;
                    } else {
                        avgY = 0;
                        for (int i = 0; i < yDataList.length; i++){
                            float tmp = Math.abs(yDataList[i]);
                            avgY = avgY - tmp;
                        }
                        avgY = avgY / 4;
                    }
                    //calculate average value of x delta speed
                    for (int i = 0; i < xDeltaList.length; i++){
                        avgDeltaX = avgDeltaX + xDeltaList[i];
                    }
                    avgDeltaX = avgDeltaX / 2;
                    //calculate average value of y delta speed
                    for (int i = 0; i < yDeltaList.length ; i++){
                        avgDeltaY = avgDeltaY + yDeltaList[i];
                    }
                    avgDeltaY = avgDeltaY / 2;

                    /**
                     *react on actions after 2 sec if average delta speed is bigger than 4 and average speed is bigger than value set by user
                     */
                    //Log.d("CURR", "      "+avgX + "       |||  " + avgY);
                    //Log.d("DELTA", "     "+avgDeltaX + "   |||  " +avgDeltaY);
                    if (lastActionTime + 2000 < System.currentTimeMillis()) {
                        if (avgDeltaX > 4 & avgX >((MainActivity)getActivity()).getxCoordinationSensitivity() & mediaPlayer.isPlaying()){
                            //Log.d("TAG", "NEXT " + avgX + "   ---   "  + ((MainActivity)getActivity()).getxCoordinationSensitivity() );
                            lastActionTime = System.currentTimeMillis();
                            clearSensorData();
                            nextSongButtonAction();
                        }
                        if (avgDeltaX > 4 & avgX < - ((MainActivity)getActivity()).getxCoordinationSensitivity() & mediaPlayer.isPlaying()) {
                            //Log.d("TAG", "PREV " + avgX + "   ---   "  + ((MainActivity)getActivity()).getxCoordinationSensitivity() );
                            lastActionTime = System.currentTimeMillis();
                            clearSensorData();
                            previousSongButtonAction();
                        }

                        if (avgDeltaY > 4 & avgY > ((MainActivity)getActivity()).getyCoordinationSensitivity() && mediaPlayer.isPlaying()) {
                            //Log.d("TAG", "SONG " + avgY + "   ---   "  + ((MainActivity)getActivity()).getyCoordinationSensitivity() );
                            lastActionTime = System.currentTimeMillis();
                            clearSensorData();
                            nextRandomSong();
                        }
                        if (avgDeltaY > 4 & avgY < - ((MainActivity)getActivity()).getyCoordinationSensitivity() && mediaPlayer.isPlaying()) {
                            //Log.d("TAG", "ARTIST " + avgY + "   ---   "  + ((MainActivity)getActivity()).getyCoordinationSensitivity() );
                            lastActionTime = System.currentTimeMillis();
                            clearSensorData();
                            nextRandomArtist();
                        }
                    } else {
                        clearSensorData();
                    }
                }
            }
        }
    }

    public  void clearSensorData () {
        ax = 0;
        dAx = 0;
        ay = 0;
        dAy = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        goOnSongListListener = (GoOnSongListListener) context;
        goOnSettingsListener = (GoOnSettingsListener) context;
        goOnHelpListener = (GoOnHelpListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        goOnSongListListener = null;
        goOnSettingsListener = null;
        goOnHelpListener = null;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
        if(currentSong.getSongPath().startsWith("'")){
            String path = currentSong.getSongPath();
            path = path.substring(1, path.length()-1);
            this.currentSong.setSongPath(path);
        }
        if(currentSong.getSongName().startsWith("'")){
            String name = currentSong.getSongName();
            name = name.substring(1, name.length()-1);
            this.currentSong.setSongName(name);
        }
    }

    public Boolean getRepeat() {
        return isRepeat;
    }

    public void setRepeat(Boolean repeat) {
        isRepeat = repeat;
        if (isRepeat){
            playedSongIndexList.clear();
            playedSongIndexList.add(getCurrentSongIndex());
        }
    }

    public Boolean getRandom() {
        return isRandom;
    }

    public void setRandom(Boolean random) {
        isRandom = random;
        if (isRandom) {
            previousRandomValues.clear();
        }
    }

    public List<Song> getPlayList() {
        return playList;
    }

    public void setPlayList(List<Song> playList) {
        this.playList = playList;
        playedSongIndexList.clear();
        Collections.sort(this.playList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getSongName().compareToIgnoreCase(o2.getSongName());
            }
        });
    }

    public int getCurrentSongIndex() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        currentSongIndex = preferences.getInt("currentSongIndex", currentSongIndex);
        return currentSongIndex;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("currentSongIndex", currentSongIndex);
        editor.commit();
        this.currentSongIndex = currentSongIndex;
    }
}

