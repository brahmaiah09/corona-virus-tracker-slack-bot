package com.mbcoder.coronatrackerslackbot.service;

import com.mbcoder.coronatrackerslackbot.model.corona.WorldometerData;
import com.mbcoder.coronatrackerslackbot.model.slack.Attachment;
import com.mbcoder.coronatrackerslackbot.model.slack.SlackRequest;
import com.mbcoder.coronatrackerslackbot.model.slack.SlackResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SlackService {

    public static final Logger LOGGER = LoggerFactory.getLogger(SlackService.class);

    @Autowired
    DataProviderService dataProviderService;

    public SlackResponse handleSlashCommand(SlackRequest slackRequest) {
        LOGGER.debug("New slash command received: {}", slackRequest.getCommand());
        SlackResponse slackResponse = new SlackResponse();
        slackResponse.setType("ephemeral");
        slackResponse.setDeleteOriginal(true);
        switch (slackRequest.getCommand()) {
            case "/corona":
                List<Attachment> attachments;
                if (slackRequest.getText() != null && !slackRequest.getText().isEmpty()) {
                    String countryName = slackRequest.getText().trim();
                    attachments = getCountryResult(countryName);
                    if (attachments.isEmpty()) {
                        return new SlackResponse("No information found for given country name!", "ephemeral");
                    }
                    slackResponse.setText(String.format("COVID-19 CORONAVIRUS REPORT FOR *%s*", countryName));
                } else {
                    attachments = getTotalResult();
                    if (attachments.isEmpty()) {
                        return new SlackResponse("No information found, please re-try later!", "ephemeral");
                    }
                    slackResponse.setText("COVID-19 CORONAVIRUS REPORT");
                }
                attachments.forEach(slackResponse::addAttachment);
                break;
            default:
                LOGGER.debug("Command: {} is not supported!", slackRequest.getCommand());
                return new SlackResponse("Command is not supported!", "ephemeral");
        }
        return slackResponse;
    }

    private List<Attachment> getCountryResult(String countryName) {
        WorldometerData data = dataProviderService.getCountryData(countryName);
        if (data == null) {
            return Collections.EMPTY_LIST;
        }
        return createAttachments(data);
    }

    private List<Attachment> getTotalResult() {
        WorldometerData data = dataProviderService.getTotalData();
        if (data == null) {
            return Collections.EMPTY_LIST;
        }
        return createAttachments(data);
    }

    private List<Attachment> createAttachments(WorldometerData data) {
        List<Attachment> attachments = new ArrayList<>();

        Attachment attachment1 = new Attachment();
        attachment1.setTitle("Confirmed Cases");
        attachment1.setText(String.format("%,d", data.getCases()));
        attachment1.setColor("#999999");
        attachments.add(attachment1);

        Attachment attachment2 = new Attachment();
        attachment2.setTitle("Total Deaths");
        attachment2.setText(String.format("%,d", data.getDeaths()));
        attachment2.setColor("#CC0000");
        attachments.add(attachment2);

        Attachment attachment3 = new Attachment();
        attachment3.setTitle("Total Recovered");
        attachment3.setText(String.format("%,d", data.getRecovered()));
        attachment3.setColor("#00AA00");
        attachments.add(attachment3);

        return attachments;
    }
}
