package com.example.fhirkafka.consumer;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class KafkaConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    
    private FhirContext fhirContext = FhirContext.forR4();
    
    @KafkaListener(topics = "${kafka.topic:patient-topic}", groupId = "fhir-group")
    public void consume(Map<String, Object> notification) {
        try {
            log.info("==========================================");
            log.info("📨 NOTIFICACIÓN RECIBIDA DESDE KAFKA");
            log.info("==========================================");
            log.info("Paciente ID: {}", notification.get("patientId"));
            log.info("Operación: {}", notification.get("operation"));
            log.info("Timestamp: {}", notification.get("timestamp"));
            
            String json = (String) notification.get("data");
            Patient patient = fhirContext.newJsonParser().parseResource(Patient.class, json);
            
            String patientName = patient.getNameFirstRep().getNameAsSingleString();
            log.info("Paciente: {}", patientName);
            log.info("✅ Notificación procesada exitosamente");
            
            // Simular procesamiento (opcional)
            Thread.sleep(1000);
            
        } catch (Exception e) {
            log.error("Error procesando notificación: {}", e.getMessage());
        }
    }
}
