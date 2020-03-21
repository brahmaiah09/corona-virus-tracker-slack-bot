package com.mbcoder.coronatrackerslackbot.service;

import com.mbcoder.coronatrackerslackbot.model.corona.JohnsHopkinsCSSEData;
import com.mbcoder.coronatrackerslackbot.model.corona.WorldometerData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class DataRetrieverService {

    private static final String BASE_URL = "https://corona.lmao.ninja";
    private static final String ALL_URL = "/all";
    private static final String COUNTRIES_URL = "/countries";
    private static final String JHUCSSE_URL = "/jhucsse";

    private final RestTemplate restTemplate;
    HttpEntity<String> entity;

    public DataRetrieverService() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        // Somehow requests are not going through if there is no user agent ( on Heroku )
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        entity = new HttpEntity<>("parameters", headers);
        this.restTemplate = new RestTemplate();
    }

    public WorldometerData getTotal() {
        return restTemplate.exchange(BASE_URL + ALL_URL, HttpMethod.GET, entity, WorldometerData.class).getBody();
    }

    public WorldometerData[] getAllCountries() {
        return restTemplate.exchange(BASE_URL + COUNTRIES_URL, HttpMethod.GET, entity, WorldometerData[].class).getBody();
    }

    public WorldometerData getCountry(String countryName) {
        return restTemplate.exchange(BASE_URL + COUNTRIES_URL + "/" + countryName, HttpMethod.GET, entity, WorldometerData.class).getBody();
    }

    public JohnsHopkinsCSSEData[] getAllCountriesFromJHCSSE() {
        return restTemplate.exchange(BASE_URL + JHUCSSE_URL, HttpMethod.GET, entity, JohnsHopkinsCSSEData[].class).getBody();
    }
}