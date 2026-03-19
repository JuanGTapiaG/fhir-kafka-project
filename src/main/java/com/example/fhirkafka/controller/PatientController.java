package com.example.fhirkafka.controller;

import com.example.fhirkafka.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> request) {
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");
        String identifier = request.get("identifier");
        
        var patient = patientService.create(firstName, lastName, identifier);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Paciente guardado exitosamente (Kafka)");
        response.put("patientId", patient.getId());
        response.put("name", firstName + " " + lastName);
        response.put("cedula", identifier);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test")
    public String test() {
        return "✅ Proyecto Kafka funcionando correctamente";
    }
}
