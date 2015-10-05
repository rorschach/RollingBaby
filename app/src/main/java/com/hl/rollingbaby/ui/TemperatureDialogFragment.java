package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
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
public class TemperatureDialogFragment extends DialogFragment {

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

    //持有的Activity实例
    private OnTemperatureInteractionListener mListener;

    private AppCompatDialog dialog;

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
    private void initView() {
        current.setText(mCurrentTemperature + "");
        setting.setText(mSettingTemperature + "");

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
            mHeatingState = Constants.TEMPERATURE_UP;
            icon.setBackgroundResource(R.drawable.sun_background);
            setting.setTextColor(
                    getActivity().getResources().getColor(R.color.red));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffdb4437"), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#ffdb4437"), PorterDuff.Mode.SRC_IN);
            }
            state.setText(getActivity().getResources().getString(R.string.heating));
        } else if (mSettingTemperature < mCurrentTemperature) {
            mHeatingState = Constants.TEMPERATURE_DOWN;
            icon.setBackgroundResource(R.drawable.moon_background);
            setting.setTextColor(
                    getActivity().getResources().getColor(R.color.blue));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ff4285f4"), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#ff4285f4"), PorterDuff.Mode.SRC_IN);
            }
            state.setText(getActivity().getResources().getString(R.string.cool_down));
        } else {
            icon.setBackgroundResource(R.drawable.sun_background);
            mHeatingState = Constants.HEATING_CLOSE;
            setting.setTextColor(
                    getActivity().getResources().getColor(R.color.green));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ff0f9d58"), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#ff0f9d58"), PorterDuff.Mode.SRC_IN);
            }
            state.setText(getActivity().getResources().getString(R.string.unHeating));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
        int height = Utils.dpToPx(Utils.getScreenHeight(getActivity()) / 3);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        return dialog;
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
        dialog.dismiss();
    }

    /**
     * 刷新数据
     *
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
