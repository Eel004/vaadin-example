package com.example.application.backend.service.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LatestData {
    private Long deaths;
    private Long confirmed;
    private Long recovered;
}
