package com.example.ksmusic_md;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnPlayingActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView backView;
    private TextView titleView;
    private TextView artistAndAlbumView;
    private CircleImageView circleImageView;
    private TextView currentTimeView;
    private TextView totalTimeView;
    private SeekBar seekBar;
    private ImageView myFavoriteView;
    private ImageView previousView;
    private ImageView playingOrPauseView;
    private ImageView nextView;
    private ImageView collectView;
    private List<Music> musicList;

    private int position;
    private boolean isStop;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            seekBar.setProgress(msg.what);
            currentTimeView.setText(MusicUtils.formatime(msg.what));
        }
    };
    public class MyThread implements Runnable{
        @Override
        public void run() {
            while(mediaPlayer != null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(mediaPlayer.getCurrentPosition());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_playing);
        bindView();

        backView.setOnClickListener(this);
        myFavoriteView.setOnClickListener(this);
        previousView.setOnClickListener(this);
        playingOrPauseView.setOnClickListener(this);
        nextView.setOnClickListener(this);
        collectView.setOnClickListener(this);

        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);
        musicList = (List<Music>)this.getIntent().getSerializableExtra("musicList");

        play();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
    }

    private void bindView(){
        backView = (ImageView)findViewById(R.id.playing_back);
        titleView = (TextView)findViewById(R.id.playing_music_name);
        artistAndAlbumView = (TextView)findViewById(R.id.playing_artist_and_album);
        circleImageView = (CircleImageView)findViewById(R.id.playing_album_pic);
        currentTimeView = (TextView)findViewById(R.id.playing_current_time);
        seekBar = (SeekBar)findViewById(R.id.playing_seekbar);
        totalTimeView = (TextView)findViewById(R.id.playing_total_time);
        myFavoriteView = (ImageView)findViewById(R.id.playing_my_favorite);
        previousView = (ImageView)findViewById(R.id.playing_previous);
        playingOrPauseView = (ImageView)findViewById(R.id.playing_or_pause);
        nextView = (ImageView)findViewById(R.id.playing_next);
        collectView = (ImageView)findViewById(R.id.playing_collect);
    }

    public void play(){
        isStop = false;
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try{
            mediaPlayer.setDataSource(musicList.get(position).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e){
            e.printStackTrace();
        }
        titleView.setText(musicList.get(position).getName());
        artistAndAlbumView.setText(musicList.get(position).getArtist() + "-" + musicList.get(position).getAlbum());
        totalTimeView.setText(MusicUtils.formatime(musicList.get(position).getDuration()));
        if (MusicUtils.isMyFavorite(musicList.get(position).getSongId())){
            myFavoriteView.setImageResource(R.drawable.my_favorite);
        } else {
            myFavoriteView.setImageResource(R.drawable.is_not_my_favorite);
        }
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(1);
        MyThread myThread = new MyThread();
        new Thread(myThread).start();
    }

    protected void onResume(){
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        seekBar.setMax(mediaPlayer.getDuration());
        super.onResume();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.playing_back:
                finish();
                break;
            case R.id.playing_my_favorite:
                if(MusicUtils.isMyFavorite(musicList.get(position).getSongId())) {
                    MusicUtils.delForSongList(musicList.get(position).getSongId(),903);
                    myFavoriteView.setImageResource(R.drawable.is_not_my_favorite);
                } else {
                    MusicUtils.addToSongList(musicList.get(position).getSongId(),903);
                    myFavoriteView.setImageResource(R.drawable.my_favorite);
                }
                break;
            case R.id.playing_previous:
                position--;
                if(position == -1){
                    position = musicList.size() - 1;
                }
                play();
                break;
            case R.id.playing_or_pause:
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    playingOrPauseView.setImageResource(R.drawable.pause_btn);
                } else {
                    mediaPlayer.start();
                    playingOrPauseView.setImageResource(R.drawable.playing_btn);
                }
                break;
            case R.id.playing_next:
                position++;
                if(position == musicList.size()){
                    position = 0;
                }
                play();
                break;
            case R.id.playing_collect:
                addToSongList();
                break;
        }
    }

    public void addToSongList(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(OnPlayingActivity.this);
        dialog.setTitle("收藏到歌单");
        String[] names = new String[MusicUtils.SongListInfos.size()];
        for (int i = 0; i < MusicUtils.SongListInfos.size(); i++){
            names[i] = MusicUtils.SongListInfos.get(i).getName();
        }
        dialog.setCancelable(true);
        dialog.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MusicUtils.addToSongList(musicList.get(position).getSongId(), MusicUtils.SongListInfos.get(which).getSongListId());
            }
        });
        dialog.show();
    }
}
