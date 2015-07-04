package com.hl.rollingbaby.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hl.rollingbaby.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class HumidityFragment extends Fragment {

    public HumidityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_humidity, container, false);
    }
}
