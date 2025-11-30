package com.phishguard.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResultDto {

    private boolean isDanger;
    private String riskLevel;

    @JsonProperty("score")
    private double riskScore;

    private String category;

    @JsonProperty("explanation")
    private String reason;
}
