package com.paigeliu.azure_pgnotify;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaEventHubProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventHubProducer.class);
    private static String PROPERTY_FILE = "kafka.properties";

    public Producer<Long, String> createProducer() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE);
        if (input == null) {
            LOGGER.error("unable to find {}", PROPERTY_FILE);
            return null;
        }
        Properties prop = new Properties();
        prop.load(input);
        prop.put(ProducerConfig.CLIENT_ID_CONFIG, "PGNotifyProducer");
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(prop);
    }
}
