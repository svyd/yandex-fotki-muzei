package com.blogspot.vsvydenko.yafotki_muzei;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vsvydenko on 26.02.14.
 */
public class SettingsActivity extends Activity{

    public static int ONE_HOUR      = 1000 * 60 * 60 * 1;
    public static int THREE_HOURS   = 1000 * 60 * 60 * 3;
    public static int SIX_HOURS     = 1000 * 60 * 60 * 6;
    public static int NINE_HOURS    = 1000 * 60 * 60 * 9;
    public static int TWELVE_HOURS  = 1000 * 60 * 60 * 12;
    public static int EVERY_DAY     = 1000 * 60 * 60 * 24;


    private List<Interval> mIntervalList = new ArrayList<Interval>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        initializeContent();
    }

    private void initializeContent() {
        setupIntervalSpinner();
        setupWiFiCheckBox();
    }

    private void setupWiFiCheckBox() {
        CheckBox mUpdateOnWifiOnly = (CheckBox) findViewById(R.id.updateOnWifiOnly);
        mUpdateOnWifiOnly.setChecked( PreferenceHelper.isWiFiChecked());
        mUpdateOnWifiOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceHelper.setWifiChecked(isChecked);
            }
        });
    }

    private void setupIntervalSpinner() {

        Spinner mRefreshIntervalSpinner = (Spinner) findViewById(R.id.refreshIntervalSpinner);

        mIntervalList.clear();
        mIntervalList.add(new Interval(R.string.everyHour, ONE_HOUR));
        mIntervalList.add(new Interval(R.string.everyThreeHours, THREE_HOURS));
        mIntervalList.add(new Interval(R.string.everySixHours, SIX_HOURS));
        mIntervalList.add(new Interval(R.string.everyNineHours, NINE_HOURS));
        mIntervalList.add(new Interval(R.string.everyTwelveHours, TWELVE_HOURS));
        mIntervalList.add(new Interval(R.string.everyDay, EVERY_DAY));

        mRefreshIntervalSpinner.setAdapter(new ArrayAdapter<Interval>(this,
                android.R.layout.simple_list_item_1, mIntervalList));
        mRefreshIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                PreferenceHelper.setInterval(mIntervalList.get(arg2).getTimeMillis());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        int preference = PreferenceHelper.getInterval();

        for (int i = 0; i < mIntervalList.size(); i++)
            if (preference == mIntervalList.get(i).getTimeMillis())
                mRefreshIntervalSpinner.setSelection(i, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class Interval {

        private int name, timeMillis;

        public Interval(int name, int timeMillis) {
            this.name = name;
            this.timeMillis = timeMillis;
        }

        public String getName() {
            return getString(name);
        }

        public int getTimeMillis() {
            return timeMillis;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

}
