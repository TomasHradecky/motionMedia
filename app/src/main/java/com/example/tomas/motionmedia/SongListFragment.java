package com.example.tomas.motionmedia;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import static android.R.attr.button;
import static android.R.attr.fragment;
import static android.R.attr.type;

public class SongListFragment extends Fragment {
    private ListView songListView;
    private List<Song> songList;
    private List<Object> trackList;
    private List<String> artistList;
    private GoOnMainListener goOnMainListener;
    private SongsManager songsManager = new SongsManager();
    private View layout;
    private Button artistButton, allSongListButton;

    public SongListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (trackList == null || artistList==null || songList==null){
            trackList = songsManager.parseAllAudio(getContext());
            artistList = songsManager.getArtistsList();
            songList = songsManager.getSongList();
        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        //final View layoutSongs = inflater.inflate(R.layout.fragment_song_list, container, false);
        final Context context = getContext();
        layout = inflater.inflate(R.layout.fragment_expandable_song_list, container, false);
        artistButton = (Button) layout.findViewById(R.id.artistListButton);
        allSongListButton = (Button) layout.findViewById(R.id.allSongListButton);

        setButtons(inflater,container);
/*
        songListView = (ListView) layout.findViewById(R.id.songList);
        final Context context = getContext();
        songListView.setAdapter(new SongListAdapter(context, songList));

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongListAdapter adapter = new SongListAdapter(context, songList);
                Song song = (Song) adapter.getItem(position);
                goOnMainListener.goOnMain(song, songList);
            }
        });*/
        return layout;
    }

    public void setButtons (final LayoutInflater inflater, final ViewGroup container ) {
        artistButton.setEnabled(false);
        final ExpandableListView expList = (ExpandableListView) layout.findViewById(R.id.expandSongList);
        final ListView list = (ListView) layout.findViewById(R.id.songList);
        list.setVisibility(View.GONE);

        expList.setAdapter(new ExpandableSongListAdapter(getContext(), trackList, artistList));

        artistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout = inflater.inflate(R.layout.fragment_expandable_song_list, container, false);
                //ExpandableListView list = (ExpandableListView) layout.findViewById(R.id.expandSongList);
                expList.setAdapter(new ExpandableSongListAdapter(getContext(), trackList, artistList));
                allSongListButton.setEnabled(true);
                artistButton.setEnabled(false);
                list.setVisibility(View.GONE);
                expList.setVisibility(View.VISIBLE);
            }
        });

        allSongListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //layout = inflater.inflate(R.layout.fragment_song_list, container, false);
                //songListView = (ListView) layout.findViewById(R.id.songList);
                //songListView.setAdapter(new SongListAdapter(getContext(), songList));

                list.setAdapter(new SongListAdapter(getContext(), songList));
                artistButton.setEnabled(true);
                allSongListButton.setEnabled(false);
                expList.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SongListAdapter adapter = new SongListAdapter(getContext(), songList);
                        Song song = (Song) adapter.getItem(position);
                        goOnMainListener.goOnMain(song, songList);
                    }
                });
            }
        });
    }

    public interface GoOnMainListener {
        public void goOnMain(Song song, List<Song> songList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        goOnMainListener = (GoOnMainListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
