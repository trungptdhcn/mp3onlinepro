package com.cntt.freemusicdownloadnow.event;

/**
 * Created by TRUNGPT on 8/12/16.
 */
public class BufferingUpdateEvent
{
    private int progress;

    public BufferingUpdateEvent(int progress)
    {
        this.progress = progress;
    }

    public int getProgress()
    {
        return progress;
    }

    public void setProgress(int progress)
    {
        this.progress = progress;
    }
}
