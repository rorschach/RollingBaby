package com.hl.rollingbaby.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;
import com.hl.rollingbaby.network.MessageService;
import com.hl.rollingbaby.network.StatusService;

public class SwingActivity extends BaseActivity implements
        SwingFragment.OnSwingFragmentInteractionListener {

    private MessageService.MessageBinder messageBinder;
    private MessageService messageService;
    private SwingFragment swingFragment;

    private String mSwingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swing);

        if (savedInstanceState == null) {
            swingFragment = SwingFragment.newInstance(Constants.SWING_CLOSE);
            swingFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(
                    R.id.swing_container, swingFragment).commit();
        }

        init();
    }


    private void init() {
        initViews();
        getData();
        showContent();
    }

    @Override
    public void initViews() {

    }

    @Override
    public void getData() {

    }

    @Override
    public void showContent() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {

            StatusService.startActionProcessSwing(this, mSwingMode);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public String getSwingMode() {
        return null;
    }

    @Override
    public void saveSwingMode(String soundMode) {

    }
}
