package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketDataReader implements DataReader{
    @Override
    public void readData(DataStorage dataStorage) throws IOException {

    }

    @Override
    public void readData(DataStorage dataStorage, String websocketUrl) throws IOException {

        try {
            URI url = new URI(websocketUrl);
            WebSocketClientImpl client = new WebSocketClientImpl(url, dataStorage);
            client.connectBlocking();

            if (!client.isConnectionSuccessful()) {
                throw new IOException("Failed to connect to WebSocket");
            }

        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new IOException("Failed to connect to WebSocket", e);
        }

    }
}
