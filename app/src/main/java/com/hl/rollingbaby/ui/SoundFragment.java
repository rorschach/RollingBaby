package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.views.WaveView;

public class SoundFragment extends Fragment {

    private static final String TAG = "SoundFragment";

    private SeekBar seekBar;
    private WaveView waveView;
    private ImageButton playBt;
    private ImageButton rewindBt;
    private ImageButton forwardBt;

    private int mPlayState;
    private String mSoundMode;

    private static final String ARG_TEMPERATURE = Constants.CURRENT_TEMPERATURE_VALUE;
    private static final String ARG_HEATING_STATE = Constants.HEATING_STATE;
    private static final String ARG_SOUND_MODE = Constants.CURRENT_SOUND_MODE;
    private static final String ARG_PLAY_STATE = Constants.PLAY_STATE;
    private static final String ARG_SWING_MODE = Constants.CURRENT_SWING_MODE;

    private OnSoundFragmentInteractionListener mListener;

    public static SoundFragment newInstance(
            int temperature, String heatingState,
            String soundMode, int playState, String swingMode) {
        SoundFragment fragment = new SoundFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TEMPERATURE, temperature);
        args.putString(ARG_HEATING_STATE, heatingState);
        args.putString(ARG_SOUND_MODE, soundMode);
        args.putInt(ARG_PLAY_STATE, playState);
        args.putString(ARG_SWING_MODE, swingMode);
        fragment.setArguments(args);
        return fragment;
    }

    public SoundFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSoundMode = getArguments().getString(ARG_SOUND_MODE);
            mPlayState = getArguments().getInt(ARG_PLAY_STATE);

            Log.d(TAG, mSoundMode + ":" + mPlayState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound, container, false);

        initViews(view);

        return view;
    }

    public void initViews(View view) {
        waveView = (WaveView) view.findViewById(R.id.wave_view);
        seekBar = (SeekBar)  view.findViewById(R.id.seek_bar);
        playBt = (ImageButton) view.findViewById(R.id.play);
        rewindBt = (ImageButton) view.findViewById(R.id.rewind);
        forwardBt = (ImageButton) view.findViewById(R.id.forward);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waveView.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        setPlayButton();
        playBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Math.abs(mPlayState - 1);
                if (mPlayState == 0) {
                    waveView.setPlayStation(true);
                } else {
                    waveView.setPlayStation(false);
                }
                Log.d(TAG, "Play state after click: " + mPlayState);
                setPlayButton();
                mListener.setSoundState(mPlayState, mSoundMode);
            }
        });

        rewindBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = 3;
                mListener.setSoundState(mPlayState, mSoundMode);
            }
        });

        forwardBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = 2;
                mListener.setSoundState(mPlayState, mSoundMode);
            }
        });

    }

    private void setPlayButton() {
        if (mPlayState == 0) {
            waveView.setPlayStation(true);
            playBt.setImageResource(R.drawable.pause);
        }else {
            waveView.setPlayStation(false);
            playBt.setImageResource(R.drawable.play);
        }
        Toast.makeText(getActivity(), mSoundMode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSoundFragmentInteractionListener) activity;
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

    public int getPlayState() {
       return mPlayState;
    }

    public String getSoundMode() {
        return mSoundMode;
    }

    public interface OnSoundFragmentInteractionListener {

        public void setSoundState(int playState, String soundMode);
    }

}
