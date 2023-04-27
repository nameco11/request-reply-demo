package com.zzz.requestreplydemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RequestReplyDemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RequestReplyDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

//        StreamsBuilder build topology của Kafka Streams.
//        Sử dụng builder.stream("request-topic") để đọc dữ liệu từ topic "request-topic" vào một KStream.
//        lambda mapValues trên KStream để xử lý dữ liệu.
//        Sử dụng to("response-topic", Produced.with(Serdes.String(), Serdes.String())) để ghi kết quả < process > vào topic "response-topic" với các Serdes tương ứng.
//        Build topology từ StreamsBuilder bằng cách call builder.build().
//        Tạo một KafkaStreams từ topology và cấu hình properties.
//        Bắt đầu KafkaStreams bằng cách gọi streams.start().
//        Thêm một ShutdownHook để đảm bảo streams được đóng khi ứng dụng dừng.

//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "request-reply-app");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        StreamsBuilder builder = new StreamsBuilder();
//        KStream<String, String> input = builder.stream("request-topic");
//        KStream<String, String> processed = input.mapValues(value -> "Processed: " + value);
//        processed.to("response-topic", Produced.with(Serdes.String(), Serdes.String()));
//        Topology topology = builder.build();
//        KafkaStreams streams = new KafkaStreams(topology, props);
//        streams.start();
//        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
