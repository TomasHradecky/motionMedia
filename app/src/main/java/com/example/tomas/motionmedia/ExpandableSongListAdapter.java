package com.example.tomas.motionmedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomas on 31.10.2016.
 */

public class ExpandableSongListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private static LayoutInflater inflater = null;
    private List<Object> songList = new ArrayList<>();
    private List<String> artistList = new ArrayList<>();
    private ArrayList<Song> child;

    public ExpandableSongListAdapter (Context context, List<Object> songList, List<String> artistList) {
        this.context = context;
        this.songList = songList;
        this.artistList = artistList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return artistList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ArrayList<String>) songList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList <Song> child =(ArrayList<Song>) songList.get(groupPosition);
        return child.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.top_song_list_item, null);
        }
        TextView artist = (TextView) view.findViewById(R.id.topItemNametextView);
        artist.setText(artistList.get(groupPosition));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.song_list_item, null);
        }
        child = (ArrayList<Song>) songList.get(groupPosition);
        TextView song = (TextView) view.findViewById(R.id.songNametextView);
        TextView artist = (TextView) view.findViewById(R.id.artistNametextView);
        artist.setVisibility(View.GONE);
        song.setMaxLines(1);
        song.setTextSize(18);
        song.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60));
        song.setText("      - " + child.get(childPosition).getSongName());
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
