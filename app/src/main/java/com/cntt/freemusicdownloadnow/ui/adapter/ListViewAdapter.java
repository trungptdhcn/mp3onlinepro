package com.cntt.freemusicdownloadnow.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;
import com.cntt.freemusicdownloadnow.ui.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by TRUNGPT on 7/23/16.
 */
public class ListViewAdapter extends BaseAdapter
{
    List<Song> data = new ArrayList<>();
    private Activity activity;
    private int selectedIndex = -1;

    public ListViewAdapter(Activity context, List<Song> data)
    {
        this.data = data;
        this.activity = context;
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        Song song = data.get(position);
        LayoutInflater inflater = activity.getLayoutInflater();
        final ViewHolder holder;
        if (convertView != null)
        {
            holder = (ViewHolder) convertView.getTag();
        }
        else
        {
            convertView = inflater.inflate(R.layout.playlist_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
//        FontUtils.setFont(activity, ((ViewHolder) holder).tvTitle, "fonts/Roboto-Regular.ttf");
//        FontUtils.setFont(activity, ((ViewHolder) holder).tvSinger, "fonts/Roboto-Regular.ttf");
        ((ViewHolder) holder).tvTitle.setText(data.get(position).getTitle());
        ((ViewHolder) holder).tvSinger.setText(data.get(position).getArtist());
        holder.tvDuration.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(data.get(position).getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(data.get(position).getDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(data.get(position).getDuration()))
        ));
        if (position == selectedIndex)
        {
            holder.rlRootView.setBackgroundResource(R.color.white_transparent);
        }
        else
        {
            holder.rlRootView.setBackgroundResource(android.R.color.transparent);
        }
        Picasso.with(activity)
                .load(song.getUrlThumbnail())
                .placeholder(R.drawable.default_album_mid)
                .into(holder.imageView);
        return convertView;
    }

    public synchronized List<Song> getData()
    {
        synchronized (data)
        {
            return data;
        }
    }

    public void setData(List<Song> data)
    {
        synchronized (this.data)
        {
            this.data = data;
        }
    }

    static class ViewHolder
    {
        @Bind(R.id.playlist_item_tvTitle)
        TextView tvTitle;
        @Bind(R.id.playlist_item_tvSinger)
        TextView tvSinger;
        @Bind(R.id.playlist_item_rlRootView)
        RelativeLayout rlRootView;
        @Bind(R.id.playlist_item_ivThumbnail)
        ImageView imageView;
         @Bind(R.id.playlist_item_tvDuration)
        TextView tvDuration;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }

    public synchronized int getSelectedIndex()
    {
            return selectedIndex;
    }

    public synchronized void setSelectedIndex(final int selectedIndex)
    {
        synchronized (data)
        {
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ListViewAdapter.this.selectedIndex = selectedIndex;
                    notifyDataSetChanged();
                }
            });

        }

    }

    public void insertToList(final List<Song> songs)
    {
        synchronized (data)
        {
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    ListViewAdapter.this.data.addAll(songs);
                    notifyDataSetChanged();
                }
            });

        }
    }

    @Override
    public synchronized void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
    }
}
