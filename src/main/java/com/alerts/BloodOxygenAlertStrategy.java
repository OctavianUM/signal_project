package com.alerts;

import java.util.List;
import com.data_management.PatientRecord;
import java.util.ArrayList;

public class BloodOxygenAlertStrategy implements AlertStrategy {

    public BloodOxygenAlertStrategy() {
    }

    @Override
    public Alert checkAlert(List<PatientRecord> patientRecords) {
        List<PatientRecord> saturation = getPatientSaturation(patientRecords);
        if (isBloodSaturationCritical(saturation))
            return new BloodOxygenAlertFactory().createAlert(patientRecords.get(0).getPatientId(), null, patientRecords.get(patientRecords.size()-1).getTimestamp());


        return null;
    }

    public List<PatientRecord> getPatientSaturation(List<PatientRecord> patientRecords) {
        List<PatientRecord> saturation = new ArrayList<>();

        for (PatientRecord patientRecord : patientRecords)
            if (patientRecord.getRecordType().equals("Saturation"))
                saturation.add(patientRecord);

        return saturation;
    }

    /**
     * Checks if the patient's blood oxygen saturation level is critical.
     *
     * @param saturation A list of PatientRecord objects representing blood oxygen saturation readings.
     * @return true if the oxygen saturation level is critical; false otherwise.
     */
    public boolean isBloodSaturationCritical(List<PatientRecord> saturation) {
        if (saturation.isEmpty())
            return false;

        // Check if the latest oxygen saturation reading is below the critical threshold
        if (saturation.get(saturation.size() - 1).getMeasurementValue() < AlertConstants.OXYGEN_SATURATION)
            return true;

        // Check for a significant drop in oxygen saturation within 10 minutes
        for (int i = 0; i < saturation.size() - 1; i++) {
            for (int j = i + 1; j < saturation.size(); j++) {
                long timeDifference = saturation.get(j).getTimestamp() - saturation.get(i).getTimestamp();
                double valueDifference = saturation.get(i).getMeasurementValue() - saturation.get(j).getMeasurementValue();

                if (timeDifference < 600000 && valueDifference >= AlertConstants.OXYGEN_DROP)
                    return true;

            }
        }
        return false;
    }
}
