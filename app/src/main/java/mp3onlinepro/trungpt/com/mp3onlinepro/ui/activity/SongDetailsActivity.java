package mp3onlinepro.trungpt.com.mp3onlinepro.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import de.greenrobot.event.EventBus;
import me.relex.circleindicator.CircleIndicator;
import mp3onlinepro.trungpt.com.mp3onlinepro.MyApplication;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.FavoriteSong;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.FavoriteSongDao;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.BufferingUpdateEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.PlayerPreparedEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.UpdateProgressEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.UpdateUIEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.player.MusicState;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.adapter.SongInfoPlayerAdapter;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.customview.FadePageTransformer;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.model.Song;
import mp3onlinepro.trungpt.com.mp3onlinepro.utils.ImageUtil;

import java.util.concurrent.TimeUnit;

public class SongDetailsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener
{

    @Bind(R.id.activity_song_details_viewPager)
    ViewPager viewPager;
    @Bind(R.id.indicator)
    CircleIndicator indicator;
    @Bind(R.id.activity_song_details_ivCover)
    ImageView ivCover;
    @Bind(R.id.activity_song_details_seekbar)
    SeekBar seekBar;
    @Bind(R.id.activity_song_details_btPlay)
    ImageButton btPlay;
    @Bind(R.id.activity_song_details_btPause)
    ImageButton btPause;
    @Bind(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @Bind(R.id.activity_song_details_tvSongsName)
    TextView tvSongsName;
    @Bind(R.id.activity_song_details_tvArtistName)
    TextView tvArtistName;
    @Bind(R.id.activity_song_details_btFav)
    CheckBox btFav;
    @Bind(R.id.activity_song_details_btRepeat)
    CheckBox btRepeat;
    @Bind(R.id.activity_song_details_tvTotalDuration)
    TextView tvTotalDuration;
    @Bind(R.id.activity_song_details_tvCurentDuration)
    TextView tvCurentDuration;
    SongInfoPlayerAdapter adapter;
    Song song;
    Animation animation;
    FavoriteSongDao favoriteSongDao;
    @Bind(R.id.activity_song_details_btCirclePause)
    ImageView ivCircleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_song_details);
        ButterKnife.bind(this);
        //=====================ADS==================================
        mInterstitialAd = new InterstitialAd(SongDetailsActivity.this);
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
        favoriteSongDao = ((MyApplication) getApplication()).getDaoSession().getFavoriteSongDao();
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animation.setFillAfter(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            song = extras.getParcelable("song");

        }
        updateUI();
        seekBar.setOnSeekBarChangeListener(this);
        btFav.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mInterstitialAd.show();
                if (btFav.isChecked())
                {
                    updateToDB(song);

                }
                else
                {
                    favoriteSongDao.deleteByKey(song.getId());
                }
            }
        });

        btRepeat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MyApplication) getApplicationContext()).getMusicService().repeat(btRepeat.isChecked());
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            song = extras.getParcelable("song");
            updateUI();
        }
    }


    private void updateUI()
    {

        FavoriteSong favoriteSong = favoriteSongDao.loadByRowId(song.getId());
        btRepeat.setChecked(((MyApplication) getApplicationContext()).getMusicService().isRepeat());
        tvTotalDuration.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(song.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(song.getDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(song.getDuration()))));
        if (favoriteSong != null)
        {
            btFav.setChecked(true);
        }
        else
        {
            btFav.setChecked(false);
        }
        if (adapter == null)
        {
            adapter = new SongInfoPlayerAdapter(this, song);
            viewPager.setAdapter(adapter);
            indicator.setViewPager(viewPager);
            viewPager.setPageTransformer(true, new FadePageTransformer());
        }
        else
        {
            adapter.setSong(song);
            adapter.notifyDataSetChanged();
        }

        tvSongsName.setText(song.getTitle());
        tvArtistName.setText(song.getArtist());
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Picasso.with(this)
                .load(song.getUrlThumbnail())
                .resize(width, height)
                .centerCrop()
                .into(new Target()
                {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                    {
                        Bitmap blurred = ImageUtil.blurRenderScript(SongDetailsActivity.this, bitmap, 25);//second parametre is radius
                        ivCover.setImageBitmap(blurred);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable)
                    {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable)
                    {

                    }
                });

        if (((MyApplication) getApplicationContext()).getMusicService().getMusicState().equals(MusicState.PLAYING)||
            ((MyApplication) getApplicationContext()).getMusicService().getMusicState().equals(MusicState.PAUSE))
        {
            ivCircleButton.setVisibility(View.VISIBLE);
            progressWheel.setVisibility(View.GONE);
        }
        else if (((MyApplication) getApplicationContext()).getMusicService().getMusicState().equals(MusicState.PREPARING))
        {
            ivCircleButton.setVisibility(View.GONE);
            progressWheel.setVisibility(View.VISIBLE);
        }
    }

    public void onEventMainThread(UpdateProgressEvent event)
    {
        seekBar.setProgress((int) (100 * event.getProgress()));
        tvCurentDuration.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(event.getCurrentDuration()),
                TimeUnit.MILLISECONDS.toSeconds(event.getCurrentDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(event.getCurrentDuration()))));
    }

    public void onEventMainThread(UpdateUIEvent event)
    {
        song = event.getSong();
        MusicState musicState = event.getMusicState();
        if (musicState == MusicState.PAUSE)
        {
            btPlay.setVisibility(View.VISIBLE);
            btPause.setVisibility(View.GONE);
            progressWheel.setVisibility(View.GONE);
            ivCircleButton.setVisibility(View.VISIBLE);
        }
        else if (musicState == MusicState.PLAYING)
        {
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            progressWheel.setVisibility(View.GONE);
            ivCircleButton.setVisibility(View.VISIBLE);
        }
        else  if (musicState == MusicState.BUFFERING)
        {
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
            progressWheel.setVisibility(View.VISIBLE);
            ivCircleButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.activity_song_details_btPause)
    public void pauseClick()
    {
        ((MyApplication) getApplicationContext()).getMusicService().pause();
        btPlay.setVisibility(View.VISIBLE);
        btPause.setVisibility(View.GONE);
    }

    @OnClick(R.id.activity_song_details_btPlay)
    public void resumeClick()
    {
        ((MyApplication) getApplicationContext()).getMusicService().resume();
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.activity_song_details_btPre)
    public void preClick()
    {
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.GONE);
        progressWheel.setVisibility(View.VISIBLE);
        ((MyApplication) getApplicationContext()).getMusicService().previous();

    }

    @OnClick(R.id.activity_song_details_btNext)
    public void nextClick()
    {
        btPlay.setVisibility(View.GONE);
        btPause.setVisibility(View.GONE);
        progressWheel.setVisibility(View.VISIBLE);
        ivCircleButton.setVisibility(View.GONE);
        ((MyApplication) getApplicationContext()).getMusicService().next();
    }


    public void onEventMainThread(PlayerPreparedEvent event)
    {
        progressWheel.setVisibility(View.GONE);
        btPause.setVisibility(View.VISIBLE);
        song = event.getSong();
        updateUI();
    }

    public void onEventMainThread(BufferingUpdateEvent event)
    {
        seekBar.setSecondaryProgress(event.getProgress());
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(-1, R.anim.slide_out_up);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {

        if (!fromUser)
        {
            return;
        }
        long duration = song.getDuration();
        float percentage = (float) progress / (float) 100;
        float newposition = percentage * duration;
        ((MyApplication) getApplicationContext()).getMusicService().seekTo((int) newposition);
    }

    @OnClick(R.id.activity_song_details_yourPlayList)
    public void setFav()
    {
        startActivity(new Intent(this, YourFavoriteActivity.class));
    }

    public void updateToDB(Song song)
    {
        FavoriteSong favoriteSong = FavoriteSong.convertFromSong(song);
        favoriteSongDao.insertOrReplace(favoriteSong);
    }

    @OnClick(R.id.activity_song_details_btDownload)
    public void downloadSong()
    {
        Toast.makeText(this,"Feature comming soon",Toast.LENGTH_SHORT).show();

    }

    public void deleteDB(Song song)
    {
        favoriteSongDao.deleteByKey(song.getId());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        progressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        progressWheel.setVisibility(View.GONE);
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
