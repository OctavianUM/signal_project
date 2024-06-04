package alerts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import com.alerts.AlertGenerator;
import com.data_management.PatientRecord;
import com.data_management.DataStorage;


public class AlertGeneratorTest {

    private final int patientId = 100;
    @Test
    void isECGDataCritical() {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        ArrayList<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(patientId, 100, "ECG", 1712345678900L));
        records.add(new PatientRecord(patientId, 200, "ECG", 1712345678901L));
        records.add(new PatientRecord(patientId, 300, "ECG", 1712345678902L));
        records.add(new PatientRecord(patientId, 400, "ECG", 1712345678903L));

        boolean emittedAlert = alertGenerator.isECGDataCritical(records);

        assertTrue(emittedAlert);
    }

    @Test
    void isBloodSaturationCritical() {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        ArrayList<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(patientId, 50, "Saturation", 1712345678900L));
        records.add(new PatientRecord(patientId, 45, "Saturation", 1712345678901L));
        records.add(new PatientRecord(patientId, 25, "Saturation", 1712345678902L));

        boolean emittedAlert = alertGenerator.isBloodSaturationCritical(records);

        assertTrue(emittedAlert);
    }

    @Test
    void isBloodPressureCritical() {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        ArrayList<PatientRecord> systolicPressure = new ArrayList<>();
        systolicPressure.add(new PatientRecord(patientId, 50, "SystolicPressure", 1712345678900L));
        systolicPressure.add(new PatientRecord(patientId, 60, "SystolicPressure", 1712345678901L));
        systolicPressure.add(new PatientRecord(patientId, 70, "SystolicPressure", 1712345678902L));

        ArrayList<PatientRecord> diastolicPressure = new ArrayList<>();
        diastolicPressure.add(new PatientRecord(patientId, 50, "DiastolicPressure", 1712345678901L));
        diastolicPressure.add(new PatientRecord(patientId, 65, "DiastolicPressure", 1712345678902L));
        diastolicPressure.add(new PatientRecord(patientId, 85, "DiastolicPressure", 1712345678903L));

        boolean emittedAlert = alertGenerator.isBloodPressureCritical(systolicPressure, diastolicPressure);

        assertTrue(emittedAlert);
    }

    @Test
    void isBloodPressureCriticalThreshold() {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        ArrayList<PatientRecord> systolicPressure = new ArrayList<>();
        systolicPressure.add(new PatientRecord(patientId, 50, "SystolicPressure", 1712345678900L));
        systolicPressure.add(new PatientRecord(patientId, 60, "SystolicPressure", 1712345678901L));
        systolicPressure.add(new PatientRecord(patientId, 0, "SystolicPressure", 1712345678901L));

        ArrayList<PatientRecord> diastolicPressure = new ArrayList<>();
        diastolicPressure.add(new PatientRecord(patientId, 50, "diastolicPressure", 1712345678901L));
        diastolicPressure.add(new PatientRecord(patientId, 60, "diastolicPressure", 1712345678902L));
        diastolicPressure.add(new PatientRecord(patientId, 55, "diastolicPressure", 1712345678903L));

        boolean emittedAlert = alertGenerator.isBloodPressureCritical(systolicPressure, diastolicPressure);

        assertTrue(emittedAlert);
    }

    @Test
    void isThereHypotensiveHypoxemia() {
        DataStorage storage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(storage);

        ArrayList<PatientRecord> bloodSaturation = new ArrayList<>();
        bloodSaturation.add(new PatientRecord(patientId, 30, "Saturation", 1712345678900L));
        bloodSaturation.add(new PatientRecord(patientId, 25, "Saturation", 1712345678901L));
        bloodSaturation.add(new PatientRecord(patientId, 10, "Saturation", 1712345678902L));

        ArrayList<PatientRecord> systolicPressure = new ArrayList<>();
        systolicPressure.add(new PatientRecord(patientId, 40, "SystolicPressure", 1712345678903L));
        systolicPressure.add(new PatientRecord(patientId, 50, "SystolicPressure", 1712345678904L));
        systolicPressure.add(new PatientRecord(patientId, 20, "SystolicPressure", 1712345678905L));

        boolean emittedAlert = alertGenerator.isThereHypotensiveHypoxemia(bloodSaturation, systolicPressure);

        assertTrue(emittedAlert);
    }
}