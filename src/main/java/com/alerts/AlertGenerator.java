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
        if (isBloodPressureCritical(systolicPressures, diastolicPressures))
            triggerAlert(new Alert(patientId, "BloodPressure", System.currentTimeMillis()));
        if (isBloodSaturationCritical(saturations))
            triggerAlert(new Alert(patientId, "BloodSaturation", System.currentTimeMillis()));
        if (isThereHypotensiveHypoxemia(saturations, systolicPressures))
            triggerAlert(new Alert(patientId, "HypotensiveHypoxemia", System.currentTimeMillis()));
        if (isECGDataCritical(ecgs))
            triggerAlert(new Alert(patientId, "ECGData", System.currentTimeMillis()));

    }

    public boolean isBloodPressureCritical(List<PatientRecord> systolicPressure, List<PatientRecord> diastolicPressure) {
        if (checkBloodPressureDifference(systolicPressure)) return true;
        if (checkBloodPressureDifference(diastolicPressure)) return true;

        if (!systolicPressure.isEmpty() & (systolicPressure.get(systolicPressure.size()-1).getMeasurementValue() > AlertConstants.SYSTOLIC_MAX || systolicPressure.get(systolicPressure.size()-1).getMeasurementValue() < AlertConstants.SYSTOLIC_MIN))
            return true;
        else return !diastolicPressure.isEmpty() && (diastolicPressure.get(diastolicPressure.size() - 1).getMeasurementValue() > AlertConstants.DIASTOLIC_MAX || diastolicPressure.get(diastolicPressure.size() - 1).getMeasurementValue() < AlertConstants.DIASTOLIC_MIN);
    }

    private boolean checkBloodPressureDifference(List<PatientRecord> bloodPressure) {
        for (int i = 0; i < bloodPressure.size() - 2; i++) {
            double difference1 = bloodPressure.get(i).getMeasurementValue() - bloodPressure.get(i + 1).getMeasurementValue() - bloodPressure.get(i).getMeasurementValue() - bloodPressure.get(i).getMeasurementValue();
            double difference2 = bloodPressure.get(i).getMeasurementValue() - bloodPressure.get(i + 2).getMeasurementValue() - bloodPressure.get(i).getMeasurementValue() - bloodPressure.get(i).getMeasurementValue();

            if (Math.abs(difference1) > AlertConstants.BLOOD_PRESSURE_DIFFERENCE  && Math.abs(difference2) > AlertConstants.BLOOD_PRESSURE_DIFFERENCE)
                return true;
        }
        return false;
    }

    public boolean isBloodSaturationCritical(List<PatientRecord> saturation) {
        if (saturation.isEmpty())
            return false;
        if (saturation.get(saturation.size()-1).getMeasurementValue() < AlertConstants.OXYGEN_SATURATION)
            return true;

        for (int i = 0; i < saturation.size() - 1; i++)
            for (int j = i + 1; j < saturation.size(); j++)
                if (saturation.get(j).getTimestamp() - saturation.get(i).getTimestamp() < 600000 && saturation.get(i).getMeasurementValue() - saturation.get(j).getMeasurementValue() >= AlertConstants.OXYGEN_DROP)
                    return true;

        return false;
    }

    public boolean isThereHypotensiveHypoxemia(List<PatientRecord> saturation, List<PatientRecord> systolicPressure) {
        if (systolicPressure.isEmpty() || saturation.isEmpty())
            return false;
        return systolicPressure.get(systolicPressure.size() - 1).getMeasurementValue() < AlertConstants.SYSTOLIC_MIN && saturation.get(saturation.size() - 1).getMeasurementValue() < AlertConstants.OXYGEN_SATURATION;
    }

    public boolean isECGDataCritical(List<PatientRecord> ecgData) {
        if (ecgData.isEmpty())
            return false;

        double average = ecgData.stream()
                .limit(ecgData.size() - 1)
                .mapToDouble(PatientRecord::getMeasurementValue)
                .average()
                .orElse(0);

        double standardDeviation = Math.sqrt(ecgData.stream()
                .limit(ecgData.size() - 1)
                .mapToDouble(PatientRecord::getMeasurementValue)
                .map(x -> Math.pow(x - average, 2)).average()
                .orElse(0));

        return ecgData.get(ecgData.size() - 1).getMeasurementValue() > average + 2 * standardDeviation;
    }

    private void triggerAlert(Alert alert) {
        System.out.println("Triggered alert for patient with ID: " + alert.getPatientId() + "\nCondition: " + alert.getCondition());
        // Implementation might involve logging the alert or notifying staff
    }
}
