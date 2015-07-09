package com.hl.rollingbaby.network;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.hl.rollingbaby.bean.Constants;

public class StatusService extends IntentService implements ServiceConnection{

    private static final String TAG = "StatusService";

    private static final String ACTION_GET_STATUS =
            "com.hl.rollingbaby.network.action.GET_STATUS";

    private static final String ACTION_PROCESS_TEMPERATURE =
            "com.hl.rollingbaby.network.action.PROCESS_TEMPERATURE";

    private static final String ACTION_PROCESS_SOUND =
            "com.hl.rollingbaby.network.action.PROCESS_SOUND";

    private static final String ACTION_PROCESS_SWING =
            "com.hl.rollingbaby.network.action.PROCESS_SWING";

    public static final String ACTION_UPDATE_MAIN_UI =
            "com.hl.rollingbaby.network.action.UPDATE_MAIN_UI";

    /*----------------------------------------------------------------*/

    public static final String EXTRA_TEMPERATURE_VALUE =
            "com.hl.rollingbaby.network.extra.TEMPERATURE_VALUE";

    public static final String EXTRA_HEATING_STATE =
            "com.hl.rollingbaby.network.extra.HEATING_STATE";

    public static final String EXTRA_PLAY_STATE =
            "com.hl.rollingbaby.network.extra.PLAY_STATE";

    public static final String EXTRA_SOUND_MODE =
            "com.hl.rollingbaby.network.extra.SOUND_MODE";

    public static final String EXTRA_SWING_MODE =
            "com.hl.rollingbaby.network.extra.SWING_MODE";

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;

    private SharedPreferences dataPreferences;
    private SharedPreferences.Editor dataEditor;

    private int mTemperature;
    private String mHeatingState;

    private String mSoundMode;
    private int mPlayState;

    private String mSwingMode;


    public static void startActionGetStatus(Context context) {
        Intent intent = new Intent(context, StatusService.class);
        intent.setAction(ACTION_GET_STATUS);
        context.startService(intent);
    }

    public static void startActionProcessTemperature(
            Context context, int temperature) {
        Intent intent = new Intent(context, StatusService.class);
        intent.setAction(ACTION_PROCESS_TEMPERATURE);
        intent.putExtra(EXTRA_PLAY_STATE, temperature);
        context.startService(intent);
    }

    public static void startActionProcessSound(
            Context context, int playState, String soundMode) {
        Intent intent = new Intent(context, StatusService.class);
        intent.setAction(ACTION_PROCESS_SOUND);
        intent.putExtra(EXTRA_PLAY_STATE, playState);
        intent.putExtra(EXTRA_SOUND_MODE, soundMode);
        context.startService(intent);
    }

    public static void startActionProcessSwing(
            Context context, String swingMode) {
        Intent intent = new Intent(context, StatusService.class);
        intent.setAction(ACTION_PROCESS_SWING);
        intent.putExtra(EXTRA_SWING_MODE, swingMode);
        context.startService(intent);
    }

    public StatusService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent connectIntent = new Intent(this, MessageService.class);
        bindService(connectIntent, this, BIND_AUTO_CREATE);
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageService != null) {
            unbindService(this);
        }
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
    protected void onHandleIntent(Intent intent) {

        dataPreferences = getSharedPreferences(
                Constants.FILE_NAME, Context.MODE_PRIVATE);
        dataEditor = dataPreferences.edit();

        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_GET_STATUS.equals(action)) {
                handleActionGetStatus();

            } else if (ACTION_PROCESS_TEMPERATURE.equals(action)) {
                final int temperature = intent.getIntExtra(EXTRA_TEMPERATURE_VALUE, 25);
                handleActionProcessTemperature(temperature);

            } else if (ACTION_PROCESS_SOUND.equals(action)) {
                final int playState = intent.getIntExtra(EXTRA_PLAY_STATE, 1);
                final String soundMode = intent.getStringExtra(EXTRA_SOUND_MODE);
                handleActionProcessSound(playState, soundMode);

            }else if (ACTION_PROCESS_SWING.equals(action)) {
                final String swingMode = intent.getStringExtra(EXTRA_SWING_MODE);
                handleActionProcessSwing(swingMode);
            }
        }
    }

    private void handleActionGetStatus() {

        //get data from sharedPreferences file

        mTemperature = dataPreferences.getInt(
                Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);

        mHeatingState = dataPreferences.getString(
                Constants.HEATING_STATE, Constants.CLOSE);

        mPlayState = dataPreferences.getInt(
                Constants.PLAY_STATE, Constants.SOUND_STOP);

        mSoundMode = dataPreferences.getString(
                Constants.CURRENT_SOUND_MODE, Constants.CLOSE);

        mSwingMode = dataPreferences.getString(
                Constants.CURRENT_SWING_MODE, Constants.CLOSE);


        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_MAIN_UI);
        intent.putExtra(EXTRA_TEMPERATURE_VALUE, mTemperature);
        intent.putExtra(EXTRA_HEATING_STATE, mHeatingState);
        intent.putExtra(EXTRA_PLAY_STATE, mPlayState);
        intent.putExtra(EXTRA_SOUND_MODE, mSoundMode);
        intent.putExtra(EXTRA_SWING_MODE, mSwingMode);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleActionProcessTemperature(int temperatureValue) {

        //TODO:send message to server then save it in SharedPreferences file
        sendMessageToServer(Constants.TEMPERATURE_TAG + temperatureValue);

        dataEditor.putInt(Constants.CURRENT_TEMPERATURE_VALUE, temperatureValue)
                .commit();
//        mTemperature = dataPreferences.getInt(
//                Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);

        //TODO:update UI to notice user
    }

    private void handleActionProcessSound(int playState, String soundMode) {

        //TODO:send message to server then save it in SharedPreferences file
        sendMessageToServer(Constants.SOUND_TAG + soundMode + playState);

        dataEditor.putInt(Constants.PLAY_STATE, playState)
                .commit();
        dataEditor.putString(Constants.CURRENT_SOUND_MODE, soundMode)
                .commit();
//        mPlayState = dataPreferences.getInt(
//                Constants.PLAY_STATE, Constants.SOUND_PAUSE);
//        mSoundMode = dataPreferences.getString(

//                Constants.CURRENT_SOUND_MODE, Constants.CLOSE);
        //TODO:update UI to notice user
    }

    private void handleActionProcessSwing(String swingMode) {

        //TODO:send message to server then save it in SharedPreferences file
        sendMessageToServer(Constants.SWING_TAG + swingMode);

        dataEditor.putString(Constants.CURRENT_SWING_MODE, swingMode)
                .commit();
//        mSwingMode = dataPreferences.getString(
//                Constants.CURRENT_SWING_MODE, Constants.CLOSE);

        //TODO:update UI to notice user
    }

    private void sendMessageToServer(String message) {
        messageBinder.sendMessage(message);
    }

    private void test() {
        Log.d(TAG, "test");
        if (messageBinder != null) {
            messageBinder.buildNotification(
                    MessageService.NOTIFICATION_TEST,
                    "TEST", "TEST");
        }
    }


}
