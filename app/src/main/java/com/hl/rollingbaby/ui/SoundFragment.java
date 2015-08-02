package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.views.WaveView;

public class SoundFragment extends Fragment {

    private static final String TAG = "SoundFragment";

    private WaveView waveView;
    private ImageButton playBt;

    private int mPlayState;
    private String mSoundMode;

    private OnSoundFragmentInteractionListener mListener;

    public static SoundFragment newInstance(
            int currentTem, int settingTem,String heatingState,
            String soundMode, int playState, String swingMode) {
        SoundFragment fragment = new SoundFragment();
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

    public SoundFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSoundMode = getArguments().getString(Constants.ARG_SOUND_MODE);
            mPlayState = getArguments().getInt(Constants.ARG_PLAY_STATE);
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
        SeekBar seekBar = (SeekBar)  view.findViewById(R.id.seek_bar);
        playBt = (ImageButton) view.findViewById(R.id.play);
        ImageButton rewindBt = (ImageButton) view.findViewById(R.id.rewind);
        ImageButton forwardBt = (ImageButton) view.findViewById(R.id.forward);
        Switch modeSwitch = (Switch) view.findViewById(R.id.mode_switch);

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
                if (mPlayState == Constants.SOUND_PAUSE_PLAY) {
                    waveView.setPlayStation(true);
                } else {
                    waveView.setPlayStation(false);
                }
                Log.d(TAG, "Play state after click: " + mPlayState);
                setPlayButton();
                mListener.setSoundState(mPlayState, mSoundMode);
            }
        });

        forwardBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Constants.SOUND_NEXT;
                mListener.setSoundState(mPlayState, mSoundMode);
            }
        });

        rewindBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Constants.SOUND_PREVIOUS;
                mListener.setSoundState(mPlayState, mSoundMode);
            }
        });

        if (mSoundMode.equals(Constants.SOUND_MUSIC)) {
            modeSwitch.setChecked(true);
        } else {
            modeSwitch.setChecked(false);
        }
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSoundMode = Constants.SOUND_MUSIC;
                } else {
                    mSoundMode = Constants.SOUND_STORY;
                }
                String temp;
                if (mSoundMode.equals(Constants.SOUND_MUSIC)) {
                    temp = getActivity().getResources().getString(R.string.music_toast);
                } else {
                    temp = getActivity().getResources().getString(R.string.story_toast);
                }
                Toast.makeText(getActivity(), temp, Toast.LENGTH_SHORT).show();
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
        void setSoundState(int playState, String soundMode);
    }

}
