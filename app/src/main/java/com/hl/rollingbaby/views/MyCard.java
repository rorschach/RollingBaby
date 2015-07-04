package com.hl.rollingbaby.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hl.rollingbaby.R;

/**
 * Created by test on 15-6-7.
 */
public class MyCard extends RelativeLayout {
    private TextView header;
    private TextView description;
    private ImageView icon;

    public MyCard(Context context) {
        super(context);
        init();
    }

    public MyCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setResouce(int headerText, int iconSrc) {
        header.setText(headerText);
        icon.setImageResource(iconSrc);
    }

    public void setResouce(String headerText, int iconSrc) {
        header.setText(headerText);
        icon.setImageResource(iconSrc);
    }

    private void init() {
        inflate(getContext(), R.layout.card, this);
//        setBackgroundColor(getResources().getColor(R.color.card_background));

        //Add missing top level attributes
        int padding = (int) getResources().getDimension(R.dimen.card_padding);
        setPadding(padding, padding, padding, padding);

        this.header = (TextView) findViewById(R.id.header);
        this.icon = (ImageView) findViewById(R.id.icon);
    }
}