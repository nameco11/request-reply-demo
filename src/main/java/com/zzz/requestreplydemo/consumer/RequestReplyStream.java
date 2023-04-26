package com.zzz.requestreplydemo.consumer;

import com.zzz.requestreplydemo.config.RequestKey;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
@EnableKafkaStreams
public class RequestReplyStream {

    private static final String REQUEST_TOPIC = "request-topic";
    private static final String RESPONSE_TOPIC = "response-topic";

    @Bean
    public KStream<RequestKey, String> kStream(org.apache.kafka.streams.StreamsBuilder streamsBuilder) {
        Serde<RequestKey> requestKeySerde = new JsonSerde<>(RequestKey.class);
        KStream<RequestKey, String> kStream = streamsBuilder.stream(REQUEST_TOPIC, Consumed.with(requestKeySerde, Serdes.String()));

        kStream.mapValues(value -> "Processed: " + value)
                .to(RESPONSE_TOPIC, Produced.with(requestKeySerde, Serdes.String()));

        return kStream;
    }
}
