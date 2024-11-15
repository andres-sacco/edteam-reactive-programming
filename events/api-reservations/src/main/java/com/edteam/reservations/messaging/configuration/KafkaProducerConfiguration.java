package com.edteam.reservations.messaging.configuration;

import com.edteam.reservations.dto.PaymentDTO;
import com.edteam.reservations.dto.ReservationTransactionDTO;
import com.edteam.reservations.messaging.serializer.PaymentSerializer;
import com.edteam.reservations.messaging.serializer.ReservationSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfiguration {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.producer.client-id}")
    private String producerClientId;

    @Bean
    public ProducerFactory<String, PaymentDTO> producerPaymentFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PaymentSerializer.class);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, producerClientId);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, PaymentDTO> kafkaPaymentTemplate() {
        return new KafkaTemplate<>(producerPaymentFactory());
    }

    @Bean
    public ProducerFactory<String, ReservationTransactionDTO> producerReservationFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ReservationSerializer.class);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, producerClientId);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ReservationTransactionDTO> kafkaReservationTemplate() {
        return new KafkaTemplate<>(producerReservationFactory());
    }
}
