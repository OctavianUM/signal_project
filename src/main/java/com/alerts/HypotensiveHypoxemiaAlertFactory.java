package com.alerts;

public class HypotensiveHypoxemiaAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new HypotensiveHypoxemiaAlert(patientId, timestamp);
    }
}