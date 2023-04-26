package com.zzz.requestreplydemo.controller;

import com.zzz.requestreplydemo.producer.RequestReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestReplyController {

    @Autowired
    private RequestReplyService requestReplyService;

    @GetMapping("/request/{message}")
    public String sendRequest(@PathVariable("message") String message,
                              @RequestHeader("session_id") String sessionId,
                              @RequestHeader("serial_id") String serialId) {
        return requestReplyService.sendRequest(message, sessionId, serialId);
    }
}
