# 📁 FHIR Kafka Project

[![Java](https://img.shields.io/badge/Java-21-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen)]()
[![Kafka](https://img.shields.io/badge/Kafka-4.1.1-red)]()
[![H2](https://img.shields.io/badge/H2-Database-blue)]()
[![License](https://img.shields.io/badge/License-MIT-yellow)]()

Sistema de notificaciones en tiempo real usando **FHIR R4** y **Apache Kafka** como plataforma de streaming.

---

## 📋 Descripción

Este proyecto implementa un sistema de notificaciones en tiempo real donde cada vez que se crea un paciente (recurso FHIR), se publica automáticamente un mensaje en Kafka para ser procesado por consumidores.

### ✨ Características

- ✅ API REST para crear pacientes (FHIR R4)
- ✅ Publicación automática de eventos en Kafka
- ✅ Consumidor que procesa las notificaciones en tiempo real
- ✅ Persistencia en H2 (archivo permanente)
- ✅ Interfaz de monitoreo H2 Database

---

## 🏗️ Arquitectura
┌─────────────┐ ┌──────────────┐ ┌─────────────┐ ┌─────────────┐
│ Cliente │────▶│ API REST │────▶│ Kafka │────▶│ Consumidor │
│ (curl) │ │ Spring Boot │ │ Broker │ │ Microservicio│
└─────────────┘ └──────────────┘ └─────────────┘ └─────────────┘
│ │ │
▼ ▼ ▼
┌──────────────┐ ┌─────────────┐ ┌─────────────┐
│ Interceptor │ │ Topic │ │ Procesa │
│ FHIR │ │patient-topic│ │ Notificación│
└──────────────┘ └─────────────┘ └─────────────┘
│
▼
┌──────────────┐
│ Base de │
│ Datos H2 │
│ (Permanente)│
└──────────────┘

### 📡 Flujo de Datos

1. **Cliente** → Crea paciente vía API REST (`POST /api/patients`)
2. **API** → Guarda paciente en H2 y activa el publicador
3. **Publicador** → Envía notificación JSON a Kafka (topic: `patient-topic`)
4. **Kafka** → Almacena el mensaje en el topic
5. **Consumidor** → Escucha el topic y procesa la notificación
6. **Confirmación** → API responde al cliente con éxito

---

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.2.2 | Framework de aplicación |
| Apache Kafka | 4.1.1 | Plataforma de streaming |
| Spring Kafka | 3.2.2 | Integración Kafka con Spring |
| HAPI FHIR | 7.4.0 | Estructuras FHIR R4 |
| H2 Database | 2.2.224 | Base de datos embebida |
| Maven | 3.8+ | Gestor de dependencias |

---

## 📋 Requisitos Previos

### Software Necesario

```bash
- Java 21
- Maven 3.8+
- Apache Kafka 4.1.1 (sin Zookeeper, modo KRaft)
```

### Instalación en Debian/Ubuntu

### Actualizar repositorios
```bash
sudo apt update
```

### Instalar Java 21
```bash
sudo apt install openjdk-21-jdk -y
```

### Instalar Maven
```bash
sudo apt install maven -y
```

### Verificar instalaciones
```bash
java -version
mvn -version
```

## 🚀 Instalación de Kafka 4.1.1 (KRaft)

### Paso 1: Descargar Kafka

```bash
cd /tmp
wget https://downloads.apache.org/kafka/4.1.1/kafka_2.13-4.1.1.tgz
tar -xzf kafka_2.13-4.1.1.tgz
sudo mv kafka_2.13-4.1.1 /opt/kafka
```

### Paso 2: Crear directorios para datos

```bash
sudo mkdir -p /tmp/kafka-logs
sudo chmod 777 /tmp/kafka-logs
```

### Paso 3: Iniciar Kafka con KRaft (sin Zookeeper)

```bash
cd /opt/kafka
```

### Generar ID único para el clúster
```bash
KAFKA_CLUSTER_ID=$(bin/kafka-storage.sh random-uuid)
```
### Formatear storage
```bash
bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/server.properties --standalone
```
### Iniciar Kafka
```bash
bin/kafka-server-start.sh -daemon config/server.properties
```

### Paso 4: Crear el topic para el proyecto

```bash
bin/kafka-topics.sh --create \
  --topic patient-topic \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1
```

### Paso 5: Verificar instalación

```bash
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```
Debería mostrar: patient-topic

## 📦 Instalación del Proyecto

### Paso 1: Clonar repositorio

```bash
git clone https://github.com/JuanGTapiaG/fhir-kafka-project.git
cd fhir-kafka-project
```

### Paso 2: Compilar

```bash
mvn clean compile
```

### Paso 3: Ejecutar aplicación

```bash
mvn spring-boot:run
```

### Paso 4: Verificar que funciona

```bash
curl http://localhost:8081/api/patients/test
```

Respuesta: ✅ Proyecto Kafka funcionando

## 📡 API Endpoints

### 1. Verificar Estado

```bash
GET /api/patients/test
```

### Ejemplo:

```bash
curl http://localhost:8081/api/patients/test
```

### Respuesta:

```text
✅ Proyecto Kafka funcionando
```

### 2. Crear Paciente

```bash
POST /api/patients
Content-Type: application/json
```

### Ejemplo con Omar Gómez:

```bash
curl -X POST http://localhost:8081/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Omar",
    "lastName": "Gómez",
    "identifier": "1007588983"
  }'
```

### Respuesta:

```json
{
  "message": "Paciente guardado exitosamente",
  "patientId": "3f8469df-fc0d-4819-bbd2-2a433b8652aa",
  "name": "Omar Gómez"
}
```

### 3. Ejemplos Adicionales

Ana Martínez
```bash
curl -X POST http://localhost:8081/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ana",
    "lastName": "Martínez",
    "identifier": "52436789"
  }'
```

Carlos Rodríguez
```bash
curl -X POST http://localhost:8081/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Carlos",
    "lastName": "Rodríguez",
    "identifier": "78901234"
  }'
```

## 👀 Monitoreo

### Kafka (línea de comandos)


Ver topics
```bash
cd /opt/kafka
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```
Ver detalles del topic
```bash
bin/kafka-topics.sh --describe --topic patient-topic --bootstrap-server localhost:9092
```
Consumir mensajes (ver en tiempo real)
```bash
bin/kafka-console-consumer.sh \
  --topic patient-topic \
  --bootstrap-server localhost:9092 \
  --from-beginning
```

### H2 Database Console

URL: http://localhost:8081/h2-console

JDBC URL: jdbc:h2:file:./data/kafka-testdb

Usuario: sa

Contraseña: (vacío)

### Consultas útiles:

Ver todos los pacientes
```sql
SELECT * FROM PATIENT;
```
Ver paciente por cédula
```sql
SELECT * FROM PATIENT WHERE IDENTIFIER = '1007588983';
```

## 📁 Estructura del Proyecto

```text
fhir-kafka-project/
├── pom.xml
├── README.md
├── data/
│   └── kafka-testdb.mv.db          # Base de datos H2 (permanente)
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── fhirkafka/
        │               ├── FhirKafkaApplication.java
        │               ├── config/
        │               │   ├── KafkaConfig.java
        │               │   └── FhirConfig.java
        │               ├── controller/
        │               │   └── PatientController.java
        │               ├── service/
        │               │   └── PatientService.java
        │               ├── publisher/
        │               │   └── KafkaPublisher.java
        │               └── consumer/
        │                   └── KafkaConsumer.java
        └── resources/
            └── application.yml
```
### Descripción de Componentes
Componente	Archivo	Función
Main	FhirKafkaApplication.java	Punto de entrada
Config Kafka	KafkaConfig.java	Configura topics
Config FHIR	FhirConfig.java	Configura contexto FHIR
Controller	PatientController.java	Endpoints REST
Service	PatientService.java	Lógica de negocio y H2
Publisher	KafkaPublisher.java	Publica en Kafka
Consumer	KafkaConsumer.java	Consume de Kafka

## 🚀 Scripts Útiles

### Iniciar Kafka rápidamente

```bash
nano ~/iniciar-kafka.sh
```

### !/bin/bash

```bash
cd /opt/kafka
KAFKA_CLUSTER_ID=$(bin/kafka-storage.sh random-uuid 2>/dev/null)
bin/kafka-storage.sh format -t $KAFKA_CLUSTER_ID -c config/server.properties --standalone > /dev/null 2>&1
bin/kafka-server-start.sh -daemon config/server.properties
sleep 3
echo "✅ Kafka iniciado"
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```
```bash
chmod +x ~/iniciar-kafka.sh
~/iniciar-kafka.sh
```

### Script de prueba completo

```bash
nano ~/test-kafka.sh
```

### !/bin/bash
```bash
echo "=================================="
echo "🧪 TEST PROYECTO KAFKA"
echo "=================================="

echo -e "\n1. Verificando aplicación..."
curl -s http://localhost:8081/api/patients/test || echo "❌ App no responde"

echo -e "\n\n2. Creando paciente Omar Gómez..."
curl -s -X POST http://localhost:8081/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Omar",
    "lastName": "Gómez",
    "identifier": "1007588983"
  }' | json_pp

echo -e "\n\n3. Verificando Kafka..."
/opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092 | grep patient-topic

echo -e "\n✅ Prueba completada"
```
```bash
chmod +x ~/test-kafka.sh
~/test-kafka.sh
```
## ❗Solución de Problemas

## Error: Kafka no conecta

### Verificar que Kafka está corriendo
```bash
ps aux | grep kafka
```
### Si no está, iniciarlo
```bash
cd /opt/kafka
bin/kafka-server-start.sh -daemon config/server.properties
```
### Verificar puerto
```bash
netstat -tlnp | grep 9092
```

## Error: Puerto 8081 ocupado

### Ver proceso
```bash
sudo lsof -i :8081
```
### Matar proceso
```bash
sudo kill -9 <PID>
```
### O matar todos los procesos Java
```bash
pkill -f java
```

## Error: Tabla PATIENT no encontrada en H2
```sql
CREATE TABLE IF NOT EXISTS PATIENT (
    ID VARCHAR(100) PRIMARY KEY,
    FIRST_NAME VARCHAR(100),
    LAST_NAME VARCHAR(100),
    IDENTIFIER VARCHAR(50),
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Error: JDBC URL incorrecta en H2
Usar:

```text
jdbc:h2:file:./data/kafka-testdb
```
NO usar:

jdbc:h2:mem:testdb

jdbc:h2:file://data/testdb

jdbc:h2:~/testdb

## 📊 Cumplimiento de Requisitos
###	Requisito	Estado	Verificación
1	Crear paciente FHIR	✅	POST /api/patients
2	Publicar en Kafka	✅	Logs y kafka-console-consumer
3	Consumir desde Kafka	✅	Logs del consumidor
4	Persistencia en H2	✅	SELECT * FROM PATIENT;

## 👤 Autores
### Omar Gomez
### Juan Tapia

## 📄Licencia
MIT License

Copyright (c) 2026 Juan Tapia

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## 🙏 Agradecimientos
Apache Kafka por la plataforma de streaming

Spring Boot por el framework

HAPI FHIR por la biblioteca FHIR

Comunidad de código abierto

## 📞 Contacto
Issues: https://github.com/JuanGTapiaG/fhir-kafka-project/issues

## 🏁 Estado del Proyecto
✅ Producción - Versión 1.0.0 estable

¡Gracias por usar este proyecto! 🚀
