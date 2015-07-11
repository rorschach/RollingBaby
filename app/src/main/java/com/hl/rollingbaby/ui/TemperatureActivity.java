package com.hl.rollingbaby.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.bean.MessageTarget;
import com.hl.rollingbaby.network.MessageService;
import com.hl.rollingbaby.network.StatusService;

public class TemperatureActivity extends BaseActivity implements ServiceConnection,
        TemperatureFragment.OnTemperatureFragmentInteractionListener{

    private static final String TAG = "TemperatureActivity";

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;

    private int mTemperature;
    private String mHeatingState;
    private String mSoundMode;
    private int mPlayState;
    private String mSwingMode;

    private TemperatureFragment temperatureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        getIntentFromHome();

        if (savedInstanceState == null) {
            temperatureFragment = TemperatureFragment.newInstance(
                    mTemperature, mHeatingState, mSoundMode, mPlayState, mSwingMode);
            temperatureFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    R.id.temperature_container, temperatureFragment).commit();
        }
    }

    private void getIntentFromHome() {
        Intent intent = getIntent();
        mTemperature = intent.getIntExtra(
                Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);
        mHeatingState = intent.getStringExtra(Constants.HEATING_STATE);
        mSoundMode = intent.getStringExtra(Constants.CURRENT_SOUND_MODE);
        mPlayState = intent.getIntExtra(Constants.PLAY_STATE, Constants.SOUND_STOP);
        mSwingMode = intent.getStringExtra(Constants.CURRENT_SWING_MODE);
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
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
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
        int id = item.getItemId();
        if (id == R.id.action_sync) {

//            StatusService.startActionProcessTemperature(this, mTemperature);
            setTemperatureState(mTemperature);

            return true;
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
    public void setTemperatureState(int temperature) {
        messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                + temperature + ";"
                + Constants.SWING_TAG + mSwingMode + ";"
                + Constants.SOUND_TAG + mSoundMode + mPlayState + ";\n");
    }

}
