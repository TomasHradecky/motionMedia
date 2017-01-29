package com.example.tomas.motionmedia;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Tomas Hradecky on 29.12.2016.
 */

public class SongListFragment extends Fragment {
    private List<Song> actualPlayList = new ArrayList<>();
    private List<Song> allSongList = new ArrayList<>();
    private List<Object> objectSongList = new ArrayList<>();
    private List<String> artistList = new ArrayList<>();
    private List<Song> songForDelList = new ArrayList<>();
    private GoOnMainListener goOnMainListener;
    private Database db;
    private View layout;
    private Button artistButton, allSongListButton, songsForDelButton, actualPlayListButton;
    private ListView allSongListView;
    private ListView actualSongListView;
    private ExpandableListView expListView;
    private boolean useExpList = false;
    private File file;

    /**
     * nonparametric constructor
     */
    public SongListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (objectSongList.isEmpty() || artistList.isEmpty() || actualPlayList.isEmpty() || allSongList.isEmpty()){
            objectSongList = ((MainActivity)getActivity()).getObjectSongList();
            artistList = ((MainActivity)getActivity()).getArtistList();
            setActualPlayList(((MainActivity)getActivity()).getDb().getPlaylistSongs());
            allSongList =  ((MainActivity)getActivity()).getAllSongList();
            if (allSongList.isEmpty()){
                setAllSongList(((MainActivity)getActivity()).getDb().getAllSongs());
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_expandable_song_list, container, false);
        artistButton = (Button) layout.findViewById(R.id.artistListButton);
        allSongListButton = (Button) layout.findViewById(R.id.allSongListButton);
        actualPlayListButton = (Button) layout.findViewById(R.id.actualSongListButton);
        songsForDelButton = (Button) layout.findViewById(R.id.songForDelListButton);
        setActualPlayList(((MainActivity)getActivity()).getDb().getPlaylistSongs());
        sortActualPlayList();
        setButtons(inflater,container);
        return layout;
    }

    private class PlaylistSaver extends AsyncTask<List<Song>, Void, String> {

        @Override
        protected String doInBackground(List<Song>... playList) {
            ((MainActivity)getActivity()).getDb().clearPlaylistSongs();
            ((MainActivity)getActivity()).getDb().markPlaylistSongs(playList[0]);
            return "Executed";
        }
    }

