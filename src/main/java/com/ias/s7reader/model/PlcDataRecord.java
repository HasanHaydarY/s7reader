package com.ias.s7reader.model;

import java.time.Instant;

public class PlcDataRecord {

    private String timestamp;
    private String deviceName;
    private String variableName;
    private String dataType;
    private Object value;
    private String description;
    private boolean readSuccess;
    private String errorMessage;

    public PlcDataRecord() {
        this.timestamp = Instant.now().toString();
    }

    public static PlcDataRecord success(String deviceName, String variableName,
                                         String dataType, Object value, String description) {
        PlcDataRecord r = new PlcDataRecord();
        r.deviceName = deviceName;
        r.variableName = variableName;
        r.dataType = dataType;
        r.value = value;
        r.description = description;
        r.readSuccess = true;
        return r;
    }

    public static PlcDataRecord failure(String deviceName, String variableName,
                                         String dataType, String errorMessage) {
        PlcDataRecord r = new PlcDataRecord();
        r.deviceName = deviceName;
        r.variableName = variableName;
        r.dataType = dataType;
        r.readSuccess = false;
        r.errorMessage = errorMessage;
        return r;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getVariableName() { return variableName; }
    public void setVariableName(String variableName) { this.variableName = variableName; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isReadSuccess() { return readSuccess; }
    public void setReadSuccess(boolean readSuccess) { this.readSuccess = readSuccess; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
