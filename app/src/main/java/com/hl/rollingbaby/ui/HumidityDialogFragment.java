package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.Utils;
import com.hl.rollingbaby.interfaces.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */
public class HumidityDialogFragment extends BaseDialogFragment {

    @Bind(R.id.humidity)
    TextView humidity;
    @Bind(R.id.wetting_state)
    TextView wettingState;

    private static final String TAG = "HumidityDialogFragment";

    private AppCompatDialog dialog;
    private int mHumidity;

    private OnHumidityInteractionListener mListener;

    public static HumidityDialogFragment newInstance(int humidity) {
        HumidityDialogFragment fragment = new HumidityDialogFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_HUMIDITY, humidity);
        fragment.setArguments(args);
        return fragment;
    }

    public HumidityDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHumidity = getArguments().getInt(Constants.ARG_HUMIDITY, 50);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_humidity_dialog, container, false);
        ButterKnife.bind(this, view);

        initView(view);
        return view;
    }

    @Override
    protected void initView(View view) {
        String text = getActivity().getResources().getString(R.string.humidity_state)
                + mHumidity + "%";
        humidity.setText(text);
        if (mHumidity >= 80) {
            wettingState.setText(getActivity().getResources().getString(R.string.isWetting));
        }else {
            wettingState.setText(getActivity().getResources().getString(R.string.notWetting));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
        int height = Utils.dpToPx(Utils.getScreenHeight(getActivity()) / 5);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnHumidityInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHumidityInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refreshData(int humidity) {
        mHumidity = humidity;
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_HUMIDITY, mHumidity);
        this.setArguments(args);
        Log.d(TAG, "refreshData : " + mHumidity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface OnHumidityInteractionListener {

        void showHumidityDialog();

        void refreshHumidityItemData();

    }

}
