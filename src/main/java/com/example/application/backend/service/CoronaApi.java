package com.example.application.backend.service;

import com.example.application.backend.service.model.Country;
import com.example.application.backend.service.model.DataWrapper;
import com.example.application.backend.service.model.Timeline;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "coronaapi", url = "${coronaapi.url}")
public interface CoronaApi {

    @RequestMapping(value = "/countries")
    DataWrapper<List<Country>> countries();

    @RequestMapping(value = "/countries/{code}")
    DataWrapper<Country> countries(@PathVariable String code);

    @RequestMapping(value = "timeline")
    DataWrapper<List<Timeline>> timeline();
}
