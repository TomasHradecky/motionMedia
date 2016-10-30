package com.example.tomas.motionmedia;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import static android.R.attr.type;

public class SongListFragment extends Fragment {
    private ListView songListView;
    private List<Song> songList;
    private GoOnMainListener goOnMainListener;
    private SongsManager songsManager = new SongsManager();

    public SongListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //parseAllAudio();
        songList = songsManager.parseAllAudio(getContext());
        System.out.println(songList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_song_list, container, false);

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
        });
        return layout;
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
