package com.cntt.freemusicdownloadnow.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.google.android.gms.ads.NativeExpressAdView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;
import com.cntt.freemusicdownloadnow.ui.model.Song;
import com.cntt.freemusicdownloadnow.utils.Utils;

/**
 * Created by TRUNGPT on 8/4/16.
 */
public class SongInfoPlayerAdapter extends PagerAdapter
{
    Song song;
    private Activity activity;

    public SongInfoPlayerAdapter(Activity activity, Song song)
    {
        this.activity = activity;
        this.song = song;
    }

    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        return 2;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position)
    {
        final LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = null;
        if (position == 0)
        {
            viewLayout = inflater.inflate(R.layout.song_info_player_cover_item, null);
            final ImageView ivCover = (ImageView) viewLayout.findViewById(R.id.song_info_player_cover_item_ivCover);
            Picasso.with(activity)
                    .load(song.getUser().getAvatarUrl())
                    .into(new Target()
                    {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                        {
                            ivCover.setImageBitmap(bitmap);
                            RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            rotateAnimation.setDuration(100000);
                            ivCover.startAnimation(rotateAnimation);

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable)
                        {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable)
                        {

                        }
                    });
        }
        else if (position == 1)
        {
            viewLayout = inflater.inflate(R.layout.song_info_player_lyric_item, null);
            NativeExpressAdView nativeAdView = (NativeExpressAdView) viewLayout.findViewById(R.id.nativeAdView);
            Utils.loadNativeAds(nativeAdView);
        }
        container.addView(viewLayout);
        return viewLayout;
    }

    public boolean isViewFromObject(View view, Object o)
    {
        return view == o;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object)
    {
        container.removeView((RelativeLayout) object);
    }

    public void setSong(Song song)
    {
        this.song = song;
    }
}
