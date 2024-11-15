package com.edteam.reservations.messaging.producer;

import com.edteam.reservations.dto.ReservationTransactionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReservationTransactionProducer {

    private static final String TOPIC = "reservation-transactions";

    private final KafkaTemplate<String, ReservationTransactionDTO> kafkaPaymentTemplate;

    @Autowired
    public ReservationTransactionProducer(KafkaTemplate<String, ReservationTransactionDTO> kafkaPaymentTemplate) {
        this.kafkaPaymentTemplate = kafkaPaymentTemplate;
    }

    public Mono<Void> sendMessage(ReservationTransactionDTO message) {
        return Mono.fromRunnable(() -> kafkaPaymentTemplate.send(TOPIC, message)).then();
    }
}
