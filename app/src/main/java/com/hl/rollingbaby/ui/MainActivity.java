package com.hl.rollingbaby.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.bean.ItemData;
import com.hl.rollingbaby.bean.MessageTarget;
import com.hl.rollingbaby.network.MessageService;
import com.race604.flyrefresh.FlyRefreshLayout;
import com.hl.rollingbaby.R;
import com.hl.rollingbaby.views.SampleItemAnimator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        MessageTarget, Handler.Callback, ServiceConnection,
        FlyRefreshLayout.OnPullRefreshListener,
        SoundDialogFragment.OnSoundInteractionListener,
        TemperatureDialogFragment.OnTemperatureInteractionListener,
        SwingDialogFragment.OnSwingInteractionListener {

    private static final String TAG = "MainActivity";

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;

    private FlyRefreshLayout mFlyLayout;
    private RecyclerView mListView;
    private ItemAdapter mAdapter;
    private ArrayList<ItemData> mDataSet = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;

    private SoundDialogFragment soundDialog;
    private TemperatureDialogFragment temperatureDialog;
    private SwingDialogFragment swingDialog;

    private int mCurrentTemperature;
    private int mSettingTemperature;
    private String mHeatingState;
    private String mSoundMode;
    private int mPlayState;
    private String mSwingMode;

    private long mExitTime = 0;

    private boolean isInActivity = false;
    private boolean isDataChanged = false;

    private Handler handler = new Handler(this);

    String temperatureTemp = "";
    String heatingTemp = "";
    String soundTemp = "";
    String playTemp = "";
    String swingTemp = "";

    private Toast toast;


    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initDataSet();
        setContentView(R.layout.activity_main);
        getMessageFromServer("t.c.33;sw.c;so.m.1");
        initView();
        toast = Toast.makeText(this, getResources().getString(R.string.back_bt), Toast.LENGTH_SHORT);

    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFlyLayout = (FlyRefreshLayout) findViewById(R.id.fly_layout);

        mFlyLayout.setOnPullRefreshListener(this);

        mListView = (RecyclerView) findViewById(R.id.list);

        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        mAdapter = new ItemAdapter(this);

        mListView.setAdapter(mAdapter);

        mListView.setItemAnimator(new SampleItemAnimator());

        soundDialog = SoundDialogFragment.newInstance(mSoundMode, mPlayState);
        temperatureDialog = TemperatureDialogFragment.newInstance(
                mCurrentTemperature, mSettingTemperature, mHeatingState);
        swingDialog = SwingDialogFragment.newInstance(mSwingMode);

        initItemData();
    }

    private void initItemData() {
        mDataSet.clear();
        mDataSet.add(new ItemData(R.drawable.thermometer_blue_48,
                getResources().getString(R.string.title_temperature),
                temperatureTemp + heatingTemp));
        mDataSet.add(new ItemData(R.drawable.music_blue_48,
                getResources().getString(R.string.title_sound),
                soundTemp + " / " + playTemp));
        mDataSet.add(new ItemData(R.drawable.carousel_blue_48,
                getResources().getString(R.string.title_swing),
                swingTemp));
        refreshItemData();
        mAdapter.notifyDataSetChanged();
    }

    private void refreshItemData() {
        resetTemperatureItemData();
        resetSoundItemData();
        resetSwingItemData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onPause() {
        super.onPause();
        isInActivity = false;
//        LocalBroadcastManager broadcastManager =
//                LocalBroadcastManager.getInstance(this);
//        broadcastManager.unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        isInActivity = true;

        Log.d(TAG, "onResume" + mSoundMode + ":" + mPlayState);
//        LocalBroadcastManager broadcastManager =
//                LocalBroadcastManager.getInstance(this);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(StatusService.ACTION_PROCESS_TEMPERATURE);
//        intentFilter.addAction(StatusService.ACTION_PROCESS_SWING);
//        intentFilter.addAction(StatusService.ACTION_PROCESS_SOUND);
//        receiver = new UpdateUIReceiver();
//        broadcastManager.registerReceiver(receiver, intentFilter);
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
                } else {
                    //TODO:connect failed, show to user
                    showFailedDialog();
                }
                break;

            case Constants.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                getMessageFromServer(readMessage);
                if (!isInActivity) {
                    messageBinder.buildNotification(
                            MessageService.NOTIFICATION_READ_MESSAGE,
                            getResources().getString(R.string.read_title),
                            getResources().getString(R.string.read_content));
                }
                break;

            case Constants.MESSAGE_SEND:
        }
        return true;
    }

    public void getMessageFromServer(String readMessage) {
        try {
            String state[] = readMessage.split(";");
            String A = state[0];//like T.O.25,get the '25'
            String temperatureList[] = A.split("\\.");
            mHeatingState = temperatureList[1];
            mCurrentTemperature = Integer.valueOf(temperatureList[2]);

            String C = state[1];//like SW.S, get the 'S'
            String swingList[] = C.split("\\.");
            mSwingMode = swingList[1];
            String B = state[2];//like SO.M.1,get the 'M' and '1'
            String soundList[] = B.split("\\.");
            mSoundMode = soundList[1];
            mPlayState = Integer.valueOf(soundList[2]);

            if (mSettingTemperature == 0) {
                mSettingTemperature = mCurrentTemperature;
            }

            refreshItemData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh(FlyRefreshLayout view) {
        View child = mListView.getChildAt(0);
        if (child != null) {
            bounceAnimateView(child.findViewById(R.id.icon));
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFlyLayout.onRefreshFinish();
            }
        }, 2000);
    }

    private void bounceAnimateView(View view) {
        if (view == null) {
            return;
        }

        Animator swing = ObjectAnimator.ofFloat(view, "rotationX", 0, 30, -20, 0);
        swing.setDuration(400);
        swing.setInterpolator(new AccelerateInterpolator());
        swing.start();
    }

    @Override
    public void onRefreshAnimationEnd(FlyRefreshLayout view) {
        if (messageBinder.getConnectState()) {
            sendCommand();
        } else {
            //TODO:connect failed, show to user
            showFailedDialog();

        }
//        refreshItemData();
    }

    public void sendCommand() {
        if (isDataChanged) {
            messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                    + mSettingTemperature + ";"
                    + Constants.SWING_TAG + mSwingMode + ";"
                    + Constants.SOUND_TAG + mSoundMode + mPlayState + ";\n");
        } else {
            messageBinder.sendMessage(Constants.COMMAND_REFRESH + ";\n");
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private LayoutInflater mInflater;

        public ItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.view_list_item, viewGroup, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
            final ItemData data = mDataSet.get(i);
            itemViewHolder.icon.setImageResource(data.icon);
            itemViewHolder.title.setText(data.title);
            itemViewHolder.subTitle.setText(data.subTitle);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView icon;
        TextView title;
        TextView subTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (position) {
                case 0:
                    showTemperatureDialog();
                    break;
                case 1:
                    showSoundDialog();
                    break;
                case 2:
                    showSwingDialog();
                    break;
            }
        }
    }

    @Override
    public void showTemperatureDialog() {
        temperatureDialog.show(getFragmentManager(), "temperatureDialog");
        temperatureDialog.refreshView(mCurrentTemperature, mSettingTemperature, mHeatingState);
    }

    @Override
    public void setTemperatureState(int settingTemperature, String heatingState) {
        mSettingTemperature = settingTemperature;
        mHeatingState = heatingState;
        resetTemperatureItemData();
        isDataChanged = true;
    }

    private void resetTemperatureItemData() {

        if (mHeatingState.equals(Constants.HEATING_OPEN)) {
            heatingTemp = getResources().getString(R.string.heating_main);
        } else if (mHeatingState.equals(Constants.COOL_DOWN)) {
            heatingTemp = getResources().getString(R.string.cool_down_main);
        } else {
            heatingTemp = getResources().getString(R.string.unHeating);
        }

        if (mCurrentTemperature != mSettingTemperature) {
            temperatureTemp = mCurrentTemperature + " ~ " + mSettingTemperature + "℃";
        } else {
            temperatureTemp = mCurrentTemperature + "℃";
        }

        mDataSet.get(0).subTitle = temperatureTemp + heatingTemp;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSoundDialog() {
        soundDialog.show(getFragmentManager(), "soundDialog");
        soundDialog.refreshView(mSoundMode, mPlayState);
    }

    @Override
    public void setSoundStatus(String soundMode, int playState) {
        mSoundMode = soundMode;
        mPlayState = playState;
        resetSoundItemData();
        isDataChanged = true;
    }

    private void resetSoundItemData() {

        if (mSoundMode.equals(Constants.SOUND_MUSIC)) {
            soundTemp = getResources().getString(R.string.music_mode);
        } else if (mSoundMode.equals(Constants.SOUND_STORY)) {
            soundTemp = getResources().getString(R.string.story_mode);
        }

        if (mPlayState == Constants.SOUND_STOP) {
            playTemp = getResources().getString(R.string.close);
            mDataSet.get(1).subTitle = playTemp;
        } else {
            playTemp = getResources().getString(R.string.play);
            mDataSet.get(1).subTitle = soundTemp + " / " + playTemp;
        }


        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSwingDialog() {
        swingDialog.show(getFragmentManager(), "swingDialog");
        swingDialog.refreshView(mSwingMode);
    }

    @Override
    public void setSwingState(String swingMode) {
        mSwingMode = swingMode;
        resetSwingItemData();
        isDataChanged = true;
    }

    private void resetSwingItemData() {
        if (mSwingMode.equals(Constants.SWING_SLEEP)) {
            swingTemp = getResources().getString(R.string.open);
        } else if (mSwingMode.equals(Constants.SWING_CLOSE)) {
            swingTemp = getResources().getString(R.string.close);
        }
        mDataSet.get(2).subTitle = swingTemp;
        mAdapter.notifyDataSetChanged();
    }

    private void showFailedDialog() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.fail_title));
        builder.setMessage(getResources().getString(R.string.fail_content));
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
//        ImageView imageView = new ImageView(this);
//        imageView.setImageResource(R.drawable.sun_background);
//        builder.setView(imageView);
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        quitToast();
    }

    private void quitToast() {
        if(null == toast.getView().getParent()){
            toast.show();
        }else{
            this.finish();
        }
    }

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


/*
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
*/



