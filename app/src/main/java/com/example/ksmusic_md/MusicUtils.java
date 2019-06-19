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
                /*if(music.getSize() > 1024 * 800){
                    //大于800K
                    if(music.getName().contains("-")){
                        String[] str = music.getName().split("-");
                        music.setName(str[1]);
                    }
                    String time = formatime(music.getDuration());
                }*/
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
        //Toast.makeText(MyApplication.getContext(),"initMusicList()",Toast.LENGTH_LONG).show();
        MUSIC_LIST = LitePal.findAll(Music.class);
        //Toast.makeText(MyApplication.getContext(),String.valueOf(MUSIC_LIST.size()),Toast.LENGTH_LONG).show();
        if (MUSIC_LIST.isEmpty()){
            //Toast.makeText(MyApplication.getContext(),"MUSIC_LIST == null",Toast.LENGTH_LONG).show();
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
        SongListInfo songListInfo2 = new SongListInfo();
        SongListInfo songListInfo3 = new SongListInfo();
        SongListInfo songListInfo4 = new SongListInfo();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String currentTime = sdf.format(date);
        myFavorite.setSongListId(903);
        myFavorite.setName("我喜欢的音乐");
        myFavorite.setBuildTime(currentTime);
        songListInfo.setSongListId((int)System.currentTimeMillis());
        songListInfo.setName("XT");
        songListInfo.setBuildTime(currentTime);
        songListInfo.save();
        date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("yy年MM月dd日 HH:mm:ss");
        currentTime = sdf.format(date);
        songListInfo1.setSongListId((int)System.currentTimeMillis() + 1);
        songListInfo1.setBuildTime(currentTime);
        songListInfo1.setName("Unique");
        songListInfo1.save();
        date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("yy年MM月dd日 HH:mm:ss");
        currentTime = sdf.format(date);
        songListInfo2.setSongListId((int)System.currentTimeMillis() + 2);
        songListInfo2.setBuildTime(currentTime);
        songListInfo2.setName("汪星人");
        songListInfo2.save();
        date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("yy年MM月dd日 HH:mm:ss");
        currentTime = sdf.format(date);
        songListInfo3.setSongListId((int)System.currentTimeMillis() + 3);
        songListInfo3.setBuildTime(currentTime);
        songListInfo3.setName("Test");
        songListInfo3.save();
        date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("yy年MM月dd日 HH:mm:ss");
        currentTime = sdf.format(date);
        songListInfo4.setSongListId((int)System.currentTimeMillis() + 4);
        songListInfo4.setBuildTime(currentTime);
        songListInfo4.setName("女神");
        songListInfo4.save();
        myFavorite.save();
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
        LitePal.deleteAll(SongList.class,"songListId = ? and songId = ?",String.valueOf(songListId),String.valueOf(songId));
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

    public static void newSongList(String name) {
        SongListInfo songListInfo1 = new SongListInfo();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String currentTime = sdf.format(date);
        songListInfo1.setSongListId((int) System.currentTimeMillis());
        songListInfo1.setBuildTime(currentTime);
        songListInfo1.setName(name);
        songListInfo1.save();
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
}
