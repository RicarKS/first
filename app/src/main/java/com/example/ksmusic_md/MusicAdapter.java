package com.example.ksmusic_md;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private List<Music> mList;

    public void setmList(List<Music> mList) {
        this.mList = mList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView myFavoritePic;
        TextView position;
        TextView musicName;
        TextView artistAndAlbum;
        TextView time;
        View musicView;

        public ViewHolder(View view){
            super(view);
            musicView = view;
            myFavoritePic = (ImageView)view.findViewById(R.id.music_list_pic);
            position = (TextView)view.findViewById(R.id.music_list_position);
            musicName = (TextView)view.findViewById(R.id.music_list_name);
            artistAndAlbum = (TextView)view.findViewById(R.id.music_liat_artist_and_album);
            time = (TextView)view.findViewById(R.id.music_list_time);
        }
    }

    public MusicAdapter(List<Music> list){
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_item,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.musicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里写播放服务的相关逻辑
                int position = holder.getAdapterPosition();
                Bundle bundle = new Bundle();
                bundle.putSerializable("musicList",(Serializable)mList);
                Intent intent = new Intent(MyApplication.getContext(),OnPlayingActivity.class);
                intent.putExtra("position",position);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getContext().startActivity(intent);
            }
        });
        holder.myFavoritePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加进我喜欢的音乐
                int position = holder.getAdapterPosition();
                if(MusicUtils.isMyFavorite(mList.get(position).getSongId())){
                    holder.myFavoritePic.setImageResource(R.drawable.is_not_my_favorite);
                    MusicUtils.delForSongList(mList.get(position).getSongId(),903);
                } else {
                    holder.myFavoritePic.setImageResource(R.drawable.my_favorite);
                    MusicUtils.addToSongList(mList.get(position).getSongId(), 903);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Music music = mList.get(i);
        if(MusicUtils.isMyFavorite(music.getSongId())){
            Glide.with(MyApplication.getContext()).load(R.drawable.my_favorite).into(viewHolder.myFavoritePic);
        } else {
            Glide.with(MyApplication.getContext()).load(R.drawable.is_not_my_favorite).into(viewHolder.myFavoritePic);
        }
        viewHolder.position.setText(i + 1 + ".");
        viewHolder.musicName.setText(music.getName());
        viewHolder.artistAndAlbum.setText(music.getArtist() + "-" + music.getAlbum());
        viewHolder.time.setText(MusicUtils.formatime(music.getDuration()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
