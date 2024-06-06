package com.alerts;

public class HypotensiveHypoxemiaAlert implements Alert {
    private int patientId;
    private long timestamp;

    public HypotensiveHypoxemiaAlert(int patientId, long timestamp){
        this.patientId = patientId;
        this.timestamp = timestamp;
    }

    @Override
    public String getCondition() {
        return "HypotensiveHypoxemia";
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