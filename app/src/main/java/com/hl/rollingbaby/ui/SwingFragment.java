package com.hl.rollingbaby.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hl.rollingbaby.R;
import com.skyfishjy.library.RippleBackground;

/**
 * A placeholder fragment containing a simple view.
 */
public class SwingFragment extends Fragment {

    public SwingFragment() {
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
//                rippleBackground.stopRippleAnimation();
            }
        });

        return view;
    }
}
