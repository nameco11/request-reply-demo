package com.zzz.requestreplydemo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestKey {
    private String sessionId;
    private String serialId;
}
