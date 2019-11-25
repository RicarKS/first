package com.example.ksmusic_md;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MusicUtils {
    public static List<Music> MUSIC_LIST = new ArrayList<Music>();
    public static  List<SongListInfo> SongListInfos = new ArrayList<SongListInfo>();
    public static int currentSongListId;

    public static List<Music> getMusicList(Context context){
        List<Music> musicList = new ArrayList<Music>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,
                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if(cursor != null){
            while (cursor.moveToNext()){
                Music music = new Music();
                int isMusic = 1;
                if(isMusic == 0){
                    continue;
                }
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if(size < 1024 * 800){
                    continue;
                }
                music.setIsMusic(isMusic);
                music.setSize(size);
                music.setSongId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                music.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                music.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                music.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                music.setAlbumId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                music.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                music.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                music.save();
                musicList.add(music);
            }
            cursor.close();
        }
        return musicList;
    }

    public static List<SongListInfo> getSongListInfo(){
        List<SongListInfo> songListInfos = new ArrayList<SongListInfo>();
        songListInfos = LitePal.findAll(SongListInfo.class);
        return songListInfos;
    }

    public static String formatime(int time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String mTime = sdf.format(date);
        return mTime;
    }

    public static void initMusicList(){
        LitePal.getDatabase();
        MUSIC_LIST = LitePal.findAll(Music.class);
        if (MUSIC_LIST.isEmpty()){
            MUSIC_LIST = getMusicList(MyApplication.getContext());
        }
    }

    public static void initSongList(){
        boolean isFirstStart = PreferenceUtils.getBoolean(MyApplication.getContext(),PreferenceUtils.FIRST_START,true);
        if(isFirstStart){
            createMyFavorite();
            PreferenceUtils.setBoolean(MyApplication.getContext(),PreferenceUtils.FIRST_START,false);
        }
        SongListInfos = getSongListInfo();
    }

    private static void createMyFavorite(){
        SongListInfo myFavorite = new SongListInfo();
        SongListInfo songListInfo = new SongListInfo();
        SongListInfo songListInfo1 = new SongListInfo();

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String currentTime = sdf.format(date);

        myFavorite.setSongListId(903);
        myFavorite.setName("我喜欢的音乐");
        myFavorite.setBuildTime(currentTime);
        myFavorite.save();

        date = new Date(System.currentTimeMillis());
        currentTime = sdf.format(date);
        songListInfo.setSongListId((int)System.currentTimeMillis());
        songListInfo.setName("XT");
        songListInfo.setBuildTime(currentTime);
        songListInfo.save();

        date = new Date(System.currentTimeMillis());
        currentTime = sdf.format(date);
        songListInfo1.setSongListId((int)System.currentTimeMillis() + 1);
        songListInfo1.setBuildTime(currentTime);
        songListInfo1.setName("Unique");
        songListInfo1.save();
    }

    public static boolean isMyFavorite(long songId){
        List<SongList> songList = LitePal.select("*").where("songId = ? and songListId = ?",String.valueOf(songId),"903").find(SongList.class);
        if (!songList.isEmpty()){
            return true;
        }
        return false;
    }

    public static void addToSongList(long songId, int songListId){
        List<SongList> songLists = LitePal.select("*").where("songListId = ? and songId = ?",String.valueOf(songListId),String.valueOf(songId)).find(SongList.class);
        SongList songList = new SongList();
        if(songLists.isEmpty()){
            songList.setSongId(songId);
            songList.setSongListId(songListId);
            songList.save();
        } else {
            Toast.makeText(MyApplication.getContext(),"歌单内已经有这首歌了哦",Toast.LENGTH_SHORT).show();
        }
    }

    public static void delForSongList(long songId, int songListId){
        if (LitePal.select("*").where("songListId = ? and songId = ?",String.valueOf(songListId),String.valueOf(songId)).find(SongList.class).isEmpty()){
            Toast.makeText(MyApplication.getContext(),"歌曲不存在，请刷新",Toast.LENGTH_SHORT).show();
        } else {
            LitePal.deleteAll(SongList.class, "songListId = ? and songId = ?", String.valueOf(songListId), String.valueOf(songId));
        }
    }

    public static List<SongList> getSongList(int songListId){
        List<SongList> songLists = LitePal.select("*").where("songListId = ?",String.valueOf(songListId)).find(SongList.class);
        return songLists;
    }

    public static List<Music> getMusicListFromSongListId(int songListId){
        List<SongList> songLists = LitePal.select("*").where("songListId = ?",String.valueOf(songListId)).find(SongList.class);
        List<Music> tempMusicList = new ArrayList<Music>();
        List<Music> musicList = new ArrayList<Music>();
        Music music = new Music();
        if (!songLists.isEmpty()){
            for(SongList songList : songLists){
                long songId = songList.getSongId();
                tempMusicList = LitePal.select("*").where("songId = ?",String.valueOf(songId)).find(Music.class);
                music = tempMusicList.get(0);
                musicList.add(music);
                tempMusicList.clear();
            }
        }
        return musicList;
    }

    public static SongListInfo getNewSongList(String name) {
        SongListInfo songListInfo1 = new SongListInfo();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String currentTime = sdf.format(date);
        songListInfo1.setSongListId((int) System.currentTimeMillis());
        songListInfo1.setBuildTime(currentTime);
        songListInfo1.setName(name);
        songListInfo1.save();
        return songListInfo1;
    }

    public static String getSongListNameForId(int songListId){
        List<SongListInfo> songListInfos = LitePal.select("*").where("songListId = ?",String.valueOf(songListId)).find(SongListInfo.class);
        SongListInfo songListInfo = new SongListInfo();
        if (!SongListInfos.isEmpty()){
            songListInfo = songListInfos.get(0);
            return songListInfo.getName();
        }
        return "Unknown";
    }

    public static void removeSongListInfo(int songListId){
        LitePal.deleteAll(SongListInfo.class,"songListId = ?",String.valueOf(songListId));
        LitePal.deleteAll(SongList.class,"songListId = ?",String.valueOf(songListId));
        SongListInfos = getSongListInfo();
    }
}
