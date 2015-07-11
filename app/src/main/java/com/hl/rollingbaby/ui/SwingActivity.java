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
import com.hl.rollingbaby.network.StatusService;

public class SwingActivity extends BaseActivity implements ServiceConnection,
        SwingFragment.OnSwingFragmentInteractionListener {

    private static final String TAG = "SwingActivity";

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;
    private SwingFragment swingFragment;

    private String mSwingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swing);

        Intent intent = getIntent();
        mSwingMode = intent.getStringExtra(Constants.CURRENT_SWING_MODE);

        if (savedInstanceState == null) {
            swingFragment = SwingFragment.newInstance(mSwingMode);
            swingFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    R.id.swing_container, swingFragment).commit();
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
            messageBinder.sendMessage(mSwingMode + "\n");

            return true;
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
    public void setSwingMode(String soundMode) {
        messageBinder.sendMessage(Constants.COMMAND_TAG + Constants.COMMAND_EXECUTE
                + Constants.SWING_TAG + soundMode);

    }
}
