package mp3onlinepro.trungpt.com.mp3onlinepro.player;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import de.greenrobot.event.EventBus;
import mp3onlinepro.trungpt.com.mp3onlinepro.MainActivity;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.BufferingUpdateEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.PlayerPreparedEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.UpdateProgressEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.UpdateUIEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.activity.SongDetailsActivity;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.model.Song;
import mp3onlinepro.trungpt.com.mp3onlinepro.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TRUNGPT on 7/26/16.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MusicControlListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener
        , MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener

{
    MusicState musicState = MusicState.PAUSE;

    private MediaPlayer player;
    private List<Song> songs = new ArrayList<>();
    Song currentSong;
    private int songIndex = 0;
    private int currentDuration = 0;


    private Utilities utils;
    private Handler progressBarHandler = new Handler();
    private final IBinder musicBind = new MusicBinder();

    //Notification
    private int NOTIFICATION_ID = 1111;
    public static final String NOTIFY_PREVIOUS = "com.trungpt.mp3downloadpro.previous";
    public static final String NOTIFY_STOP = "com.trungpt.mp3downloadpro.stop";
    public static final String NOTIFY_PAUSE = "com.trungpt.mp3downloadpro.pause";
    public static final String NOTIFY_PLAY = "com.trungpt.mp3downloadpro.play";
    public static final String NOTIFY_NEXT = "com.trungpt.mp3downloadpro.next";

    private RemoteViews remoteViews;
    NotificationManager mNotificationManager;
    Notification notification;

    private boolean isRepeat = false;
    MediaControlsBroadcastReceiver mediaControlsBroadcastReceiver;

    SharedPreferences sharedPreferences;

    public void setRepeat(boolean repeat)
    {
        isRepeat = repeat;
    }

    public boolean isRepeat()
    {
        return isRepeat;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent)
    {
        Log.e("trung dai ca", percent + "");
        EventBus.getDefault().post(new BufferingUpdateEvent(percent));
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra)
    {
        MusicState musicStateTemp = musicState;
        switch (what)
        {

            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                musicState = MusicState.BUFFERING;
                updateUI();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                musicState = MusicState.PLAYING;
                updateUI();
                break;
        }
        return false;

    }

    public class MusicBinder extends Binder
    {
        public MusicService getService()
        {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sharedPreferences = getApplicationContext().getSharedPreferences("song_temp", MODE_PRIVATE);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        utils = new Utilities();
        initMediaPlayerIfNeed();
        registerRemoteClient();
    }

    public void initMediaPlayerIfNeed()
    {
        if (player == null)
        {
            player = new MediaPlayer();
            player.setWakeMode(getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);


        }
        else
        {
            player.reset();
        }
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
        player.setOnBufferingUpdateListener(this);
        player.setOnInfoListener(this);
        player.setOnCompletionListener(this);
    }


    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }


    @Override
    public void onPrepared(MediaPlayer mp)
    {
        player.setLooping(isRepeat);
        player.start();
        musicState = MusicState.PLAYING;
        updateUI();
        updateLockScreen(songs.get(songIndex));
        EventBus.getDefault().post(new PlayerPreparedEvent(songs.get(songIndex)));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        mediaControlsBroadcastReceiver = new MediaControlsBroadcastReceiver();
        IntentFilter theFilter = new IntentFilter();
        registerReceiver(mediaControlsBroadcastReceiver, theFilter);
        return musicBind;
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {

        if (!isRepeat)
        {
            next();
        }
        else
        {
            player.start();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        return true;
    }

    @Override
    public void play() throws IOException
    {
        Log.e("trung dai caplay", songIndex + "");
        musicState = MusicState.BUFFERING;
        initMediaPlayerIfNeed();
        showNotification();
        Song song = songs.get(songIndex);
        Uri urlSource = Uri.parse(song.getUrlSource());
        player.setDataSource(getApplicationContext(), urlSource);
        player.prepareAsync();
        updateUI();
        saveToSharePreference(songs.get(songIndex));
    }

    private void saveToSharePreference(Song song)
    {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json;
        if (song != null)
        {
            json = gson.toJson(song); // myObject - instance of MyObject
        }
        else
        {
            json = "";
        }
        prefsEditor.putString("current_song", json);
        prefsEditor.commit();
    }

    @Override
    public void pause()
    {
        if (player != null && musicState == MusicState.PLAYING)
        {
            player.pause();
            musicState = MusicState.PAUSE;
            currentDuration = player.getCurrentPosition();
            updateUI();
        }
    }

    @Override
    public void stop()
    {
        if (player != null)
        {
            player.stop();
            player.release();
            player = null;
            if (mNotificationManager != null)
            {
                mNotificationManager.cancel(NOTIFICATION_ID);
            }
        }
        saveToSharePreference(null);
    }

    @Override
    public void next()
    {
        if (songIndex < songs.size() - 1)
        {
            songIndex = songIndex + 1;
            Log.e("trung dai canext", songIndex + "");
            try
            {
                play();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(this, "No have song index", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void previous()
    {
        if (songIndex > 0)
        {
            songIndex = songIndex - 1;
            try
            {
                play();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        else
        {
            Toast.makeText(this, "No song index", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void repeat(boolean isRepeat)
    {
        this.isRepeat = isRepeat;
    }

    @Override
    public void resume()
    {
        if (player != null && musicState == MusicState.PAUSE && currentSong != null)
        {
            player.seekTo(currentDuration);
            player.start();
            musicState = MusicState.PLAYING;
            updateUI();
        }
    }

    @Override
    public void seekTo(int progress)
    {
        player.seekTo(progress);
//        player.start();
        musicState = MusicState.PLAYING;
        updateUI();
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        switch (focusChange)
        {

        }
    }

    public void setSongs(List<Song> songs)
    {
        this.songs = songs;
    }

    public List<Song> getSongs()
    {
        return songs;
    }

    public void setSongIndex(int songIndex)
    {
        this.songIndex = songIndex;
    }

    private Runnable mUpdateTimeTask = new Runnable()
    {
        public void run()
        {
            long totalDuration = 0;
            try
            {
                if (player != null)
                {
                    totalDuration = player.getDuration();
                }
            }
            catch (IllegalStateException e)
            {
                e.printStackTrace();
            }
            long currentDuration = 0;
            try
            {
                if (player != null)
                {
                    currentDuration = player.getCurrentPosition();
                }
            }
            catch (IllegalStateException e)
            {
                e.printStackTrace();
            }
            float progress = (utils.getProgressPercentage(currentDuration,
                    totalDuration));
            EventBus.getDefault().post(new UpdateProgressEvent(progress,currentDuration));
            progressBarHandler.postDelayed(this, 100);
        }
    };

    public void updateProgressBar()
    {
        progressBarHandler.postDelayed(mUpdateTimeTask, 100);
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        Thread.currentThread().interrupt();
        unregisterReceiver(mediaControlsBroadcastReceiver);
        player.release();
        player = null;
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        progressBarHandler.removeCallbacks(mUpdateTimeTask);
        Thread.currentThread().interrupt();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void showNotification()
    {
        if (songIndex < songs.size())
        {
            currentSong = songs.get(songIndex);
            String title = currentSong.getTitle();
            String artist = currentSong.getArtist();
            String urlThumbnail = currentSong.getUrlThumbnail();
            NotificationCompat.Builder builder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_notification);
            Intent resultIntent = new Intent(this, SongDetailsActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra("song", songs.get(songIndex));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    resultIntent, 0);
            remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
            remoteViews.setTextViewText(R.id.custom_notification_tvTitle, title);
            notification = builder.build();
            Picasso.with(getApplicationContext()).load(urlThumbnail)
                    .resize(200, 200)
                    .centerCrop()
                    .into(remoteViews, R.id.custom_notification_ivThumbnail, NOTIFICATION_ID, notification);
            notification.contentIntent = pendingIntent;
            setListeners(remoteViews);

            notification.contentView = remoteViews;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }

    }

    private void updateNotification()
    {
        if (player.isPlaying())
        {
            remoteViews.setViewVisibility(R.id.custom_notification_btPlay, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.custom_notification_btPause, View.GONE);
        }
        else
        {
            remoteViews.setViewVisibility(R.id.custom_notification_btPlay, View.GONE);
            remoteViews.setViewVisibility(R.id.custom_notification_btPause, View.VISIBLE);
        }
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void setListeners(RemoteViews view)
    {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_STOP);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        next.putExtra("state", "next");
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.custom_notification_btPrevious, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.custom_notification_close, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.custom_notification_btPlay, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.custom_notification_btNext, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.custom_notification_btPause, pPlay);
    }

    private RemoteControlClient remoteControlClient;
    private ComponentName remoteComponentName;
    AudioManager audioManager;

    @SuppressLint("NewApi")
    private void registerRemoteClient()
    {
        remoteComponentName = new ComponentName(getApplicationContext(), this.getClass().getName());
        try
        {
            if (remoteControlClient == null)
            {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        }
        catch (Exception ex)
        {
        }
    }

    private void updateLockScreen(Song song)
    {
        if (remoteControlClient == null)
        {
            return;
        }
        RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, song.getArtist());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, song.getArtist());
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, song.getTitle());
        Target artworkTarget = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom)
            {
                remoteControlClient.editMetadata(false).putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK,
                        bitmap).apply();
            }

            @Override
            public void onBitmapFailed(Drawable drawable)
            {
            }

            @Override
            public void onPrepareLoad(Drawable drawable)
            {
            }
        };
        Picasso.with(this).load(song.getUrlThumbnail()).skipMemoryCache().into(artworkTarget);
        metadataEditor.apply();
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public void updateUI()
    {
        updateProgressBar();
        updateNotification();
        EventBus.getDefault().post(new UpdateUIEvent(musicState, songIndex, songs.get(songIndex)));
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        if (mNotificationManager != null)
        {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
        sharedPreferences.edit().clear().commit();
    }

    public int getSongIndex()
    {
        return songIndex;
    }

    public MusicState getMusicState()
    {
        return musicState;
    }
}
