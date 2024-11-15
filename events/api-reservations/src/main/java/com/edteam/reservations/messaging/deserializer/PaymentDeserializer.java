package com.edteam.reservations.messaging.deserializer;

import com.edteam.reservations.dto.PaymentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PaymentDeserializer implements Deserializer<PaymentDTO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDeserializer.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PaymentDTO deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, PaymentDTO.class);
        } catch (IOException e) {
            LOGGER.error("Occurs an error while deserializing payment data", e);
            return null;
        }
    }
}