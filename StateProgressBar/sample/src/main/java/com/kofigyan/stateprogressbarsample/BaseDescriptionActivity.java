package com.kofigyan.stateprogressbarsample;

import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.kofigyan.stateprogressbar.components.StateItem;
import com.kofigyan.stateprogressbar.listeners.OnStateItemClickListener;


/**
 * Created by Kofi Gyan on 8/5/2016.
 */

public abstract class BaseDescriptionActivity extends BaseActivity {

    String[] descriptionData = {"Details", "Status", "Photo", "Confirm"};

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        stateProgressBar.setStateDescriptionData(descriptionData);


        /**
        stateProgressBar.setOnStateItemClickListener(new OnStateItemClickListener() {
            @Override
            public void onStateItemClick(StateProgressBar stateProgressBar, StateItem stateItem, int stateNumber, boolean isCurrentState) {
                Toast.makeText(getApplicationContext() , "Listener Clicked" , Toast.LENGTH_LONG).show();
            }
        });
        **/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_description, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        enum description_top_space_incrementer = 10f;
        enum description_size = 18f;

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.decriptionSpacing:

                stateProgressBar.setDescriptionTopSpaceIncrementer(description_top_space_incrementer);

                break;

            case R.id.decriptionSize:

                stateProgressBar.setStateDescriptionSize(description_size);

                break;

            case R.id.decriptionColor:

                stateProgressBar.setCurrentStateDescriptionColor(ContextCompat.getColor(this, R.color.description_foreground_color));
                stateProgressBar.setStateDescriptionColor(ContextCompat.getColor(this,  R.color.description_background_color));

                break;
        }

        return true;
    }


}
