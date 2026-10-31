package com.datapilot.api.messaging;

import com.datapilot.api.events.DatasetVersionCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Create the Kafka publisher
@Component
public class DatasetVersionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DatasetVersionEventPublisher.class);
    private final KafkaTemplate<String, DatasetVersionCreated> kafkaTemplate;
    private final String topic;

    public DatasetVersionEventPublisher(
            KafkaTemplate<String, DatasetVersionCreated> kafkaTemplate,
            @Value("${datapilot.kafka.topics.dataset-version-created}") String topic){
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(DatasetVersionCreated event){
        try{
            // KafkaTemplate.send() returns a future
            kafkaTemplate.send(topic, event.datasetId().toString(), event)
                    .whenComplete((result, exception) -> {  // whenComplete() -> we have attached a callback, it handles the outcome of the producer's send operation.
                        // successful completion means the broker acknowledged the message according to the configured settings; the precise durability guarantee depends on those settings.
                        if(exception != null){
                            log.error(
                                    "Failed to publish DatasetVersionCreated. " + "eventId={}, datasetId={}, versionNumber={}",
                                    event.eventId(),
                                    event.datasetId(),
                                    event.versionNumber(),
                                    exception
                            );
                        } else{
                            log.info(
                                    "Published DatasetVersionCreated. "+ "topic={}, eventId={}, datasetId={}, "+ "versionNumber={}",
                                    topic,
                                    event.eventId(),
                                    event.datasetId(),
                                    event.versionNumber()

                            );
                        }
                    });
        } catch (RuntimeException exception){
            log.error(
                    "Failed to initiate DatasetVersionCreated publication. "+ "eventId={}",
                    event.eventId(),
                    exception
            );
        }
    }
}
