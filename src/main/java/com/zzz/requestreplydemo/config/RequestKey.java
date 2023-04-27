package com.zzz.requestreplydemo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestKey {

    private String sessionId;
    private String serialId;
    private UUID requestId;

    public RequestKey(String sessionId, String serialId) {
        this.sessionId = sessionId;
        this.serialId = serialId;
        this.requestId = UUID.randomUUID();
    }
}
