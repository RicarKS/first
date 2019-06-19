package com.example.ksmusic_md;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder>{

    private List<SongListInfo> mSongListInfos;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView songListItemImage;
        TextView songListItemText;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            songListItemImage = (ImageView)view.findViewById(R.id.song_list_item_pic);
            songListItemText = (TextView)view.findViewById(R.id.song_list_item_text);
        }
    }

    public SongListAdapter(List<SongListInfo> songListInfos){
        /*SongListInfo songListInfo = new SongListInfo();
        songListInfo.setBuildTime("09:03");
        songListInfo.setName("添加新的歌单");
        songListInfo.setSongListId(-1);*/
        mSongListInfos = songListInfos;
        //mSongListInfos.add(songListInfo);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.song_list_item,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        SongListInfo songListInfo = mSongListInfos.get(i);
        viewHolder.songListItemText.setText(songListInfo.getName());
        Glide.with(MyApplication.getContext()).load(R.drawable.song_list_default_pic).into(viewHolder.songListItemImage);
        if(songListInfo.getId() == -1) {
            Glide.with(MyApplication.getContext()).load(R.drawable.song_list_add).into(viewHolder.songListItemImage);
        } else if (songListInfo.getId() == 903){
            Glide.with(MyApplication.getContext()).load(R.drawable.my_favorite_pic).into(viewHolder.songListItemImage);
        }
    }

    @Override
    public int getItemCount() {
        return mSongListInfos.size();
    }
}
