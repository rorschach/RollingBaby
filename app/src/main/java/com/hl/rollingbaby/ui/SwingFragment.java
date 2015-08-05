package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;

public class SwingFragment extends Fragment {

    private static final String TAG = "SwingFragment";

    private OnSwingFragmentInteractionListener mListener;

    private String mSwingMode;

//    private static final String ARG_CURRENT_TEMPERATURE = Constants.CURRENT_TEMPERATURE_VALUE;
//    private static final String ARG_SETTING_TEMPERATURE = Constants.SETTING_TEMPERATURE_VALUE;
//    private static final String ARG_HEATING_STATE = Constants.HEATING_STATE;
//    private static final String ARG_SOUND_MODE = Constants.CURRENT_SOUND_MODE;
//    private static final String ARG_PLAY_STATE = Constants.PLAY_STATE;
//    private static final String ARG_SWING_MODE = Constants.CURRENT_SWING_MODE;

    public static SwingFragment newInstance(
            int currentTem, int settingTem, String heatingState,
            String soundMode, int playState, String swingMode) {
        SwingFragment fragment = new SwingFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_CURRENT_TEMPERATURE, currentTem);
        args.putInt(Constants.ARG_SETTING_TEMPERATURE, settingTem);
        args.putString(Constants.ARG_HEATING_STATE, heatingState);
        args.putString(Constants.ARG_SOUND_MODE, soundMode);
        args.putInt(Constants.ARG_PLAY_STATE, playState);
        args.putString(Constants.ARG_SWING_MODE, swingMode);
        fragment.setArguments(args);
        Log.d(TAG, swingMode);
        return fragment;
    }

    public SwingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSwingMode = getArguments().getString(Constants.ARG_SWING_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swing, container, false);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSwingFragmentInteractionListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSoundInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getSwingMode() {
        return mSwingMode;
    }

    public interface OnSwingFragmentInteractionListener {
        void setSwingMode(String soundMode);
    }

}
