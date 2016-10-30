package com.example.tomas.motionmedia;

import android.content.Context;
import android.graphics.Color;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private GoOnSongListListener goOnSongListListener;
    private Song song;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Button playButton, randomButton, repeatButton;
    private TextView songTimeCurent, songName, songArtist, songAlbum, songTimeEnd;
    private Boolean isRandom = false;
    private Boolean isRepeat = false;
    private List<Song> songList = new ArrayList<Song>();
    private Random random = new Random();
    private List<Integer> previousRandomValues = new ArrayList<Integer>();
    private int previousRandomValuesListCounter;
    private List<Integer> playedSongIndexList = new ArrayList<>();
    private SeekBar songSeekBar;
    private ImageView songImageView;
    private AsyncPlayer asyncPlayer = new AsyncPlayer("");
    private  final Handler handler=new Handler();

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View layout;
        layout =  inflater.inflate(R.layout.fragment_main, container, false);

        repeatButton = (Button) layout.findViewById(R.id.repeatButton);
        randomButton = (Button) layout.findViewById(R.id.randomButton);
        playButton = (Button) layout.findViewById(R.id.playButton);
        Button nextButton = (Button) layout.findViewById(R.id.nextButton);
        Button previousButton = (Button) layout.findViewById(R.id.prevButton);
        Button trackListButton = (Button) layout.findViewById(R.id.trackListButton);
        songName = (TextView) layout.findViewById(R.id.songNameText);
        songTimeCurent = (TextView) layout.findViewById(R.id.timeText1);
        songTimeEnd = (TextView) layout.findViewById(R.id.timeText2);
        songArtist = (TextView) layout.findViewById(R.id.artistTextView);
        songAlbum = (TextView) layout.findViewById(R.id.albumTextView);
        songSeekBar = (SeekBar) layout.findViewById(R.id.songSeekBar);
        songImageView = (ImageView) layout.findViewById(R.id.imageView);

        if (song != null){
            setSongDescription(song);
            setSongTime(song);
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
                } else {
                    randomButton.setShadowLayer(8,-1,2,Color.WHITE);
                }
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeat(!getRepeat());
                if (isRepeat){
                    repeatButton.setShadowLayer(8,-1,2,Color.BLUE);
                } else {
                    repeatButton.setShadowLayer(8,-1,2,Color.WHITE);
                }
            }
        });

        setPlayButton();

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRandom) {
                    if (previousRandomValuesListCounter - 1 > (- 1)){
                        setAnotherSong(previousRandomValues.get(previousRandomValuesListCounter-1));
                        setSongDescription(getSong());
                        previousRandomValuesListCounter--;
                    } else {
                        previousRandomValuesListCounter = previousRandomValues.size() - 1;
                        setAnotherSong(previousRandomValues.get(previousRandomValuesListCounter-1));
                        setSongDescription(getSong());
                        previousRandomValuesListCounter--;
                    }
                } else {
                    int i = songList.indexOf(song);
                    i--;
                    if (-1 < i){
                        setAnotherSong(i);
                        setSongDescription(getSong());
                    } else {
                        i = songList.size() - 1;
                        setAnotherSong(i);
                        setSongDescription(getSong());
                    }
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               nextSong();
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
        return layout;
    }

    public void setPlayButton () {
        if (mediaPlayer.isPlaying()){
            playButton.setText("Pause");
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playButton.setText("Play");
                    mediaPlayer.pause();
                    setPlayButton();
                }
            });
        } else {
            playButton.setText("Play");
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playButton.setText("Pause");
                    mediaPlayer.start();
                    setPlayButton();
                }
            });
        }
    }

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

    public void setSongTime (Song song){
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

    public void setSongDescription (Song song){
        songName.setText("Song: " + song.getSongName());
        songAlbum.setText( "Album: " + song.getSongArtist());
        songArtist.setText("Artist: " + song.getSongArtist());
        songSeekBar.setMax(mediaPlayer.getDuration()/1000);
        songTimeEnd.setText( String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(song.getSongLength()),
                TimeUnit.MILLISECONDS.toSeconds(song.getSongLength()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(song.getSongLength()))
        ));
    }

    public void setAnotherSong (int i) {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        setSong(songList.get(i));
        play(songList.get(i).getSongPath());
    }

    public void play (String path) {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try{
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    nextSong();
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (!isRepeat){
            playedSongIndexList.add(songList.indexOf(getSong()));
        }
    }

    public interface GoOnSongListListener {
        public void goOnSongList();
    }

    public void nextSong () {
        if (isRandom && isRepeat) {
            int low = 0;
            int high = songList.size();
            int result = random.nextInt(high - low) + low;
            setAnotherSong(result);
            setSongDescription(getSong());
            refreshPreviousRandomValuesList(result);
        } else if (!isRandom && isRepeat) {
            int i = songList.indexOf(song);
            i++;
            if (i < songList.size() ){
                setAnotherSong(i);
                setSongDescription(getSong());
            } else {
                i = 0;
                setAnotherSong(i);
                setSongDescription(getSong());
            }
        } else if (!isRandom && !isRepeat) {
            int i = songList.indexOf(getSong());
            i ++;
            if (i < songList.size() && !playedSongIndexList.contains(i) ){
                setAnotherSong(i);
                setSongDescription(getSong());}
            else if (i == songList.size() && !playedSongIndexList.contains(i)) {
                i=0;
                setAnotherSong(i);
                setSongDescription(getSong());
            } else {
                mediaPlayer.stop();
                mediaPlayer.reset();
                songTimeEnd.setText("0:00");
                songTimeCurent.setText("0:00");
                setPlayButton();
            }
        } else if (isRandom && !isRepeat) {
            int low = 0;
            int high = songList.size();
            int result = random.nextInt(high - low) + low;
            while (playedSongIndexList.contains(result)){
                result = random.nextInt(high - low) + low;
            }
            if (!previousRandomValues.contains(result)){
                setAnotherSong(result);
                setSongDescription(getSong());
                refreshPreviousRandomValuesList(result);
            } else {
                mediaPlayer.stop();
                mediaPlayer.reset();
                songTimeEnd.setText("0:00");
                songTimeCurent.setText("0:00");
                setPlayButton();
            }

        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        goOnSongListListener = (GoOnSongListListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        goOnSongListListener = null;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Boolean getRepeat() {
        return isRepeat;
    }

    public void setRepeat(Boolean repeat) {
        isRepeat = repeat;
        if (isRepeat){
            playedSongIndexList = new ArrayList<>();
            playedSongIndexList.add(songList.indexOf(getSong()));
        }
    }

    public Boolean getRandom() {
        return isRandom;
    }

    public void setRandom(Boolean random) {
        isRandom = random;
        if (isRandom) {
            previousRandomValues = new ArrayList<>();
        }
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
}
