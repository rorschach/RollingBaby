package com.hl.rollingbaby.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.hl.rollingbaby.R;

public class SwingDialogFragment extends DialogFragment {

    private static final String TAG = "SwingDialogFragment";

    private static final String ARG_SWING_MODE = "SWING_MODE";

    private String mSwingMode;

    private OnSwingInteractionListener mListener;

    private AppCompatDialog dialog;

    private TextView tx;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swing_dialog, container, false);
        tx = (TextView) view.findViewById(R.id.tx);
        tx.setText(mSwingMode);

//        float height = view.getMeasuredHeight();
//        float width = view.getMeasuredWidth();
        tx.setPivotX(50f);
        tx.setPivotY(-100f);

        ObjectAnimator animator = ObjectAnimator.ofFloat(tx, "rotation", 45F, -45F);
        animator.setDuration(1500);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 400);
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
        mListener.setSwingState(mSwingMode);
    }

    public interface OnSwingInteractionListener {

        void showSwingDialog();

        void setSwingState(String swingMode);

    }

}
