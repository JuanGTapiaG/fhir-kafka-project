package com.example.fhirkafka.service;

import ca.uhn.fhir.context.FhirContext;
import com.example.fhirkafka.publisher.KafkaPublisher;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.HumanName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import java.util.UUID;

@Service
public class PatientService {
    
    @Autowired
    private KafkaPublisher kafkaPublisher;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private FhirContext fhirContext = FhirContext.forR4();
    
    @PostConstruct
    public void init() {
        try {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS PATIENT (
                    ID VARCHAR(100) PRIMARY KEY,
                    FIRST_NAME VARCHAR(100),
                    LAST_NAME VARCHAR(100),
                    IDENTIFIER VARCHAR(50),
                    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
            jdbcTemplate.execute(createTableSQL);
            System.out.println("✅ Tabla PATIENT creada/verificada");
        } catch (Exception e) {
            System.err.println("❌ Error creando tabla: " + e.getMessage());
        }
    }
    
    @Transactional
    public Patient create(String firstName, String lastName, String identifier) {
        try {
            Patient patient = new Patient();
            String patientId = UUID.randomUUID().toString();
            patient.setId(patientId);
            
            HumanName name = patient.addName();
            name.setFamily(lastName);
            name.addGiven(firstName);
            
            patient.addIdentifier()
                .setSystem("http://hospital.com/cedula")
                .setValue(identifier);
            
            String sql = "INSERT INTO PATIENT (ID, FIRST_NAME, LAST_NAME, IDENTIFIER) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, patientId, firstName, lastName, identifier);
            
            System.out.println("==========================================");
            System.out.println("✅ PACIENTE GUARDADO (Kafka)");
            System.out.println("==========================================");
            System.out.println("   ID: " + patientId);
            System.out.println("   Nombre: " + firstName + " " + lastName);
            System.out.println("   Cédula: " + identifier);
            System.out.println("==========================================");
            
            kafkaPublisher.publish(patient, "CREATED");
            
            return patient;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            throw new RuntimeException("Error al crear paciente: " + e.getMessage());
        }
    }
}
