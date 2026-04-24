package com.ias.s7reader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OutputConfig {

    private String jsonFilePath = "output/plc_data.json";
    private boolean prettyPrint = true;
    private int maxEntriesPerFile = 10000;
    private boolean rotateLogs = true;

    public String getJsonFilePath() { return jsonFilePath; }
    public void setJsonFilePath(String jsonFilePath) { this.jsonFilePath = jsonFilePath; }

    public boolean isPrettyPrint() { return prettyPrint; }
    public void setPrettyPrint(boolean prettyPrint) { this.prettyPrint = prettyPrint; }

    public int getMaxEntriesPerFile() { return maxEntriesPerFile; }
    public void setMaxEntriesPerFile(int maxEntriesPerFile) { this.maxEntriesPerFile = maxEntriesPerFile; }

    public boolean isRotateLogs() { return rotateLogs; }
    public void setRotateLogs(boolean rotateLogs) { this.rotateLogs = rotateLogs; }
}
