package mp3onlinepro.trungpt.com.mp3onlinepro.event;

import mp3onlinepro.trungpt.com.mp3onlinepro.ui.model.Song;

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
