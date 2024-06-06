package com.alerts;

public interface AlertFactory {
    Alert createAlert(int patientId, String condition, long timestamp);
}
