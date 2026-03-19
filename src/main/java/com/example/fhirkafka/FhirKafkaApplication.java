package com.example.fhirkafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FhirKafkaApplication {
    public static void main(String[] args) {
        SpringApplication.run(FhirKafkaApplication.class, args);
        System.out.println("==========================================");
        System.out.println("🚀 PROYECTO FHIR CON KAFKA INICIADO");
        System.out.println("==========================================");
        System.out.println("📌 Puerto: 8081");
        System.out.println("📌 Kafka: localhost:9092");
        System.out.println("📌 Topic: patient-topic");
        System.out.println("📌 H2 DB: http://localhost:8081/h2-console");
        System.out.println("==========================================");
    }
}
