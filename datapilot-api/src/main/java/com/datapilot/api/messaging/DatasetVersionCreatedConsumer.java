package com.datapilot.api.messaging;

import com.datapilot.api.events.DatasetVersionCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DatasetVersionCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(DatasetVersionCreatedConsumer.class);

    // @KafkaListener -> Tells Spring Kafka to listen to the specified topic
    // topics -> topics	Identifies which Kafka topic to subscribe to
    // consume() -> Invoked when a message is received
    @KafkaListener(topics = "${datapilot.kafka.topics.dataset-version-created}")
    public void consume(DatasetVersionCreated event){
        log.info(
                "Received DatasetVersionCreated event. " +
                        "eventId={}, datasetId={}, versionNumber={}, occurredAt={}",
                event.eventId(),
                event.datasetId(),
                event.versionNumber(),
                event.occurredAt()
        );
    }


}
