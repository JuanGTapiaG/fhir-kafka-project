package com.example.fhirkafka.publisher;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaPublisher {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaPublisher.class);
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topic:patient-topic}")
    private String topic;
    
    private FhirContext fhirContext = FhirContext.forR4();
    
    public void publish(Patient patient, String operation) {
        try {
            String patientJson = fhirContext.newJsonParser().encodeResourceToString(patient);
            
            Map<String, Object> notification = new HashMap<>();
            notification.put("resourceType", "Patient");
            notification.put("patientId", patient.getId());
            notification.put("operation", operation);
            notification.put("timestamp", Instant.now().toString());
            notification.put("data", patientJson);
            
            log.info("📤 Enviando notificación a Kafka - Topic: {}, Paciente: {}", topic, patient.getId());
            
            kafkaTemplate.send(topic, patient.getId(), notification)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Mensaje enviado a Kafka - Partición: {}, Offset: {}", 
                            result.getRecordMetadata().partition(), 
                            result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ Error enviando a Kafka: {}", ex.getMessage());
                    }
                });
            
        } catch (Exception e) {
            log.error("Error en publicador Kafka: {}", e.getMessage());
        }
    }
}
