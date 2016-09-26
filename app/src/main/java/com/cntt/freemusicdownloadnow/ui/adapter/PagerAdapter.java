package com.cntt.freemusicdownloadnow.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.cntt.freemusicdownloadnow.ui.fragment.CommentFragment;
import com.cntt.freemusicdownloadnow.ui.fragment.RelatedFragment;
import com.cntt.freemusicdownloadnow.ui.fragment.SongListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trung on 04/26/2016.
 */
public class PagerAdapter extends FragmentPagerAdapter
{

    private final String[] TITLES = {"Songs", "Related", "Comment"};
    List<Fragment> fragments = new ArrayList<>();
    private String categoryKey;
    public PagerAdapter(FragmentManager fm, String categoryKey)
    {
        super(fm);
        this.categoryKey = categoryKey;
        SongListFragment songListFragment = new SongListFragment();
        RelatedFragment relatedFragment = new RelatedFragment();
        CommentFragment commentFragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("categoryKey", categoryKey);
        songListFragment.setArguments(bundle);
//        relatedFragment.setArguments(bundle);
//        commentFragment.setArguments(bundle);
        this.fragments.add(songListFragment);
        this.fragments.add(relatedFragment);
        this.fragments.add(commentFragment);
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return TITLES[position];
    }

    @Override
    public int getCount()
    {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragments.get(position);
    }

}
