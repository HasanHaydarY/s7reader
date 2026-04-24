package com.ias.s7reader.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppConfig {

    private PlcConfig plc;
    private OutputConfig output;
    private List<VariableConfig> variables;

    public PlcConfig getPlc() { return plc; }
    public void setPlc(PlcConfig plc) { this.plc = plc; }

    public OutputConfig getOutput() { return output; }
    public void setOutput(OutputConfig output) { this.output = output; }

    public List<VariableConfig> getVariables() { return variables; }
    public void setVariables(List<VariableConfig> variables) { this.variables = variables; }
}
