package com.alerts;

// Represents an alert
public interface Alert {
    String getCondition();
    int getPatientId();
    long getTimestamp();
}
