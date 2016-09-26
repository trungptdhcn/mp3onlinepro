package com.cntt.freemusicdownloadnow.event;

import com.cntt.freemusicdownloadnow.ui.model.Song;

/**
 * Created by TRUNGPT on 7/25/16.
 */
public class PlayerPreparedEvent
{
    private Song song;

    public PlayerPreparedEvent(Song song)
    {
        this.song = song;
    }

    public Song getSong()
    {
        return song;
    }
}
