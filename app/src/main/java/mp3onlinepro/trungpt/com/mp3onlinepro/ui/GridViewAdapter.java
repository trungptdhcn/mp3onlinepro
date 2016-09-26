package mp3onlinepro.trungpt.com.mp3onlinepro.ui;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TRUNGPT on 8/9/16.
 */
public class GridViewAdapter extends BaseAdapter
{
    private Activity activity;
    String[] categories = {"Trending Music","Alternative Rock","Ambient","Classical","Country","Dance & EDM","Disco"
    ,"Dubstep","Electronic","Folk","Hip Hop & Rap","House","Indie","Jazz","Latin","Metal","Piano","Pop","R&B &Soul","Reggae","Soundtrack",
    "Techno","Trance","Trap","World","Rock"};
    String[] categoryKeys = {"all-music","alternativerock"
            , "ambient" , "classical","country","danceedm"
            ,"disco","dubstep","electronic","folksingersongwriter"
            ,"hiphoprap","house","indie","jazzblues","latin","metal","piano","pop","rbsoul"
            ,"reggae","soundtrack","techno","trance","trap","world","rock"};
    int[] thumbs = {R.drawable.ic_trending,R.drawable.ic_alternative_rock,R.drawable.ic_ambient,
                    R.drawable.ic_classical,R.drawable.ic_country,R.drawable.ic_dem,R.drawable.ic_disco,
                    R.drawable.ic_dubstep,R.drawable.ic_electronic,R.drawable.ic_folk
                    ,R.drawable.ic_hiphop,R.drawable.ic_house,R.drawable.ic_indian,R.drawable.ic_jazz,R.drawable.ic_latin
                    ,R.drawable.ic_metal,R.drawable.ic_piano,R.drawable.ic_pop,R.drawable.ic_rnb,R.drawable.ic_reggae,
                    R.drawable.ic_metal,R.drawable.ic_techno,R.drawable.ic_trance,R.drawable.ic_trap,R.drawable.ic_world,R.drawable.ic_rock};
    public GridViewAdapter(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public int getCount()
    {
        return categories.length;
    }

    @Override
    public Object getItem(int position)
    {
        return categories[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = activity.getLayoutInflater();
        final ViewHolder holder;
        if (convertView != null)
        {
            holder = (ViewHolder) convertView.getTag();
        }
        else
        {
            convertView = inflater.inflate(R.layout.categories_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Picasso.with(activity)
                .load(thumbs[position])
                .placeholder(R.drawable.default_album_mid)
                .resize(width / 3, width / 3)
                .into(holder.ivThumbnail);
        holder.tvTitle.setText(categories[position]);
        return convertView;
    }


    static class ViewHolder
    {
        @Bind(R.id.categories_layout_item_ivThumbnail)
        ImageView ivThumbnail;
        @Bind(R.id.categories_layout_item_tvTitle)
        TextView tvTitle;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }

    public String[] getCategoryKeys()
    {
        return categoryKeys;
    }
}

