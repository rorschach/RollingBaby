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

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.views.WaveView;

public class SoundFragment extends Fragment {

    private static final String TAG = "SoundFragment";

    private SeekBar seekBar;
    private WaveView waveView;
    private ImageButton playButton;

    private int mPlayState;
    private String mSoundMode;

    private static final String ARG_SOUND_MODE = Constants.CURRENT_SOUND_MODE;
    private static final String ARG_PLAY_STATE = Constants.PLAY_STATE;

    private OnSoundFragmentInteractionListener mListener;

    public static SoundFragment newInstance(int playState, String soundMode) {
        SoundFragment fragment = new SoundFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SOUND_MODE, playState);
        args.putString(ARG_PLAY_STATE, soundMode);
        fragment.setArguments(args);
        return fragment;
    }

    public SoundFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPlayState = getArguments().getInt(ARG_SOUND_MODE);
            mSoundMode = getArguments().getString(ARG_PLAY_STATE);
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
        playButton = (ImageButton) view.findViewById(R.id.play);

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

//        mPlayState = mListener.getPlayState();
//        mSoundMode = mListener.getSoundMode();


        setPlayButton();
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Math.abs(mPlayState - 1);
                if (mPlayState == 1) {
                    waveView.setPlayStation(true);
                } else {
                    waveView.setPlayStation(false);
                }
                Log.d(TAG, "Play state after click: " + mPlayState);
                setPlayButton();
                mListener.savePlayState(mPlayState);
            }
        });
    }

    private void setPlayButton() {
        if (mPlayState == 1) {
            playButton.setImageResource(R.drawable.pause);
        }else {
            playButton.setImageResource(R.drawable.play);
        }
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

    public interface OnSoundFragmentInteractionListener {

        public int getPlayState();

        public void savePlayState(int playState);

        public String getSoundMode();

        public void saveSoundMode(String soundMode);
    }


}
