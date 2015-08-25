package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;

public class TemperatureDialogFragment extends DialogFragment {

    private static final String TAG = "TemperatureDialogFragment";

    private static final String ARG_CURRENT_TEMPERATURE = "CURRENT_TEMPERATURE";
    private static final String ARG_SETTING_TEMPERATURE = "SETTING_TEMPERATURE";
    private static final String ARG_HEATING_STATE = "HEATING_STATE";

    private int mCurrentTemperature;
    private int mSettingTemperature;
    private String mHeatingState;

    private OnTemperatureInteractionListener mListener;

    private AppCompatDialog dialog;
    private TextView currentTx;
    private TextView settingTx;
    private ImageView icon;
    private SeekBar seekBar;
    private TextView heatingTx;

    public static TemperatureDialogFragment newInstance(
            int currentTemperature, int settingTemperature, String heatingState) {
        TemperatureDialogFragment fragment = new TemperatureDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_CURRENT_TEMPERATURE, currentTemperature);
        args.putInt(Constants.ARG_SETTING_TEMPERATURE, settingTemperature);
        args.putString(Constants.ARG_HEATING_STATE, heatingState);
        fragment.setArguments(args);
        Log.d(TAG, "current:" + currentTemperature);
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
        Log.d(TAG, "current::" + mCurrentTemperature);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature_dialog, container, false);

        currentTx = (TextView) view.findViewById(R.id.current_temperature);
        settingTx = (TextView) view.findViewById(R.id.setting_temperature);
        icon = (ImageView) view.findViewById(R.id.heating_icon);
        heatingTx = (TextView) view.findViewById(R.id.heating_state);
        seekBar = (SeekBar) view.findViewById(R.id.temperature_seekBar);

        currentTx.setText(mCurrentTemperature + "");
        settingTx.setText(mSettingTemperature + "");

        seekBar.setProgress(mSettingTemperature);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 25;
                mSettingTemperature = progress;
                settingTx.setText("" + progress);
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

        return view;
    }

    private void resetView() {
        if (mSettingTemperature > mCurrentTemperature) {
            mHeatingState = Constants.HEATING_OPEN;
            icon.setBackgroundResource(R.drawable.sun_background);
            settingTx.setTextColor(
                    getActivity().getResources().getColor(R.color.red));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffdb4437"), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#ffdb4437"), PorterDuff.Mode.SRC_IN);
            }
            heatingTx.setText(getActivity().getResources().getString(R.string.heating));
        } else if (mSettingTemperature < mCurrentTemperature) {
            mHeatingState = Constants.COOL_DOWN;
            icon.setBackgroundResource(R.drawable.moon_background);
            settingTx.setTextColor(
                    getActivity().getResources().getColor(R.color.blue));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ff4285f4"), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#ff4285f4"), PorterDuff.Mode.SRC_IN);
            }
            heatingTx.setText(getActivity().getResources().getString(R.string.cool_down));
        } else {
            icon.setBackgroundResource(R.drawable.sun_background);
            mHeatingState = Constants.HEATING_CLOSE;
            settingTx.setTextColor(
                    getActivity().getResources().getColor(R.color.green));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ff0f9d58"), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#ff0f9d58"), PorterDuff.Mode.SRC_IN);
            }
            heatingTx.setText(getActivity().getResources().getString(R.string.unHeating));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSettingTemperature = seekBar.getProgress() + 25;
        mListener.setTemperatureState(mCurrentTemperature, mSettingTemperature, mHeatingState);
        dialog.dismiss();
    }

    public void refreshView(
            int currentTemperature, int settingTemperature, String heatingState) {
        mCurrentTemperature = currentTemperature;
        mSettingTemperature = settingTemperature;
        mHeatingState = heatingState;
        Bundle args = new Bundle();
        mCurrentTemperature = getArguments().getInt(ARG_CURRENT_TEMPERATURE, Constants.DEFAULT_TEMPERATURE);
        mSettingTemperature = getArguments().getInt(ARG_SETTING_TEMPERATURE, mCurrentTemperature);
        mHeatingState = getArguments().getString(ARG_HEATING_STATE);
        this.setArguments(args);
    }

    public interface OnTemperatureInteractionListener {

        void showTemperatureDialog();

        void setTemperatureState(int mCurrentTemperature, int settingTemperature, String heatingState);

    }

}
