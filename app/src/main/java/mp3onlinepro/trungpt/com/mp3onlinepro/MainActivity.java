package mp3onlinepro.trungpt.com.mp3onlinepro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.analytics.Tracker;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import mp3onlinepro.trungpt.com.mp3onlinepro.base.StringUtils;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.RestfulService;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.dto.CategoriesDTO;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.dto.TrackDTO;
import mp3onlinepro.trungpt.com.mp3onlinepro.network.dto.TrackDetailDTO;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.GridViewAdapter;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.activity.PlaylistActivity;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.activity.SongDetailsActivity;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.adapter.ListViewAdapter;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.customview.EndlessScrollListener;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.model.Song;
import mp3onlinepro.trungpt.com.mp3onlinepro.utils.Const;
import mp3onlinepro.trungpt.com.mp3onlinepro.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener
{

    @Bind(R.id.activity_main_gridview)
    GridViewWithHeaderAndFooter gridView;
    @Bind(R.id.activity_main_listview)
    ListView listView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.search_view)
    MaterialSearchView searchView;
    @Bind(R.id.activity_main_progressbar)
    ProgressBar progressBar;
    GridViewAdapter adapter;
    ListViewAdapter listSearchAdapter;

    private int offset = 0;
    private boolean isNexPage;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
        MobileAds.initialize(getApplicationContext(), getString(R.string.app_id));
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(this);
        View header = getLayoutInflater().inflate(R.layout.native_ads, gridView, false);
        NativeExpressAdView nativeExpressAdView = (NativeExpressAdView)header.findViewById(R.id.nativeAdView);
        nativeExpressAdView.loadAd(new AdRequest.Builder().build());
        gridView.addHeaderView(header, null, false);
        adapter = new GridViewAdapter(MainActivity.this);
        gridView.setAdapter(adapter);

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @OnItemClick(R.id.activity_main_gridview)
    public void songDetails(int position)
    {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra("categoryKey", adapter.getCategoryKeys()[position]);
        startActivity(intent);
    }


    @Override
    public boolean onQueryTextSubmit(final String query)
    {
        gridView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        toolbar.setTitle(query);
        if (listSearchAdapter != null)
        {
            listSearchAdapter.getData().clear();
            listSearchAdapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.VISIBLE);
        RestfulService.getInstance(false).search(query, offset, Const.LIMIT, new Callback<List<TrackDetailDTO>>()
        {
            @Override
            public void success(List<TrackDetailDTO> trackDetailDTOs, Response response)
            {
                List<Song> songs = new ArrayList<Song>();
                for (TrackDetailDTO trackDetailDTO : trackDetailDTOs)
                {
                    songs.add(Song.convertFromSongDTO(trackDetailDTO));
                }
                isNexPage = songs.size() < Const.LIMIT ? false : true;
                offset = offset + Const.LIMIT;
                listSearchAdapter = new ListViewAdapter(MainActivity.this, songs);
                listView.setAdapter(listSearchAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error)
            {
                progressBar.setVisibility(View.GONE);
            }
        });
        EndlessScrollListener endlessScrollListener = new EndlessScrollListener()
        {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount)
            {
                progressBar.setVisibility(View.VISIBLE);
                if (isNexPage)
                {
                    RestfulService.getInstance(false).search(query, offset, Const.LIMIT, new Callback<List<TrackDetailDTO>>()
                    {
                        @Override
                        public void success(List<TrackDetailDTO> trackDetailDTOs, Response response)
                        {
                            List<Song> songs = new ArrayList<Song>();
                            for (TrackDetailDTO trackDetailDTO : trackDetailDTOs)
                            {
                                songs.add(Song.convertFromSongDTO(trackDetailDTO));
                            }
                            isNexPage = songs.size() < Const.LIMIT ? false : true;
                            if (listSearchAdapter == null)
                            {
                                listSearchAdapter = new ListViewAdapter(MainActivity.this, songs);
                                listView.setAdapter(listSearchAdapter);
                            }
                            else
                            {
                                listSearchAdapter.getData().addAll(songs);
                                listSearchAdapter.notifyDataSetChanged();
                            }
                            progressBar.setVisibility(View.GONE);
                            offset = offset + Const.LIMIT;
                        }

                        @Override
                        public void failure(RetrofitError error)
                        {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                }
                return true;
            }
        };
        listView.setOnScrollListener(endlessScrollListener);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        return false;
    }

    @Override
    public void onSearchViewShown()
    {

    }

    @Override
    public void onSearchViewClosed()
    {

    }

    @Override
    public void onBackPressed()
    {
        toolbar.setTitle("");
        if (searchView.isSearchOpen())
        {
            searchView.closeSearch();
        }
        else if (listView.getVisibility() == View.VISIBLE)
        {
            listView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }
        else
        {
            super.onBackPressed();
        }
    }
    @OnItemClick(R.id.activity_main_listview)
    public void choseSearchSong(int position)
    {
        List<Song> songs = new ArrayList<Song>();
        Song song = listSearchAdapter.getData().get(position);
        songs.add(song);
        ((MyApplication)getApplicationContext()).getMusicService().getSongs().clear();
        ((MyApplication)getApplicationContext()).getMusicService().setSongs(songs);
        ((MyApplication) getApplicationContext()).getMusicService().setSongIndex(0);
        try
        {
            ((MyApplication) getApplicationContext()).getMusicService().play();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, SongDetailsActivity.class);
        intent.putExtra("song", song);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }
}
