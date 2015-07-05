package com.hl.rollingbaby.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.ui.HomeActivity;
import com.hl.rollingbaby.ui.SettingsActivity;

import java.util.ArrayList;

public class MessageService extends Service {

    private static final String TAG = MessageService.class.getSimpleName();
    public static final String CONVERSATION_ID = "conversation_id";

    public static final int NOTIFICATION_CONNECT_FAILED = 0;
    public static final int NOTIFICATION_CONNECT_SUCCESS = 1;
    public static final int NOTIFICATION_SEND_MESSAGE = 2;
    public static final int NOTIFICATION_READ_MESSAGE = 3;

    public static String SERVER_HOST;
    public static int SERVER_PORT;

    private MessageManager messageManager;
    private MessageBinder messageBinder = new MessageBinder();
    private Context context;

    private SharedPreferences dataPreferences;
    private SharedPreferences.Editor dataEditor;

    private int temperature = 25;
    private int playState = 0;
    private String soundMode = Constants.CLOSE;
    private ArrayList<String> status = new ArrayList<>();



    public static final String EOL = "\n";

    public MessageService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = MessageService.this;
        dataPreferences = getSharedPreferences(
                Constants.FILE_NAME, Context.MODE_PRIVATE);
        dataEditor = dataPreferences.edit();

        Log.d(TAG, "MessageService is OnCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "MessageService is onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "MessageService is onBind");
        return messageBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MessageService is onDestroy");
        messageManager.interrupt();
        messageManager.setConnectState(false);
    }



    public void getData() {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String ipPref = pref.getString(SettingsActivity.KEY_IP, "192.168.97.86:8888");
            String ip[] = ipPref.split(":");
            SERVER_HOST = ip[0];
            SERVER_PORT = Integer.valueOf(ip[1]);
            Log.d(TAG, SERVER_HOST + ":" + SERVER_PORT);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public class MessageBinder extends Binder {

        public MessageService getService() {
            return MessageService.this;
        }

        public void startConnect(Handler handler) {
            getData();
            messageManager = new MessageManager(handler, SERVER_HOST, SERVER_PORT);
            messageManager.setConnectState(true);
            messageManager.start();
            Log.d(TAG, "MessageBinder is ConnectToServer");
        }

        public void stopConnect() {
            messageManager.interrupt();
            messageManager.setConnectState(false);
        }

        public void restartConnect(Handler handler) {
            stopConnect();
            //delay
            startConnect(handler);
        }

        public boolean getConnectState() {
            return messageManager.getConnectState();
        }

        public void sendMessage(String message) {
            messageManager.write(message.getBytes());
        }

        public void buildNotification(int requestCode, String title, String content) {

            PendingIntent intent = PendingIntent.getActivity(
                    context, requestCode, new Intent(context, HomeActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager manager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Notification notification = new Notification.Builder(context)
                        .setTicker(TAG)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setContentIntent(intent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .build();
                manager.notify(requestCode, notification);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setTicker(TAG)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(title)
                                .setContentText(content)
                                .setAutoCancel(true)
                                .setContentIntent(intent)
                                .setDefaults(Notification.DEFAULT_ALL);
                manager.notify(requestCode, mBuilder.build());
            }
        }

        public ArrayList<String> getStatusFromSharedPreference() {
            getTemperature();
            getSoundMode();
            getPlayState();
            status.add(temperature + "");
            status.add(soundMode);
            status.add(playState + "");
            Log.d(TAG, status.size() + " : status.size()");

            return status;
        }

        private void setStatusToSharedPreferences() {
            dataEditor.putInt(Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);
            dataEditor.putInt(Constants.CURRENT_HUMIDITY_VALUE, Constants.DEFAULT_HUMIDITY);
            dataEditor.putString(Constants.CURRENT_SOUND_MODE, Constants.CLOSE);
            dataEditor.putString(Constants.CURRENT_SWING_MODE, Constants.CLOSE);
            dataEditor.commit();
        }

        public int getTemperature() {
            if (dataPreferences != null) {
                temperature = dataPreferences.getInt(
                        Constants.CURRENT_TEMPERATURE_VALUE, Constants.DEFAULT_TEMPERATURE);
                return temperature;
            }
            Log.d(TAG, temperature + "");
            return temperature;
        }

        public void saveTemperature(int temperature) {
            if (dataEditor != null) {
                dataEditor.putInt(Constants.CURRENT_TEMPERATURE_VALUE, temperature)
                        .commit();
            }
        }

        public int getPlayState() {
            if (dataPreferences != null) {
                playState =  dataPreferences.getInt(
                        Constants.PLAY_STATE, Constants.SOUND_PAUSE);
                return playState;
            }
            Log.d(TAG, playState + "");
            return playState;
        }

        public void savePlayState(int playState) {
            if (dataEditor != null) {
                dataEditor.putInt(Constants.PLAY_STATE, playState)
                        .commit();
            }
        }

        public String getSoundMode() {
            if (dataPreferences != null) {
                soundMode =  dataPreferences.getString(
                        Constants.CURRENT_SOUND_MODE, Constants.CLOSE);
                return soundMode;
            }
            Log.d(TAG, soundMode);
            return soundMode;
        }

        public void saveSoundMode(String soundMode) {
            if (dataEditor != null) {
                dataEditor.putString(Constants.CURRENT_SOUND_MODE, soundMode)
                        .commit();
            }
        }

    }
}
