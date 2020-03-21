package com.mbcoder.coronatrackerslackbot.service;

import com.mbcoder.coronatrackerslackbot.model.corona.WorldometerData;
import com.mbcoder.coronatrackerslackbot.model.slack.Attachment;
import com.mbcoder.coronatrackerslackbot.model.slack.Field;
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
        attachment2.setTitle("Total Recovered");
        attachment2.setText(String.format("%,d", data.getRecovered()));
        attachment2.setColor("#00AA00");
        attachments.add(attachment2);

        Attachment attachment3 = new Attachment();
        attachment3.setTitle("Total Deaths");
        attachment3.setText(String.format("%,d", data.getDeaths()));
        attachment3.setColor("#CC0000");
        attachments.add(attachment3);

        if (data.getTodayCases() != null && data.getTodayDeaths() != null && data.getActive() != null && data.getCritical() != null) {
            Attachment attachment4 = new Attachment();
            Field field1 = new Field();
            field1.setTitle("Today's Cases");
            field1.setValue(String.format("%,d", data.getTodayCases()));
            field1.setShort(true);
            attachment4.addField(field1);

            Field field2 = new Field();
            field2.setTitle("Today's Deaths");
            field2.setValue(String.format("%,d", data.getTodayDeaths()));
            field2.setShort(true);
            attachment4.addField(field2);

            Field field3 = new Field();
            field3.setTitle("Active Cases");
            field3.setValue(String.format("%,d", data.getActive()));
            field3.setShort(true);
            attachment4.addField(field3);

            Field field4 = new Field();
            field4.setTitle("Critical Cases");
            field4.setValue(String.format("%,d", data.getCritical()));
            field4.setShort(true);
            attachment4.addField(field4);

            attachment4.setColor("#CC0000");
            attachment4.setFooter("Data taken from Worldometers.info");
            attachments.add(attachment4);
        }

        return attachments;
    }
}
