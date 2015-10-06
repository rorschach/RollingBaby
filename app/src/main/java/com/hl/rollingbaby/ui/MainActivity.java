package com.hl.rollingbaby.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.Utils;
import com.hl.rollingbaby.entity.ItemData;
import com.hl.rollingbaby.interfaces.Constants;
import com.hl.rollingbaby.interfaces.MessageProcesser;
import com.hl.rollingbaby.interfaces.MessageTarget;
import com.hl.rollingbaby.network.MessageService;
import com.hl.rollingbaby.views.FlyItemAnimator;
import com.race604.flyrefresh.FlyRefreshLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 用户主界面
 */
public class MainActivity extends AppCompatActivity implements
        Handler.Callback, ServiceConnection,
        MessageTarget, MessageProcesser,
        FlyRefreshLayout.OnPullRefreshListener,
        TemperatureDialogFragment.OnTemperatureInteractionListener,
        HumidityDialogFragment.OnHumidityInteractionListener,
        SoundDialogFragment.OnSoundInteractionListener,
        SwingDialogFragment.OnSwingInteractionListener {

    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.fly_layout)
    FlyRefreshLayout flyLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG = "MainActivity";

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;

    private ItemAdapter mAdapter;
    private ArrayList<ItemData> mDataSet = new ArrayList<>();

    private TemperatureDialogFragment temperatureDialog;
    private HumidityDialogFragment humidityDialog;
    private SoundDialogFragment soundDialog;
    private SwingDialogFragment swingDialog;
    private ConnectFailedFragment failedFragment;

    private int mCurrentTemperature = 33;
    private int mSettingTemperature = 33;
    private int mHumidity = 60;
    private String mHeatingState = "n";
    private String mSoundMode = "m";
    private int mPlayState = 1;
    private String mSwingMode = "c";

    private boolean isInActivity = false;
    public static boolean isDialogShown = false;
    private boolean isChangeTemperature = false;

    private Handler handler = new Handler(this);

    private String humidityTemp = "";
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
        ButterKnife.bind(this);
        initView();
        if (savedInstanceState == null) {
            mAdapter.setAnimateItems(true);
        }
    }

    /**
     * 初始化视图及3个状态界面数据
     */
    private void initView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        flyLayout.setOnPullRefreshListener(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLayoutManager);
        list.setItemAnimator(new FlyItemAnimator());
        mAdapter = new ItemAdapter(this);
        list.setAdapter(mAdapter);

        temperatureDialog = TemperatureDialogFragment.newInstance(
                mCurrentTemperature, mSettingTemperature, mHeatingState);
        humidityDialog = HumidityDialogFragment.newInstance(mHumidity);
        soundDialog = SoundDialogFragment.newInstance(mSoundMode, mPlayState);
        swingDialog = SwingDialogFragment.newInstance(mSwingMode);
        failedFragment = ConnectFailedFragment.newInstance();
        initItemData();

        toast = Toast.makeText(this, getResources().getString(R.string.back_bt), Toast.LENGTH_SHORT);
    }

    /**
     * 初始化主界面列表数据
     */
    private void initItemData() {
        mDataSet.clear();
        mDataSet.add(new ItemData(R.drawable.thermometer_blue_48,
                getResources().getString(R.string.title_temperature),
                temperatureTemp + heatingTemp));
        mDataSet.add(new ItemData(R.drawable.humidity_blue_48,
                getResources().getString(R.string.title_humidity),
                humidityTemp));
        mDataSet.add(new ItemData(R.drawable.music_blue_48,
                getResources().getString(R.string.title_sound),
                soundTemp + " / " + playTemp));
        mDataSet.add(new ItemData(R.drawable.carousel_blue_48,
                getResources().getString(R.string.title_swing),
                swingTemp));
        refreshItemData();
    }

    /**
     * 刷新列表数据
     */
    public void refreshItemData() {
        refreshTemperatureItemData();
        refreshHumidityItemData();
        refreshSoundItemData();
        refreshSwingItemData();
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
        isDialogShown = false;
        Log.d(TAG, "onResume-" + isDialogShown);
    }

    /**
     * 断开与后台服务的连接
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendExitMessage();
        if (messageService != null) {
            unbindService(this);
        }
    }

    /**
     * 绑定后台服务时的操作
     *
     * @param name    组件名
     * @param service 绑定的服务
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        messageBinder = (MessageService.MessageBinder) service;
        messageService = messageBinder.getService();
        messageBinder.startConnect(((MessageTarget) this).getHandler());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    /**
     * 处理系统收到的消息后在此处更新视图
     *
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
                    showConnectedFailedDialog();
//                    failedFragment.show(getFragmentManager(), "connectFailed");
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
            if (state.length == 4) {
                parseMultipleMessage(state);
            } else if (state.length == 1) {
                parseSingleMessage(state);
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

        switch (tag) {
            case Constants.MUSIC_TAG:
            case Constants.STORY_TAG:
                mSoundMode = messageArray[1];
                mPlayState = Integer.valueOf(messageArray[2]);
                break;
            case Constants.SWING_TAG:
                mSwingMode = messageArray[1];
                break;
            case Constants.HUMIDITY_TAG:
                mHumidity = Integer.valueOf(messageArray[1]);
                break;
            case Constants.TEMPERATURE_TAG:
                mHeatingState = messageArray[1];
                mCurrentTemperature = Integer.valueOf(messageArray[2]);
                break;
            default:
                break;
        }
    }

    private void parseMultipleMessage(String[] state) {
        try {
            String SOUND = state[0];//like M.1,get the 'M' and '1'
            String[] soundList = SOUND.split("\\.");
            mSoundMode = soundList[0];
            mPlayState = Integer.valueOf(soundList[1]);


            String SWING = state[1];//like SW.S, get the 'S'
            String[] swingList = SWING.split("\\.");
            mSwingMode = swingList[1];

            String HUMIDITY = state[2];//like SW.S, get the 'S'
            String[] humidityList = HUMIDITY.split("\\.");
            mHumidity = Integer.valueOf(humidityList[1]);

            String TEMPERATURE = state[3];//like T.O.25,get the '25'
            String[] temperatureList = TEMPERATURE.split("\\.");
            mHeatingState = temperatureList[1];
            mCurrentTemperature = Integer.valueOf(temperatureList[2]);
            Log.d(TAG, "tem-" + mCurrentTemperature);

            if (!isChangeTemperature) {
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
    public void sendRefreshRequest() {
        messageBinder.sendMessage(Constants.REFRESH_TAG + ";\n");
    }

    public void sendExitMessage() {
        Log.d(TAG, "sendExitMessage");
        if (mPlayState == Constants.SOUND_PLAY) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    messageBinder.sendMessage(mSoundMode + Constants.SOUND_STOP + ";\n");
                }
            }, 100);
        }

        if (mSwingMode.equals(Constants.SWING_OPEN)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    messageBinder.sendMessage(Constants.SWING_TAG + Constants.SWING_CLOSE + ";\n");
                }
            }, 100);
        }

        messageBinder.sendMessage(Constants.TEMPERATURE_TAG + 0 + ";\n");
    }

    @Override
    public void sendSingleMessage(String tag) {
        switch (tag) {
            case Constants.MUSIC_TAG:
            case Constants.STORY_TAG:
                messageBinder.sendMessage(mSoundMode + mPlayState + ";\n");
                break;
            case Constants.SWING_TAG:
                messageBinder.sendMessage(Constants.SWING_TAG + mSwingMode + ";\n");
                break;
            case Constants.HUMIDITY_TAG:
                messageBinder.sendMessage(Constants.HUMIDITY_TAG + mHumidity + ";\n");
                break;
            case Constants.TEMPERATURE_TAG:
                messageBinder.sendMessage(Constants.TEMPERATURE_TAG + mSettingTemperature + ";\n");
                break;
            default:
                break;
        }
    }

    /**
     * 刷新时的操作
     *
     * @param view FlyRefreshLayout
     */
    @Override
    public void onRefresh(FlyRefreshLayout view) {
        View child = list.getChildAt(0);
        if (child != null) {
            bounceAnimateView(child.findViewById(R.id.icon));
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                flyLayout.onRefreshFinish();
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
        Log.d(TAG, "getConnectState-" + messageBinder.getConnectState());
        if (messageBinder.getConnectState()) {
            sendRefreshRequest();
        }else {
            showConnectedFailedDialog();
//            failedFragment.show(getFragmentManager(), "connectFailed");
        }
    }

    /**
     * 列表数据适配器
     */
    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private LayoutInflater mInflater;
        private int lastAnimatedPosition = -1;
        private boolean animateItems = false;

        public ItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.view_list_item, viewGroup, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            runEnterAnimation(holder.itemView, position);
            final ItemData data = mDataSet.get(position);
            holder.icon.setImageResource(data.icon);
            holder.title.setText(data.title);
            holder.subTitle.setText(data.subTitle);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public void setAnimateItems(boolean animated) {
            this.animateItems = animated;
        }

        private void runEnterAnimation(View view, int position) {

            if (!animateItems) {
                return;
            }

            if (position > lastAnimatedPosition) {
                lastAnimatedPosition = position;
                view.setTranslationY(Utils.getScreenHeight(MainActivity.this));
                view.animate()
                        .translationY(0)
                        .setInterpolator(new AccelerateInterpolator())
                        .setDuration(500)
                        .start();
            }
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
         * @param v recyclerView
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (position) {
                case 0:
                    showTemperatureDialog();
                    break;
                case 1:
                    showHumidityDialog();
                    break;
                case 2:
                    showSoundDialog();
                    break;
                case 3:
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
        mCurrentTemperature = currentTemperature;
        mSettingTemperature = settingTemperature;
        mHeatingState = heatingState;
        refreshTemperatureItemData();
        sendSingleMessage(Constants.TEMPERATURE_TAG);
    }

    /**
     * 刷新温度数据项内容
     */
    @Override
    public void refreshTemperatureItemData() {
        switch (mHeatingState) {
            case Constants.HEATING_OPEN:
                heatingTemp = getResources().getString(R.string.temperature_up);
                break;
            case Constants.HEATING_CLOSE:
                heatingTemp = getResources().getString(R.string.temperature_down);
                break;
            default:
                heatingTemp = getResources().getString(R.string.temperature_close);
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
    public void showHumidityDialog() {
        humidityDialog.show(getFragmentManager(), "humidityDialog");
        humidityDialog.refreshData(mHumidity);
    }

    @Override
    public void refreshHumidityItemData() {
        String tx;
        if (mHumidity >= 80) {
            tx = " / " + getResources().getString(R.string.mIsWetting);
        } else {
            tx = "";
        }
        humidityTemp = mHumidity + "%" + tx;
        mDataSet.get(1).subTitle = humidityTemp;
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
        mSoundMode = soundMode;
        mPlayState = playState;
        isChangeTemperature = true;
        refreshSoundItemData();
        sendSingleMessage(soundMode);
    }

    /**
     * 刷新声音数据项内容
     */
    @Override
    public void refreshSoundItemData() {
        if (mSoundMode.equals(Constants.MUSIC_TAG)) {
            soundTemp = getResources().getString(R.string.music_mode);
        } else if (mSoundMode.equals(Constants.STORY_TAG)) {
            soundTemp = getResources().getString(R.string.story_mode);
        }

        if (mPlayState == Constants.SOUND_STOP || mPlayState == Constants.SOUND_RECEIVE_PAUSE) {
            playTemp = getResources().getString(R.string.close);
            mDataSet.get(2).subTitle = playTemp;
        } else if(mPlayState == Constants.SOUND_PLAY){
            playTemp = getResources().getString(R.string.isPlaying);
            mDataSet.get(2).subTitle = soundTemp + " / " + playTemp;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSwingDialog() {
        swingDialog.show(getFragmentManager(), "swingDialog");
        swingDialog.refreshData(mSwingMode);
    }

    /**
     * 设置摇摆状态
     *
     * @param swingMode 摇摆模式
     */
    @Override
    public void updateSwingStatus(String swingMode) {
        mSwingMode = swingMode;
        refreshSwingItemData();
        sendSingleMessage(Constants.SWING_TAG);
    }

    /**
     * 刷新摇摆状态
     */
    @Override
    public void refreshSwingItemData() {
        if (mSwingMode.equals(Constants.SWING_OPEN)) {
            swingTemp = getResources().getString(R.string.open);
        } else if (mSwingMode.equals(Constants.SWING_CLOSE)) {
            swingTemp = getResources().getString(R.string.close);
        }
        mDataSet.get(3).subTitle = swingTemp;
        mAdapter.notifyDataSetChanged();
    }

    public static void setIsDialogShown(boolean shown) {
        isDialogShown = shown;
    }

    /**
     * 连接失败后显示的对话框
     */
    private void showConnectedFailedDialog() {
        Log.d(TAG, "isDialogShown-" + isDialogShown);
        if (!isDialogShown) {
            if (soundDialog.isVisible()) {
                soundDialog.dismiss();
            }
            if (swingDialog.isVisible()) {
                swingDialog.dismiss();
            }
            if (humidityDialog.isVisible()) {
                humidityDialog.dismiss();
            }
            if (temperatureDialog.isVisible()) {
                temperatureDialog.dismiss();
            }
            failedFragment.show(getFragmentManager(), "connectFailed");
        }
        Log.d(TAG, "isDialogShown-" + isDialogShown);
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



