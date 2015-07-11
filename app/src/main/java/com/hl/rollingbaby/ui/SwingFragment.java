package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.skyfishjy.library.RippleBackground;

public class SwingFragment extends Fragment {

    private static final String TAG = "SwingFragment";

    private OnSwingFragmentInteractionListener mListener;

    private String mSwingMode;

    private static final String ARG_SWING_MODE = Constants.CURRENT_SWING_MODE;

    public static SwingFragment newInstance(String swingMode) {
        SwingFragment fragment = new SwingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SWING_MODE, swingMode);
        fragment.setArguments(args);
        Log.d(TAG, swingMode);
        return fragment;
    }

    public SwingFragment() {
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
        View view = inflater.inflate(R.layout.fragment_swing, container, false);

        final RippleBackground rippleBackground=
                (RippleBackground)view.findViewById(R.id.content);
        ImageView imageView=(ImageView)view.findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSwingFragmentInteractionListener) activity;

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

    public interface OnSwingFragmentInteractionListener {

        public void setSwingMode(String soundMode);
        //mListener.setSwingMode(mSwingMode);
    }

}
