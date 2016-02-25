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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hl.rollingbaby.interfaces.Constants;
import com.hl.rollingbaby.entity.ItemData;
import com.hl.rollingbaby.interfaces.MessageProcesser;
import com.hl.rollingbaby.interfaces.MessageTarget;
import com.hl.rollingbaby.network.MessageService;
import com.race604.flyrefresh.FlyRefreshLayout;
import com.hl.rollingbaby.R;
import com.hl.rollingbaby.views.FlyItemAnimator;

import java.util.ArrayList;

/**
 * 用户主界面
 */
public class MainActivity extends AppCompatActivity implements
        Handler.Callback, ServiceConnection,
        MessageTarget, MessageProcesser,
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

    private int mCurrentTemperature = 33;
    private int mSettingTemperature = 33;
    private String mHeatingState = "c";
    private String mSoundMode = "m";
    private int mPlayState = 1;
    private String mSwingMode = "c";

    private int playStateFlag = 1;

    private boolean isInActivity = false;
    private boolean isDataChanged = false;

    private Handler handler = new Handler(this);

    private String temperatureTemp = "";
    private String heatingTemp = "";
    private String soundTemp = "";
    private String playTemp = "";
    private String swingTemp = "";

    private Toast toast;

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        parseMessage("t.c.33;sw.c;so.m.1");
        parseMessage("sw.s;");
        initView();
        toast = Toast.makeText(this, getResources().getString(R.string.back_bt), Toast.LENGTH_SHORT);
    }

    /**
     * 初始化视图及3个状态界面数据
     */
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mFlyLayout = (FlyRefreshLayout) findViewById(R.id.fly_layout);
        mFlyLayout.setOnPullRefreshListener(this);

        mListView = (RecyclerView) findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new ItemAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setItemAnimator(new FlyItemAnimator());

        soundDialog = SoundDialogFragment.newInstance(mSoundMode, mPlayState);
        temperatureDialog = TemperatureDialogFragment.newInstance(
                mCurrentTemperature, mSettingTemperature, mHeatingState);
        swingDialog = SwingDialogFragment.newInstance(mSwingMode);

        initItemData();
    }

    /**
     * 初始化主界面列表数据
     */
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
//        mAdapter.notifyDataSetChanged();
    }

    /**
     * 刷新列表数据
     */
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
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置标志位
     */
    @Override
    protected void onPause() {
        super.onPause();
        isInActivity = false;
    }

    /**
     * 绑定后台服务
     */
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MessageService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        isInActivity = true;
        Log.d(TAG, "onResume" + mSoundMode + ":" + mPlayState);
    }

    /**
     * 断开与后台服务的连接
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageService != null) {
            unbindService(this);
        }
    }

    /**
     * 绑定后台服务时的操作
     * @param name    组件名
     * @param service 绑定的服务
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        messageBinder = (MessageService.MessageBinder) service;
        messageService = messageBinder.getService();
        messageBinder.startConnect(getHandler());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    /**
     * 处理系统收到的消息后在此处更新视图
     * @param msg message对象
     * @return
     */
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
                    showFailedDialog();
                }
                break;
            case Constants.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                parseMessage(readMessage);
                if (!isInActivity) {
                    messageBinder.buildNotification(
                            MessageService.NOTIFICATION_READ_MESSAGE,
                            getResources().getString(R.string.read_title),
                            getResources().getString(R.string.read_content));
                }
                break;
            case Constants.MESSAGE_SEND:
                break;
        }
        return true;
    }

    /**
     * 处理接收到的消息
     *
     * @param readMessage 收到的消息
     */
    @Override
    public void parseMessage(final String readMessage) {
        try {
            String[] state = readMessage.split(";");
            Log.d(TAG, "..." + state.length);
            if (state.length == 3) {
                parseMultipleMessage(state);
            } else if (state.length == 1) {
                parseSingleMessage(state);
            }

            if (mSettingTemperature == 0) {
                mSettingTemperature = mCurrentTemperature;
            }

            refreshItemData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseSingleMessage(String[] state) {
        String singleMessage = state[0];
        String[] messageArray = singleMessage.split("\\.");
        String tag = messageArray[0];
        Log.d(TAG, tag);

        switch (tag) {
            case Constants.SOUND_TAG:
                mSoundMode = messageArray[1];
                mPlayState = Integer.valueOf(messageArray[2]);
                break;
            case Constants.SWING_TAG:
                mSwingMode = messageArray[1];
                break;
            case Constants.TEMPERATURE_TAG:
                mHeatingState = messageArray[1];
                mCurrentTemperature = Integer.valueOf(messageArray[2]);
                break;
            case Constants.HUMIDITY_TAG:
                //TODO
                break;
            default:
                break;
        }
    }

    private void parseMultipleMessage(String[] state) {
        try {
            String A = state[0];//like T.O.25,get the '25'
            String[] temperatureList = A.split("\\.");
            mHeatingState = temperatureList[1];
            mCurrentTemperature = Integer.valueOf(temperatureList[2]);

            String C = state[1];//like SW.S, get the 'S'
            String[] swingList = C.split("\\.");
            mSwingMode = swingList[1];
            String B = state[2];//like SO.M.1,get the 'M' and '1'
            String[] soundList = B.split("\\.");
            mSoundMode = soundList[1];
            mPlayState = Integer.valueOf(soundList[2]);

            playStateFlag = mPlayState;

            if (mSettingTemperature == 0) {
                mSettingTemperature = mCurrentTemperature;
            }
            refreshItemData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给服务器
     */
    @Override
    public void sendMultipleMessage() {
        if (isDataChanged) {
            if (mPlayState != playStateFlag) {
                messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                        + mSettingTemperature + ";"
                        + Constants.SWING_TAG + mSwingMode + ";"
                        + Constants.SOUND_TAG + mSoundMode + mPlayState + ";\n");
            } else {
                messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                        + mSettingTemperature + ";"
                        + Constants.SWING_TAG + mSwingMode + ";"
                        + Constants.SOUND_TAG + mSoundMode + Constants.SOUND_NOTHING + ";\n");
            }
            isDataChanged = false;
        } else {
            messageBinder.sendMessage(Constants.COMMAND_REFRESH + ";\n");
        }
    }

    @Override
    public void sendSingleMessage(int tag) {
        if (isDataChanged) {
            switch (tag) {
                case 1:
                    messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                            + mSettingTemperature + ";"
                            + Constants.COMMAND_REFRESH + ";\n");
                    break;
                case 2:
                    //TODO
                    break;
                case 3:
                    if (mPlayState != playStateFlag) {
                        messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                                + Constants.SOUND_TAG + mSoundMode + mPlayState
                                + Constants.COMMAND_REFRESH + ";\n");
                    } else {
                        messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                                + Constants.SOUND_TAG + mSoundMode + Constants.SOUND_NOTHING
                                + Constants.COMMAND_REFRESH + ";\n");
                    }
                    break;
                case 4:
                    messageBinder.sendMessage(Constants.COMMAND_EXECUTE + ";"
                            + Constants.SWING_TAG + mSwingMode + ";"
                            + Constants.COMMAND_REFRESH + ";\n");
                    break;
                default:
                    break;
            }
            isDataChanged = false;
        } else {
            messageBinder.sendMessage(Constants.COMMAND_REFRESH + ";\n");
        }
    }

    /**
     * 刷新时的操作
     *
     * @param view
     */
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
            sendMultipleMessage();
        } else {
            showFailedDialog();
        }
    }

    /**
     * 列表数据适配器
     */
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

    /**
     * viewHolder数据缓存
     */
    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        /**
         * 点击列表不同项进入相应界面
         *
         * @param v
         */
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
        temperatureDialog.refreshData(mCurrentTemperature, mSettingTemperature, mHeatingState);
    }

    /**
     * 设置温度状态
     *
     * @param currentTemperature 当前温度
     * @param settingTemperature 设定温度
     * @param heatingState       加热状态
     */
    @Override
    public void updateTemperatureState(int currentTemperature, int settingTemperature, String heatingState) {
        isDataChanged = true;
        mCurrentTemperature = currentTemperature;
        mSettingTemperature = settingTemperature;
        mHeatingState = heatingState;

        resetTemperatureItemData();
        sendSingleMessage(1);
        Log.d(TAG, "updateTemperatureStatus");
    }

    /**
     * 刷新温度数据项内容
     */
    private void resetTemperatureItemData() {

        switch (mHeatingState) {
            case Constants.HEATING_OPEN:
                heatingTemp = getResources().getString(R.string.heating_main);
                break;
            case Constants.COOL_DOWN:
                heatingTemp = getResources().getString(R.string.cool_down_main);
                break;
            default:
                heatingTemp = getResources().getString(R.string.unHeating);
                break;
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
        soundDialog.refreshData(mSoundMode, mPlayState);
    }

    /**
     * 设置声音状态
     *
     * @param soundMode 声音状态
     * @param playState 播放状态
     */
    @Override
    public void updateSoundStatus(String soundMode, int playState) {
        isDataChanged = true;
        mSoundMode = soundMode;
        mPlayState = playState;

        resetSoundItemData();
        sendSingleMessage(3);
        Log.d(TAG, "updateSoundStatus");
    }

    /**
     * 刷新声音数据项内容
     */
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
        Log.d(TAG, "showSwingDialog" + mSwingMode);
        swingDialog.show(getFragmentManager(), "swingDialog");
        swingDialog.refreshData(mSwingMode);
        Log.d(TAG, "showSwingDialog" + mSwingMode);
    }

    /**
     * 设置摇摆状态
     *
     * @param swingMode
     */
    @Override
    public void updateSwingStatus(String swingMode) {
        Log.d(TAG, "updateSwingStatus : " + mSwingMode);
        isDataChanged = true;
        mSwingMode = swingMode;

        resetSwingItemData();
        sendSingleMessage(4);
        Log.d(TAG, "updateSwingStatus : " + mSwingMode);
    }

    /**
     * 刷新摇摆状态
     */
    private void resetSwingItemData() {
        Log.d(TAG, "resetSwingItemData : " + mSwingMode);
        if (mSwingMode.equals(Constants.SWING_SLEEP)) {
            swingTemp = getResources().getString(R.string.open);
        } else if (mSwingMode.equals(Constants.SWING_CLOSE)) {
            swingTemp = getResources().getString(R.string.close);
        }
        mDataSet.get(2).subTitle = swingTemp;
        mAdapter.notifyDataSetChanged();
        Log.d(TAG, "resetSwingItemData : " + mSwingMode);
    }

    /**
     * 连接失败后显示的对话框
     */
    private void showFailedDialog() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.fail_title));
        builder.setView(R.layout.failed_dialog);
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
    }

    /**
     * 双击退出提示
     */
    @Override
    public void onBackPressed() {
        quitToast();
    }

    private void quitToast() {
        if (null == toast.getView().getParent()) {
            toast.show();
        } else {
            this.finish();
        }
    }
}



