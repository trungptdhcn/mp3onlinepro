package com.cntt.freemusicdownloadnow.utils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;

/**
 * Created by TRUNGPT on 8/23/16.
 */
public class Utils
{
    private static AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice("")
            .build();
    public static void loadBannerAds(AdView adView)
    {
        adView.loadAd(adRequest);
    }

    public static void loadNativeAds(NativeExpressAdView nativeExpressAdView)
    {
        nativeExpressAdView.loadAd(adRequest);
    }
}
