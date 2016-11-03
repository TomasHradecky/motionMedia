package com.example.tomas.motionmedia;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static com.example.tomas.motionmedia.R.id.songList;

public class SongListFragment extends Fragment {
    private List<Song> actualPlayList;
    private List<Song> allSongList;
    private List<Object> objectSongList;
    private List<String> artistList;
    private List<Song> songForDelList = new ArrayList<>();
    private GoOnMainListener goOnMainListener;
    private View layout;
    private Button artistButton, allSongListButton, songsForDelButton, actualPlayListButton;


    public SongListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (objectSongList == null || artistList == null || actualPlayList == null || allSongList == null){
            objectSongList = ((MainActivity)getActivity()).getObjectSongList();
            artistList = ((MainActivity)getActivity()).getArtistList();
            actualPlayList = ((MainActivity)getActivity()).getActualPlaylist();
            allSongList =  ((MainActivity)getActivity()).getAllSongList();
        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getContext();
        layout = inflater.inflate(R.layout.fragment_expandable_song_list, container, false);
        artistButton = (Button) layout.findViewById(R.id.artistListButton);
        allSongListButton = (Button) layout.findViewById(R.id.allSongListButton);
        actualPlayListButton = (Button) layout.findViewById(R.id.actualSongListButton);
        songsForDelButton = (Button) layout.findViewById(R.id.songForDelListButton);
        setButtons(inflater,container);
        return layout;
    }

    public void setButtons (final LayoutInflater inflater, final ViewGroup container ) {
        artistButton.setEnabled(false);
        final ExpandableListView expList = (ExpandableListView) layout.findViewById(R.id.expandSongList);
        final ListView list = (ListView) layout.findViewById(songList);
        list.setVisibility(View.GONE);
        expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ExpandableSongListAdapter adapter = new ExpandableSongListAdapter(getContext(), objectSongList,artistList);
                Song song = (Song) adapter.getChild(groupPosition, childPosition);
                actualPlayList = new ArrayList<Song>();
                int i = 0;
                while (adapter.getChildrenCount(groupPosition) > i){
                    actualPlayList.add((Song) adapter.getChild(groupPosition,i));
                    i++;
                }
                goOnMainListener.goOnMain(song, actualPlayList);
                return true;
            }
        });

        expList.setAdapter(new ExpandableSongListAdapter(getContext(), objectSongList, artistList));

        artistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout = inflater.inflate(R.layout.fragment_expandable_song_list, container, false);
                artistButton.setEnabled(false);
                allSongListButton.setEnabled(true);
                actualPlayListButton.setEnabled(true);
                songsForDelButton.setEnabled(true);
                list.setVisibility(View.GONE);
                expList.setAdapter(new ExpandableSongListAdapter(getContext(), objectSongList, artistList));
                expList.setVisibility(View.VISIBLE);
                expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        ExpandableSongListAdapter adapter = new ExpandableSongListAdapter(getContext(), objectSongList,artistList);
                        Song song = (Song) adapter.getChild(groupPosition, childPosition);
                        actualPlayList = new ArrayList<Song>();
                        int i = 0;
                        while (adapter.getChildrenCount(groupPosition) > i){
                            actualPlayList.add((Song) adapter.getChild(groupPosition,i));
                            i++;
                        }
                        goOnMainListener.goOnMain(song, actualPlayList);
                        return true;
                    }
                });
            }
        });

        allSongListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allSongListButton.setEnabled(false);
                artistButton.setEnabled(true);
                actualPlayListButton.setEnabled(true);
                songsForDelButton.setEnabled(true);
                expList.setVisibility(View.GONE);
                list.setAdapter(new SongListAdapter(getContext(), allSongList));
                list.setVisibility(View.VISIBLE);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SongListAdapter adapter = new SongListAdapter(getContext(), allSongList);
                        Song song = (Song) adapter.getItem(position);
                        actualPlayList = allSongList;
                        goOnMainListener.goOnMain(song, allSongList);
                    }
                });
            }
        });
        actualPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualPlayListButton.setEnabled(false);
                allSongListButton.setEnabled(true);
                artistButton.setEnabled(true);
                songsForDelButton.setEnabled(true);
                expList.setVisibility(View.GONE);
                list.setAdapter(new SongListAdapter(getContext(), actualPlayList));
                list.setVisibility(View.VISIBLE);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SongListAdapter adapter = new SongListAdapter(getContext(), actualPlayList);
                        Song song = (Song) adapter.getItem(position);
                        goOnMainListener.goOnMain(song, actualPlayList);
                    }
                });
            }
        });

        songsForDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songsForDelButton.setEnabled(false);
                actualPlayListButton.setEnabled(true);
                allSongListButton.setEnabled(true);
                artistButton.setEnabled(true);
                expList.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                list.setAdapter(new SongListAdapter(getContext(), songForDelList));
            }
        });
    }

    public void refreshSongs () {
        objectSongList = new ArrayList<>();
        artistList = new ArrayList<>();
        actualPlayList = new ArrayList<>();
        allSongList = new ArrayList<>();
        objectSongList = ((MainActivity)getActivity()).getObjectSongList();
        artistList = ((MainActivity)getActivity()).getArtistList();
        actualPlayList = ((MainActivity)getActivity()).getActualPlaylist();
        allSongList =  ((MainActivity)getActivity()).getAllSongList();
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
