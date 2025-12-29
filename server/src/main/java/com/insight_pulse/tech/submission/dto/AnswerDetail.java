package com.insight_pulse.tech.submission.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerDetail {
    @JsonProperty("sys-name")    
    private String sysName;

    @JsonProperty("sys-email")
    private String sysEmail;

    private Map<String, Object> dynamicFields = new HashMap<>();

    @JsonAnySetter
    public void add(String key, Object value) {
        dynamicFields.put(key, value);
    }

    public String getSysName() { return sysName; }
    public String getSysEmail() { return sysEmail; }
    public Map<String, Object> getDynamicFields() { return dynamicFields; }
    
    public Map<String, Object> getAllAnswers() {
        Map<String, Object> all = new HashMap<>(dynamicFields);
        all.put("sys-name", sysName);
        all.put("sys-email", sysEmail);
        return all;
    }
}
