package mp3onlinepro.trungpt.com.mp3onlinepro.event;


import mp3onlinepro.trungpt.com.mp3onlinepro.player.MusicState;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.model.Song;

/**
 * Created by TRUNGPT on 8/3/16.
 */
public class UpdateUIEvent
{
    private MusicState musicState;
    private int songIndex;
    private Song song;

    public UpdateUIEvent(MusicState musicState, int songIndex, Song song)
    {
        this.musicState = musicState;
        this.songIndex = songIndex;
        this.song = song;
    }

    public MusicState getMusicState()
    {
        return musicState;
    }

    public int getSongIndex()
    {
        return songIndex;
    }

    public Song getSong()
    {
        return song;
    }
}
