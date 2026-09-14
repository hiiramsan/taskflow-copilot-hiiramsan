# Copilot instructions for taskflow-api

Purpose: quick reference to help Copilot sessions understand, build, test and modify this repository.

1) Build, test and lint commands

- Build & run in Docker (recommended):
  - docker compose up --build
  - docker compose down
- Run locally (requires JDK 21 + Maven):
  - mvn spring-boot:run
- Tests:
  - Run full test suite: mvn test
  - Run with coverage gate: mvn verify
  - Run single test class: mvn -Dtest=FullyQualifiedClassName test
  - Run single test method: mvn -Dtest=ClassName#methodName test
  - Run integration repo test(s) with Docker: mvn test -Ddocker.tests=true
- Useful Maven profiles/flags:
  - Profile `cobertura` (activated by default in terminal via !m2e.version) contains JaCoCo coverage gate
  - Profile `docker-it` (activate with -Ddocker.tests=true) includes ITs and sets api.version for Testcontainers

2) High-level architecture (big picture)

- Spring Boot 3.5 REST API (Java 21). Entry point: com.taskflow.TaskflowApiApplication.
- Layered design:
  - controllers (HTTP endpoints): com.taskflow.controller
  - services (business logic): com.taskflow.service
  - repositories (JPA data access): com.taskflow.repository (Spring Data JPA)
  - domain/model: com.taskflow.model (entities such as Task, Project, User)
  - DTOs + mappers: com.taskflow.dto and com.taskflow.mapper for API <-> domain translation
  - security: JWT-based filters and JwtService in com.taskflow.security; route rules in com.taskflow.config.SecurityConfig
  - exceptions and a global @RestControllerAdvice map domain/validation errors to HTTP codes
- Data seeding: DataSeeder pre-populates users and projects on startup (useful for local/demo)
- API docs: springdoc-openapi provides Swagger UI at /swagger-ui/index.html
- Static assets: lightweight static HTML/JS in src/main/resources/static for manual testing
- Tests: unit, slice (@WebMvcTest/@DataJpaTest), integration (@SpringBootTest) + optional Testcontainers Postgres IT
- Packaging: multi-stage Dockerfile builds the jar with Maven then runs JRE-only image

3) Key repository conventions and patterns

- Domain-first validation:
  - Creation factory methods enforce temporal rules (e.g., Task.crear checks dueDate not in past).
  - Entities may be rehydrated by JPA (constructor protected) — rehydrate paths intentionally bypass certain create-time checks.
- Checked vs unchecked for validation/flow control:
  - TaskValidationException is checked and mapped to 400 via the global advice.
  - TaskStateException (unchecked) used when translating domain failures for status changes -> 422.
- Security pattern:
  - Route-level permitAll for public endpoints (/, /auth/**, /info, swagger, h2-console). Any other request requires authentication.
  - Use @EnableMethodSecurity and @PreAuthorize for data-driven rules (e.g., owner check on project delete). JwtAuthenticationFilter authenticates requests statelessly.
- Error handling:
  - GlobalExceptionHandler centralizes mapping domain exceptions -> HTTP responses. Controllers typically throw exceptions and do not manually craft status codes for errors.
- Ordering & strategies:
  - TaskOrders is a central catalog of Comparator strategies used by TaskService.listar() (POR_URGENCIA orders vencidas first via Task.estaVencida()).
- DTOs & mappers:
  - Controllers expose DTOs (TaskRequest/TaskResponse) and use mappers to convert to/from entities; entities are not returned directly.
- Tests & CI expectations:
  - Coverage gate in profile `cobertura` enforces >=70% line coverage on mvn verify.
  - Testcontainers usage is opt-in (mvn test -Ddocker.tests=true) and relies on Docker available locally.

4) Files to read first for typical tasks

- README.md (project overview + Docker/mvn commands)
- pom.xml (profiles: cobertura, docker-it; deps: jjwt, springdoc-openapi, testcontainers)
- src/main/java/com/taskflow/config/SecurityConfig.java (security rules & CORS behavior)
- src/main/java/com/taskflow/model/Task.java (domain validations: crear, estaVencida, setStatus)
- src/main/java/com/taskflow/service/TaskService.java and TaskOrders.java (list/filter/order behavior)
- src/main/java/com/taskflow/controller/TaskController.java (endpoints contract)
- src/main/java/com/taskflow/advice/GlobalExceptionHandler.java (exception → HTTP mapping)

5) Other assistant configs discovered

- No CLAUDE.md, AGENTS.md, .cursorrules, .windsurfrules, or other known assistant-rule files were found.

---

Created .github/copilot-instructions.md with the above guidance.

If you want this tuned (e.g., add exact mvn commands for running a single test file from within IDE, or include quick snippets for common edits like adding a new REST endpoint), say what to add and it will be updated.