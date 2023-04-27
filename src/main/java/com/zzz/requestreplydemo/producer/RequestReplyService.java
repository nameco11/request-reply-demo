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

    @Autowired
    private ReplyingKafkaTemplate<RequestKey, String, String> replyingKafkaTemplate;


//    RequestKey: Sử dụng sessionId và serialId, RequestKey để sử dụng làm khóa (key - correlation id )cho Kafka message.
//    ProducerRecord: Sử dụng RequestKey, message, và topic REQUEST_TOPIC, init ProducerRecord để request đến Kafka.
//    replyingKafkaTemplate,  gửi request đến Kafka và nhận response
//    sendAndReceive return RequestReplyFuture đại diện cho kết quả trả về của yêu cầu.
//    Lấy kết quả gửi: Sử dụng future.getSendFuture().get() để lấy kết quả gửi của yêu cầu. Nếu gửi thành công, kết quả sẽ được lưu trong SendResult.
//    Lấy kết quả nhận: Sử dụng future.get() để lấy kết quả nhận của yêu cầu. Đây là một phương thức đồng bộ và sẽ chờ đợi cho đến khi nhận được kết quả trả về từ Kafka.
//    Trả về kết quả: Nếu nhận được kết quả trả về, return value của response.
    public String sendRequest(String message, String sessionId, String serialId) {
        // sessionId và serialId,  sử dụng làm key cho Kafka message
        RequestKey requestKey = new RequestKey(sessionId, serialId);
        ProducerRecord<RequestKey, String> record = new ProducerRecord<>(REQUEST_TOPIC, requestKey, message);
        RequestReplyFuture<RequestKey, String, String> future = replyingKafkaTemplate.sendAndReceive(record);

        try {
            SendResult<RequestKey, String> sendResult = future.getSendFuture().get(10, TimeUnit.SECONDS);
            System.out.println("Sent request: " + sendResult.getRecordMetadata());
            ConsumerRecord<RequestKey, String> response = future.get(10, TimeUnit.SECONDS);
            System.out.println("Received response: " + response);
            return response.value();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Error processing request-reply", e);
        }
    }
}

