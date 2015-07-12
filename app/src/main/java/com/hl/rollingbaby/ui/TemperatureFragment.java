package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.views.SeekArc;
import com.hl.rollingbaby.views.SeekArc.OnSeekArcChangeListener;

public class TemperatureFragment extends Fragment {

    private static final String TAG = "TemperatureFragment";

    private SeekArc mSeekArc;
//    private SeekBar mRotation;
//    private SeekBar mStartAngle;
//    private SeekBar mSweepAngle;
//    private SeekBar mArcWidth;
//    private SeekBar mProgressWidth;
//    private CheckBox mRoundedEdges;
//    private CheckBox mTouchInside;
//    private CheckBox mClockwise;
    private TextView mSeekArcProgress;

    private static final String ARG_TEMPERATURE = Constants.CURRENT_TEMPERATURE_VALUE;
    private static final String ARG_HEATING_STATE = Constants.HEATING_STATE;
    private static final String ARG_SOUND_MODE = Constants.CURRENT_SOUND_MODE;
    private static final String ARG_PLAY_STATE = Constants.PLAY_STATE;
    private static final String ARG_SWING_MODE = Constants.CURRENT_SWING_MODE;

    private int mTemperature;
    private String mHeatingState;

    private OnTemperatureFragmentInteractionListener mListener;

    public static TemperatureFragment newInstance(
            int temperature, String heatingState,
            String soundMode, int playState, String swingMode) {
        TemperatureFragment fragment = new TemperatureFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TEMPERATURE, temperature);
        args.putString(ARG_HEATING_STATE, heatingState);
        args.putString(ARG_SOUND_MODE, soundMode);
        args.putInt(ARG_PLAY_STATE, playState);
        args.putString(ARG_SWING_MODE, swingMode);
        fragment.setArguments(args);
        return fragment;
    }

    public TemperatureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTemperature = getArguments().getInt(ARG_TEMPERATURE);
            mHeatingState = getArguments().getString(ARG_HEATING_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        mSeekArc = (SeekArc) view.findViewById(R.id.seekArc);
        mSeekArcProgress = (TextView) view.findViewById(R.id.seekArcProgress);
//        mRotation = (SeekBar) view.findViewById(R.id.rotation);
//        mStartAngle = (SeekBar) view.findViewById(R.id.startAngle);
//        mSweepAngle  = (SeekBar) view.findViewById(R.id.sweepAngle);
//        mArcWidth = (SeekBar) view.findViewById(R.id.arcWidth);
//        mProgressWidth = (SeekBar) view.findViewById(R.id.progressWidth);
//        mRoundedEdges = (CheckBox) view.findViewById(R.id.roundedEdges);
//        mTouchInside = (CheckBox) view.findViewById(R.id.touchInside);
//        mClockwise = (CheckBox) view.findViewById(R.id.clockwise);
//
//        mRotation.setProgress(mSeekArc.getArcRotation());
//        mStartAngle.setProgress(mSeekArc.getStartAngle());
//        mSweepAngle.setProgress(mSeekArc.getSweepAngle());
//        mArcWidth.setProgress(mSeekArc.getArcWidth());
//        mProgressWidth.setProgress(mSeekArc.getProgressWidth());

        setTemperature(mTemperature, mHeatingState);

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
                mTemperature = progress;
                mSeekArcProgress.setText(String.valueOf(progress));
            }
        });

//        mRotation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar arg0) {
//
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar arg0) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar view, int progress, boolean fromUser) {
//                mSeekArc.setArcRotation(progress);
//                mSeekArc.invalidate();
//            }
//        });
//
//        mStartAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar arg0) {
//
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar arg0) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar view, int progress, boolean fromUser) {
//                mSeekArc.setStartAngle(progress);
//                mSeekArc.invalidate();
//            }
//        });
//
//        mSweepAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar arg0) {
//
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar arg0) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar view, int progress, boolean fromUser) {
//                mSeekArc.setSweepAngle(progress);
//                mSeekArc.invalidate();
//            }
//        });
//
//        mArcWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar arg0) {
//
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar arg0) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar view, int progress, boolean fromUser) {
//                mSeekArc.setArcWidth(progress);
//                mSeekArc.invalidate();
//            }
//        });
//
//        mProgressWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar arg0) {
//
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar arg0) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar view, int progress, boolean fromUser) {
//                mSeekArc.setProgressWidth(progress);
//                mSeekArc.invalidate();
//            }
//        });
//
//        mRoundedEdges.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//                mSeekArc.setRoundedEdges(isChecked);
//                mSeekArc.invalidate();
//            }
//        });
//
//        mTouchInside.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//                mSeekArc.setTouchInSide(isChecked);
//            }
//        });
//
//        mClockwise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//                mSeekArc.setClockwise(isChecked);
//                mSeekArc.invalidate();
//            }
//        });

        return view;
    }

    public void setTemperature(int temperature, String heatingState) {
//        mTemperature = temperature;
//        mHeatingState = heatingState;
        mSeekArc.setProgress(temperature);
        mSeekArcProgress.setText(temperature + "/" + heatingState);
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

    public void test() {
       Log.d(TAG, "test");
    }

    public int getTemperatureState() {
        return mTemperature;
    }

    public interface OnTemperatureFragmentInteractionListener {

        public void setTemperatureState(int temperature);

    }
}
