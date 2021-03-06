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

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.interfaces.Constants;
import com.hl.rollingbaby.views.WaveView;

/**
 * 声音状态界面
 */
public class SoundDialogFragment extends DialogFragment {

    private static final String TAG = "SoundDialogFragment";

    private String mSoundMode;
    private int mPlayState;

    private static String sSoundTemp;
    private static int sPlayTemp;

    //持有的Activity实例
    private OnSoundInteractionListener mListener;

    private AppCompatDialog dialog;
    private WaveView waveView;
    private ImageButton playBt;
    private Switch modeSwitch;
    private TextView test;

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
        sSoundTemp = mSoundMode;
        sPlayTemp = mPlayState;
        Log.d(TAG, "onCreate : " + mSoundMode + ":" + mPlayState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sound_dialog, container, false);
        initView(v);
        return v;
    }

    /**
     * 初始化各控件
     * @param view 对应的布局视图
     */
    private void initView(View view) {

        Log.d(TAG, "initView : " + mSoundMode + ":" + mPlayState);

        waveView = (WaveView) view.findViewById(R.id.wave_view);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        playBt = (ImageButton) view.findViewById(R.id.play);
        ImageButton rewindBt = (ImageButton) view.findViewById(R.id.rewind);
        ImageButton forwardBt = (ImageButton) view.findViewById(R.id.forward);
        modeSwitch = (Switch) view.findViewById(R.id.mode_switch);
        test = (TextView) view.findViewById(R.id.test);

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

        resetView();

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
                resetView();
                mListener.updateSoundStatus(mSoundMode, mPlayState);
//                if (!sSoundTemp.equals(mSoundMode) || (sPlayTemp != mPlayState)) {
//                }
            }
        });

        forwardBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Constants.SOUND_NEXT;
                mListener.updateSoundStatus(mSoundMode, mPlayState);
//                if (!sSoundTemp.equals(mSoundMode) || (sPlayTemp != mPlayState)) {
//                }
            }
        });

        rewindBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayState = Constants.SOUND_LAST;
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
                    mSoundMode = Constants.SOUND_MUSIC;
                    temp = getActivity().getResources().getString(R.string.music_mode);
                } else {
                    mSoundMode = Constants.SOUND_STORY;
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

        if (mPlayState == Constants.SOUND_PAUSE_PLAY) {
            waveView.setPlayStation(true);
            playBt.setImageResource(R.drawable.pause_bt_50);
        } else {
            waveView.setPlayStation(false);
            playBt.setImageResource(R.drawable.play_bt_50);
        }

        String temp;
        if (mSoundMode.equals(Constants.SOUND_MUSIC)) {
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
        dialog.setTitle("Sound");
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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
        dialog.dismiss();
    }

    /**
     *释放持有的对Activity的引用
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * 刷新数据
     * @param soundMode 声音模式
     * @param playState 播放状态
     */
    public void refreshData(String soundMode, int playState) {
        mSoundMode = soundMode;
        mPlayState = playState;
        Bundle args = new Bundle();
        args.putString(Constants.ARG_SOUND_MODE, soundMode);
        args.putInt(Constants.ARG_PLAY_STATE, playState);
        this.setArguments(args);
        Log.d(TAG, "refreshData : " + mSoundMode + ":" + mPlayState);
    }

    //对外部公开的接口
    public interface OnSoundInteractionListener {

        //进入声音状态界面
        void showSoundDialog();

        //更新数据
        void updateSoundStatus(String soundMode, int playState);
    }

}
