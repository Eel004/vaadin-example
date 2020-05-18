package com.example.application.backend.service;

import com.example.application.backend.domain.Country;

import java.util.List;

public interface CoronaService {

    List<Country> findAll();

    Country getById(String id);
}
