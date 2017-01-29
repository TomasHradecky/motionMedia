package com.example.tomas.motionmedia;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Tomas Hradecky on 27.12.2016.
 * Fragment with information about application
 */
public class HelpFragment extends Fragment {
    private View layout;
    private SensorManager sensorManager;

    public HelpFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_help, container, false);

        TextView title = (TextView) layout.findViewById(R.id.titleTextView);
        TextView helpText = (TextView) layout.findViewById(R.id.helpTextView);
        ImageView phoneImage = (ImageView) layout.findViewById(R.id.phoneImageView);
        String linearAvailabelityString;

        sensorManager=(SensorManager)getActivity().getSystemService(SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            linearAvailabelityString = "available";
        } else {
            linearAvailabelityString = "unavailable";
        }



        helpText.setText("Thank´s that You are using this application. Motion Media is a music player with SMART functions which You will love, as I hope :)." + "\n\n" +
                "First feature is, that You can let MotionMedia show frequently skipped song to easily delete them from your device." + "\n\n" +
                "Second feature is, that MotionMedia can be controlled by movement, concretely by data from accelerometer in your device." + "\n" +
                "Your phone get data from axis that are oriented as You can see in the picture bellow." + "\n" +
                "If You will move with device on X axis You can control next song for positive direction and previous for negative direction." + "\n" +
                "On Y axis You can control next random song for positive direction and next random artist for negative direction." + "\n\n" +

                "Both mentioned features has to be activated in settings of this application, where You can also set sensitivity of motion control and cont of skips for offering songs to delete" + "\n\n" +
                "Note: If You don´t have linear accelerometer in your device please keep your device horizontally with ground during motion control for best result" + "\n\n" +
                "Linear accelerometer is " + linearAvailabelityString +  " in this device."
        );

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
