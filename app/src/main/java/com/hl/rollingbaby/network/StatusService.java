package com.hl.rollingbaby.network;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.session.MediaSessionManager;
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

    private static final String ACTION_SEND_BROADCAST =
            "com.hl.rollingbaby.network.action.SEND_BROADCAST";

    /*----------------------------------------------------------------*/

    private static final String EXTRA_PROCESS_TYPE =
            "com.hl.rollingbaby.network.extra.PROCESS_TYPE";

    private static final String EXTRA_TEMPERATURE_VALUE =
            "com.hl.rollingbaby.network.extra.TEMPERATURE_VALUE";

    private static final String EXTRA_PLAY_STATE =
            "com.hl.rollingbaby.network.extra.PLAY_STATE";
    private static final String EXTRA_SOUND_MODE =
            "com.hl.rollingbaby.network.extra.SOUND_MODE";



    private SharedPreferences dataPreferences;
    private SharedPreferences.Editor dataEditor;

    private int mTemperature = 25;

    private String soundState;
    private String mSoundMode = Constants.CLOSE;
    private int mPlayState = 0;

    private ArrayList<String> mStatusList = new ArrayList<>();

    public static void startActionGetTemperature(Context context) {
        Intent intent = new Intent(context, StatusService.class);
        intent.setAction(ACTION_GET_STATUS);
        context.startService(intent);
    }

    public static void startActionProcessTemperature(
            Context context, String type, int temperature) {
        Intent intent = new Intent(context, StatusService.class);
        intent.setAction(ACTION_PROCESS_TEMPERATURE);
        intent.putExtra(EXTRA_PROCESS_TYPE, type);
        intent.putExtra(EXTRA_PLAY_STATE, temperature);
        context.startService(intent);
    }

    public static void startActionProcessSound(
            Context context, String type, int playState, String soundMode) {
        Intent intent = new Intent(context, StatusService.class);
        intent.setAction(ACTION_PROCESS_SOUND);
        intent.putExtra(EXTRA_PROCESS_TYPE, type);
        intent.putExtra(EXTRA_PLAY_STATE, playState);
        intent.putExtra(EXTRA_SOUND_MODE, soundMode);
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
            if (ACTION_PROCESS_TEMPERATURE.equals(action)) {
                final String type = intent.getStringExtra(EXTRA_PROCESS_TYPE);
                final int temperature = intent.getIntExtra(EXTRA_TEMPERATURE_VALUE, 25);
                handleActionProcessTemperature(type, temperature);
            } else if (ACTION_PROCESS_SOUND.equals(action)) {
                final String type = intent.getStringExtra(EXTRA_PROCESS_TYPE);
                final int playState = intent.getIntExtra(EXTRA_PLAY_STATE, 1);
                final String soundMode = intent.getStringExtra(EXTRA_SOUND_MODE);
                handleActionProcessSound(type, playState, soundMode);
            }else if (ACTION_GET_STATUS.equals(action)) {
                handleActionGetStatus();
            }
        }
    }

    private void handleActionGetStatus() {

        mTemperature = dataPreferences.getInt(
                Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);

        mPlayState = dataPreferences.getInt(
                Constants.PLAY_STATE, Constants.SOUND_PAUSE);

        mSoundMode = dataPreferences.getString(
                Constants.CURRENT_SOUND_MODE, Constants.CLOSE);

        Intent intent = new Intent(Constants.UPDATE_UI_BROADCAST);
        intent.setAction(Constants.UPDATE_UI_BROADCAST);
        intent.putExtra(EXTRA_TEMPERATURE_VALUE, mTemperature);
        intent.putExtra(EXTRA_PLAY_STATE, mPlayState);
        intent.putExtra(EXTRA_SOUND_MODE, mSoundMode);
        sendBroadcast(intent);
        Log.d(TAG, mTemperature + ":" + mPlayState + ":" + mSoundMode);

    }

    private void handleActionProcessTemperature(String type, int temperatureValue) {
        if (type.equals(Constants.GET)) {
            mTemperature = dataPreferences.getInt(
                    Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);

        }else if (type.equals(Constants.SET)) {
            dataEditor.putInt(Constants.CURRENT_TEMPERATURE_VALUE, temperatureValue)
                    .commit();
            mTemperature = dataPreferences.getInt(
                    Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);
        }
        Log.d(TAG, type + ":" + mTemperature);
    }

    private void handleActionProcessSound(String type, int playState, String soundMode) {
        if (type.equals(Constants.GET)) {

            mPlayState = dataPreferences.getInt(
                    Constants.PLAY_STATE, Constants.SOUND_PAUSE);

            mSoundMode = dataPreferences.getString(
                    Constants.CURRENT_SOUND_MODE, Constants.CLOSE);

        }else if (type.equals(Constants.SET)) {

            //TODO:send message to server then save it in SharedPreferences file

            dataEditor.putInt(Constants.PLAY_STATE, playState)
                    .commit();

            dataEditor.putString(Constants.CURRENT_SOUND_MODE, soundMode)
                    .commit();

            mPlayState = dataPreferences.getInt(
                    Constants.PLAY_STATE, Constants.SOUND_PAUSE);

            mSoundMode = dataPreferences.getString(
                    Constants.CURRENT_SOUND_MODE, Constants.CLOSE);
        }

        soundState = mSoundMode + "/" + mPlayState;
        Log.d(TAG,type + ":" + soundState);
    }




}
