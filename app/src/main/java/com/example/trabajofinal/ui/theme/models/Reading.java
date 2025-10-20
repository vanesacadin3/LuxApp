package com.example.trabajofinal.ui.theme.models;

public class Reading {
    private String sensor;
    private float value;
    private long timestamp;

    public Reading(String sensor, float value, long timestamp) {
        this.sensor = sensor;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getSensor() {
        return sensor;
    }

    public float getValue() {    // 👈 CAMBIO AQUÍ
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
