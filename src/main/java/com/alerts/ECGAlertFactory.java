package com.alerts;

public class ECGAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, timestamp);
    }
}