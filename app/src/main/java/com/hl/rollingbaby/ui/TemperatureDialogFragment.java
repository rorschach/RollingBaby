package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hl.rollingbaby.R;

public class TemperatureDialogFragment extends DialogFragment {
    private static final String ARG_CURRENT_TEMPERATURE = "CURRENT_TEMPERATURE";
    private static final String ARG_SETTING_TEMPERATURE = "SETTING_TEMPERATURE";
    private static final String ARG_HEATING_STATE = "HEATING_STATE";

    private int mCurrentTemperature;
    private int mSettingTemperature;
    private String mHeatingState;

    private OnTemperatureInteractionListener mListener;

    private AppCompatDialog dialog;
    private TextView mCurrentTmeTx;
    private TextView mSettingTemTx;
    private SeekBar mTemperatureSeekBar;

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
        mCurrentTmeTx = (TextView) view.findViewById(R.id.current_temperature);
        mSettingTemTx = (TextView) view.findViewById(R.id.setting_temperature);
        mTemperatureSeekBar = (SeekBar) view.findViewById(R.id.temperature_seekBar);

        mCurrentTmeTx.setText("Current : " + mCurrentTemperature);
        mSettingTemTx.setText("Setting : " + mSettingTemperature);
        mTemperatureSeekBar.setProgress(mSettingTemperature);
        mTemperatureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mSettingTemperature = progress;
                mSettingTemTx.setText("Setting : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
//        dialog.getWindow().setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        mSettingTemperature = mTemperatureSeekBar.getProgress();
        mListener.setTemperatureState(mSettingTemperature, mHeatingState);
    }

    public interface OnTemperatureInteractionListener {

        void showTemperatureDialog();

        void setTemperatureState(int settingTemperature, String heatingState);

    }

}
