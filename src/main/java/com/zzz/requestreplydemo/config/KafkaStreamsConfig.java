package com.zzz.requestreplydemo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.streams.application-id}")
    private String applicationId;

    @Value("${kafka.streams.default-key-serde}")
    private String defaultKeySerde;

    @Value("${kafka.streams.default-value-serde}")
    private String defaultValueSerde;


    private static final String REQUEST_TOPIC = "request-topic";
    private static final String RESPONSE_TOPIC = "response-topic";

    // Tạo topic REQUEST_TOPIC
    @Bean
    public NewTopic requestTopic() {
        return new NewTopic(REQUEST_TOPIC, 1, (short) 1);
    }

    // Tạo topic RESPONSE_TOPIC
    @Bean
    public NewTopic responseTopic() {
        return new NewTopic(RESPONSE_TOPIC, 1, (short) 1);
    }


//    Nếu sử dụng StreamsBuilderFactoryBean không cần phải cấu hình các bean của Kafka Consumer và Kafka Producer.
//    Kafka Streams đã được tích hợp sẵn với Kafka Consumer và Kafka Producer trong cùng một thư viện
//    Kafka Streams sẽ tự động quản lý các Kafka Consumer và Kafka Producer cần thiết để xử lý dữ liệu.
//    Nó sử dụng cấu hình của Kafka Streams để cấu hình các Consumer và Producer tương ứng.
//    @Bean
//    public StreamsBuilderFactoryBean streamsBuilderFactoryBean() {
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, defaultKeySerde);
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, defaultValueSerde);
//        return new StreamsBuilderFactoryBean(props);
//    }

//    StreamsConfig.APPLICATION_ID_CONFIG: Định danh cho ứng dụng Kafka Streams
//    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG: bootstrap server của Kafka
//    StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG: Class Serde mặc định cho khóa trong Kafka Streams,sử dụng Serdes.String().getClass() để xác định lớp Serde cho String.
//    StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG: Class Serde mặc định cho giá trị trong Kafka Streams, sử dụng Serdes.String().getClass() để xác định lớp Serde cho String.
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "request-reply-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public ConsumerFactory<RequestKey, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "response-group");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(configProps);
    }


    @Bean
    public ProducerFactory<RequestKey, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<RequestKey, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

//    ReplyingKafkaTemplate để gửi các message Kafka và nhận kết quả trả về từ consumer. ReplyingKafkaTemplate hỗ trợ cơ chế Request-Reply trong Kafka.
//    Param:
//    pf: Đối tượng ProducerFactory dùng để tạo Producer cho template.
//    container: Đối tượng KafkaMessageListenerContainer dùng để lắng nghe và xử lý các message trả về từ Kafka.
    @Bean
    public ReplyingKafkaTemplate<RequestKey, String, String> replyingKafkaTemplate(
            ProducerFactory<RequestKey, String> pf,
            KafkaMessageListenerContainer<RequestKey, String> container) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

//    Bean này tạo KafkaMessageListenerContainer để lắng nghe các message trả về từ topic RESPONSE_TOPIC.
//    Param:
//    cf: ConsumerFactory dùng để tạo Consumer cho container.
//    containerProperties: Cấu hình cho container, bằng cách chuyển topic RESPONSE_TOPIC vào ContainerProperties.
    @Bean
    public KafkaMessageListenerContainer<RequestKey, String> replyContainer(
            ConsumerFactory<RequestKey, String> cf) {
        ContainerProperties containerProperties = new ContainerProperties(RESPONSE_TOPIC);
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }


}


