package com.randonautica.app.Classes;

import java.util.HashMap;
import java.util.Map;

public class Verify {

    private long timestamp;
    private Boolean validated;
    private Integer points;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public long getTimestamp() {
        return timestamp;
    }

    public Boolean getValidated() {
        return validated;
    }

    public Integer getPoints() {
        return points;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}