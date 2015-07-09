package com.hl.rollingbaby.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.bean.MessageTarget;
import com.hl.rollingbaby.network.MessageService;
import com.hl.rollingbaby.network.StatusService;


public class HomeActivity extends BaseActivity implements
        MessageTarget, Handler.Callback, ServiceConnection,
        SwipeRefreshLayout.OnRefreshListener,
        StatusFragment.OnStatusFragmentInteractionListener{

    private SwipeRefreshLayout mSwipeRefreshWidget;

    private static final String TAG = "HomeActivity";
    private boolean isInActivity = false;

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;

    private UpdateUIReceiver receiver;

    private StatusFragment statusFragment;

    private Handler handler = new Handler(this);

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {

            case Constants.CONNECT_SUCCESS:
                if (!isInActivity) {
                    messageBinder.buildNotification(
                            MessageService.NOTIFICATION_CONNECT_SUCCESS,
                            "CONNECT_SUCCESS", "CONNECT_SUCCESS");
                }else {
                    Log.d(TAG, String.valueOf(isInActivity));
                }
                break;

            case Constants.CONNECT_FAILED:
                if (!isInActivity) {
                    messageBinder.buildNotification(
                            MessageService.NOTIFICATION_CONNECT_FAILED,
                            "CONNECT_FAIL", "CONNECT_FAIL");
                }else {
                    Log.d(TAG, String.valueOf(isInActivity));
                }
                break;

            case Constants.MESSAGE_READ:
                if (!isInActivity) {
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    geMessageFromServer(readMessage);
                    messageBinder.buildNotification(
                            MessageService.NOTIFICATION_READ_MESSAGE,
                            "READ", readMessage);
                }else {
                    Log.d(TAG, String.valueOf(isInActivity));
                }
                break;

            case Constants.MESSAGE_SEND:
                Object obj = msg.obj;
//                statusFragment.setMessageManager((MessageManager) obj);
//                messageBinder.sendMessage(obj + "");
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSwipeRefreshWidget =
                (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorScheme(R.color.red, R.color.yellow,
                R.color.blue, R.color.green);
        mSwipeRefreshWidget.setOnRefreshListener(this);

        isInActivity = true;
        Log.d(TAG, String.valueOf(isInActivity));

        if (savedInstanceState == null) {
            statusFragment = StatusFragment.newInstance(
                    Constants.DEFAULT_TEMPERATURE,
                    Constants.CLOSE,
                    Constants.SOUND_MUSIC,
                    Constants.SOUND_STOP,
                    Constants.SWING_SLLEP);
            statusFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    R.id.root_container, statusFragment).commit();
        }

    }

    public void initViews() {
    }

    public void getData() {
    }

    public void showContent() {
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager broadcastManager =
                LocalBroadcastManager.getInstance(this);
        broadcastManager.unregisterReceiver(receiver);

        isInActivity = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, this, BIND_AUTO_CREATE);

        LocalBroadcastManager broadcastManager =
                LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(StatusService.ACTION_UPDATE_MAIN_UI);
        receiver = new UpdateUIReceiver();
        broadcastManager.registerReceiver(receiver, intentFilter);

        isInActivity = true;
    }

    public class UpdateUIReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("UpdateUIReceiver", "onReceive");
            if (StatusService.ACTION_UPDATE_MAIN_UI.equals(action)) {

                final int temperature =
                        intent.getIntExtra(StatusService.EXTRA_TEMPERATURE_VALUE,36);
                final String heatingState =
                        intent.getStringExtra(StatusService.EXTRA_HEATING_STATE);

                final String soundMode =
                        intent.getStringExtra(StatusService.EXTRA_SOUND_MODE);
                final int playState =
                        intent.getIntExtra(StatusService.EXTRA_PLAY_STATE, 0);

                final String swingMode =
                        intent.getStringExtra(StatusService.EXTRA_SWING_MODE);

                setCard(temperature, heatingState, soundMode, playState, swingMode);
            }
        }
    }

    public void setCard(int temperature, String heatingState, String soundMode, int playState, String swingMode) {
        statusFragment.getCardStatus(
                temperature, heatingState, soundMode, playState, swingMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageService != null) {
            unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        messageBinder = (MessageService.MessageBinder) service;
        messageService = messageBinder.getService();
        messageBinder.startConnect(((MessageTarget) this).getHandler());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(this, "Service disconnected", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void geMessageFromServer(String readMessage) {
        Log.d(TAG, "is in getMachineState : " + readMessage);

        try {
            String state[] = readMessage.split(":");
            //TODO:process status and display it in UI
            String temperature = state[0];//like TO25,get the '25'
            String sound = state[1];//like SOM1,get the 'M' and '1'
            String swing = state[2];//like SWS, get the 'S'
            //setCard(25, Music, Stop, Sleep);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        messageBinder.sendMessage(message);
    }

    @Override
    public void onRefresh() {

        if (messageBinder.getConnectState()) {
            // TODO:get data from server and update UI
            messageBinder.sendMessage(Constants.COMMAND_REFRESH + "\n");

        } else {
            // TODO:get data from SharedPreferences and update UI
            StatusService.startActionGetStatus(this);
        }
        Toast.makeText(this, "refresh done", Toast.LENGTH_SHORT).show();
        hideRefreshProgress();
    }

    public void showRefreshProgress() {
        mSwipeRefreshWidget.setRefreshing(true);
    }

    public void hideRefreshProgress() {
        mSwipeRefreshWidget.setRefreshing(false);
    }

    public void enableSwipe() {
        mSwipeRefreshWidget.setEnabled(true);
    }

    public void disableSwipe() {
        mSwipeRefreshWidget.setEnabled(false);
    }


}
