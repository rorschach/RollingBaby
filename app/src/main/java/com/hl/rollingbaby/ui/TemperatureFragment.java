package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.views.SeekArc;
import com.hl.rollingbaby.views.SeekArc.OnSeekArcChangeListener;

public class TemperatureFragment extends Fragment {

    private static final String TAG = "TemperatureFragment";

    private SeekArc mSeekArc;
    private TextView mSeekArcProgress;

    private int currentTemperature;
    private int settingTemperature;
    private String mHeatingState;

    private OnTemperatureFragmentInteractionListener mListener;

    public static TemperatureFragment newInstance(
            int currentTem, int settingTem, String heatingState,
            String soundMode, int playState, String swingMode) {
        TemperatureFragment fragment = new TemperatureFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_CURRENT_TEMPERATURE, currentTem);
        args.putInt(Constants.ARG_SETTING_TEMPERATURE, settingTem);
        args.putString(Constants.ARG_HEATING_STATE, heatingState);
        args.putString(Constants.ARG_SOUND_MODE, soundMode);
        args.putInt(Constants.ARG_PLAY_STATE, playState);
        args.putString(Constants.ARG_SWING_MODE, swingMode);
        fragment.setArguments(args);
        return fragment;
    }

    public TemperatureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTemperature = getArguments().getInt(Constants.ARG_CURRENT_TEMPERATURE);
            settingTemperature = getArguments().getInt(Constants.ARG_SETTING_TEMPERATURE);
            mHeatingState = getArguments().getString(Constants.ARG_HEATING_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        mSeekArc = (SeekArc) view.findViewById(R.id.seekArc);
        mSeekArcProgress = (TextView) view.findViewById(R.id.seekArcProgress);

        mSeekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress,
                                          boolean fromUser) {
                progress += 25;
                settingTemperature = progress;
                if (settingTemperature != currentTemperature) {
                    mSeekArcProgress.setText(currentTemperature + "~" + settingTemperature);
                } else {
                    mSeekArcProgress.setText(currentTemperature + "");
                }
            }
        });

        setTemperature(currentTemperature, settingTemperature);

        return view;
    }

    public void setTemperature(int currentTem, int settingTem) {
        mSeekArc.setProgress(currentTem - 25);
        if (currentTem != settingTem) {
            mSeekArcProgress.setText(currentTem + "~" + settingTem);
        } else {
            mSeekArcProgress.setText(currentTem + "");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTemperatureFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public int getTemperatureState() {
        return settingTemperature;
    }

    public interface OnTemperatureFragmentInteractionListener {
        void setTemperatureState(int temperature);
    }
}
