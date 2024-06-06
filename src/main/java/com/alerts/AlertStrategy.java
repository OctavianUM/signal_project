package com.alerts;

import java.util.List;
import com.data_management.PatientRecord;
import com.alerts.Alert;

public interface AlertStrategy {
    Alert checkAlert(List<PatientRecord> patientRecords);
}