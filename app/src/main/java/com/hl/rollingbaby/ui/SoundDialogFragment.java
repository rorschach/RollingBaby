package com.hl.rollingbaby.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.Utils;
import com.hl.rollingbaby.interfaces.Constants;
import com.hl.rollingbaby.views.WaveView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 声音状态界面
 */
public class SoundDialogFragment extends BaseDialogFragment {

    private static final String TAG = "SoundDialogFragment";

    @Bind(R.id.wave_view)
    WaveView waveView;
    @Bind(R.id.test)
    TextView test;
    @Bind(R.id.rewind)
    ImageButton rewind;
    @Bind(R.id.play)
    ImageButton play;
    @Bind(R.id.forward)
    ImageButton forward;
    @Bind(R.id.seek_bar)
    SeekBar seekBar;
    @Bind(R.id.mode_switch)
    Switch modeSwitch;

    private String mSoundMode;
    private int mPlayState;

    //持有的Activity实例
    private OnSoundInteractionListener mListener;

    /**
     * 获取SoundDialogFragment的实例
     * @param soundMode 声音模式
     * @param playState 播放状态
     * @return fragment实例
     */
    public static SoundDialogFragment newInstance(String soundMode, int playState) {
        SoundDialogFragment fragment = new SoundDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_SOUND_MODE, soundMode);
        args.putInt(Constants.ARG_PLAY_STATE, playState);
        fragment.setArguments(args);
        return fragment;
    }

    public SoundDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSoundMode = getArguments().getString(Constants.ARG_SOUND_MODE);
            mPlayState = getArguments().getInt(Constants.ARG_PLAY_STATE, Constants.SOUND_STOP);
        }
        Log.d(TAG, "onCreate : " + mSoundMode + ":" + mPlayState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sound_dialog, container, false);
        ButterKnife.bind(this, v);
        initView(v);
        return v;
    }

    /**
     * 初始化各控件
     */
    @Override
    protected void initView(final View view) {

        if (Utils.isAndroid4P1()) {
            view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                @Override
                @TargetApi(16)
                public void onDraw() {
                    view.getViewTreeObserver().removeOnDrawListener(this);
                }
            });
        }

        if (Utils.isAndroid4P1()) {
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

        resetView();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Math.abs(mPlayState - 1);
                if (mPlayState == Constants.SOUND_RECEIVE_PAUSE || mPlayState == Constants.SOUND_STOP) {
                    waveView.setPlayStation(true);
                } else if (mPlayState == Constants.SOUND_PLAY) {
                    waveView.setPlayStation(false);
                }
                Log.d(TAG, "Play state after click: " + mPlayState);
                resetView();
                mListener.updateSoundStatus(mSoundMode, mPlayState);
//                if (!sSoundTemp.equals(mSoundMode) || (sPlayTemp != mPlayState)) {
//                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Constants.SOUND_NEXT;
                mListener.updateSoundStatus(mSoundMode, mPlayState);
//                if (!sSoundTemp.equals(mSoundMode) || (sPlayTemp != mPlayState)) {
//                }
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Constants.SOUND_PREVIOUS;
                mListener.updateSoundStatus(mSoundMode, mPlayState);
//                if (!sSoundTemp.equals(mSoundMode) || (sPlayTemp != mPlayState)) {
//                }
            }
        });

        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String temp;
                if (isChecked) {
                    mSoundMode = Constants.MUSIC_TAG;
                    temp = getActivity().getResources().getString(R.string.music_mode);
                } else {
                    mSoundMode = Constants.STORY_TAG;
                    temp = getActivity().getResources().getString(R.string.story_mode);
                }
                mListener.updateSoundStatus(mSoundMode, mPlayState);
                test.setText(temp);
            }
        });
    }

    /**
     * 根据用户操作更新视图
     */
    private void resetView() {

        if (mPlayState == Constants.SOUND_PLAY) {
            waveView.setPlayStation(true);
            play.setImageResource(R.drawable.pause_bt_50);
        } else {
            waveView.setPlayStation(false);
            play.setImageResource(R.drawable.play_bt_50);
        }

        String temp;
        if (mSoundMode.equals(Constants.MUSIC_TAG)) {
            modeSwitch.setChecked(true);
            temp = getActivity().getResources().getString(R.string.music_mode);
        } else {
            modeSwitch.setChecked(false);
            temp = getActivity().getResources().getString(R.string.story_mode);
        }
        test.setText(temp);
        Log.d(TAG, "resetView : " + mSoundMode + ":" + mPlayState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
        int height = Utils.dpToPx(Utils.getScreenHeight(getActivity()) / 3);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        return dialog;
    }

    /**
     * @param activity 需要持有的Activity实例
     */
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
    }

    /**
     * 释放持有的对Activity的引用
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * 刷新数据
     *
     * @param soundMode 声音模式
     * @param playState 播放状态
     */
    public void refreshData(String soundMode, int playState) {
        mSoundMode = soundMode;
        mPlayState = playState;
        Bundle args = new Bundle();
        args.putString(Constants.ARG_SOUND_MODE, mSoundMode);
        args.putInt(Constants.ARG_PLAY_STATE, mPlayState);
        this.setArguments(args);
        Log.d(TAG, "refreshData : " + mSoundMode + ":" + mPlayState);
    }

    //对外部公开的接口
    public interface OnSoundInteractionListener {

        //进入声音状态界面
        void showSoundDialog();

        //更新数据
        void updateSoundStatus(String soundMode, int playState);

        void refreshSoundItemData();
    }

}
