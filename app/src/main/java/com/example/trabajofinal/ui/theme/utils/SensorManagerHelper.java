package com.example.trabajofinal.ui.theme.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorManagerHelper {

    private final SensorManager sensorManager;
    private final Sensor lightSensor;

    public SensorManagerHelper(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = (sensorManager != null) ? sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) : null;
    }

    public boolean isLightSensorAvailable() {
        return lightSensor != null;
    }

    public void registerListener(SensorEventListener listener) {
        if (lightSensor != null) {
            sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void unregisterListener(SensorEventListener listener) {
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }
}
