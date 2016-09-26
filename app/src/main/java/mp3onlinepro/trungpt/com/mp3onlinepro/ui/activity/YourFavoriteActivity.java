package mp3onlinepro.trungpt.com.mp3onlinepro.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.google.gson.Gson;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.poliveira.apps.parallaxlistview.ParallaxListView;
import com.poliveira.apps.parallaxlistview.ParallaxScrollEvent;
import com.squareup.picasso.Picasso;
import de.greenrobot.event.EventBus;
import mp3onlinepro.trungpt.com.mp3onlinepro.MyApplication;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;
import mp3onlinepro.trungpt.com.mp3onlinepro.base.StringUtils;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.FavoriteSong;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.FavoriteSongDao;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.PlayerPreparedEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.UpdateProgressEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.UpdateUIEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.player.MusicState;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.adapter.ListViewAdapter;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.customview.ArcImageView;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YourFavoriteActivity extends AppCompatActivity implements AdapterView.OnItemClickListener
{
    @Bind(R.id.container)
    RelativeLayout rlContainer;
    ListViewAdapter adapter;
    List<Song> songs = new ArrayList<>();
    List<FavoriteSong> favoriteSongs = new ArrayList<>();
    FavoriteSongDao favoriteSongDao;

    @Bind(R.id.mini_bar_container)
    RelativeLayout minibar;
    @Bind(R.id.mini_bar_time_arc)
    public ArcImageView arcImageView;
    @Bind(R.id.mini_bar_pause_button)
    public ImageView btPause;
    @Bind(R.id.mini_bar_play_button)
    public ImageView btPlay;
    @Bind(R.id.mini_bar_tvTitle)
    public TextView tvTitle;
    @Bind(R.id.mini_bar_tvArtist)
    public TextView tvArtist;
    @Bind(R.id.mini_bar_ivThumbnail)
    public ImageView ivMiniBarThumbnail;
    @Bind(R.id.progress_wheel)
    public ProgressWheel progressWheel;
    @Bind(R.id.mini_bar_circle_button)
    public ImageView ivCircleButton;
    ParallaxListView listView;
    Animation rotation;
    private Song currentSong;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_your_favorite);
        ButterKnife.bind(this);
        favoriteSongDao = ((MyApplication) getApplication()).getDaoSession().getFavoriteSongDao();
        favoriteSongs = favoriteSongDao.loadAll();
        if (favoriteSongs.size() > 0)
        {
            songs = Song.convertSongFavoriteSong(favoriteSongs);
        }
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setFillAfter(true);
        adapter = new ListViewAdapter(this, songs);
        rlContainer.removeAllViews();
        final View v = getLayoutInflater().inflate(R.layout.include_listview, rlContainer, true);
        listView = (ParallaxListView) v.findViewById(R.id.view);
        listView.setAdapter(adapter);
        listView.setParallaxView(getLayoutInflater().inflate(R.layout.view_header, listView, false));
        final int size = Math.round(48 * getResources().getDisplayMetrics().density);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.setMargins(0, 0, Math.round(16 * getResources().getDisplayMetrics().density), 0);
        listView.setScrollEvent(new ParallaxScrollEvent()
        {
            @Override
            public void onScroll(double percentage, double offset, View parallaxView)
            {
                double translation = parallaxView.getHeight() - (parallaxView.getHeight() * percentage) + size / 2;
            }
        });
        sharedPreferences = getSharedPreferences("song_temp", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("current_song", "");
        if (StringUtils.isNotEmpty(json))
        {
            currentSong = new Gson().fromJson(json, Song.class);
            updateUI();
            if ( ((MyApplication)getApplicationContext()).getMusicService().getMusicState().equals(MusicState.PAUSE))
            {
                btPlay.setVisibility(View.VISIBLE);
                btPause.setVisibility(View.GONE);
            }
            else if ( ((MyApplication) getApplicationContext()).getMusicService().getMusicState().equals(MusicState.PLAYING))
            {
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
            }
        }
        ((MyApplication) getApplicationContext()).getMusicService().getSongs().clear();
        ((MyApplication) getApplicationContext()).getMusicService().setSongs(songs);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        currentSong = songs.get(position-1);
        minibar.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.VISIBLE);
        songPicked(position - 1);
        updateUI();
        ivCircleButton.setVisibility(View.GONE);
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
    }

    private void updateUI()
    {
        tvTitle.setText(currentSong.getTitle());
        tvArtist.setText(currentSong.getArtist());
        Picasso.with(this)
                .load(currentSong.getUrlThumbnail())
                .placeholder(R.drawable.default_album_mid)
                .into(ivMiniBarThumbnail);
    }

    public void songPicked(final int position)
    {
        try
        {
            ((MyApplication) getApplicationContext()).getMusicService().setSongIndex(position);
            ((MyApplication) getApplicationContext()).getMusicService().play();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                adapter.setSelectedIndex(position);
                listView.invalidateViews();
                listView.requestLayout();
            }
        });
    }

    public void onEventMainThread(UpdateProgressEvent event)
    {
        arcImageView.setProgress(event.getProgress());
    }

    public void onEventMainThread(PlayerPreparedEvent event)
    {
        ivCircleButton.setVisibility(View.VISIBLE);
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
        ivMiniBarThumbnail.startAnimation(rotation);
        progressWheel.setVisibility(View.GONE);
    }

    public void onEventMainThread(final UpdateUIEvent event)
    {

        MusicState musicState = event.getMusicState();
        if (musicState == MusicState.PAUSE)
        {
            btPlay.setVisibility(View.VISIBLE);
            btPause.setVisibility(View.GONE);
        }
        else if (musicState == MusicState.PLAYING)
        {
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    adapter.setSelectedIndex(event.getSongIndex());
                    listView.invalidateViews();
                    listView.requestLayout();
                }
            });

        }
        updateUI();
    }

    @OnClick(R.id.mini_bar_rlContent)
    public void clickSongDetails()
    {
        Intent intent = new Intent(this, SongDetailsActivity.class);
        intent.putExtra("song", currentSong);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

}
