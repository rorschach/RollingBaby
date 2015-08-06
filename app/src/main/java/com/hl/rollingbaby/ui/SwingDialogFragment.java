package com.hl.rollingbaby.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;

public class SwingDialogFragment extends DialogFragment {

    private static final String TAG = "SwingDialogFragment";

    private static final String ARG_SWING_MODE = "SWING_MODE";

    private String mSwingMode;
    private static String sSwingTemp;
    private boolean isSwingMode = false;
    ObjectAnimator animator;

    private OnSwingInteractionListener mListener;

    private AppCompatDialog dialog;

    //    private TextView tx;
    private ImageView swingIcon;
    private FloatingActionButton actionButton;

    public static SwingDialogFragment newInstance(String swingMode) {
        SwingDialogFragment fragment = new SwingDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SWING_MODE, swingMode);
        fragment.setArguments(args);
        return fragment;
    }

    public SwingDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSwingMode = getArguments().getString(ARG_SWING_MODE);
        }

        sSwingTemp = mSwingMode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swing_dialog, container, false);
//        tx = (TextView) view.findViewById(R.id.tx);
//        tx.setText(mSwingMode);
        swingIcon = (ImageView) view.findViewById(R.id.swing_icon);

        actionButton = (FloatingActionButton) view.findViewById(R.id.fab);

        swingIcon.setPivotX(50f);
        swingIcon.setPivotY(-250f);

        animator = ObjectAnimator.ofFloat(swingIcon, "rotation", 45F, -45F);
        animator.setDuration(1500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());


        resetSwingAnimation();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSwingMode = !isSwingMode;
                resetSwingAnimation();
            }
        });

        return view;
    }

    private void resetSwingAnimation() {
        if (!isSwingMode) {
            animator.cancel();
            mSwingMode = Constants.SWING_CLOSE;
            actionButton.setImageResource(R.drawable.play_bt_64);
        }else {
            animator.start();
            mSwingMode = Constants.SWING_SLEEP;
            actionButton.setImageResource(R.drawable.pause_bt_64);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, 800);
        return dialog;
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        dialog.dismiss();
        animator.cancel();
        if (!sSwingTemp.equals(mSwingMode)) {
            mListener.setSwingState(mSwingMode);
        }
    }

    public void refreshView(String swingMode) {
        mSwingMode = swingMode;
        Bundle args = new Bundle();
        args.putString(Constants.ARG_SWING_MODE, swingMode);
        this.setArguments(args);
    }

    public interface OnSwingInteractionListener {

        void showSwingDialog();

        void setSwingState(String swingMode);

    }

}
