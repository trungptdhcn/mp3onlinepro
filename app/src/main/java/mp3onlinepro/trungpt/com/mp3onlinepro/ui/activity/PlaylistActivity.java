package mp3onlinepro.trungpt.com.mp3onlinepro.ui.activity;

import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.*;
import com.squareup.picasso.Picasso;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import mp3onlinepro.trungpt.com.mp3onlinepro.MyApplication;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.FavoriteSong;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.FavoriteSongDao;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.PlayerPreparedEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.RestfulService;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.dto.CategoriesDTO;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.dto.TrackDTO;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.adapter.ListViewAdapter;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.adapter.PagerAdapter;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.model.Song;
import mp3onlinepro.trungpt.com.mp3onlinepro.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity
{

    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.activity_playlist_tvPlayListName)
    public TextView tvPlayListName;
    @Bind(R.id.activity_playlist_thumbnail)
    public ImageView thumbnail;
    @Bind(R.id.activity_playlist_ivPlayListThumbnail)
    public CircleImageView ivArtist;
    @Bind(R.id.activity_playlist_tvArtist)
    public TextView tvArtistHeader;
    @Bind(R.id.activity_playlist_rlInfomation)
    public RelativeLayout rlInfomation;
    @Bind(R.id.adView)
    public AdView adView;
    private PagerAdapter adapter;
    Song song;
    String categoryKey;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);
        //=====================ADS==================================
        mInterstitialAd = new InterstitialAd(this);
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
        MobileAds.initialize(getApplicationContext(), getString(R.string.app_id));
        Utils.loadBannerAds(adView);
        //=====================ADS==================================
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            categoryKey = extras.getString("categoryKey");
        }
        adapter = new PagerAdapter(getSupportFragmentManager(),categoryKey);
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
        pager.setOffscreenPageLimit(3);

    }

    public void onEventMainThread(PlayerPreparedEvent event)
    {
        song = event.getSong();
        tvArtistHeader.setText(song.getUser().getFullName());
        tvPlayListName.setText(song.getTitle());
        Picasso.with(this)
                .load(song.getUrlThumbnail())
                .resize(rlInfomation.getWidth(), rlInfomation.getHeight())
                .centerCrop()
                .placeholder(R.drawable.default_album_mid)
                .into(thumbnail);

        Picasso.with(this)
                .load(song.getUser().getAvatarUrl())
                .placeholder(R.drawable.default_album_mid)
                .into(ivArtist);
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
