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
    private TextView currentText;
    private TextView settingText;
    private ImageView heatingIcon;
    private SeekBar seekBar;

    public static TemperatureDialogFragment newInstance(
            int currentTemperature, int settingTemperature, String heatingState) {
        TemperatureDialogFragment fragment = new TemperatureDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT_TEMPERATURE, currentTemperature);
        args.putInt(ARG_SETTING_TEMPERATURE, settingTemperature);
        args.putString(ARG_HEATING_STATE, heatingState);
        fragment.setArguments(args);
        return fragment;
    }

    public TemperatureDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentTemperature = getArguments().getInt(ARG_CURRENT_TEMPERATURE, 36);
            mSettingTemperature = getArguments().getInt(ARG_SETTING_TEMPERATURE, 36);
            mHeatingState = getArguments().getString(ARG_HEATING_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature_dialog, container, false);
        currentText = (TextView) view.findViewById(R.id.current_temperature);
        settingText = (TextView) view.findViewById(R.id.setting_temperature);
        heatingIcon = (ImageView) view.findViewById(R.id.heating_icon);

        seekBar = (SeekBar) view.findViewById(R.id.temperature_seekBar);

        settingText.setTextColor(
                getActivity().getResources().getColor(R.color.green));
        currentText.setTextColor(
                getActivity().getResources().getColor(R.color.green));
        currentText.setText("Current : " + mCurrentTemperature);
        settingText.setText("Setting : " + mSettingTemperature);

        seekBar.setProgress(mSettingTemperature);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 25;
                mSettingTemperature = progress;
                settingText.setText("Setting : " + progress);
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
            //TODO:change heatingState
            heatingIcon.setBackgroundResource(R.drawable.sun_50);
            settingText.setTextColor(
                    getActivity().getResources().getColor(R.color.red));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ffdb4437"), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#ffdb4437"), PorterDuff.Mode.SRC_IN);
            }

        } else if(mSettingTemperature < mCurrentTemperature) {
            //TODO:change heatingState
            heatingIcon.setBackgroundResource(R.drawable.winter_50);
            settingText.setTextColor(
                    getActivity().getResources().getColor(R.color.blue));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ff4285f4"), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#ff4285f4"), PorterDuff.Mode.SRC_IN);
            }
        }else {
//            heatingIcon.setBackground(null);
            settingText.setTextColor(
                    getActivity().getResources().getColor(R.color.green));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#ff0f9d58"), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#ff0f9d58"), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 400);

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
        dialog.dismiss();
        mListener.setTemperatureState(mSettingTemperature, mHeatingState);
    }

    public interface OnTemperatureInteractionListener {

        void showTemperatureDialog();

        void setTemperatureState(int settingTemperature, String heatingState);

    }

}
