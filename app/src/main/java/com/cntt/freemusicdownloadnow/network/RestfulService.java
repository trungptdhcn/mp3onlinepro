package com.cntt.freemusicdownloadnow.network;


/**
 * Created by Trung on 7/22/2015.
 */
public class RestfulService
{
    private static RestfulServiceIn restfulServiceIn;

    public static RestfulServiceIn getInstance(boolean isVersion)
    {
        restfulServiceIn = RestfulAdapter.getRestAdapter(isVersion).create(RestfulServiceIn.class);
        return restfulServiceIn;
    }
}
