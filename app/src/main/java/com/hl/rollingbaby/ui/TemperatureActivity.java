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

    private int temp;

    private TemperatureFragment temperatureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        getIntentFromHomeActivity();

        if (savedInstanceState == null) {
            temperatureFragment = TemperatureFragment.newInstance(
                    mTemperature, mHeatingState, mSoundMode, mPlayState, mSwingMode);
            temperatureFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    R.id.temperature_container, temperatureFragment).commit();
        }
        temp = mTemperature;
    }

    private void getIntentFromHomeActivity() {
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

        switch (item.getItemId()) {
            case R.id.action_sync:
                mTemperature = temperatureFragment.getTemperatureState();
                setTemperatureState(mTemperature);
                if (mTemperature > temp) {
                    mHeatingState = Constants.HEATING_OPEN;
                } else {
                    mHeatingState = Constants.HEATING_CLOSE;
                }
                Log.d(TAG, "mHeatingState is " + mHeatingState);
                return true;

            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(Constants.CURRENT_TEMPERATURE_VALUE, mTemperature);
                intent.putExtra(Constants.HEATING_STATE, mHeatingState);
                setResult(RESULT_OK, intent);
                Log.d(TAG, "onBackPressed : " + mTemperature);
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.CURRENT_TEMPERATURE_VALUE, mTemperature);
        intent.putExtra(Constants.HEATING_STATE, mHeatingState);
        setResult(RESULT_OK, intent);
        Log.d(TAG, "onBackPressed : " + mTemperature);
        finish();
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
