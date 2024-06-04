package com.data_management;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileDataReader implements DataReader {

    private String fileLocation;
    private static final String COMMAND = "--output file:";

    public FileDataReader(String fileLocation) {
        this.fileLocation = fileLocation;
    }
    @Override
    public void readData(DataStorage dataStorage) throws IOException{
        if (fileLocation.startsWith(COMMAND))
            fileLocation = fileLocation.substring(COMMAND.length());

        try (BufferedReader br = new BufferedReader(new FileReader(fileLocation))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] patientData = line.split(",");
                if (patientData.length == 4) {
                    int patientID = Integer.parseInt(patientData[0]);
                    double measurementValue = Double.parseDouble(patientData[1]);
                    String recordType = patientData[2];
                    long timestamp = Long.parseLong(patientData[3]);
                    dataStorage.addPatientData(patientID, measurementValue, recordType, timestamp);
                }
            }
        } catch (Exception e) {
            throw new IOException("An error occurred in readData(DataStorage) while reading from file: " + e.getMessage());
        }
    }

}