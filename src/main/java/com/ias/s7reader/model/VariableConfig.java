package com.ias.s7reader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VariableConfig {

    private String name;
    private String deviceName;
    private String areaType;
    private int dbNumber;
    private int startIndex;
    private String dataType;
    private int bitNo;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getAreaType() { return areaType; }
    public void setAreaType(String areaType) { this.areaType = areaType; }

    public int getDbNumber() { return dbNumber; }
    public void setDbNumber(int dbNumber) { this.dbNumber = dbNumber; }

    public int getStartIndex() { return startIndex; }
    public void setStartIndex(int startIndex) { this.startIndex = startIndex; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public int getBitNo() { return bitNo; }
    public void setBitNo(int bitNo) { this.bitNo = bitNo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Variable{name='" + name + "', type=" + dataType +
               ", area=" + areaType + ", db=" + dbNumber + ", idx=" + startIndex + "}";
    }
}
