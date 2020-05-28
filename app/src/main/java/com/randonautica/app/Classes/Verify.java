package com.randonautica.app.Classes;

import java.util.HashMap;
import java.util.Map;

public class Verify {

    private long timestamp;
    private Boolean validated;
    private Integer anomalypoints;
    private Integer attractorpoints;
    private Integer voidpoints;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public long getTimestamp() {
        return timestamp;
    }

    public Boolean getValidated() {
        return validated;
    }

    public Integer getAnomalypoints() {
        return anomalypoints;
    }

    public Integer getAttractorpoints() {
        return attractorpoints;
    }

    public Integer getVoidpoints() {
        return voidpoints;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}