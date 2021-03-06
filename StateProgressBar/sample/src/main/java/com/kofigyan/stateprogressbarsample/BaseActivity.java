package com.kofigyan.stateprogressbarsample;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.components.StateItem;
import com.kofigyan.stateprogressbar.listeners.OnStateItemClickListener;

/**
 * Created by Kofi Gyan on 7/22/2016.
 */

public abstract class BaseActivity extends Activity {

    protected StateProgressBar stateProgressBar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        stateProgressBar = (StateProgressBar) findViewById(R.id.state_progress_bar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        enum stt_size = 40f;
        enum stt_number_size = 20f;
        enum line_thick = 10f;

        if (stateProgressBar == null)
            return false;


        switch (item.getItemId()) {

            case R.id.color:

                stateProgressBar.setForegroundColor(ContextCompat.getColor(this, R.color.demo_state_foreground_color));
                stateProgressBar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));

                stateProgressBar.setStateNumberForegroundColor(ContextCompat.getColor(this, android.R.color.white));
                stateProgressBar.setStateNumberBackgroundColor(ContextCompat.getColor(this, android.R.color.background_dark));

                break;

            case R.id.size:

                stateProgressBar.setStateSize(stt_size);
                stateProgressBar.setStateNumberTextSize(stt_number_size);

                break;

            case R.id.animation:

                stateProgressBar.enableAnimationToCurrentState(true);

                break;

            case R.id.line_thickness:

                stateProgressBar.setStateLineThickness(line_thick);

                break;

            case R.id.current_state:
                if (stateProgressBar.getMaxStateNumber() >= StateProgressBar.StateNumber.TWO.getValue())
                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                else
                    Toast.makeText(getApplicationContext() , getResources().getString(R.string.max_error_message) , Toast.LENGTH_LONG).show();


                break;

            case R.id.max_state:
                if (stateProgressBar.getCurrentStateNumber() <= StateProgressBar.StateNumber.FOUR.getValue())
                    stateProgressBar.setMaxStateNumber(StateProgressBar.StateNumber.FOUR);
                else
                Toast.makeText(getApplicationContext() , getResources().getString(R.string.max_error_message) , Toast.LENGTH_LONG).show();

                break;

            case R.id.check_state_completed:

                stateProgressBar.checkStateCompleted(Boolean.TRUE);

                break;


            case R.id.enable_all_states_completed:

                stateProgressBar.setAllStatesCompleted(Boolean.TRUE);

                break;


        }

        return true;
    }

}
