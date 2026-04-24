package com.ias.s7reader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlcConfig {

    private String ip;
    private int rack;
    private int slot;
    private int pduSize = 480;
    private int readIntervalMs = 1000;
    private int connectionTimeoutMs = 5000;

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public int getRack() { return rack; }
    public void setRack(int rack) { this.rack = rack; }

    public int getSlot() { return slot; }
    public void setSlot(int slot) { this.slot = slot; }

    public int getPduSize() { return pduSize; }
    public void setPduSize(int pduSize) { this.pduSize = pduSize; }

    public int getReadIntervalMs() { return readIntervalMs; }
    public void setReadIntervalMs(int readIntervalMs) { this.readIntervalMs = readIntervalMs; }

    public int getConnectionTimeoutMs() { return connectionTimeoutMs; }
    public void setConnectionTimeoutMs(int connectionTimeoutMs) { this.connectionTimeoutMs = connectionTimeoutMs; }

    @Override
    public String toString() {
        return "PlcConfig{ip='" + ip + "', rack=" + rack + ", slot=" + slot +
               ", pduSize=" + pduSize + ", readIntervalMs=" + readIntervalMs + "}";
    }
}
