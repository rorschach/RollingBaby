package com.hl.rollingbaby.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataSet();
        setContentView(R.layout.activity_main);

        initView();
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

        soundDialog = SoundDialogFragment.newInstance("Music", 0);
        temperatureDialog = TemperatureDialogFragment.newInstance(36, 36, "Heating");
        swingDialog = SwingDialogFragment.newInstance("Sleep");
    }

    private void initDataSet() {
        mDataSet.add(new ItemData(Color.parseColor("#76A9FC"),
                R.drawable.thermometer_white_64, "Temperature Status"));
        mDataSet.add(new ItemData(Color.GRAY,
                R.drawable.music_white_64, "Sound Status"));
        mDataSet.add(new ItemData(Color.GRAY,
                R.drawable.music_white_64, "Swing Status"));
    }

    private void addItemData() {
        ItemData itemData = new ItemData(Color.parseColor("#FFC970"),
                R.drawable.thermometer, "Magic Cube Show");
        mDataSet.add(0, itemData);
        mAdapter.notifyItemInserted(0);
        mLayoutManager.scrollToPosition(0);
    }

    private void resetItemData() {
        mDataSet.clear();
        mDataSet.add(new ItemData(
                Color.parseColor("#76A9FC"),
                R.drawable.thermometer,
                "Open heating"));
        mDataSet.add(new ItemData(
                Color.GRAY,
                R.drawable.thermometer,
                "Music playing"));
        mDataSet.add(new ItemData(
                Color.parseColor("#76A7FC"),
                R.drawable.thermometer,
                "Swing in sleep mode"));
        mAdapter.notifyDataSetChanged();
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
            showSoundDialog();
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
                }
                break;

            case Constants.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                geMessageFromServer(readMessage);
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

    public void geMessageFromServer(String readMessage) {

        Log.d(TAG, readMessage);
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
            if (isDataChanged) {
                sendCommand();
            } else {
                messageBinder.sendMessage(Constants.COMMAND_REFRESH + ";\n");
            }
        } else {
            //TODO:connect failed, show to user
        }
    }

    public void sendCommand() {
        messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                + mSettingTemperature + ";"
                + Constants.SWING_TAG + mSwingMode + ";"
                + Constants.SOUND_TAG + mSoundMode + mPlayState + ";\n");
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
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(data.color);
            itemViewHolder.icon.setBackgroundDrawable(drawable);
            itemViewHolder.icon.setImageResource(data.icon);
            itemViewHolder.title.setText(data.title);
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

        public ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
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
    }

    //TODO:update temperature item in here
    @Override
    public void setTemperatureState(int settingTemperature, String heatingState) {
        mSettingTemperature = settingTemperature;
        mHeatingState = heatingState;
        mDataSet.get(0).title = settingTemperature + ":" + heatingState;
        mAdapter.notifyDataSetChanged();
        isDataChanged = true;
    }

    @Override
    public void showSoundDialog() {
        soundDialog.show(getFragmentManager(), "soundDialog");
    }

    //TODO:update sound item in here
    @Override
    public void setSoundStatus(String soundMode, int playState) {
        mSoundMode = soundMode;
        mPlayState = playState;
        mDataSet.get(1).title = soundMode + ":" + playState;
        mAdapter.notifyDataSetChanged();
        isDataChanged = true;
    }

    @Override
    public void showSwingDialog() {
        swingDialog.show(getFragmentManager(), "swingDialog");
    }

    //TODO:update swing item in here
    @Override
    public void setSwingState(String swingMode) {
        mSwingMode = swingMode;
        mDataSet.get(2).title = "mode : " + swingMode;
        mAdapter.notifyDataSetChanged();
        isDataChanged = true;
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



