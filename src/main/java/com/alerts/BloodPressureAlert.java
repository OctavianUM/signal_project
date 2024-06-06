package com.alerts;

public class BloodPressureAlert implements Alert {
    private int patientId;
    private long timestamp;

    public BloodPressureAlert(int patientId, long timestamp){
        this.patientId = patientId;
        this.timestamp = timestamp;
    }

    @Override
    public String getCondition() {
        return "BloodPressure";
    }

    @Override
    public int getPatientId() {
        return patientId;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}