package com.alerts;

import java.util.List;
import com.data_management.PatientRecord;
import java.util.ArrayList;

public class ECGAlertStrategy implements AlertStrategy {

    public ECGAlertStrategy() {
    }
    @Override
    public Alert checkAlert(List<PatientRecord> patientRecords) {
        List<PatientRecord> ecgData = getECGData(patientRecords);
        if (isECGDataCritical(ecgData))
            return new ECGAlertFactory().createAlert(patientRecords.get(0).getPatientId(), null, patientRecords.get(patientRecords.size()-1).getTimestamp());

        return null;
    }

    private List<PatientRecord> getECGData(List<PatientRecord> patientRecords) {
        List<PatientRecord> ecgData = new ArrayList<>();

        for (PatientRecord patientRecord : patientRecords)
            if (patientRecord.getRecordType().equals("ECG"))
                ecgData.add(patientRecord);

        return ecgData;
    }

    /**
     * Checks if the patient's ECG data indicates a critical condition.
     *
     * @param ecgData A list of PatientRecord objects representing ECG readings.
     * @return true if the ECG data indicates a critical condition; false otherwise.
     */
    public boolean isECGDataCritical(List<PatientRecord> ecgData) {
        if (ecgData.isEmpty()) {
            return false;
        }

        // Calculate the average of the ECG data, excluding the latest reading
        double average = ecgData.stream()
                .limit(ecgData.size() - 1)
                .mapToDouble(PatientRecord::getMeasurementValue)
                .average()
                .orElse(0);

        // Calculate the standard deviation of the ECG data, excluding the latest reading
        double standardDeviation = Math.sqrt(ecgData.stream()
                .limit(ecgData.size() - 1)
                .mapToDouble(PatientRecord::getMeasurementValue)
                .map(x -> Math.pow(x - average, 2))
                .average()
                .orElse(0));

        // Check if the latest ECG reading is significantly higher than the average
        return ecgData.get(ecgData.size() - 1).getMeasurementValue() > average + 2 * standardDeviation;
    }
}