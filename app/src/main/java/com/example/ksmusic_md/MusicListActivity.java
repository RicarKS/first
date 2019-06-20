package com.example.ksmusic_md;


import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

public class MusicListActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private  MusicAdapter musicAdapter;
    private SongListAdapter songListAdapter;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView songListPic;
    private NavigationView navView;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit(){
        if ((System.currentTimeMillis() - mExitTime) > 2000){
            Toast.makeText(MyApplication.getContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView();
        showLocalMusic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    public void bindView(){
        setContentView(R.layout.activity_music_list);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMusicList();
            }
        });
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        songListPic = (ImageView)findViewById(R.id.song_list_pic);
        Glide.with(MyApplication.getContext()).load(R.drawable.music_list_default).into(songListPic);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        collapsingToolbarLayout.setTitle("本地音乐");
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        musicAdapter = new MusicAdapter(MusicUtils.MUSIC_LIST);
        recyclerView.setAdapter(musicAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.refresh:
                //Toast.makeText(this,"这里写搜索的逻辑",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicListActivity.this);
                builder.setTitle("开发人员名单");
                builder.setCancelable(true);
                builder.setMessage("软件开发：徐涛\n       前端：刘余兵\n产品设计：任洁品\n软件测试：宋浩伟\n报告编写：程立杰\n       统筹：朱建涛\n\n"+
                        "感谢5401张文彬与赵华清的大力支持！");
                builder.show();
                break;
            case R.id.add:
                AlertDialog.Builder diaLog = new AlertDialog.Builder(MusicListActivity.this);
                diaLog.setTitle("添加新的歌单");
                final EditText editText = new EditText(MusicListActivity.this);
                editText.setHint("歌单名称");
                editText.setMaxLines(1);
                diaLog.setView(editText);
                diaLog.setCancelable(true);
                diaLog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString();
                        SongListInfo songListInfo = MusicUtils.getNewSongList(name);
                        MusicUtils.SongListInfos.add(songListInfo);
                    }
                });
                diaLog.show();
            default:
        }
        return true;
    }

    private void refreshMusicList(){
        swipeRefresh.setRefreshing(false);
    }

    public void showMyFavorite(){
        //MenuItem item = (MenuItem)findViewById(R.id.refresh);
        //item.setTitle("添加");
        navView.setCheckedItem(R.id.nav_my_favorite);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_local_music:
                        navView.setCheckedItem(R.id.nav_local_music);
                        showLocalMusic();
                        break;
                    case R.id.nav_my_favorite:
                        navView.setCheckedItem(R.id.nav_my_favorite);
                        showMyFavorite();
                        break;
                    case R.id.nav_song_list:
                        navView.setCheckedItem(R.id.nav_song_list);
                        showSongList();
                        //Toast.makeText(MusicListActivity.this,"显示歌单的相关逻辑",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
        musicAdapter.setmList(MusicUtils.getMusicListFromSongListId(903));
        musicAdapter.notifyDataSetChanged();
        collapsingToolbarLayout.setTitle("我喜欢的音乐");
        Glide.with(MyApplication.getContext()).load(R.drawable.my_favorite_pic).into(songListPic);
        RecyclerViewUtil recyclerViewUtil = new RecyclerViewUtil(MusicListActivity.this,recyclerView);
        recyclerViewUtil.setOnItemLongClickListener(new RecyclerViewUtil.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position, View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicListActivity.this);
                builder.setTitle("收藏到歌单");
                String[] names = new String[MusicUtils.SongListInfos.size()];
                for (int i = 0; i < MusicUtils.SongListInfos.size(); i++){
                    names[i] = MusicUtils.SongListInfos.get(i).getName();
                }
                builder.setCancelable(true);
                builder.setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MusicUtils.addToSongList(MusicUtils.getMusicListFromSongListId(903).get(position).getSongId(), MusicUtils.SongListInfos.get(which).getSongListId());
                    }
                });
                builder.show();
            }
        });
    }

    public void showLocalMusic(){
        //MenuItem item = (MenuItem)findViewById(R.id.refresh);
        //item.setTitle("title");
        navView.setCheckedItem(R.id.nav_local_music);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_local_music:
                        navView.setCheckedItem(R.id.nav_local_music);
                        showLocalMusic();
                        break;
                    case R.id.nav_my_favorite:
                        navView.setCheckedItem(R.id.nav_my_favorite);
                        showMyFavorite();
                        break;
                    case R.id.nav_song_list:
                        navView.setCheckedItem(R.id.nav_song_list);
                        showSongList();
                        //Toast.makeText(MusicListActivity.this,"显示歌单的相关逻辑",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
        collapsingToolbarLayout.setTitle("本地音乐");
        musicAdapter.setmList(MusicUtils.MUSIC_LIST);
        musicAdapter.notifyDataSetChanged();
        RecyclerViewUtil recyclerViewUtil = new RecyclerViewUtil(MusicListActivity.this,recyclerView);
        recyclerViewUtil.setOnItemLongClickListener(new RecyclerViewUtil.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position, View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicListActivity.this);
                builder.setTitle("收藏到歌单");
                String[] names = new String[MusicUtils.SongListInfos.size()];
                for (int i = 0; i < MusicUtils.SongListInfos.size(); i++){
                    names[i] = MusicUtils.SongListInfos.get(i).getName();
                }
                builder.setCancelable(true);
                builder.setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MusicUtils.addToSongList(MusicUtils.MUSIC_LIST.get(position).getSongId(), MusicUtils.SongListInfos.get(which).getSongListId());
                    }
                });
                builder.show();
            }
        });
    }

    public void showSongList(){
        setContentView(R.layout.song_list);

        toolbar = (Toolbar)findViewById(R.id.song_list_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.song_list_collapsing_toolbar);
        songListPic = (ImageView)findViewById(R.id.song_list_pic2);
        Glide.with(MyApplication.getContext()).load(R.drawable.song_list_title_pic).into(songListPic);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.song_list_drawer_layout);
        navView = (NavigationView)findViewById(R.id.song_list_nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.song_list_swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                songListAdapter.setList(MusicUtils.SongListInfos);
                songListAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });
        collapsingToolbarLayout.setTitle("歌单");
        recyclerView = (RecyclerView)findViewById(R.id.song_list_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        songListAdapter = new SongListAdapter(MusicUtils.SongListInfos);
        recyclerView.setAdapter(songListAdapter);
        navView.setCheckedItem(R.id.nav_song_list);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_local_music:
                        bindView();
                        //navView.setCheckedItem(R.id.nav_local_music);
                        showLocalMusic();
                        break;
                    case R.id.nav_my_favorite:
                        //navView.setCheckedItem(R.id.nav_my_favorite);
                        bindView();
                        showMyFavorite();
                        break;
                    case R.id.nav_song_list:
                        navView.setCheckedItem(R.id.nav_song_list);
                        showSongList();
                        //Toast.makeText(MusicListActivity.this,"显示歌单的相关逻辑",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                songListAdapter.notifyDataSetChanged();
                return false;
            }
        });
        RecyclerViewUtil recyclerViewUtil = new RecyclerViewUtil(MyApplication.getContext(),recyclerView);
        recyclerViewUtil.setOnItemClickListener(new RecyclerViewUtil.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                SongListInfo songListInfo = MusicUtils.SongListInfos.get(position);
                if (songListInfo.getSongListId() == 903){
                    bindView();
                    showMyFavorite();
                } else {
                    bindView();
                    showSongListMusic(songListInfo.getSongListId());
                }
            }
        });
        recyclerViewUtil.setOnItemLongClickListener(new RecyclerViewUtil.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, View view) {
                final SongListInfo songListInfo = MusicUtils.SongListInfos.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicListActivity.this);
                builder.setTitle("删除歌单" + songListInfo.getName() + "?");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MusicUtils.removeSongListInfo(songListInfo.getSongListId());
                        songListAdapter.setList(MusicUtils.SongListInfos);
                        songListAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }

    public void showSongListMusic(final int songListId){
        //MenuItem item = (MenuItem)findViewById(R.id.refresh);
        //item.setTitle("添加");
        navView.setCheckedItem(R.id.song_list_nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_local_music:
                        navView.setCheckedItem(R.id.nav_local_music);
                        showLocalMusic();
                        break;
                    case R.id.nav_my_favorite:
                        navView.setCheckedItem(R.id.nav_my_favorite);
                        showMyFavorite();
                        break;
                    case R.id.nav_song_list:
                        navView.setCheckedItem(R.id.nav_song_list);
                        showSongList();
                        //Toast.makeText(MusicListActivity.this,"显示歌单的相关逻辑",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
        musicAdapter.setmList(MusicUtils.getMusicListFromSongListId(songListId));
        musicAdapter.notifyDataSetChanged();
        collapsingToolbarLayout.setTitle(MusicUtils.getSongListNameForId(songListId));
        Glide.with(MyApplication.getContext()).load(R.drawable.my_favorite_pic).into(songListPic);
        RecyclerViewUtil recyclerViewUtil = new RecyclerViewUtil(MusicListActivity.this,recyclerView);
        recyclerViewUtil.setOnItemLongClickListener(new RecyclerViewUtil.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position, View view) {
                if (view.getId() != R.id.music_list_pic) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MusicListActivity.this);
                    builder.setTitle("删除音乐？");
                    builder.setMessage("从" + MusicUtils.getSongListNameForId(songListId) + "中删除" + MusicUtils.getMusicListFromSongListId(songListId).get(position).getName() + "?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MusicUtils.delForSongList(MusicUtils.getMusicListFromSongListId(songListId).get(position).getSongId(), songListId);
                            musicAdapter.setmList(MusicUtils.getMusicListFromSongListId(songListId));
                            musicAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                } else {

                }
            }
        });
    }
}
