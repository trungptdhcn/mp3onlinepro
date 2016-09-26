package com.cntt.freemusicdownloadnow.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cntt.freemusicdownloadnow.MyApplication;
import com.cntt.freemusicdownloadnow.base.BaseFragment;
import com.cntt.freemusicdownloadnow.base.StringUtils;
import com.cntt.freemusicdownloadnow.event.PlayerPreparedEvent;
import com.cntt.freemusicdownloadnow.event.UpdateProgressEvent;
import com.cntt.freemusicdownloadnow.event.UpdateUIEvent;
import com.cntt.freemusicdownloadnow.network.dto.CategoriesDTO;
import com.cntt.freemusicdownloadnow.network.dto.TrackDTO;
import com.cntt.freemusicdownloadnow.network.dto.TrackDetailDTO;
import com.cntt.freemusicdownloadnow.player.MusicState;
import com.cntt.freemusicdownloadnow.ui.activity.SongDetailsActivity;
import com.cntt.freemusicdownloadnow.ui.adapter.ListViewAdapter;
import com.cntt.freemusicdownloadnow.ui.async.AsyncTaskGetListener;
import com.cntt.freemusicdownloadnow.ui.async.GetSongAsync;
import com.cntt.freemusicdownloadnow.ui.customview.ArcImageView;
import com.cntt.freemusicdownloadnow.ui.customview.EndlessScrollListener;
import com.cntt.freemusicdownloadnow.ui.model.Song;
import com.cntt.freemusicdownloadnow.utils.Const;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;

/**
 * Created by TRUNGPT on 8/11/16.
 */
public class SongListFragment extends BaseFragment implements AsyncTaskGetListener {
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

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        categoryKey = getArguments().getString("categoryKey");
        sharedPreferences = getActivity().getSharedPreferences("song_temp", Context.MODE_PRIVATE);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_songlist;
    }

    @Override
    public void setDataToView(Bundle savedInstanceState) {
        //=====================ADS==================================
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.ads_fullscreen));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();
        //=====================ADS==================================

        rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        rotation.setFillAfter(true);
        String json = sharedPreferences.getString("current_song", "");
        if (StringUtils.isNotEmpty(json)) {
            currentSong = new Gson().fromJson(json, Song.class);
            updateUI();
            if (((MyApplication) getActivity().getApplicationContext()).getMusicService().getMusicState().equals(MusicState.PAUSE)) {
                btPlay.setVisibility(View.VISIBLE);
                btPause.setVisibility(View.GONE);
            } else if (((MyApplication) getActivity().getApplicationContext()).getMusicService().getMusicState().equals(MusicState.PLAYING)) {
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
            }
        }
        ((MyApplication) getActivity().getApplicationContext()).getMusicService().getSongs().clear();

        GetSongAsync songAsync = new GetSongAsync("trending", categoryKey, 0);
        songAsync.setListener(this);
        songAsync.execute();
        EndlessScrollListener endlessScrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                progressBar.setVisibility(View.VISIBLE);
                if (StringUtils.isNotEmpty(nextHref)) {
                    GetSongAsync songAsync = new GetSongAsync("trending", categoryKey, offset);
                    songAsync.setListener(SongListFragment.this);
                    songAsync.execute();
                    return true;
                } else {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }
        };
        listView.setOnScrollListener(endlessScrollListener);

    }

    @OnItemClick(R.id.fragment_songlist_listview)
    public void clickSong(final int position) {
        minibar.setVisibility(View.VISIBLE);
        progressWheel.setVisibility(View.VISIBLE);
        currentSong = adapter.getData().get(position);
        songPicked(position);
        updateUI();
        ivCircleButton.setVisibility(View.GONE);
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
    }

    private void updateUI() {
        tvTitle.setText(currentSong.getTitle());
        tvArtist.setText(currentSong.getArtist());
        Picasso.with(getActivity())
                .load(currentSong.getUrlThumbnail())
                .placeholder(R.drawable.default_album_mid)
                .into(ivMiniBarThumbnail);
    }

    public void songPicked(final int position) {
        try {
            ((MyApplication) getActivity().getApplicationContext()).getMusicService().setSongIndex(position);
            ((MyApplication) getActivity().getApplicationContext()).getMusicService().play();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SongListFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshListViewSelection(position);
            }
        });
    }

    private synchronized void refreshListViewSelection(final int position) {
        synchronized (adapter) {
            adapter.setSelectedIndex(position);
            listView.invalidateViews();
            listView.requestLayout();
        }
    }


    public void onEventMainThread(UpdateProgressEvent event) {
        arcImageView.setProgress(event.getProgress());
    }

    public void onEventMainThread(PlayerPreparedEvent event) {
        ivCircleButton.setVisibility(View.VISIBLE);
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
        ivMiniBarThumbnail.startAnimation(rotation);
        progressWheel.setVisibility(View.GONE);
    }


    @OnClick(R.id.mini_bar_pause_button)
    public void pauseClick() {
        ((MyApplication) getActivity().getApplicationContext()).getMusicService().pause();
        btPlay.setVisibility(View.VISIBLE);
        btPause.setVisibility(View.GONE);
    }

    @OnClick(R.id.mini_bar_play_button)
    public void resumeClick() {
        ((MyApplication) getActivity().getApplicationContext()).getMusicService().resume();
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.mini_bar_rlContent)
    public void clickSongDetails() {
        mInterstitialAd.show();
        if (currentSong != null) {
            Intent intent = new Intent(getActivity(), SongDetailsActivity.class);
            intent.putExtra("song", currentSong);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }

    }

    public void onEventMainThread(final UpdateUIEvent event) {

        MusicState musicState = event.getMusicState();
        currentSong = event.getSong();
        if (musicState == MusicState.PAUSE) {
            btPlay.setVisibility(View.VISIBLE);
            btPause.setVisibility(View.GONE);
        } else if (musicState == MusicState.PLAYING) {
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            SongListFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshListViewSelection(event.getSongIndex());
                }
            });

        }
        updateUI();

    }


    @Override
    public void prepare() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void complete(Object obj) {
        final List<Song> songs = new ArrayList<Song>();
        if (obj instanceof CategoriesDTO) {
            CategoriesDTO categoriesDTO = (CategoriesDTO) obj;
            List<TrackDTO> trackDTOs = categoriesDTO.getTrackDTOs();
            for (TrackDTO trackDTO : trackDTOs) {
                songs.add(Song.convertFromSongDTO(trackDTO.getTrackDetailDTO()));
            }
            nextHref = categoriesDTO.getNextHref();
        } else {
            List<TrackDetailDTO> trackDetailDTOs = (List<TrackDetailDTO>) obj;
            songs.addAll(Song.convertSongFromSongDTO(trackDetailDTOs));
            nextHref = "not null";
        }
        if (adapter == null) {
            adapter = new ListViewAdapter(getActivity(), songs);
            listView.setAdapter(adapter);
            if (isAdded())
                ((MyApplication) getActivity().getApplicationContext()).getMusicService().setSongs(songs);
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
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

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

}