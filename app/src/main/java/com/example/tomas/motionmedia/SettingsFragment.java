package com.example.tomas.motionmedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * fragment with settings of motion control sensitivity and skipped song detection
 */
public class SettingsFragment extends Fragment {
    private View layout;
    private SeekBar xSensitivitySeekBar, ySensitivitySeekBar, zSensitivitySeekBar, weekSkipSeekBar, totalSkipSeekBar;
    private Button xDefaultButton, yDefaultButton, zDefaultButton, clearWeekSkipButton, clearTotalSkipButton;
    private TextView xValue, yValue, zValue, weekSkipTextView, totalSkipTextView, weekSkipValueTextView, totalSkipValueTextView;


    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_settings, container, false);
        final Switch motionControlSwitch = (Switch) layout.findViewById(R.id.motionControlSwitch);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor editor = preferences.edit();

        //prepare components
        TextView xAxisDescription = (TextView) layout.findViewById(R.id.xAxisTextView);
        xSensitivitySeekBar = (SeekBar) layout.findViewById(R.id.xAxisSeekBar);
        xDefaultButton = (Button) layout.findViewById(R.id.defaultXButton);
        xValue = (TextView) layout.findViewById(R.id.xValue);

        TextView yAxisDescription = (TextView) layout.findViewById(R.id.yAxisTextView);
        ySensitivitySeekBar = (SeekBar) layout.findViewById(R.id.yAxisSeekBar);
        yDefaultButton = (Button) layout.findViewById(R.id.defaultYButton);
        yValue = (TextView) layout.findViewById(R.id.yValue);

        TextView zAxisDescription = (TextView) layout.findViewById(R.id.zAxisTextView);
        zSensitivitySeekBar = (SeekBar) layout.findViewById(R.id.zAxisSeekBar);
        zDefaultButton = (Button) layout.findViewById(R.id.defaultZButton);
        zValue = (TextView) layout.findViewById(R.id.zValue);

        clearWeekSkipButton = (Button) layout.findViewById(R.id.clearWeekSkippedButton);
        clearTotalSkipButton = (Button) layout.findViewById(R.id.clearTotalSkippedButton);

        weekSkipTextView = (TextView) layout.findViewById(R.id.weekSkipTextView);
        totalSkipTextView = (TextView) layout.findViewById(R.id.totalSkipTextView);
        weekSkipSeekBar = (SeekBar) layout.findViewById(R.id.weekSkipSeekBar);
        weekSkipSeekBar.setMax(20);
        weekSkipSeekBar.setProgress(((MainActivity)getActivity()).getCountForDelWeek());
        totalSkipSeekBar = (SeekBar) layout.findViewById(R.id.totalSkipSeekBar);
        totalSkipSeekBar.setMax(30);
        totalSkipSeekBar.setProgress(((MainActivity)getActivity()).getCountForDelTotal());
        weekSkipValueTextView = (TextView) layout.findViewById(R.id.weekSkipValuetextView);
        totalSkipValueTextView = (TextView) layout.findViewById(R.id.totalSkipValueTextView);

        final Switch skippedSongSwitch = (Switch) layout.findViewById(R.id.skippedSongSwitch);

        //set prepared components
        motionControlSwitch.setChecked(preferences.getBoolean("motionControl", false));
        xSensitivitySeekBar.setEnabled(preferences.getBoolean("xSeekBar", false));
        xSensitivitySeekBar.setProgress(preferences.getInt("xProgress", ((MainActivity)getActivity()).getxCoordinationSensitivity()));
        xValue.setText(String.valueOf(preferences.getInt("xProgress", ((MainActivity)getActivity()).getxCoordinationSensitivity())));
        xDefaultButton.setEnabled(preferences.getBoolean("xDefButton", false));

        ySensitivitySeekBar.setEnabled(preferences.getBoolean("ySeekBar", false));
        ySensitivitySeekBar.setProgress(preferences.getInt("yProgress", ((MainActivity)getActivity()).getyCoordinationSensitivity()));
        yValue.setText(String.valueOf(preferences.getInt("yProgress", ((MainActivity)getActivity()).getyCoordinationSensitivity())));
        yDefaultButton.setEnabled(preferences.getBoolean("yDefButton", false));

        zSensitivitySeekBar.setEnabled(preferences.getBoolean("zSeekBar", false));
        zSensitivitySeekBar.setProgress(preferences.getInt("zProgress", ((MainActivity)getActivity()).getzCoordinationSensitivity()));
        zValue.setText(String.valueOf(preferences.getInt("zProgress", ((MainActivity)getActivity()).getzCoordinationSensitivity())));
        zDefaultButton.setEnabled(preferences.getBoolean("zDefButton", false));

        skippedSongSwitch.setChecked(preferences.getBoolean("skippedSong", false));
        clearWeekSkipButton.setEnabled(preferences.getBoolean("clearWeekButton", false));
        clearTotalSkipButton.setEnabled(preferences.getBoolean("clearTotalButton", false));
        weekSkipSeekBar.setEnabled(preferences.getBoolean("skippedSong", false));
        weekSkipSeekBar.setProgress(preferences.getInt("weekSkip", ((MainActivity)getActivity()).getCountForDelWeek()));
        totalSkipSeekBar.setEnabled(preferences.getBoolean("skippedSong", false));
        weekSkipSeekBar.setProgress(preferences.getInt("totalSkip", ((MainActivity)getActivity()).getCountForDelTotal()));
        weekSkipValueTextView.setText(String.valueOf(preferences.getInt("countWeek",((MainActivity)getActivity()).getCountForDelWeek())));
        totalSkipValueTextView.setText(String.valueOf(preferences.getInt("countTotal",((MainActivity)getActivity()).getCountForDelTotal())));

        motionControlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ((MainActivity)getActivity()).setUseMotionControl(true);
                if ( motionControlSwitch.isChecked()){
                    xSensitivitySeekBar.setEnabled(true);
                    xDefaultButton.setEnabled(true);
                    ySensitivitySeekBar.setEnabled(true);
                    yDefaultButton.setEnabled(true);
                    zSensitivitySeekBar.setEnabled(true);
                    zDefaultButton.setEnabled(true);
                    editor.putBoolean("xSeekBar", xSensitivitySeekBar.isEnabled());
                    editor.putBoolean("xDefButton", xDefaultButton.isEnabled());
                    editor.putBoolean("ySeekBar", ySensitivitySeekBar.isEnabled());
                    editor.putBoolean("yDefButton", yDefaultButton.isEnabled());
                    editor.putBoolean("zSeekBar", zSensitivitySeekBar.isEnabled());
                    editor.putBoolean("zDefButton", zDefaultButton.isEnabled());
                    editor.commit();
                } else {
                    xSensitivitySeekBar.setEnabled(false);
                    xDefaultButton.setEnabled(false);
                    ySensitivitySeekBar.setEnabled(false);
                    yDefaultButton.setEnabled(false);
                    zSensitivitySeekBar.setEnabled(false);
                    zDefaultButton.setEnabled(false);
                    editor.putBoolean("xSeekBar", xSensitivitySeekBar.isEnabled());
                    editor.putBoolean("xDefButton", xDefaultButton.isEnabled());
                    editor.putBoolean("ySeekBar", ySensitivitySeekBar.isEnabled());
                    editor.putBoolean("yDefButton", yDefaultButton.isEnabled());
                    editor.putBoolean("zSeekBar", zSensitivitySeekBar.isEnabled());
                    editor.putBoolean("zDefButton", zDefaultButton.isEnabled());
                    editor.commit();
                }
                editor.putBoolean("motionControl", motionControlSwitch.isChecked());
                editor.commit();
            }
        });

        xSensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    xValue.setText(String.valueOf(seekBar.getProgress()));
                    ((MainActivity)getActivity()).setxCoordinationSensitivity(seekBar.getProgress());
                    editor.putInt("xProgress", seekBar.getProgress());
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
                    yValue.setText(String.valueOf(seekBar.getProgress()));
                    ((MainActivity)getActivity()).setyCoordinationSensitivity(seekBar.getProgress());
                    editor.putInt("yProgress", seekBar.getProgress());
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

        zSensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser){
                    zValue.setText(String.valueOf(seekBar.getProgress()));
                    ((MainActivity)getActivity()).setzCoordinationSensitivity(seekBar.getProgress());
                    editor.putInt("zProgress", seekBar.getProgress());
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
                xValue.setText(String.valueOf(((MainActivity)getActivity()).getxCoordinationSensitivity()));
                ((MainActivity)getActivity()).setxCoordinationSensitivity(18);
                xSensitivitySeekBar.setProgress(((MainActivity)getActivity()).getxCoordinationSensitivity());
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
                yValue.setText(String.valueOf(((MainActivity)getActivity()).getyCoordinationSensitivity()));
                ((MainActivity)getActivity()).setyCoordinationSensitivity(18);
                ySensitivitySeekBar.setProgress(((MainActivity)getActivity()).getyCoordinationSensitivity());
                editor.putBoolean("yDefButton", yDefaultButton.isEnabled());
                editor.commit();
            }
        });

        /**
         * set default sensitivity for z axis
         */
        zDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zValue.setText(String.valueOf(((MainActivity)getActivity()).getzCoordinationSensitivity()));
                ((MainActivity)getActivity()).setzCoordinationSensitivity(18);
                zSensitivitySeekBar.setProgress(((MainActivity)getActivity()).getzCoordinationSensitivity());
                editor.putBoolean("zDefButton", zDefaultButton.isEnabled());
                editor.commit();
            }
        });

        skippedSongSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ((MainActivity)getActivity()).setSkippedSong(skippedSongSwitch.isChecked());
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
                editor.putBoolean("skippedSong", skippedSongSwitch.isChecked());
                editor.commit();


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
                    ((MainActivity)getActivity()).setCountForDelWeek(progress);
                    editor.putInt("countWeek", progress);
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
                    ((MainActivity)getActivity()).setCountForDelTotal(progress);
                    editor.putInt("countTotal", progress);
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
}
