# Arquitectura de TaskFlow

Este documento es una guía de alto nivel para un desarrollador nuevo en TaskFlow. Contiene las capas principales, el recorrido de la petición `POST /projects/{projectId}/tasks`, dónde viven las reglas de negocio, cómo funciona la seguridad JWT y cómo están organizados los tests.

---

## Capas y paquetes

- Capa de presentación (controllers): los endpoints HTTP y validación de entrada están en `src/main/java/com/taskflow/controller`. Clase principal: `TaskController` (`src/main/java/com/taskflow/controller/TaskController.java`).
- DTOs y mappers: objetos de transporte entre web y dominio en `src/main/java/com/taskflow/dto` y `src/main/java/com/taskflow/mapper`. Ej.: `TaskRequest`, `TaskResponse`, `TaskMapper`.
- Capa de servicio (business logic orchestration): servicios que implementan casos de uso en `src/main/java/com/taskflow/service`. Ej.: `TaskService` (`src/main/java/com/taskflow/service/TaskService.java`).
- Capa de dominio/modelo: entidades y reglas de negocio en `src/main/java/com/taskflow/model`. Ej.: `Task` (`src/main/java/com/taskflow/model/Task.java`).
- Capa de persistencia (repositorios): interfaces Spring Data JPA en `src/main/java/com/taskflow/repository`. Ej.: `TaskRepository` (`src/main/java/com/taskflow/repository/TaskRepository.java`).
- Configuración y seguridad: `src/main/java/com/taskflow/config` y `src/main/java/com/taskflow/security`. Ej.: `SecurityConfig` (`src/main/java/com/taskflow/config/SecurityConfig.java`), `JwtService` (`src/main/java/com/taskflow/security/JwtService.java`), `JwtAuthenticationFilter` (`src/main/java/com/taskflow/security/JwtAuthenticationFilter.java`).
- Infra y utilidades: `src/main/java/com/taskflow/config`, `src/main/java/com/taskflow/exception`, `src/main/java/com/taskflow/advice` (por ejemplo `GlobalExceptionHandler` `src/main/java/com/taskflow/advice/GlobalExceptionHandler.java`).

---

## Recorrido de `POST /projects/{projectId}/tasks`

1. La petición HTTP llega a `TaskController.createTask` (`src/main/java/com/taskflow/controller/TaskController.java`), que recibe un `TaskRequest` y el `projectId` de la ruta.
2. El controller comprueba que el proyecto existe llamando a `ProjectService.buscarPorId` (`src/main/java/com/taskflow/service/ProjectService.java`). Si el Optional está vacío lanza `ProjectNotFoundException` y el advice devuelve 404.
3. El controller delega a `TaskService.crear` (`src/main/java/com/taskflow/service/TaskService.java`). Dentro de `crear`, el DTO se convierte en una entidad nueva mediante `TaskMapper.aEntidadNueva` (`src/main/java/com/taskflow/mapper/TaskMapper.java`).
4. `TaskService.crear` persiste la entidad usando `TaskRepository.save` (`src/main/java/com/taskflow/repository/TaskRepository.java`), que asigna el `id` por la BD.
5. Tras persistir, `TaskController.createTask` construye la respuesta `201 Created` y devuelve el cuerpo mapeado con `TaskMapper.aResponse` (`src/main/java/com/taskflow/mapper/TaskMapper.java`) y el header `Location` apuntando a `/tasks/{id}`.
6. Errores y validaciones: reglas de dominio (p. ej. `dueDate` no en el pasado) son aplicadas por `TaskMapper.aEntidadNueva` → `Task.crear` y lanzan `TaskValidationException`; `GlobalExceptionHandler` (`src/main/java/com/taskflow/advice/GlobalExceptionHandler.java`) traduce estas excepciones a respuestas HTTP apropiadas (400/422/404/500).

---

## Dónde viven las reglas de negocio

- Reglas transaccionales y de orquestación: en `TaskService` (`src/main/java/com/taskflow/service/TaskService.java`). Aquí se implementan flujos (crear, cambiar estado, asignaciones) y coordinación entre repositorios y servicios.
- Invariantes del modelo y validaciones de dominio: en la entidad `Task` (`src/main/java/com/taskflow/model/Task.java`) y métodos fábrica como `crear`. Esto asegura que las reglas se aplican también cuando la entidad es creada fuera de HTTP.
- Excepciones de dominio/validación: en `src/main/java/com/taskflow/exception` y son mapeadas por `GlobalExceptionHandler`.

Principio: preferir validaciones de alta cohesión en el dominio y orquestación en los servicios.

---

## Seguridad con JWT

- Emisión de token: el flujo de autenticación (por ejemplo login) usa `JwtService` (`src/main/java/com/taskflow/security/JwtService.java`) para crear tokens JWT firmados que contienen el `userId`/claims necesarios.
- Validación por request: `JwtAuthenticationFilter` (`src/main/java/com/taskflow/security/JwtAuthenticationFilter.java`) actúa sobre cada petición entrante, extrae el header `Authorization: Bearer <token>`, valida la firma y fecha, y rellena el contexto de seguridad (`Authentication`) para Spring Security.
- Configuración: `SecurityConfig` (`src/main/java/com/taskflow/config/SecurityConfig.java`) define las reglas de rutas, qué endpoints son `permitAll()` (por ejemplo `/auth/**`, `/swagger-ui/**`) y qué rutas requieren autenticación/roles. También registra el filtro JWT en la cadena de filtros.
- Uso en servicios: métodos que necesitan información del usuario obtienen el `Authentication` del SecurityContext (o reciben el `userId` del token) para decisiones de autorización (p. ej. verificar que el usuario es owner del `projectId`).

---

## Organización de tests

- Unit tests (rápidos, aislados) en `src/test/java/com/taskflow` junto a paquetes correspondientes (`service`, `model`, `mapper`). Ej.: pruebas de `Task` y `TaskService` con mocks para repositorios.
- Slice tests / WebMvcTest: controladores se prueban con `@WebMvcTest` para validar mappings HTTP y contratos sin levantar todo el contexto.
- Data / Repository tests: `@DataJpaTest` para validar mappings JPA y consultas.
- Integration tests: `@SpringBootTest` e (opcional) Testcontainers para Postgres en pruebas de integración pesada; ejecutarse con `-Ddocker.tests=true` según el README.
- Comandos útiles: `mvn test` para suite, `mvn -Dtest=FullyQualifiedClassName test` para una clase específica.

---

## Consejos rápidos

- Buscar la entrada principal en `com.taskflow` y leer `TaskService` + `Task` para entender reglas y flujos.
- Las validaciones críticas están en el dominio; cambiar comportamiento de negocio probablemente requiere editar `Task` y `TaskService` simultáneamente.
- Seguridad: cualquier cambio en el token o claims afecta `JwtService` y `JwtAuthenticationFilter`.

---

Si necesitas un diagrama o ejemplos de llamadas concretas (payloads), indícalo y se anexan ejemplos y fragmentos de código. Las fechas límite se validan en `Task.crear` (`src/main/java/com/taskflow/model/Task.java`).
