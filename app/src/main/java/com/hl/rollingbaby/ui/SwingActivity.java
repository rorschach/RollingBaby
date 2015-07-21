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

public class SwingActivity extends BaseActivity implements ServiceConnection,
        SwingFragment.OnSwingFragmentInteractionListener {

    private static final String TAG = "SwingActivity";

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;
    private SwingFragment swingFragment;

    private int currentTemperature;
    private int settingTemperature;
    private String mHeatingState;
    private String mSoundMode;
    private int mPlayState;
    private String mSwingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swing);
//        Intent intent = getIntent();
//        mSwingMode = intent.getStringExtra(Constants.CURRENT_SWING_MODE);
        getIntentFromHome();

        if (savedInstanceState == null) {
            swingFragment = SwingFragment.newInstance(
                    currentTemperature, settingTemperature, mHeatingState,
                    mSoundMode, mPlayState, mSwingMode);
            swingFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    R.id.swing_container, swingFragment).commit();
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
                mSwingMode = swingFragment.getSwingMode();
                setSwingMode(mSwingMode);
                return true;

            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(Constants.CURRENT_SWING_MODE, mSwingMode);
                setResult(RESULT_OK, intent);
                Log.d(TAG, "onBackPressed : " + mSwingMode);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        messageBinder = (MessageService.MessageBinder) service;
        messageService = messageBinder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(this, "Service disconnected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.CURRENT_SWING_MODE, mSwingMode);
        setResult(RESULT_OK, intent);
        Log.d(TAG, "onBackPressed : " + mSwingMode);
        finish();
    }

    @Override
    public void setSwingMode(String swingMode) {
        messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                + settingTemperature + ";"
                + Constants.SWING_TAG + swingMode + ";"
                + Constants.SOUND_TAG + mSoundMode + mPlayState + ";\n");

    }
}
