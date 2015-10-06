package com.hl.rollingbaby.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.ViewGroup;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.Utils;

import butterknife.ButterKnife;

/**
 * Created by root on 15-10-5.
 */
public class BaseDialogFragment extends DialogFragment {

    AppCompatDialog dialog;

    protected void initView() {
    }

    protected void initView(final View view) {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new AppCompatDialog(getActivity(), getTheme());
        int height = Utils.dpToPx(Utils.getScreenHeight(getActivity()) / 3);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
