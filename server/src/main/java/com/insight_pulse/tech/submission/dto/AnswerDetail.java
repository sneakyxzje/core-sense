package com.insight_pulse.tech.submission.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class AnswerDetail {
    @JsonAlias({"sys-name", "sysName"}) 
    private String sysName;

    @JsonAlias({"sys-email", "sysEmail"}) 
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
