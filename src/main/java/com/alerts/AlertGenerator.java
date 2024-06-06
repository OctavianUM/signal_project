package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) throws Exception {
        int patientId = patient.getPatientId();
        List<PatientRecord> patientRecords = dataStorage.getRecords(patientId, 0, Long.MAX_VALUE);
        List<PatientRecord> systolicPressures = new ArrayList<>();
        List<PatientRecord> diastolicPressures = new ArrayList<>();
        List<PatientRecord> saturations = new ArrayList<>();
        List<PatientRecord> ecgs = new ArrayList<>();

        for (PatientRecord patientRecord : patientRecords) {
            switch (patientRecord.getRecordType()) {
                case "SystolicPressure":
                    systolicPressures.add(patientRecord);
                    break;
                case "DiastolicPressure":
                    diastolicPressures.add(patientRecord);
                    break;
                case "Saturation":
                    saturations.add(patientRecord);
                    break;
                case "ECG":
                    ecgs.add(patientRecord);
                    break;
                default:
                    break;
            }
        }
/*        if (isBloodPressureCritical(systolicPressures, diastolicPressures))
            triggerAlert(new Alert(patientId, "BloodPressure", System.currentTimeMillis()));
        if (isBloodSaturationCritical(saturations))
            triggerAlert(new Alert(patientId, "BloodSaturation", System.currentTimeMillis()));
        if (isThereHypotensiveHypoxemia(saturations, systolicPressures))
            triggerAlert(new Alert(patientId, "HypotensiveHypoxemia", System.currentTimeMillis()));
        if (isECGDataCritical(ecgs))
            triggerAlert(new Alert(patientId, "ECGData", System.currentTimeMillis()));*/

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

    /**
     * Checks if the patient's blood oxygen saturation level is critical.
     *
     * @param saturation A list of PatientRecord objects representing blood oxygen saturation readings.
     * @return true if the oxygen saturation level is critical; false otherwise.
     */
    public boolean isBloodSaturationCritical(List<PatientRecord> saturation) {
        if (saturation.isEmpty()) {
            return false;
        }

        // Check if the latest oxygen saturation reading is below the critical threshold
        if (saturation.get(saturation.size() - 1).getMeasurementValue() < AlertConstants.OXYGEN_SATURATION) {
            return true;
        }

        // Check for a significant drop in oxygen saturation within 10 minutes
        for (int i = 0; i < saturation.size() - 1; i++) {
            for (int j = i + 1; j < saturation.size(); j++) {
                long timeDifference = saturation.get(j).getTimestamp() - saturation.get(i).getTimestamp();
                double valueDifference = saturation.get(i).getMeasurementValue() - saturation.get(j).getMeasurementValue();

                if (timeDifference < 600000 && valueDifference >= AlertConstants.OXYGEN_DROP) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the patient is experiencing hypotensive hypoxemia, a condition with low blood pressure and low oxygen saturation.
     *
     * @param saturation A list of PatientRecord objects representing blood oxygen saturation readings.
     * @param systolicPressure A list of PatientRecord objects representing systolic blood pressure readings.
     * @return true if the patient has hypotensive hypoxemia; false otherwise.
     */
    public boolean isThereHypotensiveHypoxemia(List<PatientRecord> saturation, List<PatientRecord> systolicPressure) {
        if (systolicPressure.isEmpty() || saturation.isEmpty()) {
            return false;
        }

        // Check if the latest systolic pressure and oxygen saturation are below critical thresholds
        return systolicPressure.get(systolicPressure.size() - 1).getMeasurementValue() < AlertConstants.SYSTOLIC_MIN &&
                saturation.get(saturation.size() - 1).getMeasurementValue() < AlertConstants.OXYGEN_SATURATION;
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

    /**
     * Triggers an alert for a patient with a specified condition.
     *
     * @param alert An Alert object containing information about the patient's condition.
     */
    private void triggerAlert(Alert alert) {
        System.out.println("Triggered alert for patient with ID: " + alert.getPatientId() + "\nCondition: " + alert.getCondition());
        // Implementation might involve logging the alert or notifying medical staff
    }
}
