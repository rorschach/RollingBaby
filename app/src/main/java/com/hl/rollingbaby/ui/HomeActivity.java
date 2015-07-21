package com.hl.rollingbaby.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

    private int currentTemperature;
    private int settingTemperature;
    private String mHeatingState;
    private String mSoundMode;
    private int mPlayState;
    private String mSwingMode;
    private long mExitTime = 0;

//    private UpdateUIReceiver receiver;

    private StatusFragment statusFragment;

    private Handler handler = new Handler(this);

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        geMessageFromServer("t.c.25;sw.c;so.m.1;");//just for test

        Log.d(TAG, "." + currentTemperature + settingTemperature
                + mHeatingState + mSoundMode
                + mPlayState + mSwingMode + ".");
        if (savedInstanceState == null) {
            statusFragment = StatusFragment.newInstance(
                    currentTemperature, settingTemperature, mHeatingState,
                    mSoundMode, mPlayState, mSwingMode);

            getFragmentManager().beginTransaction().add(
                    R.id.root_container, statusFragment).commit();
        }

        mSwipeRefreshWidget =
                (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setColorScheme(R.color.red, R.color.yellow,
                R.color.blue, R.color.green);
        mSwipeRefreshWidget.setOnRefreshListener(this);
        isInActivity = true;
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
//        LocalBroadcastManager broadcastManager =
//                LocalBroadcastManager.getInstance(this);
//        broadcastManager.unregisterReceiver(receiver);
        isInActivity = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

//        LocalBroadcastManager broadcastManager =
//                LocalBroadcastManager.getInstance(this);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(StatusService.ACTION_PROCESS_TEMPERATURE);
//        intentFilter.addAction(StatusService.ACTION_PROCESS_SWING);
//        intentFilter.addAction(StatusService.ACTION_PROCESS_SOUND);
//        receiver = new UpdateUIReceiver();
//        broadcastManager.registerReceiver(receiver, intentFilter);
        isInActivity = true;

//        setCard(currentTemperature, mHeatingState, mSoundMode, mPlayState, mSwingMode);

        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        settingTemperature = statusFragment.getSettingTemperature();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sync:
                sendStatuesToServer();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendStatuesToServer() {
        messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                + settingTemperature + ";"
                + Constants.SWING_TAG + mSwingMode + ";"
                + Constants.SOUND_TAG + mSoundMode + mPlayState + ";\n");
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {

            case Constants.CONNECT_SUCCESS:
                if (!isInActivity) {
                    messageBinder.buildNotification(
                            MessageService.NOTIFICATION_CONNECT_SUCCESS,
                            getResources().getString(R.string.success_title),
                            getResources().getString(R.string.success_content));
                }
                break;

            case Constants.CONNECT_FAILED:
                if (!isInActivity) {
                    messageBinder.buildNotification(
                            MessageService.NOTIFICATION_CONNECT_FAILED,
                            getResources().getString(R.string.fail_title),
                            getResources().getString(R.string.fail_content));
                }else {
                    setDialog();
                }
                break;

            case Constants.MESSAGE_READ:
                showRefreshProgress();
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                geMessageFromServer(readMessage);
                setCard(currentTemperature, settingTemperature, mHeatingState,
                        mSoundMode, mPlayState, mSwingMode);
                if (!isInActivity) {
                    messageBinder.buildNotification(
                            MessageService.NOTIFICATION_READ_MESSAGE,
                            getResources().getString(R.string.read_title),
                            getResources().getString(R.string.read_content));
                }
                break;

            case Constants.MESSAGE_SEND:
//                Object obj = msg.obj;
//                statusFragment.setMessageManager((MessageManager) obj);
//                messageBinder.sendMessage(obj + "");
        }
        return true;
    }

    private void setDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.fail_title));
        builder.setMessage( getResources().getString(R.string.fail_content));
        builder.setPositiveButton(getResources().getString(R.string.position_bt),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.navigation_bt),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
        Log.d(TAG, "setDialog");
    }

    @Override
    public void geMessageFromServer(String readMessage) {

        Log.d(TAG, readMessage);
        try {
            String state[] = readMessage.split(";");
            String A = state[0];//like T.O.25,get the '25'
            String temperatureList[] = A.split("\\.");
            mHeatingState = temperatureList[1];
            currentTemperature = Integer.valueOf(temperatureList[2]);

            String C = state[1];//like SW.S, get the 'S'
            String swingList[] = C.split("\\.");
            mSwingMode = swingList[1];
            String B = state[2];//like SO.M.1,get the 'M' and '1'
            String soundList[] = B.split("\\.");
            mSoundMode = soundList[1];
            mPlayState = Integer.valueOf(soundList[2]);

            if (settingTemperature == 0) {
                settingTemperature = currentTemperature;
            }

            Log.d(TAG, currentTemperature + ":"
                    + settingTemperature + ":"
                    + mHeatingState + ":"
                    + mSoundMode + ":"
                    + mPlayState + ":"
                    + mSwingMode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideRefresh() {
        mSwipeRefreshWidget.setRefreshing(false);
    }

//        public class UpdateUIReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.d("UpdateUIReceiver", "onReceive");
//            if (StatusService.ACTION_PROCESS_TEMPERATURE.equals(action)) {
//                currentTemperature =
//                        intent.getIntExtra(StatusService.EXTRA_TEMPERATURE_VALUE,
//                                Constants.DEFAULT_TEMPERATURE);
//                Log.d(TAG, "onReceive temperature" + currentTemperature);
////                mHeatingState =
////                        intent.getStringExtra(StatusService.EXTRA_HEATING_STATE);
//                Log.d(TAG, "onReceive temperature" + currentTemperature + ":" + mHeatingState);
//                setCard(currentTemperature, mHeatingState, mSoundMode, mPlayState, mSwingMode);
//            }else if (StatusService.ACTION_PROCESS_SWING.equals(action)) {
//
//                mSwingMode =
//                        intent.getStringExtra(StatusService.EXTRA_SWING_MODE);
//                Log.d(TAG, "onReceive swing" + mSwingMode);
//                setCard(currentTemperature, mHeatingState, mSoundMode, mPlayState, mSwingMode);
//            }else if (StatusService.ACTION_PROCESS_SOUND.equals(action)) {
//                mSoundMode =
//                        intent.getStringExtra(StatusService.EXTRA_SOUND_MODE);
//                mPlayState =
//                        intent.getIntExtra(StatusService.EXTRA_PLAY_STATE,
//                                Constants.SOUND_STOP);
//                Log.d(TAG, "onReceive sound" + mPlayState + ":" + mSoundMode);
//            }
//        }
//    }

    public void setCard(int currentTem, int settingTem,  String heatingState,
                        String soundMode, int playState, String swingMode) {

        statusFragment.getCardStatus(currentTem, settingTem, heatingState,
                soundMode, playState, swingMode);
        Log.d(TAG, "setCard " + currentTem + settingTem + heatingState
                + soundMode + playState + swingMode);
    }

    @Override
    public void onRefresh() {
        showRefreshProgress();
        if (messageBinder.getConnectState()) {
            messageBinder.sendMessage(Constants.COMMAND_REFRESH + ";\n");
            settingTemperature = statusFragment.getSettingTemperature();
            setCard(currentTemperature, settingTemperature,
                    mHeatingState, mSoundMode, mPlayState, mSwingMode);
        } else {
            setDialog();
        }
    }

    public void showRefreshProgress() {
        mSwipeRefreshWidget.setRefreshing(true);
    }

//    public void hideRefreshProgress() {
//        mSwipeRefreshWidget.setRefreshing(false);
//    }
//
//    public void enableSwipe() {
//        mSwipeRefreshWidget.setEnabled(true);
//    }
//
//    public void disableSwipe() {
//        mSwipeRefreshWidget.setEnabled(false);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                // 如果两次按键时间间隔大于2000毫秒，则不退出
                Toast.makeText(this, getResources().getString(R.string.back_bt),
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();// 更新mExitTime
            } else {
                System.exit(0);// 否则退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
