package com.hl.rollingbaby.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.network.StatusService;

public class TemperatureActivity extends BaseActivity implements
        TemperatureFragment.OnTemperatureFragmentInteractionListener{

    private static final String TAG = "TemperatureActivity";

    private int mTemperature;
    private String mHeatingState;

    private TemperatureFragment temperatureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        Intent intent = getIntent();
        mTemperature = intent.getIntExtra(
                Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);
        mHeatingState = intent.getStringExtra(Constants.HEATING_STATE);
        Log.d(TAG, mTemperature + ":" + mHeatingState + ":" + 0);

        if (savedInstanceState == null) {
            temperatureFragment = TemperatureFragment.newInstance(
                    mTemperature, mHeatingState);
            temperatureFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    R.id.temperature_container, temperatureFragment).commit();
        }
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
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {

            StatusService.startActionProcessTemperature(this, mTemperature);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getTemperature() {
        Log.d(TAG, mTemperature + " : getTemperature");
        return mTemperature;
    }

    @Override
    public void saveTemperature(int temperature) {
    }

}
