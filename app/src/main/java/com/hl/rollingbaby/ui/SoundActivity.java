package com.hl.rollingbaby.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.network.MessageService;

public class SoundActivity extends BaseActivity implements ServiceConnection,
        SoundFragment.OnSoundFragmentInteractionListener{

    private static final String TAG = "SoundActivity";
    private SoundFragment soundFragment;

    private int currentTemperature;
    private int settingTemperature;
    private String mHeatingState;
    private String mSoundMode;
    private int mPlayState;
    private String mSwingMode;

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

//        Intent intent = getIntent();
//        mSoundMode = intent.getStringExtra(Constants.CURRENT_SOUND_MODE);
//        mPlayState = intent.getIntExtra(Constants.PLAY_STATE, Constants.SOUND_STOP);

        getIntentFromHome();

        if (savedInstanceState == null) {
            soundFragment = SoundFragment.newInstance(
                    currentTemperature, settingTemperature,
                    mHeatingState, mSoundMode, mPlayState, mSwingMode);
            soundFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    R.id.sound_container, soundFragment).commit();
        }
        init();
    }

    private void getIntentFromHome() {
        Intent intent = getIntent();
        currentTemperature = intent.getIntExtra(
                Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);
        settingTemperature = intent.getIntExtra(
                Constants.SETTING_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);
        mHeatingState = intent.getStringExtra(Constants.HEATING_STATE);
        mSoundMode = intent.getStringExtra(Constants.CURRENT_SOUND_MODE);
        mPlayState = intent.getIntExtra(Constants.PLAY_STATE, Constants.SOUND_STOP);
        mSwingMode = intent.getStringExtra(Constants.CURRENT_SWING_MODE);
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
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageService != null) {
            unbindService(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                mPlayState = soundFragment.getPlayState();
                mSoundMode = soundFragment.getSoundMode();
//                setSoundState(mPlayState, mSoundMode);
                return true;

            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(Constants.CURRENT_SOUND_MODE, mSoundMode);
                intent.putExtra(Constants.PLAY_STATE, mPlayState);
                setResult(RESULT_OK, intent);
                Log.d(TAG, "onBackPressed : " + mSoundMode + mPlayState);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        messageBinder = (MessageService.MessageBinder) service;
        messageService = messageBinder.getService();
//        messageBinder.sendMessage(Constants.COMMAND_REFRESH);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(this, "Service disconnected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        mPlayState = soundFragment.getPlayState();
        mSoundMode = soundFragment.getSoundMode();
        Intent intent = new Intent();
        intent.putExtra(Constants.CURRENT_SOUND_MODE, mSoundMode);
        intent.putExtra(Constants.PLAY_STATE, mPlayState);
        setResult(RESULT_OK, intent);
        Log.d(TAG, "onBackPressed : " + mSoundMode + mPlayState);
        finish();
    }

    public void setSoundState(int playState, String soundMode){
        messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                + settingTemperature + ";"
                + Constants.SWING_TAG + mSwingMode + ";"
                + Constants.SOUND_TAG + soundMode + playState + ";\n");
    }
}
