package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.Utils;
import com.hl.rollingbaby.interfaces.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 温度状态界面
 */
public class TemperatureDialogFragment extends BaseDialogFragment {

    private static final String TAG = "TemperatureDialogFragment";
    @Bind(R.id.icon)
    ImageView icon;
    @Bind(R.id.state)
    TextView state;
    @Bind(R.id.setting)
    TextView setting;
    @Bind(R.id.current)
    TextView current;
    @Bind(R.id.seekBar)
    SeekBar seekBar;

    private int mCurrentTemperature;
    private int mSettingTemperature;
    private String mHeatingState;

    private int textColor;
    private String seekColor;

    //持有的Activity实例
    private OnTemperatureInteractionListener mListener;

    /**
     * 获取TemperatureDialogFragment的实例
     * @param currentTemperature 当前温度
     * @param settingTemperature 设定温度
     * @param heatingState       加热状态
     * @return fragment实例
     */
    public static TemperatureDialogFragment newInstance(
            int currentTemperature, int settingTemperature, String heatingState) {
        TemperatureDialogFragment fragment = new TemperatureDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_CURRENT_TEMPERATURE, currentTemperature);
        args.putInt(Constants.ARG_SETTING_TEMPERATURE, settingTemperature);
        args.putString(Constants.ARG_HEATING_STATE, heatingState);
        fragment.setArguments(args);
        return fragment;
    }

    public TemperatureDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentTemperature = getArguments().getInt(
                    Constants.ARG_CURRENT_TEMPERATURE, Constants.DEFAULT_TEMPERATURE);
            mSettingTemperature = getArguments().getInt(
                    Constants.ARG_SETTING_TEMPERATURE, mCurrentTemperature);
            mHeatingState = getArguments().getString(Constants.ARG_HEATING_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature_dialog, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    /**
     * 初始化各控件
     */
    @Override
    protected void initView() {
        String tx = mCurrentTemperature + "";
        current.setText(tx);
        setting.setText(tx);

        seekBar.setProgress(mSettingTemperature);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 25;
                mSettingTemperature = progress;
                setting.setText("" + progress);
                resetView();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBar.setProgress(mSettingTemperature - 25);
        resetView();
    }

    //根据用户操作更新视图
    private void resetView() {
        if (mSettingTemperature > mCurrentTemperature) {

            mHeatingState = Constants.HEATING_OPEN;
            icon.setBackgroundResource(R.drawable.sun_background);
            textColor = R.color.red;
            seekColor = "#ffdb4437";
            state.setText(getActivity().getResources().getString(R.string.temperature_up));
        } else if (mSettingTemperature < mCurrentTemperature) {

            mHeatingState = Constants.HEATING_CLOSE;
            icon.setBackgroundResource(R.drawable.moon_background);
            textColor = R.color.blue;
            seekColor = "#ff4285f4";
            state.setText(getActivity().getResources().getString(R.string.temperature_down));
        } else {

            icon.setBackgroundResource(R.drawable.sun_background);
            mHeatingState = Constants.HEATING_NONE;
            textColor = R.color.green;
            seekColor = "#ff0f9d58";
            state.setText(getActivity().getResources().getString(R.string.temperature_close));
        }

        setting.setTextColor(getActivity().getResources().getColor(textColor));
        state.setTextColor(getActivity().getResources().getColor(textColor));
        if (Utils.isAndroid4P1()) {
            seekBar.getProgressDrawable().setColorFilter(Color.parseColor(seekColor), PorterDuff.Mode.SRC_IN);
            seekBar.getThumb().setColorFilter(Color.parseColor(seekColor), PorterDuff.Mode.SRC_IN);
        }
    }

    /**
     * @param activity 需要持有的Activity实例
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTemperatureInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTemperatureInteractionListener");
        }
    }

    /**
     * 释放持有的对Activity的引用
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSettingTemperature = seekBar.getProgress() + 25;
        mListener.updateTemperatureState(mCurrentTemperature, mSettingTemperature, mHeatingState);
    }

    /**
     * 刷新数据
     * @param currentTemperature 当前温度
     * @param settingTemperature 设定温度
     * @param heatingState       加热状态
     */
    public void refreshData(
            int currentTemperature, int settingTemperature, String heatingState) {
        mCurrentTemperature = currentTemperature;
        mSettingTemperature = settingTemperature;
        mHeatingState = heatingState;
        Bundle args = new Bundle();
        mCurrentTemperature = getArguments().getInt(Constants.ARG_CURRENT_TEMPERATURE, Constants.DEFAULT_TEMPERATURE);
        mSettingTemperature = getArguments().getInt(Constants.ARG_SETTING_TEMPERATURE, mCurrentTemperature);
        mHeatingState = getArguments().getString(Constants.ARG_HEATING_STATE);
        this.setArguments(args);
    }

    //对外部公开的接口
    public interface OnTemperatureInteractionListener {

        //进入声音状态界面
        void showTemperatureDialog();

        //更新数据
        void updateTemperatureState(int mCurrentTemperature, int settingTemperature, String heatingState);

        void refreshTemperatureItemData();
    }

}
