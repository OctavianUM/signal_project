package com.alerts;

public class BloodPressureAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, timestamp);
    }
}