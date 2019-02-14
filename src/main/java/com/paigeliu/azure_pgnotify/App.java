package com.paigeliu.azure_pgnotify;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {
    private static Configurations config;
    private static Producer<Long, String> producer; 
    private static String topic;
    private static final Logger LOGGER = LoggerFactory.getLogger("azurepgnotify");

    private App() {
    }

    public static void main(String[] args) throws Exception {
        producer = new KafkaEventHubProducer().createProducer();
        config = new Configurations();
        topic = config.getProperty("pgnotify.kafka.topic");

        PGNotificationListener listener = new PGNotificationListener() {
            @Override
            public void notification(int processId, String channelName, String payload) {
                LOGGER.info("notification = " + payload);
                long time = System.currentTimeMillis();
                final ProducerRecord<Long, String> record = 
                    new ProducerRecord<Long, String>(topic, time, payload);
                producer.send(record, new Callback() {
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            LOGGER.error(exception.getMessage());
                            System.exit(1);
                        }
                    }
                });
            }
        };

        PGConnection pgconnection = new PgsqlConnection().createConnection(config);
        pgconnection.addNotificationListener(listener);
        while (true){ }
    }
}
