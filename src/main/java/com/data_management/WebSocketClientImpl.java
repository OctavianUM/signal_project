package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketClientImpl extends WebSocketClient {

    private final DataStorage dataStorage;
    private boolean connectionSuccessful;

    public WebSocketClientImpl(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
        this.connectionSuccessful = false;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server");
        connectionSuccessful = true;
    }

    @Override
    public void onMessage(String message) {
        String[] patientData = message.split(",");

        if (patientData.length == 4) {
            try {
                int patientId = Integer.parseInt(patientData[0]);
                double measurementValue = Double.parseDouble(patientData[1]);
                String recordType = patientData[2];
                long timestamp = Long.parseLong(patientData[3]);
                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);

            } catch (NumberFormatException e) {
                System.err.println("Error parsing message: " + message);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from Websocket server");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }
}