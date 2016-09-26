package mp3onlinepro.trungpt.com.mp3onlinepro.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import mp3onlinepro.trungpt.com.mp3onlinepro.MyApplication;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;
import mp3onlinepro.trungpt.com.mp3onlinepro.base.BaseFragment;
import mp3onlinepro.trungpt.com.mp3onlinepro.base.StringUtils;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.FavoriteSong;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.FavoriteSongDao;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.PlayerPreparedEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.UpdateProgressEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.UpdateUIEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.RestfulService;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.dto.CategoriesDTO;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.dto.TrackDTO;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.dto.TrackDetailDTO;
import mp3onlinepro.trungpt.com.mp3onlinepro.player.MusicState;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.activity.SongDetailsActivity;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.adapter.ListViewAdapter;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.async.AsyncTaskGetListener;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.async.GetSongAsync;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.customview.ArcImageView;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.customview.EndlessScrollListener;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.model.Song;
import mp3onlinepro.trungpt.com.mp3onlinepro.utils.Const;
import mp3onlinepro.trungpt.com.mp3onlinepro.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TRUNGPT on 8/11/16.
 */
public class SongListFragment extends BaseFragment implements AsyncTaskGetListener
{
    @Bind(R.id.fragment_songlist_listview)
    ListView listView;
    @Bind(R.id.fragment_songlist_progressbar)
    ProgressBar progressBar;

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

    ListViewAdapter adapter;
    private String nextHref;
    private int offset = 0;
    Animation rotation;
    private String categoryKey;
    private Song currentSong;
    SharedPreferences sharedPreferences;

    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        categoryKey = getArguments().getString("categoryKey");
        sharedPreferences = getActivity().getSharedPreferences("song_temp", Context.MODE_PRIVATE);
    }

    @Override
    public int getLayout()
    {
        return R.layout.fragment_songlist;
    }

    @Override
    public void setDataToView(Bundle savedInstanceState)
    {
        //=====================ADS==================================
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.ads_fullscreen));
        mInterstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdClosed()
            {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();
        //=====================ADS==================================

        rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        rotation.setFillAfter(true);
        String  json = sharedPreferences.getString("current_song","");
        if (StringUtils.isNotEmpty(json))
        {
            currentSong = new Gson().fromJson(json,Song.class);
            updateUI();
            if ( ((MyApplication) getActivity().getApplicationContext()).getMusicService().getMusicState().equals(MusicState.PAUSE))
            {
                btPlay.setVisibility(View.VISIBLE);
                btPause.setVisibility(View.GONE);
            }
            else if ( ((MyApplication) getActivity().getApplicationContext()).getMusicService().getMusicState().equals(MusicState.PLAYING))
            {
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
            }
        }
        ((MyApplication) getActivity().getApplicationContext()).getMusicService().getSongs().clear();

        GetSongAsync songAsync = new GetSongAsync("trending",categoryKey,0);
        songAsync.setListener(this);
        songAsync.execute();
        EndlessScrollListener endlessScrollListener = new EndlessScrollListener()
        {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount)
            {
                progressBar.setVisibility(View.VISIBLE);
                if (StringUtils.isNotEmpty(nextHref))
                {
                    GetSongAsync songAsync = new GetSongAsync("trending",categoryKey,offset);
                    songAsync.setListener(SongListFragment.this);
                    songAsync.execute();
                    return true;
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }
        };
        listView.setOnScrollListener(endlessScrollListener);

    }

    @OnItemClick(R.id.fragment_songlist_listview)
    public void clickSong(final int position)
    {
        minibar.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.VISIBLE);
        currentSong = adapter.getData().get(position);
        songPicked(position);
        updateUI();
        ivCircleButton.setVisibility(View.GONE);
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
    }

    private void updateUI()
    {
        tvTitle.setText(currentSong.getTitle());
        tvArtist.setText(currentSong.getArtist());
        Picasso.with(getActivity())
                .load(currentSong.getUrlThumbnail())
                .placeholder(R.drawable.default_album_mid)
                .into(ivMiniBarThumbnail);
    }

    public void songPicked(final int position)
    {
        try
        {
            ((MyApplication) getActivity().getApplicationContext()).getMusicService().setSongIndex(position);
            ((MyApplication) getActivity().getApplicationContext()).getMusicService().play();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        SongListFragment.this.getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                refreshListViewSelection(position);
            }
        });
    }

    private synchronized void refreshListViewSelection(final int position)
    {
        synchronized (adapter)
        {
            adapter.setSelectedIndex(position);
            listView.invalidateViews();
            listView.requestLayout();
        }
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


    @OnClick(R.id.mini_bar_pause_button)
    public void pauseClick()
    {
        ((MyApplication) getActivity().getApplicationContext()).getMusicService().pause();
        btPlay.setVisibility(View.VISIBLE);
        btPause.setVisibility(View.GONE);
    }

    @OnClick(R.id.mini_bar_play_button)
    public void resumeClick()
    {
        ((MyApplication) getActivity().getApplicationContext()).getMusicService().resume();
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.mini_bar_rlContent)
    public void clickSongDetails()
    {
        mInterstitialAd.show();
        if (currentSong != null)
        {
            Intent intent = new Intent(getActivity(), SongDetailsActivity.class);
            intent.putExtra("song", currentSong);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }

    }

    public void onEventMainThread(final UpdateUIEvent event)
    {

        MusicState musicState = event.getMusicState();
        currentSong = event.getSong();
        if (musicState == MusicState.PAUSE)
        {
            btPlay.setVisibility(View.VISIBLE);
            btPause.setVisibility(View.GONE);
        }
        else if (musicState == MusicState.PLAYING)
        {
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            SongListFragment.this.getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    refreshListViewSelection(event.getSongIndex());
                }
            });

        }
        updateUI();

    }


    @Override
    public void prepare()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void complete(Object obj)
    {
        final List<Song> songs = new ArrayList<Song>();
        if(obj instanceof  CategoriesDTO)
        {
            CategoriesDTO categoriesDTO = (CategoriesDTO)obj;
            List<TrackDTO> trackDTOs = categoriesDTO.getTrackDTOs();
            for (TrackDTO trackDTO : trackDTOs)
            {
                songs.add(Song.convertFromSongDTO(trackDTO.getTrackDetailDTO()));
            }
            nextHref = categoriesDTO.getNextHref();
        }
        else
        {
            List<TrackDetailDTO> trackDetailDTOs = (List<TrackDetailDTO>) obj;
            songs.addAll(Song.convertSongFromSongDTO(trackDetailDTOs));
            nextHref = "not null";
        }
        if (adapter == null)
        {
            adapter = new ListViewAdapter(getActivity(), songs);
            listView.setAdapter(adapter);
            ((MyApplication) getActivity().getApplicationContext()).getMusicService().setSongs(songs);
        }
        else
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ((MyApplication) getActivity().getApplicationContext()).getMusicService().getSongs().addAll(songs);
                    adapter.getData().addAll(songs);
                    adapter.notifyDataSetChanged();
                    listView.invalidateViews();
                    listView.requestLayout();
                }
            });


        }
        progressBar.setVisibility(View.GONE);

        offset = offset + Const.LIMIT;
    }


    //=============================ADS============================
    InterstitialAd mInterstitialAd;
    private void requestNewInterstitial()
    {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

}