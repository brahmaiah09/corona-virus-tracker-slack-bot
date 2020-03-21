package com.mbcoder.coronatrackerslackbot.model.corona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JohnsHopkinsCSSEData {
    private String country;
    private String province;
    private String updatedAt;
    private Stats stats;
    private Coordinates coordinates;

    public WorldometerData convertToWorldometerData() {
        WorldometerData worldometerData = new WorldometerData();
        worldometerData.setCountry(country);
        worldometerData.setUpdated(LocalDateTime.parse(updatedAt).toInstant(ZoneOffset.of(ZoneId.systemDefault().getId())).toEpochMilli());
        worldometerData.setCases(getCases());
        worldometerData.setDeaths(getDeaths());
        worldometerData.setRecovered(getRecovered());
        return worldometerData;
    }

    public Integer getCases() {
        return Integer.parseInt(stats.getConfirmed());
    }

    public Integer getDeaths() {
        return Integer.parseInt(stats.getDeaths());
    }

    public Integer getRecovered() {
        return Integer.parseInt(stats.getRecovered());
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Stats {
    private String confirmed;
    private String deaths;
    private String recovered;

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class Coordinates {
    private String latitude;
    private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
