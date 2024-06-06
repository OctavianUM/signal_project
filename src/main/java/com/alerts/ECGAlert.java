package com.alerts;

public class ECGAlert implements Alert {

    private int patientId;
    private long timestamp;

    public ECGAlert(int patientId, long timestamp) {
        this.patientId = patientId;
        this.timestamp = timestamp;
    }

    @Override
    public String getCondition() {
        return "ECGAlert";
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