    public void setButtons (final LayoutInflater inflater, final ViewGroup container ) {
        expListView = (ExpandableListView) layout.findViewById(R.id.expandSongList);
        allSongListView = (ListView) layout.findViewById(R.id.allSongList);
        actualSongListView = (ListView) layout.findViewById(R.id.actualSongList);
        if (!actualPlayList.isEmpty()){
            actualPlayListButton.setEnabled(false);
            allSongListButton.setEnabled(true);
            artistButton.setEnabled(true);
            songsForDelButton.setEnabled(true);
            expListView.setVisibility(View.GONE);
            allSongListView.setVisibility(View.GONE);
            actualSongListView.setAdapter(new SongListAdapter(getContext(), actualPlayList));
            actualSongListView.setVisibility(View.VISIBLE);
            actualSongListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SongListAdapter adapter = new SongListAdapter(getContext(), actualPlayList);
                    sortActualPlayList();
                    Song song = actualPlayList.get(position);
                    ((MainActivity)getActivity()).setActualPlaylist(actualPlayList);
                    goOnMainListener.goOnMain(song, actualPlayList);
                }
            });
            registerForContextMenu(allSongListView);
        } else {
            artistButton.setEnabled(false);
            allSongListView.setVisibility(View.GONE);
            actualSongListView.setVisibility(View.GONE);
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    ExpandableSongListAdapter adapter = new ExpandableSongListAdapter(getContext(), objectSongList,artistList);
                    Song song = (Song) adapter.getChild(groupPosition, childPosition);
                    actualPlayList.clear();
                    int i = 0;
                    while (adapter.getChildrenCount(groupPosition) > i){
                        actualPlayList.add((Song) adapter.getChild(groupPosition,i));
                        i++;
                    }
                    ((MainActivity)getActivity()).setActualPlaylist(actualPlayList);
                    goOnMainListener.goOnMain(song, actualPlayList);
                    return true;
                }
            });
            expListView.setAdapter(new ExpandableSongListAdapter(getContext(), objectSongList, artistList));
            registerForContextMenu(expListView);
        }

        artistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout = inflater.inflate(R.layout.fragment_expandable_song_list, container, false);
                artistButton.setEnabled(false);
                allSongListButton.setEnabled(true);
                actualPlayListButton.setEnabled(true);
                songsForDelButton.setEnabled(true);
                allSongListView.setVisibility(View.GONE);
                expListView.setAdapter(new ExpandableSongListAdapter(getContext(), objectSongList, artistList));
                expListView.setVisibility(View.VISIBLE);
                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        ExpandableSongListAdapter adapter = new ExpandableSongListAdapter(getContext(), objectSongList,artistList);
                        Song song = (Song) adapter.getChild(groupPosition, childPosition);
                        actualPlayList.clear();
                        int i = 0;
                        int z = adapter.getChildrenCount(groupPosition);
                        while (adapter.getChildrenCount(groupPosition) >= i ){
                            actualPlayList.add((Song) adapter.getChild(groupPosition,i));
                            i++;
                        }
                        sortActualPlayList();
                        ((MainActivity)getActivity()).setActualPlaylist(actualPlayList);
                        new PlaylistSaver().execute(actualPlayList);
                        goOnMainListener.goOnMain(song, actualPlayList);
                        return true;
                    }
                });
                registerForContextMenu(expListView);
            }
        });

        allSongListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allSongListButton.setEnabled(false);
                artistButton.setEnabled(true);
                actualPlayListButton.setEnabled(true);
                songsForDelButton.setEnabled(true);
                expListView.setVisibility(View.GONE);
                actualSongListView.setVisibility(View.GONE);
                SongListAdapter songListAdapter = new SongListAdapter(getContext(), allSongList);
                allSongListView.setAdapter(songListAdapter);
                allSongListView.setVisibility(View.VISIBLE);
                allSongListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SongListAdapter songListAdapter = new SongListAdapter(getContext(), allSongList);
                        Song song = (Song) songListAdapter.getItem(position);
                        actualPlayList.clear();
                        setActualPlayList(allSongList);
                        new PlaylistSaver().execute(actualPlayList);
                        ((MainActivity)getActivity()).setActualPlaylist(actualPlayList);
                        goOnMainListener.goOnMain(song, actualPlayList);
                    }
                });
                registerForContextMenu(allSongListView);

            }
        });
        actualPlayListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualPlayListButton.setEnabled(false);
                allSongListButton.setEnabled(true);
                artistButton.setEnabled(true);
                songsForDelButton.setEnabled(true);
                expListView.setVisibility(View.GONE);
                allSongListView.setVisibility(View.GONE);
                actualSongListView.setVisibility(View.VISIBLE);
                setActualPlayList(((MainActivity)getActivity()).getDb().getPlaylistSongs());
                actualSongListView.setAdapter(new SongListAdapter(getContext(), actualPlayList));
                actualSongListView.setVisibility(View.VISIBLE);
                actualSongListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SongListAdapter adapter = new SongListAdapter(getContext(), actualPlayList);
                        int i = 0;
                        actualPlayList.clear();
                        while (adapter.getCount() > i) {
                            actualPlayList.add((Song) adapter.getItem(i));
                            i++;
                        }
                        Song song = actualPlayList.get(position);
                        sortActualPlayList();
                        ((MainActivity)getActivity()).setActualPlaylist(actualPlayList);
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
                expListView.setVisibility(View.GONE);
                actualSongListView.setVisibility(View.GONE);
                allSongListView.setVisibility(View.VISIBLE);
                songForDelList = ((MainActivity)getActivity()).getDb().getSongsForDel(((MainActivity)getActivity()).getSettingsFragment().getCountForDelTotal(), ((MainActivity)getActivity()).getSettingsFragment().getCountForDelWeek());
                if (songForDelList.isEmpty()){
                    songForDelList = new ArrayList<Song>();
                    allSongListView.setAdapter(new SongListAdapter(getContext(), songForDelList));
                } else {
                    allSongListView.setAdapter(new SongListAdapter(getContext(), songForDelList));
                }
                registerForContextMenu(allSongListView);
            }
        });
    }

    public void setNextArtistSonglist (int nextArtistIndex) {
        ExpandableSongListAdapter adapter = new ExpandableSongListAdapter(getContext(), objectSongList,artistList);
        Song song = (Song) adapter.getChild(nextArtistIndex, 0);
        actualPlayList.clear();
        int i = 0;
        while (adapter.getChildrenCount(nextArtistIndex) > i){
            actualPlayList.add((Song) adapter.getChild(nextArtistIndex,i));
            i++;
        }
        ((MainActivity)getActivity()).setCurrentArtistIndex(nextArtistIndex);
        goOnMainListener.goOnMain(song, actualPlayList);
    }

     @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
         AdapterView.AdapterContextMenuInfo mInfo = null;
         ExpandableListView.ExpandableListContextMenuInfo expInfo= null;
         useExpList = false;
         int type = 0;
         try {
            mInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
         } catch (Exception e) {
             e.printStackTrace();
             useExpList = true;
             expInfo = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
             type = ExpandableListView.getPackedPositionType(expInfo.packedPosition);
         }
         if (!useExpList){
             menu.setHeaderTitle(allSongList.get(mInfo.position).getSongName());
             menu.add(Menu.NONE, R.id.itemA, Menu.NONE, "Add to actual playlist");
             menu.add(Menu.NONE, R.id.itemB, Menu.NONE, "Delete song");
         } else if (type == 1 & useExpList){
             Song s;
             int groupPos = ExpandableListView.getPackedPositionGroup(expInfo.packedPosition);
             int childPos = ExpandableListView.getPackedPositionChild(expInfo.packedPosition);
             s =(Song) (expListView.getExpandableListAdapter().getChild(groupPos,childPos));
             menu.setHeaderTitle(s.getSongName());
             menu.add(Menu.NONE, R.id.itemA, Menu.NONE, "Add to actual playlist");
             menu.add(Menu.NONE, R.id.itemB, Menu.NONE, "Delete song");
         }
    }

    private class SongListReloader extends AsyncTask<List<Song>, Void, String> {

        @Override
        protected String doInBackground(List<Song>... playList) {
            ((MainActivity)getActivity()).refreshSongs();
            allSongList = ((MainActivity)getActivity()).getAllSongList();
            objectSongList = ((MainActivity)getActivity()).getObjectSongList();
            artistList = ((MainActivity)getActivity()).getArtistList();
            return "Executed";
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = null;
        boolean useExpList = false;
        try {
            info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        } catch (Exception e) {
            e.printStackTrace();
            useExpList = true;
        }
        if (!useExpList){
            Song s;
            switch (item.getItemId()) {
                case R.id.itemA:
                    s =(Song) (allSongListView.getAdapter().getItem((int)info.id));
                    ((MainActivity)getActivity()).getDb().markPlaylistSong(s);
                    actualPlayList.add(s);
                    return true;
                case R.id.itemB:
                    s =(Song) (allSongListView.getAdapter().getItem((int)info.id));
                    if (s.getId() == ((MainActivity)getActivity()).getMainFragment().getCurrentSong().getId()){
                        ((MainActivity)getActivity()).getMainFragment().nextSongButtonAction();
                    }
                    file = new File(s.getSongPath());
                    ((MainActivity)getActivity()).getDb().delSong(s.getId());
                    String path;
                    if(s.getSongPath().startsWith("'")){
                        path = s.getSongPath();
                        path = path.substring(1, path.length()-1);
                        s.setSongPath(path);
                    } else {
                        path = file.getPath();
                    }
                    getContext().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "='" + path + "'", null);
                    file.delete();
                    allSongList.remove(s);
                    new SongListReloader().execute();
                    allSongListView.setAdapter(new SongListAdapter(getContext(), allSongList));
                    allSongListView.invalidateViews();
            }
        } else {
            ExpandableListView.ExpandableListContextMenuInfo expInfo= (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
            int groupPos = ExpandableListView.getPackedPositionGroup(expInfo.packedPosition);
            int childPos = ExpandableListView.getPackedPositionChild(expInfo.packedPosition);
            Song s;
            switch (item.getItemId()) {
                case R.id.itemA:
                    s =(Song) (expListView.getExpandableListAdapter().getChild(groupPos,childPos));
                    actualPlayList.add(s);
                    return true;
                case R.id.itemB:
                    s =(Song) (expListView.getExpandableListAdapter().getChild(groupPos,childPos));
                    if (s.getId() == ((MainActivity)getActivity()).getMainFragment().getCurrentSong().getId()){
                        ((MainActivity)getActivity()).getMainFragment().nextSongButtonAction();
                    }
                    File file = new File(s.getSongPath());
                    ((MainActivity)getActivity()).getDb().delSong(s.getId());
                    getContext().getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.MediaColumns.DATA + "='" + file.getPath() + "'", null);
                    file.delete();
                    objectSongList.remove(s);
                    new SongListReloader().execute();
                    expListView.setAdapter(new ExpandableSongListAdapter(getContext(), objectSongList, artistList));
                    expListView.invalidateViews();
            }
        }
        return super.onContextItemSelected(item);
    }

     public interface GoOnMainListener {
        public void goOnMain(Song song, List<Song> songList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        goOnMainListener = (GoOnMainListener) context;
    }

    public void sortActualPlayList (){
        Collections.sort(actualPlayList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getSongName().compareToIgnoreCase(o2.getSongName());
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setAllSongList(List<Song> allSongList) {
        Collections.sort(allSongList, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getSongName().compareToIgnoreCase(o2.getSongName());
            }
        });
        this.allSongList = allSongList;
    }

    public void setActualPlayList(List<Song> actualPlayList) {
        sortActualPlayList();
        this.actualPlayList = actualPlayList;
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

    public ListView getAllSongListView() {
        return allSongListView;
    }

    public ExpandableListView getExpListView() {
        return expListView;
    }

    public ListView getActualSongListView() {
        return actualSongListView;
    }

}
