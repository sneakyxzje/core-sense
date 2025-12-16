package com.insight_pulse.tech.campaign.domain;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormQuestion implements Serializable {
    private String id;
    private String type;
    private String label;
    private String placeholder;
    private boolean required;
    private List<String> options;
}
