package com.zzz.requestreplydemo.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzz.requestreplydemo.config.RequestKey;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class RequestReplyService {

    private static final String REQUEST_TOPIC = "request-topic";

    ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    private ReplyingKafkaTemplate<RequestKey, String, String> replyingKafkaTemplate;


    public String sendRequest(String message, String sessionId, String serialId) {
        RequestKey requestKey = new RequestKey(sessionId, serialId);

        ProducerRecord<RequestKey, String> record = new ProducerRecord<>(REQUEST_TOPIC, requestKey, message);
        record.headers().add("session_id", sessionId.getBytes(StandardCharsets.UTF_8));
        record.headers().add("serial_id", serialId.getBytes(StandardCharsets.UTF_8));

        RequestReplyFuture<RequestKey, String, String> future = replyingKafkaTemplate.sendAndReceive(record);

        try {
            SendResult<RequestKey, String> sendResult = future.getSendFuture().get(30, TimeUnit.SECONDS);
            System.out.println("Sent request: " + sendResult.getRecordMetadata());

            ConsumerRecord<RequestKey, String> response = future.get(30, TimeUnit.SECONDS);
            System.out.println("Received response: " + response);
            return response.value();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Error processing request-reply", e);
        }
    }
}

