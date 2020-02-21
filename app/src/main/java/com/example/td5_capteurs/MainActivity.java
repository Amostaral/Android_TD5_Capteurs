package com.example.td5_capteurs;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    @BindView(R.id.horizontal_scroll_view)
    HorizontalScrollView horizontalScrollView;

    @BindView(R.id.vertical_scroll_view)
    ScrollView verticalScrollView;

    private SensorManager sensorManager;

    private Sensor magneticSensor;
    private Sensor acceleratorSensor;

    private float[] acceleratorMesures = new float[3];
    private float[] magneticMesures = new float[3];
    private float[] resultMatrix = new float[9];
    private float[] floatValues = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        acceleratorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, acceleratorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, magneticSensor);
        sensorManager.unregisterListener(this, acceleratorSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticMesures = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceleratorMesures = event.values.clone();
        }

        boolean rotationOK = SensorManager.getRotationMatrix(resultMatrix, null, acceleratorMesures, magneticMesures);

        if (rotationOK) {
            // Demander au SensorManager le vecteur d'orientation associé (values)
            SensorManager.getOrientation(resultMatrix, floatValues);

            float axeX = (float) Math.toDegrees(floatValues[0]);
            float axeY = (float) Math.toDegrees(floatValues[1]);
            float axeZ = (float) Math.toDegrees(floatValues[2]);

            if (axeZ >= 10 || axeZ <= -10) {
                horizontalScrollView.smoothScrollBy((int) axeZ, 0);
            }

            if (-10 >= axeY || axeY >= 10) {
                verticalScrollView.smoothScrollBy(0, (int) axeY);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
