package data_management;

import com.alerts.AlertGenerator;

import com.cardio_generator.outputs.WebSocketOutputStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClientImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketClientTest {

    private final DataStorage storage = new DataStorage();
    private final WebSocketClientImpl client = new WebSocketClientImpl(URI.create("ws://localhost:8080"), storage);

    @Test
    void webSocketClientInitializationTest() {
        assertNotNull(client);
        client.close();
    }

    @Test
    void webSocketClientConnectTest(){
        try {
            WebSocketOutputStrategy server = new WebSocketOutputStrategy(8080);
            client.connectBlocking();
            assertTrue(client.isOpen());
            client.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    @Test
    void webSocketClientDisconnectTest() {
        try {
            WebSocketOutputStrategy server = new WebSocketOutputStrategy(8080);
            client.connectBlocking();
            client.close();
            assertFalse(client.isOpen());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Test
    public void onMessageTest() {
        String message = "100,100,ECG,1234567890"; // Sample message
        client.onMessage(message);

        List<PatientRecord> records = storage.getRecords(100, 1234567890L, 1234567890L);

        Assertions.assertEquals(1, records.size());
        Assertions.assertEquals(100, records.get(0).getMeasurementValue());
    }

    @Test
    public void onCloseTest() {
        client.onClose(1000, "Test", true);
    }
    @Test
    public void webSocketClientOnMessageTest() throws Exception {
        client.connectBlocking();

        client.onMessage("100,100,ECG,1234567891");
        client.onMessage("100,0,ECG,1234567892");

        List<PatientRecord> records = storage.getRecords(100, 1234567891L, 1234567892L);
        Assertions.assertEquals(2, records.size());
        Assertions.assertEquals(100, records.get(0).getMeasurementValue());
        Assertions.assertEquals(0, records.get(1).getMeasurementValue());

        client.close();
    }

    @Test
    public void invalidMessageTest() {
        String message = "1,invalid,EKG,1234567890";
        client.onMessage(message);
        List<PatientRecord> records = storage.getRecords(1, 1234567890L, 1234567890L);
        Assertions.assertEquals(0, records.size());
    }

    @Test
    public void dataProcessingTest() throws Exception {
        client.connectBlocking();
        client.onMessage("100,10,BloodPressure,1234567890");
        client.onMessage("100,20,BloodPressure,1234567891");
        client.onMessage("100,30,BloodPressure,1234567892");

        List<PatientRecord> records = storage.getRecords(100, 1234567890L, 1234567892L);
        Assertions.assertEquals(3, records.size());

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        alertGenerator.evaluateData(new Patient(1));

    }

}
