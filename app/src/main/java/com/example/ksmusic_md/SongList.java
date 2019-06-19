package com.example.ksmusic_md;

import org.litepal.crud.LitePalSupport;

public class SongList extends LitePalSupport {
    private int id;
    private int songListId;
    private long songId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSongListId() {
        return songListId;
    }

    public void setSongListId(int songListId) {
        this.songListId = songListId;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }
}
