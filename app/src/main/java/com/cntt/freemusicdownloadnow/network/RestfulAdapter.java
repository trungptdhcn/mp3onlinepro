package com.cntt.freemusicdownloadnow.network;

import com.squareup.okhttp.OkHttpClient;
import com.cntt.freemusicdownloadnow.utils.Const;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

import java.util.concurrent.TimeUnit;

/**
 * Created by Trung on 7/22/2015.
 */
public class RestfulAdapter
{
    private static RestAdapter restAdapter;

    public static RestAdapter getRestAdapter(boolean isVersion2)
    {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(6000, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(6000, TimeUnit.SECONDS);
        if (isVersion2)
        {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Const.BASE_URL_V2)
                    .setClient(new OkClient(okHttpClient))
                    .build();
        }
        else
        {
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Const.BASE_URL)
                    .setClient(new OkClient(okHttpClient))
                    .build();
        }
        return restAdapter;
    }
}
