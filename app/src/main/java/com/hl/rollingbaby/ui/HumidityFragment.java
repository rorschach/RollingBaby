package com.hl.rollingbaby.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.views.RubberIndicator;

public class HumidityFragment extends Fragment {

    private RubberIndicator mRubberIndicator;
    private Button button;

    public HumidityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_humidity, container, false);
        mRubberIndicator = (RubberIndicator) view.findViewById(R.id.rubber);


        mRubberIndicator.setCount(3);

        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRubberIndicator.move();
            }
        });
        return view;
    }
}
