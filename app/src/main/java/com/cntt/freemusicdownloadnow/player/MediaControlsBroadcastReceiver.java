package com.cntt.freemusicdownloadnow.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by TRUNGPT on 7/29/16.
 */
public class MediaControlsBroadcastReceiver extends BroadcastReceiver {
    private MusicService musicService;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (musicService != null) {
            String action = intent.getAction();
            Intent serviceIntent = new Intent(context, MusicService.class);
            context.startService(serviceIntent);
            MusicService.MusicBinder binder = (MusicService.MusicBinder) peekService(context, new Intent(context, MusicService.class));
            if (binder != null) {
                musicService = binder.getService();
            }

            if (action.equals(MusicService.NOTIFY_PAUSE)) {
                musicService.pause();
            }
            if (action.equals(MusicService.NOTIFY_PLAY)) {
                musicService.resume();
            }
            if (action.equals(MusicService.NOTIFY_NEXT) && intent.getStringExtra("state").equals("next")) {
                Log.e("trung dai ca notify nex", "Trung dai ca");
                musicService.next();
            }
            if (action.equals(MusicService.NOTIFY_PREVIOUS)) {
                musicService.previous();
            }
            if (action.equals(MusicService.NOTIFY_STOP)) {
                musicService.stop();
            }
        }
    }

    public void registerMusicPlayerService(MusicService musicPlayerService) {
        if (musicService != null) {
            musicService = musicPlayerService;
        }
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}
