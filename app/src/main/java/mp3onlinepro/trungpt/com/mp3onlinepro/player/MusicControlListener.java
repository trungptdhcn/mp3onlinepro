package mp3onlinepro.trungpt.com.mp3onlinepro.player;

import java.io.IOException;

/**
 * Created by TRUNGPT on 7/25/16.
 */
public interface MusicControlListener
{
    public void play() throws IOException;
    public void pause();
    public void stop();
    public void next();
    public void previous();
    public void repeat(boolean isRepeat);
    public void resume();
    public void seekTo(int progress);
}
