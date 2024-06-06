package com.alerts;

public class BloodOxygenAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, timestamp);
    }
}