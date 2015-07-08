package com.hl.rollingbaby.network;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.session.MediaSessionManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hl.rollingbaby.bean.Constants;

import java.util.ArrayList;

public class StatusService extends IntentService {

    private static final String TAG = "StatusService";

    private static final String ACTION_GET_STATUS =
            "com.hl.rollingbaby.network.action.GET_STATUS";

    private static final String ACTION_PROCESS_TEMPERATURE =
            "com.hl.rollingbaby.network.action.PROCESS_TEMPERATURE";

    private static final String ACTION_PROCESS_SOUND =
            "com.hl.rollingbaby.network.action.PROCESS_SOUND";

    private static final String ACTION_PROCESS_SWING =
            "com.hl.rollingbaby.network.action.PROCESS_SWING";

    public static final String ACTION_UPDATE_UI =
            "com.hl.rollingbaby.network.action.UPDATE_UI";

    /*----------------------------------------------------------------*/

    public static final String EXTRA_TEMPERATURE_VALUE =
            "com.hl.rollingbaby.network.extra.TEMPERATURE_VALUE";

    public static final String EXTRA_PLAY_STATE =
            "com.hl.rollingbaby.network.extra.PLAY_STATE";

    public static final String EXTRA_SOUND_MODE =
            "com.hl.rollingbaby.network.extra.SOUND_MODE";

    public static final String EXTRA_SWING_MODE =
            "com.hl.rollingbaby.network.extra.SWING_MODE";

    private SharedPreferences dataPreferences;
    private SharedPreferences.Editor dataEditor;

    private int mTemperature;

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

        mTemperature = dataPreferences.getInt(
                Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);

//        mHumidity = dataPreferences.getInt(
//                Constants.CURRENT_HUMIDITY_VALUE, Constants.DEFAULT_HUMIDITY);

        mPlayState = dataPreferences.getInt(
                Constants.PLAY_STATE, Constants.SOUND_PAUSE);

        mSoundMode = dataPreferences.getString(
                Constants.CURRENT_SOUND_MODE, Constants.CLOSE);

        mSwingMode = dataPreferences.getString(
                Constants.CURRENT_SWING_MODE, Constants.CLOSE);


        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_UI);
        intent.putExtra(EXTRA_TEMPERATURE_VALUE, mTemperature);
        intent.putExtra(EXTRA_PLAY_STATE, mPlayState);
        intent.putExtra(EXTRA_SOUND_MODE, mSoundMode);
        intent.putExtra(EXTRA_SWING_MODE, mSwingMode);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleActionProcessTemperature(int temperatureValue) {

        //TODO:send message to server then save it in SharedPreferences file

        dataEditor.putInt(Constants.CURRENT_TEMPERATURE_VALUE, temperatureValue)
                .commit();
//        mTemperature = dataPreferences.getInt(
//                Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);
    }

    private void handleActionProcessSound(int playState, String soundMode) {

        //TODO:send message to server then save it in SharedPreferences file

        dataEditor.putInt(Constants.PLAY_STATE, playState)
                .commit();
        dataEditor.putString(Constants.CURRENT_SOUND_MODE, soundMode)
                .commit();
//        mPlayState = dataPreferences.getInt(
//                Constants.PLAY_STATE, Constants.SOUND_PAUSE);
//        mSoundMode = dataPreferences.getString(
//                Constants.CURRENT_SOUND_MODE, Constants.CLOSE);
    }

    private void handleActionProcessSwing(String swingMode) {

        //TODO:send message to server then save it in SharedPreferences file

        dataEditor.putString(Constants.CURRENT_SWING_MODE, swingMode)
                .commit();
//        mSwingMode = dataPreferences.getString(
//                Constants.CURRENT_SWING_MODE, Constants.CLOSE);
    }




}
