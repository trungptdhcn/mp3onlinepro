package mp3onlinepro.trungpt.com.mp3onlinepro;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.DaoMaster;
import mp3onlinepro.trungpt.com.mp3onlinepro.db.DaoSession;
import mp3onlinepro.trungpt.com.mp3onlinepro.player.MusicService;
import org.greenrobot.greendao.database.Database;

/**
 * Created by TRUNGPT on 8/5/16.
 */
public class MyApplication extends Application
{
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    public static final boolean ENCRYPTED = true;
    private DaoSession daoSession;
    private Tracker mTracker;
    synchronized public Tracker getDefaultTracker()
    {
        if (mTracker == null)
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
    private ServiceConnection musicConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            musicBound = false;
        }
    };


    @Override
    public void onCreate()
    {
        super.onCreate();
        if (playIntent == null)
        {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"mp3onlinepro.trungpt.com.mp3onlinepro.db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }

    public MusicService getMusicService()
    {
        return musicService;
    }

}
