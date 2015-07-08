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
import com.hl.rollingbaby.network.MessageService;

public class TemperatureActivity extends BaseActivity implements
        TemperatureFragment.OnTemperatureFragmentInteractionListener{

    private static final String TAG = "TemperatureActivity";
    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;
    private int temperature;
    private TemperatureFragment temperatureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        temperatureFragment = (TemperatureFragment) getFragmentManager()
                .findFragmentById(R.id.temperature_fragment);
        Intent intent = getIntent();
        temperature = Integer.valueOf(intent.getStringExtra("TEMPERATURE"));
        temperatureFragment.setTemperature(temperature);
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
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_temperature, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getTemperature() {
        Log.d(TAG, temperature + " : getTemperature");
        return temperature;
    }

    @Override
    public void saveTemperature(int temperature) {
//        messageBinder.saveTemperature(temperature);
    }
}
