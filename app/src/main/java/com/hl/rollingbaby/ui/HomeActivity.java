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
import com.hl.rollingbaby.network.MessageManager;
import com.hl.rollingbaby.network.MessageService;
import com.hl.rollingbaby.network.StatusService;

import java.util.ArrayList;


public class HomeActivity extends BaseActivity implements
        MessageTarget, Handler.Callback, ServiceConnection,
        SwipeRefreshLayout.OnRefreshListener ,
        StatusFragment.OnStatusFragmentInteractionListener{

    private SwipeRefreshLayout mSwipeRefreshWidget;

    private static final String TAG = "HomeActivity";
    private boolean isInActivity = false;
    private ArrayList<String> list = new ArrayList<>();

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;

    private UpdateUIReceiver receiver;


    private StatusFragment statusFragment = new StatusFragment();

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
                statusFragment.setMessageManager((MessageManager) obj);
//                messageBinder.SendMessage(obj + "");
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorScheme(R.color.red, R.color.yellow,
                R.color.blue, R.color.green);
        mSwipeRefreshWidget.setOnRefreshListener(this);

        isInActivity = true;
        Log.d(TAG, String.valueOf(isInActivity));

        init();
    }

    public void init(){
        initViews();
        getData();
        showContent();
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
        isInActivity = false;
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        isInActivity = true;

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(Constants.UPDATE_UI_BROADCAST);

        receiver = new UpdateUIReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
            }
        };

        broadcastManager.registerReceiver(receiver, intentFilter);
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
//        list = messageBinder.getStatusFromSharedPreference();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<String> getStateFromSP() {
//        list = messageBinder.getStatusFromSharedPreference();
        Log.d(TAG, list.size() + " : list.size()");
        return list;
    }

    public void sendMessage(String message) {
        messageBinder.sendMessage(message);
    }

    @Override
    public void onRefresh() {
        // TODO:Write your logic here
//        if (messageBinder.getConnectState()) {
//            sendMessage(Constants.COMMAND_REFRESH);
//        } else {
//            StatusService.startActionProcessTemperature(this, Constants.GET, 0);
//            StatusService.startActionProcessSound(this, Constants.GET, 0, "");
            StatusService.startActionGetTemperature(this);
            hideRefreshProgress();
//        }
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

    public class UpdateUIReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
            String action = intent.getAction();

            if (Constants.UPDATE_UI_BROADCAST.equals(action)) {
                //TODO:update UI
//                final String type = intent.getStringExtra(StatusService.EXTRA_PROCESS_TYPE);
//                final int playState = intent.getIntExtra(EXTRA_PLAY_STATE, 1);
//                final String soundMode = intent.getStringExtra(EXTRA_SOUND_MODE);

                Toast.makeText(context, action, Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(context, action, Toast.LENGTH_SHORT).show();

        }
    }


}
