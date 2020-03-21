package com.mbcoder.coronatrackerslackbot.controller;

import com.mbcoder.coronatrackerslackbot.model.slack.SlackRequest;
import com.mbcoder.coronatrackerslackbot.model.slack.SlackResponse;
import com.mbcoder.coronatrackerslackbot.service.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SlackController {

    public static final Logger LOGGER = LoggerFactory.getLogger(SlackController.class);

    @Autowired
    SlackService slackService;

    @RequestMapping(value = "/slack/slash",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public SlackResponse onReceiveSlashCommand(@RequestParam("team_id") String teamId,
                                               @RequestParam("team_domain") String teamDomain,
                                               @RequestParam("channel_id") String channelId,
                                               @RequestParam("channel_name") String channelName,
                                               @RequestParam("user_id") String userId,
                                               @RequestParam("user_name") String userName,
                                               @RequestParam("command") String command,
                                               @RequestParam("text") String text,
                                               @RequestParam("response_url") String responseUrl) {
        SlackRequest slackRequest = new SlackRequest(teamId, teamDomain, channelId, channelName, userId, userName, command, text, responseUrl);
        LOGGER.info(slackRequest.toString());

        SlackResponse slackResponse = slackService.handleSlashCommand(slackRequest);

        LOGGER.debug(slackResponse.toString());

        return slackResponse;
    }

}
