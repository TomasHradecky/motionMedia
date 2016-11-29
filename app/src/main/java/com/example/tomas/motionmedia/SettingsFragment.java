package com.example.tomas.motionmedia;

import android.content.Context;
import android.content.SharedPreferences;
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
 * fragment with settings of motion control sensitivity and skipped song detection
 */
public class SettingsFragment extends Fragment {
    private View layout;
    private SeekBar xSensitivitySeekBar, ySensitivitySeekBar, zSensitivitySeekBar;
    private Button xDefaultButton, yDefaultButton, zDefaultButton;
    private TextView xValue, yValue, zValue;

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
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                xValue.setText(String.valueOf(seekBar.getProgress()));
                ((MainActivity)getActivity()).setxCoordinationSensitivity(seekBar.getProgress());
                editor.putInt("xProgress", seekBar.getProgress());
                editor.commit();
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
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                yValue.setText(String.valueOf(seekBar.getProgress()));
                ((MainActivity)getActivity()).setyCoordinationSensitivity(seekBar.getProgress());
                editor.putInt("yProgress", seekBar.getProgress());
                editor.commit();
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
                zValue.setText(String.valueOf(seekBar.getProgress()));
                ((MainActivity)getActivity()).setzCoordinationSensitivity(seekBar.getProgress());
                editor.putInt("zProgress", seekBar.getProgress());
                editor.commit();

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
                editor.putBoolean("skippedSong", skippedSongSwitch.isEnabled());
                editor.commit();
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
