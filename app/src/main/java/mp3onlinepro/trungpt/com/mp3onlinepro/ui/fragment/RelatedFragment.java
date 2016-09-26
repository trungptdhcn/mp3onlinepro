package mp3onlinepro.trungpt.com.mp3onlinepro.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import mp3onlinepro.trungpt.com.mp3onlinepro.R;
import mp3onlinepro.trungpt.com.mp3onlinepro.base.BaseFragment;
import mp3onlinepro.trungpt.com.mp3onlinepro.event.PlayerPreparedEvent;
import mp3onlinepro.trungpt.com.mp3onlinepro.ui.model.Song;

/**
 * Created by TRUNGPT on 8/11/16.
 */
public class RelatedFragment extends BaseFragment
{
    @Bind(R.id.fragment_information_tvDescription)
    TextView tvDescription;
    Song song;
    @Override
    public int getLayout()
    {
        return R.layout.fragment_information;
    }

    @Override
    public void setDataToView(Bundle savedInstanceState)
    {

    }

    public void onEventMainThread(PlayerPreparedEvent event)
    {
        tvDescription.setText(song.getDescription());
    }
}
