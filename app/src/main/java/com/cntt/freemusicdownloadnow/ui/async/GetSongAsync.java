package com.cntt.freemusicdownloadnow.ui.async;

import android.os.AsyncTask;
import com.cntt.freemusicdownloadnow.network.RestfulService;
import com.cntt.freemusicdownloadnow.network.RestfulServiceIn;
import com.cntt.freemusicdownloadnow.utils.Const;

/**
 * Created by TRUNGPT on 8/20/16.
 */
public class GetSongAsync extends AsyncTask<Void, Void, Object>
{
    AsyncTaskGetListener listener;
    RestfulServiceIn service;
    private String kind;
    private String genres;
    private int offset;

    public GetSongAsync(String kind, String genres, int offset)
    {
        this.kind = kind;
        this.genres = genres;
        this.offset = offset;
        if (genres.equals("all-music"))
        {
            service = RestfulService.getInstance(true);
        }
        else
        {
            service = RestfulService.getInstance(false);
        }
    }

    @Override
    protected void onPreExecute()
    {
        listener.prepare();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o)
    {
        listener.complete(o);
        super.onPostExecute(o);
    }
    @Override
    protected Object doInBackground(Void... params)
    {
        if (genres.equals("all-music"))
        {
            return service.getTrackWithCategory(kind,genres,offset, Const.LIMIT);
        }
        else
        {
            return service.getTracksWithCategory(genres,offset, Const.LIMIT);
        }
    }

    public void setListener(AsyncTaskGetListener listener)
    {
        this.listener = listener;
    }

    public AsyncTaskGetListener getListener()
    {
        return listener;
    }

    public RestfulServiceIn getService()
    {
        return service;
    }

    public void setService(RestfulServiceIn service)
    {
        this.service = service;
    }

}
