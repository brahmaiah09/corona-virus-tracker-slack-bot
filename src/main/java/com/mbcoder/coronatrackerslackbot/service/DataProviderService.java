package com.mbcoder.coronatrackerslackbot.service;

import com.mbcoder.coronatrackerslackbot.model.corona.JohnsHopkinsCSSEData;
import com.mbcoder.coronatrackerslackbot.model.corona.WorldometerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@EnableScheduling
@Service
public class DataProviderService {
    WorldometerData totalData;

    List<WorldometerData> worldometerDataList;
    Map<String, WorldometerData> worldometerDataMap;

    List<JohnsHopkinsCSSEData> johnsHopkinsCSSEDataList;
    Map<String, JohnsHopkinsCSSEData> johnsHopkinsCSSEDataMap;

    @Autowired
    DataRetrieverService dataRetrieverService;

    public DataProviderService() {
        worldometerDataList = new ArrayList<>();
        worldometerDataMap = new HashMap<>();

        johnsHopkinsCSSEDataList = new ArrayList<>();
        johnsHopkinsCSSEDataMap = new HashMap<>();
    }

    // Every 15 minute
    @Scheduled(fixedRate = 15L * 60L * 1000L, initialDelay = 0)
    private void refreshData() {
        totalData = dataRetrieverService.getTotal();
        worldometerDataList = Arrays.stream(dataRetrieverService.getAllCountries()).collect(Collectors.toList());
        johnsHopkinsCSSEDataList = Arrays.stream(dataRetrieverService.getAllCountriesFromJHCSSE()).collect(Collectors.toList());
        indexData(worldometerDataList, johnsHopkinsCSSEDataList);
    }

    private void indexData(List<WorldometerData> worldometerDataList, List<JohnsHopkinsCSSEData> johnsHopkinsCSSEDataList) {
        worldometerDataList.stream()
                .filter(data -> worldometerDataMap.get(data.getCountry().toLowerCase()) == null || worldometerDataMap.get(data.getCountry().toLowerCase()).getCases() < data.getCases())
                .forEach(data -> worldometerDataMap.put(data.getCountry().toLowerCase(), data));

        johnsHopkinsCSSEDataList.stream()
                .filter(data -> johnsHopkinsCSSEDataMap.get(data.getCountry().toLowerCase()) == null || johnsHopkinsCSSEDataMap.get(data.getCountry().toLowerCase()).getCases() < data.getCases())
                .forEach(data -> johnsHopkinsCSSEDataMap.put(data.getCountry().toLowerCase(), data));
    }

    public WorldometerData getCountryData(String country) {
        WorldometerData worldometerData = worldometerDataMap.get(country);
        if (worldometerData != null) {
            return worldometerData;
        }

        JohnsHopkinsCSSEData johnsHopkinsCSSEData = johnsHopkinsCSSEDataMap.get(country);
        if (johnsHopkinsCSSEData != null) {
            return johnsHopkinsCSSEData.convertToWorldometerData();
        }

        return dataRetrieverService.getCountry(country);
    }

    public WorldometerData getTotalData() {
        if (totalData != null) {
            return totalData;
        }
        return dataRetrieverService.getTotal();
    }
}
