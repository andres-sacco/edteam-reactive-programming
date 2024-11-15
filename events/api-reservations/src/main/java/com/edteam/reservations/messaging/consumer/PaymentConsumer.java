package com.edteam.reservations.messaging.consumer;

import com.edteam.reservations.dto.PaymentDTO;
import com.edteam.reservations.dto.PaymentStatusDTO;
import com.edteam.reservations.enums.APIError;
import com.edteam.reservations.exception.EdteamException;
import com.edteam.reservations.model.Status;
import com.edteam.reservations.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentConsumer.class);

    private final ReservationService service;

    @Autowired
    public PaymentConsumer(ReservationService service) {
        this.service = service;
    }

    @RetryableTopic(backoff = @Backoff(delay = 3000), attempts = "2", kafkaTemplate = "kafkaPaymentTemplate", dltStrategy = DltStrategy.NO_DLT)
    @KafkaListener(topics = "payments", containerFactory = "consumerListenerPaymentConsumerFactory")
    public void listen(@Payload PaymentDTO message) {
        LOGGER.info("Received message: {}", message);

        service.changeStatus(message.getId(), mapStatus(message.getStatus()))
                .doOnSuccess(result -> LOGGER.info("Status changed successfully for message: {}", message))
                .doOnError(error -> LOGGER.error("Error processing message: {}", message, error))
                .subscribe(
                        null,
                        throwable -> handleProcessingError(throwable, message)
                );
    }

    private Status mapStatus(PaymentStatusDTO status) {
        if (status.equals(PaymentStatusDTO.ACCEPTED)) {
            return Status.FINISHED;
        } else if (status.equals(PaymentStatusDTO.IN_PROGRESS)) {
            return Status.IN_PROGRESS;
        } else {
            throw new EdteamException(APIError.BAD_FORMAT);
        }
    }

    private void handleProcessingError(Throwable throwable, PaymentDTO message) {
        LOGGER.error("Failed to process message: {}", message, throwable);
    }
}