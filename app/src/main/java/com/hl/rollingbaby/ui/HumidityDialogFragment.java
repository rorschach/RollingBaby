package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.interfaces.Constants;

/**
 */
public class HumidityDialogFragment extends DialogFragment {

    private static final String TAG = "HumidityDialogFragment";

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
        // Required empty public constructor
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
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(mHumidity + "%");
        return view;
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

    public interface OnHumidityInteractionListener {

        void showHumidityDialog();

        void refreshHumidityItemData();

    }

}
