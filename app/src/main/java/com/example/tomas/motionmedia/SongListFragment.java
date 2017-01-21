package com.example.tomas.motionmedia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.tomas.motionmedia.R.id.all;
import static com.example.tomas.motionmedia.R.id.songList;

public class SongListFragment extends Fragment {
    private List<Song> actualPlayList;
    private List<Song> allSongList;
    private List<Object> objectSongList;
    private List<String> artistList;
    private List<Song> songForDelList = new ArrayList<>();
    private GoOnMainListener goOnMainListener;
    private Database db;
    private View layout;
    private Button artistButton, allSongListButton, songsForDelButton, actualPlayListButton;
    private ListView list;
    private ExpandableListView expList;
    private boolean useExpList = false;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private File file;

    /**
     * nonparametric constructor
     */
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
            //((MainActivity)getActivity()).getDb().saveSongs(allSongList);
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
        setButtons(inflater,container);
        return layout;
    }

    public void setButtons (final LayoutInflater inflater, final ViewGroup container ) {
        expList = (ExpandableListView) layout.findViewById(R.id.expandSongList);
        list = (ListView) layout.findViewById(songList);
        if (!actualPlayList.isEmpty()){
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
            registerForContextMenu(list);
        } else {
            artistButton.setEnabled(false);
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
            registerForContextMenu(expList);
        }

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
                        ((MainActivity)getActivity()).setCurrentArtistIndex(groupPosition);
                        goOnMainListener.goOnMain(song, actualPlayList);
                        return true;
                    }
                });
                registerForContextMenu(expList);
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
                SongListAdapter songListAdapter = new SongListAdapter(getContext(), allSongList);
                list.setAdapter(songListAdapter);
                list.setVisibility(View.VISIBLE);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SongListAdapter songListAdapter = new SongListAdapter(getContext(), allSongList);
                        Song song = (Song) songListAdapter.getItem(position);
                        actualPlayList = allSongList;
                        goOnMainListener.goOnMain(song, allSongList);
                    }
                });
                registerForContextMenu(list);

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
                registerForContextMenu(list);
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
                db = ((MainActivity)getActivity()).getDb();
                songForDelList = db.getSongsForDel(((MainActivity) getActivity()).getCountForDelTotal(), ((MainActivity) getActivity()).getCountForDelWeek());
                if (songForDelList.isEmpty()){
                    songForDelList = new ArrayList<Song>();
                    list.setAdapter(new SongListAdapter(getContext(), songForDelList));
                } else {
                    list.setAdapter(new SongListAdapter(getContext(), songForDelList));
                }
            }
        });
    }

    public void setNextArtistSonglist (int nextArtistIndex) {
        ExpandableSongListAdapter adapter = new ExpandableSongListAdapter(getContext(), objectSongList,artistList);
        Song song = (Song) adapter.getChild(nextArtistIndex, 0);
        actualPlayList = new ArrayList<Song>();
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
             s =(Song) (expList.getExpandableListAdapter().getChild(groupPos,childPos));
             menu.setHeaderTitle(s.getSongName());
             menu.add(Menu.NONE, R.id.itemA, Menu.NONE, "Add to actual playlist");
             menu.add(Menu.NONE, R.id.itemB, Menu.NONE, "Delete song");
         }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                    file.delete();
                } else
                {
                    Toast.makeText(getActivity(), "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
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
                    s =(Song) (list.getAdapter().getItem((int)info.id));
                    actualPlayList.add(s);
                    return true;
                case R.id.itemB:
                    s =(Song) (list.getAdapter().getItem((int)info.id));
                    file = new File(s.getSongPath());
                    file.delete();
                    ((MainActivity)getActivity()).refreshSongs();

                    /*
                    boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE);
                    } else {
                        file.delete();
                    }
                    return true;*/
            }
        } else {
            ExpandableListView.ExpandableListContextMenuInfo expInfo= (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
            int groupPos = ExpandableListView.getPackedPositionGroup(expInfo.packedPosition);
            int childPos = ExpandableListView.getPackedPositionChild(expInfo.packedPosition);

            Song s;
            switch (item.getItemId()) {
                case R.id.itemA:
                    s =(Song) (expList.getExpandableListAdapter().getChild(groupPos,childPos));
                    actualPlayList.add(s);
                    return true;
                case R.id.itemB:
                    s =(Song) (expList.getExpandableListAdapter().getChild(groupPos,childPos));
                    File file = new File(s.getSongPath());
                    file.delete();
                    ((MainActivity)getActivity()).refreshSongs();
                    /*
                    boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_STORAGE);
                    } else {
                        file.delete();
                    }

                    return true;
                */
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
