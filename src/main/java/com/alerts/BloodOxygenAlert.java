package com.alerts;

public class BloodOxygenAlert implements Alert {
    private int patientId;
    private long timestamp;

    public BloodOxygenAlert(int patientId, long timestamp){
        this.patientId = patientId;
        this.timestamp = timestamp;
    }

    @Override
    public String getCondition() {
        return "BloodOxygen";
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