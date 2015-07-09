package com.hl.rollingbaby.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.network.MessageService;
import com.hl.rollingbaby.network.StatusService;

public class SoundActivity extends BaseActivity implements
        SoundFragment.OnSoundFragmentInteractionListener{

    private static final String TAG = "SoundActivity";
    private int mPlayState;
    private String mSoundMode;
    private SoundFragment soundFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        if (savedInstanceState == null) {
            soundFragment = SoundFragment.newInstance(
                    Constants.SOUND_STOP, Constants.SOUND_MUSIC);
            soundFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    R.id.sound_container, soundFragment).commit();
        }
        init();
    }

    private void init() {
        initViews();
        getData();
        showContent();
    }

    @Override
    public void initViews() {

    }

    @Override
    public void getData() {

    }

    @Override
    public void showContent() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {

            StatusService.startActionProcessSound(this, mPlayState, mSoundMode);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getPlayState() {
        return mPlayState;
    }

    @Override
    public void savePlayState(int playState) {
//       messageBinder.savePlayState(playState);
    }

    @Override
    public String getSoundMode() {
        return mSoundMode;
    }

    @Override
    public void saveSoundMode(String soundMode) {
//        messageBinder.saveSoundMode(soundMode);
    }
}
