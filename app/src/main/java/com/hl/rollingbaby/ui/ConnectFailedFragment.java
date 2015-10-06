package com.hl.rollingbaby.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.view.ViewGroup;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.Utils;


public class ConnectFailedFragment extends BaseDialogFragment {

    private AppCompatDialog dialog;

    public static ConnectFailedFragment newInstance() {
        ConnectFailedFragment fragment = new ConnectFailedFragment();
        return fragment;
    }

    public ConnectFailedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setIsDialogShown(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.fail_title));
        builder.setView(R.layout.failed_dialog);
        builder.setPositiveButton(getResources().getString(R.string.position_bt),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                });

        builder.setNegativeButton(getResources().getString(R.string.navigation_bt),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog = builder.create();
        int height = Utils.dpToPx(Utils.getScreenHeight(getActivity()) / 4);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        return dialog;
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.setIsDialogShown(false);
    }

}
