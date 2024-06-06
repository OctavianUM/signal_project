package com.alerts;

import java.util.List;
import java.util.ArrayList;
import com.data_management.PatientRecord;

public class BloodPressureAlertStrategy implements AlertStrategy{

    public BloodPressureAlertStrategy() {
    }
    @Override
    public Alert checkAlert(List<PatientRecord> patientRecords) {
        List<List<PatientRecord>> patientData = getPatientData(patientRecords);
        if (isBloodPressureCritical(patientData.get(0), patientData.get(1)))
            return new BloodPressureAlertFactory().createAlert(patientRecords.get(0).getPatientId(), null, patientRecords.get(patientRecords.size()-1).getTimestamp());

        return null;
    }

    private List<List<PatientRecord>> getPatientData(List<PatientRecord> patientRecords) {
        List<PatientRecord> systolicPressure = new ArrayList<>();
        List<PatientRecord> diastolicPressure = new ArrayList<>();

        for (PatientRecord patientRecord : patientRecords) {
            if (patientRecord.getRecordType().equals("SystolicPressure"))
                systolicPressure.add(patientRecord);
            else if (patientRecord.getRecordType().equals("DiastolicPressure"))
                diastolicPressure.add(patientRecord);
        }

        List<List<PatientRecord>> patientData = new ArrayList<>();
        patientData.add(systolicPressure); patientData.add(diastolicPressure);
        return patientData;
    }

    /**
     * Checks if the patient's blood pressure is in a critical state.
     *
     * @param systolicPressure  A list of PatientRecord objects representing systolic blood pressure readings.
     * @param diastolicPressure A list of PatientRecord objects representing diastolic blood pressure readings.
     * @return true if the blood pressure is considered critical; false otherwise.
     */
    public boolean isBloodPressureCritical(List<PatientRecord> systolicPressure, List<PatientRecord> diastolicPressure) {
        // Check for significant differences in systolic pressure
        if (checkBloodPressureDifference(systolicPressure)) return true;
        // Check for significant differences in diastolic pressure
        if (checkBloodPressureDifference(diastolicPressure)) return true;

        // Check if the last systolic reading is outside the normal range
        if (!systolicPressure.isEmpty() &&
                (systolicPressure.get(systolicPressure.size() - 1).getMeasurementValue() > AlertConstants.SYSTOLIC_MAX ||
                        systolicPressure.get(systolicPressure.size() - 1).getMeasurementValue() < AlertConstants.SYSTOLIC_MIN)) {
            return true;
        } else {
            // Check if the last diastolic reading is outside the normal range
            return !diastolicPressure.isEmpty() &&
                    (diastolicPressure.get(diastolicPressure.size() - 1).getMeasurementValue() > AlertConstants.DIASTOLIC_MAX ||
                            diastolicPressure.get(diastolicPressure.size() - 1).getMeasurementValue() < AlertConstants.DIASTOLIC_MIN);
        }
    }

    /**
     * Checks if there are significant differences in consecutive blood pressure readings.
     *
     * @param bloodPressure A list of PatientRecord objects representing blood pressure readings.
     * @return true if there are significant differences between consecutive readings; false otherwise.
     */
    private boolean checkBloodPressureDifference(List<PatientRecord> bloodPressure) {
        for (int i = 0; i < bloodPressure.size() - 2; i++) {
            // Calculate the differences between consecutive readings
            double difference1 = bloodPressure.get(i).getMeasurementValue() - bloodPressure.get(i + 1).getMeasurementValue();
            double difference2 = bloodPressure.get(i).getMeasurementValue() - bloodPressure.get(i + 2).getMeasurementValue();

            // Check if the differences exceed the threshold
            if (Math.abs(difference1) > AlertConstants.BLOOD_PRESSURE_DIFFERENCE &&
                    Math.abs(difference2) > AlertConstants.BLOOD_PRESSURE_DIFFERENCE) {
                return true;
            }
        }
        return false;
    }
}