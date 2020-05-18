package com.example.application.backend.service.impl;

import com.example.application.backend.domain.Country;
import com.example.application.backend.domain.Day;
import com.example.application.backend.service.CoronaApi;
import com.example.application.backend.service.CoronaService;
import com.example.application.backend.service.GeoIpService;
import com.example.application.backend.service.model.LatestData;
import com.example.application.backend.service.model.Timeline;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CoronaServiceImpl implements CoronaService {

    public static final String COVID_SERVICE_CACHE = "covid-service-cache";

    private final CoronaApi coronaApi;

    public CoronaServiceImpl(CoronaApi coronaApi) {
        this.coronaApi = coronaApi;
    }

    @Override
    @Cacheable(cacheNames = COVID_SERVICE_CACHE)
    public List<Country> findAll() {
        return Stream.concat(
                Stream.of(getGlobal()),
                coronaApi.countries().getData().stream()
                        .map(this::toDomain))
                .collect(Collectors.toList());
    }

    private Country getGlobal() {
        List<Timeline> timeLine = coronaApi.timeline().getData();
        Timeline lastTimeLine = timeLine.get(0);

        LatestData latestData = new LatestData();
        latestData.setConfirmed(lastTimeLine.getConfirmed());
        latestData.setDeaths(lastTimeLine.getDeaths());
        latestData.setRecovered(lastTimeLine.getRecovered());

        com.example.application.backend.service.model.Country world = new com.example.application.backend.service.model.Country();
        world.setCode(GeoIpService.WORLD_ISO_CODE);
        world.setName("Global");
        world.setPopulation(7800000000L);
        world.setLatest_data(latestData);
        world.setTimeline(timeLine);

        return toDomain(world);
    }

    @Override
    @Cacheable(cacheNames = COVID_SERVICE_CACHE)
    public Country getById(String id) {
        if (GeoIpService.WORLD_ISO_CODE.equals(id)) {
            return getGlobal();
        } else {
            return toDomain(coronaApi.countries(id).getData());
        }
    }

    private Country toDomain(com.example.application.backend.service.model.Country c) {
        if (c != null) {
            List<Day> days = new ArrayList<>();
            if (c.getTimeline() != null) {
                days = c.getTimeline().stream()
                        .map(t -> new Day(
                                t.getDate(),
                                t.getConfirmed(),
                                t.getDeaths(),
                                t.getRecovered(),
                                t.getNew_confirmed(),
                                t.getNew_deaths(),
                                t.getNew_recovered()
                        ))
                        .collect(Collectors.toList());
            }

            return new Country(
                    c.getCode(),
                    c.getName(),
                    c.getPopulation(),
                    c.getLatest_data().getConfirmed(),
                    c.getLatest_data().getDeaths(),
                    c.getLatest_data().getRecovered(),
                    days
            );
        } else {
            return null;
        }
    }

    @Scheduled(cron = "${coronaapi.cache.evict.cron}")
    @CacheEvict(cacheNames = COVID_SERVICE_CACHE, allEntries = true)
    public void clearCache() {
    }

}
