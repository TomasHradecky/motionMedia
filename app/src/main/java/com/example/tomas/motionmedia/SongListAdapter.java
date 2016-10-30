package com.example.tomas.motionmedia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class SongListAdapter extends BaseAdapter {
    private Context context;
    private List<Song> songList;
    private static LayoutInflater inflater = null;

    public SongListAdapter (Context context, List<Song> songList ) {
        this.context = context;
        this.songList = songList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = inflater.inflate(R.layout.song_list_item, null);
        }
        TextView songName = (TextView) view.findViewById(R.id.songNametextView);
        TextView songArtist = (TextView) view.findViewById(R.id.artistNametextView);



        songName.setText(songList.get(position).getSongName());
        songArtist.setText(songList.get(position).getSongArtist());

        return view;
    }
}



