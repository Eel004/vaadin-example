package com.example.application.backend.service.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DataWrapper<T> {
    private T data;
}
