package com.hl.rollingbaby.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.Utils;
import com.hl.rollingbaby.interfaces.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 摇摆状态界面
 */
public class SwingDialogFragment extends BaseDialogFragment {

    private static final String TAG = "SwingDialogFragment";

    @Bind(R.id.swing_icon)
    ImageView swingIcon;
    @Bind(R.id.swing_tx)
    TextView swingTx;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private String mSwingMode = "s";
    private boolean isSwingMode = false;
    private ObjectAnimator animator;

    //持有的Activity实例
    private OnSwingInteractionListener mListener;

    /**
     * @param swingMode 摇摆状态
     * @return fragment实例
     */
    public static SwingDialogFragment newInstance(String swingMode) {
        SwingDialogFragment fragment = new SwingDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_SWING_MODE, swingMode);
        fragment.setArguments(args);
        return fragment;
    }

    public SwingDialogFragment() {
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
        View view = inflater.inflate(R.layout.fragment_swing_dialog, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    /**
     * 初始化各控件
     */
    @Override
    protected void initView() {
        swingIcon.setPivotX(20f);
        swingIcon.setPivotY(0f);

        animator = ObjectAnimator.ofFloat(swingIcon, "rotation", 40F, -40F);
        animator.setDuration(1500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());
        resetSwingAnimation();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSwingMode = !isSwingMode;
                resetSwingAnimation();
                mListener.updateSwingStatus(mSwingMode);
            }
        });

        Log.d(TAG, "onCreate : " + mSwingMode);
    }

    /**
     * 开启/关闭摇摆动画
     */
    private void resetSwingAnimation() {
        if (!isSwingMode) {
            animator.cancel();
            mSwingMode = Constants.SWING_CLOSE;
            fab.setImageResource(R.drawable.play_bt_64);
            swingTx.setText(getActivity().getResources().getString(R.string.swing_close_tx));
        } else {
            animator.start();
            mSwingMode = Constants.SWING_OPEN;
            fab.setImageResource(R.drawable.pause_bt_64);
            swingTx.setText(getActivity().getResources().getString(R.string.swing_open_tx));
        }
    }

    /**
     * @param activity 需要持有的Activity实例
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSwingInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSwingInteractionListener");
        }
    }

    /**
     * 释放持有的对Activity的引用
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        animator.cancel();
    }

    /**
     * 刷新数据
     *
     * @param swingMode 摇摆模式
     */
    public void refreshData(String swingMode) {
        Log.d(TAG, "refreshData : " + mSwingMode);
        mSwingMode = swingMode;
        Bundle args = new Bundle();
        args.putString(Constants.ARG_SWING_MODE, swingMode);
        this.setArguments(args);
        Log.d(TAG, "refreshData : " + mSwingMode);
    }

    //对外部公开的接口
    public interface OnSwingInteractionListener {

        //进入摇摆状态界面
        void showSwingDialog();

        //更新数据
        void updateSwingStatus(String swingMode);

        void refreshSwingItemData();

    }

}
