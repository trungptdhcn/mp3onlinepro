package mp3onlinepro.trungpt.com.mp3onlinepro.event;

/**
 * Created by TRUNGPT on 7/25/16.
 */
public class UpdateProgressEvent
{
    private float progress;
    private long currentDuration;

    public UpdateProgressEvent(float progress, long currentDuration)
    {
        this.progress = progress;
        this.currentDuration = currentDuration;
    }

    public float getProgress()
    {
        return progress;
    }

    public long getCurrentDuration()
    {
        return currentDuration;
    }
}
