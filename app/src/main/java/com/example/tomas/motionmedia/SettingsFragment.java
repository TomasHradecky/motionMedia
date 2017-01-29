package com.example.tomas.motionmedia;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Tomas Hradecky
 * fragment with settings of motion control sensitivity and skipped song detection
 */
public class SettingsFragment extends Fragment {
    private View layout;
    private SeekBar xSensitivitySeekBar, ySensitivitySeekBar, weekSkipSeekBar, totalSkipSeekBar;
    private Button xDefaultButton, yDefaultButton, clearWeekSkipButton, clearTotalSkipButton;
    private TextView xValue, yValue, weekSkipTextView, totalSkipTextView, weekSkipValueTextView, totalSkipValueTextView;
    private int xCoordinationSensitivity, yCoordinationSensitivity;
    private int defaultSensitivity = 5, defaultSkipWeek = 5, defaultSkipTotal = 10;
    private int countForDelTotal, countForDelWeek;
    private boolean useMotionControl, skippedSong;
    private Bundle bundle;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_settings, container, false);
        final Switch motionControlSwitch = (Switch) layout.findViewById(R.id.motionControlSwitch);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor editor = preferences.edit();

        //prepare components
        final TextView xAxisDescription = (TextView) layout.findViewById(R.id.xAxisTextView);
        xSensitivitySeekBar = (SeekBar) layout.findViewById(R.id.xAxisSeekBar);
        xDefaultButton = (Button) layout.findViewById(R.id.defaultXButton);
        xValue = (TextView) layout.findViewById(R.id.xValue);

        TextView yAxisDescription = (TextView) layout.findViewById(R.id.yAxisTextView);
        ySensitivitySeekBar = (SeekBar) layout.findViewById(R.id.yAxisSeekBar);
        yDefaultButton = (Button) layout.findViewById(R.id.defaultYButton);
        yValue = (TextView) layout.findViewById(R.id.yValue);

        clearWeekSkipButton = (Button) layout.findViewById(R.id.clearWeekSkippedButton);
        clearTotalSkipButton = (Button) layout.findViewById(R.id.clearTotalSkippedButton);

        weekSkipTextView = (TextView) layout.findViewById(R.id.weekSkipTextView);
        totalSkipTextView = (TextView) layout.findViewById(R.id.totalSkipTextView);
        weekSkipSeekBar = (SeekBar) layout.findViewById(R.id.weekSkipSeekBar);
        weekSkipSeekBar.setMax(20);
        totalSkipSeekBar = (SeekBar) layout.findViewById(R.id.totalSkipSeekBar);
        totalSkipSeekBar.setMax(30);
        weekSkipValueTextView = (TextView) layout.findViewById(R.id.weekSkipValuetextView);
        totalSkipValueTextView = (TextView) layout.findViewById(R.id.totalSkipValueTextView);

        final Switch skippedSongSwitch = (Switch) layout.findViewById(R.id.skippedSongSwitch);

        //set prepared components
        motionControlSwitch.setChecked(preferences.getBoolean("motionControl", false));
        xSensitivitySeekBar.setEnabled(preferences.getBoolean("xSeekBar", false));
        xSensitivitySeekBar.setProgress(preferences.getInt("xProgress", defaultSensitivity) - 3);
        xValue.setText(String.valueOf(preferences.getInt("xProgress", defaultSensitivity)));
        xDefaultButton.setEnabled(preferences.getBoolean("xDefButton", false));

        ySensitivitySeekBar.setEnabled(preferences.getBoolean("ySeekBar", false));
        ySensitivitySeekBar.setProgress(preferences.getInt("yProgress", defaultSensitivity) - 3);
        yValue.setText(String.valueOf(preferences.getInt("yProgress", defaultSensitivity)));
        yDefaultButton.setEnabled(preferences.getBoolean("yDefButton", false));

        skippedSongSwitch.setChecked(preferences.getBoolean("skippedSong", false));
        clearWeekSkipButton.setEnabled(preferences.getBoolean("skippedSong", false));
        clearTotalSkipButton.setEnabled(preferences.getBoolean("skippedSong", false));
        weekSkipSeekBar.setEnabled(preferences.getBoolean("skippedSong", false));
        weekSkipSeekBar.setProgress(preferences.getInt("countWeek", defaultSkipWeek));
        totalSkipSeekBar.setEnabled(preferences.getBoolean("skippedSong", false));
        totalSkipSeekBar.setProgress(preferences.getInt("countTotal", defaultSkipTotal));
        weekSkipValueTextView.setText(String.valueOf(preferences.getInt("countWeek",defaultSkipWeek)));
        totalSkipValueTextView.setText(String.valueOf(preferences.getInt("countTotal",defaultSkipTotal)));
        countForDelTotal = preferences.getInt("countTotal", defaultSkipTotal);
        countForDelWeek = preferences.getInt("countWeek", defaultSkipWeek);

        xCoordinationSensitivity = preferences.getInt("xProgress", defaultSensitivity);
        yCoordinationSensitivity = preferences.getInt("yProgress", defaultSensitivity);
        useMotionControl = preferences.getBoolean("motionControl", false);
        skippedSong = preferences.getBoolean("skipped", false);

        motionControlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                useMotionControl = motionControlSwitch.isChecked();
                ((MainActivity)getActivity()).setUseMotionControl(useMotionControl);
                if ( motionControlSwitch.isChecked()){
                    xSensitivitySeekBar.setEnabled(true);
                    xDefaultButton.setEnabled(true);
                    ySensitivitySeekBar.setEnabled(true);
                    yDefaultButton.setEnabled(true);
                    editor.putBoolean("xSeekBar", xSensitivitySeekBar.isEnabled());
                    editor.putBoolean("xDefButton", xDefaultButton.isEnabled());
                    editor.putBoolean("ySeekBar", ySensitivitySeekBar.isEnabled());
                    editor.putBoolean("yDefButton", yDefaultButton.isEnabled());
                    editor.commit();
                } else {
                    xSensitivitySeekBar.setEnabled(false);
                    xDefaultButton.setEnabled(false);
                    ySensitivitySeekBar.setEnabled(false);
                    yDefaultButton.setEnabled(false);
                    editor.putBoolean("xSeekBar", xSensitivitySeekBar.isEnabled());
                    editor.putBoolean("xDefButton", xDefaultButton.isEnabled());
                    editor.putBoolean("ySeekBar", ySensitivitySeekBar.isEnabled());
                    editor.putBoolean("yDefButton", yDefaultButton.isEnabled());
                    editor.commit();
                }
                editor.putBoolean("motionControl", useMotionControl);
                editor.commit();
            }
        });

        xSensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    xCoordinationSensitivity = seekBar.getProgress() + 3;
                    ((MainActivity)getActivity()).setxCoordinationSensitivity(xCoordinationSensitivity);
                    xValue.setText(String.valueOf(seekBar.getProgress()+ 3));
                    editor.putInt("xProgress", xCoordinationSensitivity);
                    editor.commit();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ySensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    yCoordinationSensitivity = seekBar.getProgress() + 3;
                    ((MainActivity)getActivity()).setyCoordinationSensitivity(yCoordinationSensitivity);
                    yValue.setText(String.valueOf(seekBar.getProgress() + 3));
                    editor.putInt("yProgress", yCoordinationSensitivity);
                    editor.commit();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /**
         * set default sensitivity for x axis
         */
        xDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xValue.setText(String.valueOf(defaultSensitivity));
                xSensitivitySeekBar.setProgress(defaultSensitivity);
                xCoordinationSensitivity = defaultSensitivity;
                ((MainActivity)getActivity()).setxCoordinationSensitivity(xCoordinationSensitivity);
                editor.putInt("xProgress", xCoordinationSensitivity);
                editor.putBoolean("xDefButton", xDefaultButton.isEnabled());
                editor.commit();
            }
        });

        /**
         * set default sensitivity for y axis
         */
        yDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yValue.setText(String.valueOf(defaultSensitivity));
                ySensitivitySeekBar.setProgress(defaultSensitivity);
                yCoordinationSensitivity = defaultSensitivity;
                ((MainActivity)getActivity()).setyCoordinationSensitivity(yCoordinationSensitivity);
                editor.putInt("yProgress", yCoordinationSensitivity);
                editor.putBoolean("yDefButton", yDefaultButton.isEnabled());
                editor.commit();
            }
        });


        skippedSongSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                skippedSong = skippedSongSwitch.isChecked();
                if (!skippedSongSwitch.isChecked() && alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);
                    Intent myIntent = new Intent(getContext(), DatabaseCleanService.class);
                    pendingIntent = PendingIntent.getService(getContext(), 0, myIntent, 0);
                    //call clear after one week for first time
                    alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis() + (1000*60*60*24*7),1000*60*60*24*7, pendingIntent);
                }
                editor.putBoolean("skippedSong", skippedSong);
                if (skippedSongSwitch.isChecked()){
                    clearTotalSkipButton.setEnabled(true);
                    clearWeekSkipButton.setEnabled(true);
                    totalSkipSeekBar.setEnabled(true);
                    weekSkipSeekBar.setEnabled(true);
                    editor.putBoolean("clearWeekButton", clearWeekSkipButton.isEnabled());
                    editor.putBoolean("clearTotalButton", clearTotalSkipButton.isEnabled());
                    editor.commit();
                } else {
                    clearTotalSkipButton.setEnabled(false);
                    clearWeekSkipButton.setEnabled(false);
                    totalSkipSeekBar.setEnabled(false);
                    weekSkipSeekBar.setEnabled(false);
                    editor.putBoolean("clearWeekButton", clearWeekSkipButton.isEnabled());
                    editor.putBoolean("clearTotalButton", clearTotalSkipButton.isEnabled());
                    editor.commit();
                }
                editor.commit();
                ((MainActivity)getActivity()).setSkippedSong(skippedSong);
            }
        });
        clearTotalSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).getDb().clearSkippedTotal();
            }
        });
        clearWeekSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).getDb().clearSkippedWeek();
            }
        });

        weekSkipSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    weekSkipValueTextView.setText(String.valueOf(progress));
                    countForDelWeek = progress;
                    ((MainActivity)getActivity()).setCountForDelWeek(countForDelWeek);
                    editor.putInt("countWeek", countForDelWeek);
                    editor.commit();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        totalSkipSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    totalSkipValueTextView.setText(String.valueOf(progress));
                    countForDelTotal = progress;
                    ((MainActivity)getActivity()).setCountForDelTotal(countForDelTotal);
                    editor.putInt("countTotal", countForDelTotal);
                    editor.commit();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        return layout;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public boolean isUseMotionControl() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        useMotionControl = preferences.getBoolean("motionControl", false);
        return useMotionControl;
    }

    public int getCountForDelTotal() {
        return countForDelTotal;
    }

    public int getCountForDelWeek() {
        return countForDelWeek;
    }
}
