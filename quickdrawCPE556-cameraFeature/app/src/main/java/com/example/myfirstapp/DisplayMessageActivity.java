package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import java.text.DecimalFormat;


import static com.example.myfirstapp.MainActivity.EXTRA_MESSAGE;

public class DisplayMessageActivity extends AppCompatActivity implements SensorEventListener {

    private TextView highScoreText;

    private Sensor AccelSensor, rotationVectorSensor;
    private SensorManager SM;

    private double lastX, lastY, lastZ;
    private double highScore = 0;
    private double deltaX = 0;
    private double deltaY = 0;
    private double deltaZ = 0;
    private double force;

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        //Create sensor manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Accelerometer and Gyro Assignment
        AccelSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        rotationVectorSensor = SM.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        SM.registerListener(this,AccelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(this,rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

        highScoreText = findViewById(R.id.highScoreText);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }
    private boolean correctOrientation = false;
    @Override
    public void onSensorChanged(SensorEvent event) {



        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            // Remap coordinate system
            float[] remappedRotationMatrix = new float[16];
            SensorManager.remapCoordinateSystem(rotationMatrix, SM.AXIS_X, SM.AXIS_Z, remappedRotationMatrix);

            // Convert to orientations
            float[] orientations = new float[3];
            SensorManager.getOrientation(remappedRotationMatrix, orientations);

            // Convert to degrees
            for (int i = 0; i < 3; i++) {
                orientations[i] = (float)(Math.toDegrees(orientations[i]));
            }


            int angleThreshold = 30;
            // Process orientation data
            // Ensures that phone is held upright throughout the game, within a tolerance of +/-20 degrees
            if ((orientations[0] > angleThreshold || orientations[0] < -angleThreshold) // check X
                    || (orientations[1] > angleThreshold || orientations[1] < -angleThreshold) // check Y
                    || (orientations[2] > angleThreshold || orientations[2] < -angleThreshold)) // check Z
            {
                // DO NOT RECORD
                correctOrientation = false;
                return;
            }
            else {
                // RECORD
                correctOrientation = true;
                return;
            }
        }
        else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if (correctOrientation) {
                displayHighScore();
                deltaX = Math.abs(lastX - event.values[0]);
                deltaY = Math.abs(lastY - event.values[1]);
                deltaZ = Math.abs(lastZ - event.values[2]);
                force = Math.pow((Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2)), 0.3333);

                // if change is less than 2, just noise
                if (deltaX < 2)
                    deltaX = 0;
                if (deltaY < 2)
                    deltaY = 0;
                if (deltaZ < 2)
                    deltaZ = 0;

                lastX = event.values[0];
                lastY = event.values[1];
                lastZ = event.values[2];
            }
            else {

            }
        }


    }

    public static final String EXTRA_MESSAGE1 = "com.example.DisplayMessageActivity.MESSAGE1";
    public static final String EXTRA_MESSAGE2 = "com.example.DisplayMessageActivity.MESSAGE2";

    public void sendScore (View view){

        Intent intent = new Intent(this, MainActivity.class);
        TextView newScore = findViewById(R.id.highScoreText);
        TextView newName = findViewById(R.id.textView);

        double score = Double.parseDouble(newScore.getText().toString());
        String name = newName.getText().toString();

        Bundle extras = new Bundle();
        extras.putDouble(EXTRA_MESSAGE1, score);
        extras.putString(EXTRA_MESSAGE2, name);
        intent.putExtras(extras);
        startActivity(intent);


    }

    public void onResume (){
        super.onResume();
        //register listener
        SM.registerListener(this,AccelSensor, SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(this,rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onPause(){
        super.onPause();
        SM.unregisterListener(this);
    }

    public void displayHighScore() {
        force = Math.pow ((Math.pow(deltaX,2)+Math.pow(deltaY,2)+Math.pow(deltaZ,2)),0.3333);
        if (force > highScore)
           highScore = force;

        highScoreText.setText(df2.format(highScore));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    //Not in use
    }
}
