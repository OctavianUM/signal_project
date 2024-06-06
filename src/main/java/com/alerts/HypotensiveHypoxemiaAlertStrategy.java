package com.alerts;

import java.util.ArrayList;
import java.util.List;
import com.data_management.PatientRecord;

public class HypotensiveHypoxemiaAlertStrategy implements AlertStrategy {

    public HypotensiveHypoxemiaAlertStrategy() {
    }

    @Override
    public Alert checkAlert(List<PatientRecord> patientRecords) {
        List<List<PatientRecord>> patientData = filterPatientData(patientRecords);

        if (isThereHypotensiveHypoxemia(patientData.get(0), patientData.get(1)))
            return new HypotensiveHypoxemiaAlertFactory().createAlert(patientRecords.get(0).getPatientId(), null,patientRecords.get(patientRecords.size()-1).getTimestamp());

        return null;
    }

    private List<List<PatientRecord>> filterPatientData(List<PatientRecord> patientRecords) {
        List<PatientRecord> saturation = new ArrayList<>();
        List<PatientRecord> diastolicPressure = new ArrayList<>();

        for (PatientRecord patientRecord : patientRecords)
            if (patientRecord.getRecordType().equals("Saturation"))
                saturation.add(patientRecord);
            else if (patientRecord.getRecordType().equals("SystolicPressure"))
                diastolicPressure.add(patientRecord);

        List<List<PatientRecord>> patientData = new ArrayList<>();
        patientData.add(saturation); patientData.add(diastolicPressure);

        return patientData;
    }

    /**
     * Checks if the patient is experiencing hypotensive hypoxemia, a condition with low blood pressure and low oxygen saturation.
     *
     * @param saturation A list of PatientRecord objects representing blood oxygen saturation readings.
     * @param systolicPressure A list of PatientRecord objects representing systolic blood pressure readings.
     * @return true if the patient has hypotensive hypoxemia; false otherwise.
     */
    public boolean isThereHypotensiveHypoxemia(List<PatientRecord> saturation, List<PatientRecord> systolicPressure) {
        if (systolicPressure.isEmpty() || saturation.isEmpty())
            return false;

        // Check if the latest systolic pressure and oxygen saturation are below critical thresholds
        return systolicPressure.get(systolicPressure.size() - 1).getMeasurementValue() < AlertConstants.SYSTOLIC_MIN &&
                saturation.get(saturation.size() - 1).getMeasurementValue() < AlertConstants.OXYGEN_SATURATION;
    }
}