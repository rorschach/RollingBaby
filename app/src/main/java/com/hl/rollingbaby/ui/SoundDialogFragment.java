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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.views.WaveView;


public class SoundDialogFragment extends DialogFragment {

    private static final String TAG = "SoundDialogFragment";

    private static final String ARG_SOUND_MODE = "SOUND_MODE";
    private static final String ARG_PLAY_STATE = "PLAY_STATE";

    private String mSoundMode;
    private int mPlayState;

    private OnSoundInteractionListener mListener;

    private AppCompatDialog dialog;
    private WaveView waveView;
    private ImageButton playBt;
    private TextView test;


    public static SoundDialogFragment newInstance(String soundMode, int playState) {
        SoundDialogFragment fragment = new SoundDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SOUND_MODE, soundMode);
        args.putInt(ARG_PLAY_STATE, playState);
        fragment.setArguments(args);
        return fragment;
    }

    public SoundDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSoundMode = getArguments().getString(ARG_SOUND_MODE);
            mPlayState = getArguments().getInt(ARG_PLAY_STATE, Constants.SOUND_STOP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sound_dialog, container, false);
        initView(v);
        return v;
    }

    private void initView(View view) {
        waveView = (WaveView) view.findViewById(R.id.wave_view);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        playBt = (ImageButton) view.findViewById(R.id.play);
        ImageButton rewindBt = (ImageButton) view.findViewById(R.id.rewind);
        ImageButton forwardBt = (ImageButton) view.findViewById(R.id.forward);
        Switch modeSwitch = (Switch) view.findViewById(R.id.mode_switch);
        test = (TextView) view.findViewById(R.id.test);

        test.setText("Mode : " + mSoundMode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#3498db"), PorterDuff.Mode.SRC_IN);
            seekBar.getThumb().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_IN);
        }
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
                mListener.setSoundStatus(mSoundMode, mPlayState);
            }
        });

        forwardBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Constants.SOUND_NEXT;
                mListener.setSoundStatus(mSoundMode, mPlayState);
            }
        });

        rewindBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Constants.SOUND_LAST;
                mListener.setSoundStatus(mSoundMode, mPlayState);
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
                String temp;
                if (isChecked) {
                    mSoundMode = Constants.SOUND_MUSIC;
                    temp = getActivity().getResources().getString(R.string.music_toast);
                } else {
                    mSoundMode = Constants.SOUND_STORY;
                    temp = getActivity().getResources().getString(R.string.story_toast);
                }
                test.setText(temp);
            }
        });
    }

    private void setPlayButton() {
        if (mPlayState == 0) {//is playing or pause
            waveView.setPlayStation(true);
            playBt.setImageResource(R.drawable.pause_bt_50);
        } else {
            waveView.setPlayStation(false);
            playBt.setImageResource(R.drawable.play_bt_50);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSoundInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSoundInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dialog.dismiss();
//        mListener.setSoundStatus(mSoundMode, mPlayState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setText(String text) {
        test.setText(text);
    }

    public interface OnSoundInteractionListener {

        void showSoundDialog();

        void setSoundStatus(String soundMode, int playState);
    }

}
