# Copilot CLI Session

> [!NOTE]
> - **Session ID:** `7370b1ee-46b1-45e6-a586-628d0d87d60f`  
> - **Started:** 9/17/2026, 10:50:35 AM  
> - **Duration:** 4m 22s  
> - **Exported:** 9/17/2026, 10:54:57 AM  

---

<sub>2s</sub>

### User

/crear-endpoint-taskflow Implementa la especificación de specs/summary.md.

---

<sub>1m 2s</sub>

### Copilot

Running the crear-endpoint-taskflow skill to implement the specification at specs/summary.md. The skill will add DTO record, mapper, service method, controller (no /api prefix), unit test with Mockito, @WebMvcTest slice, and run mvn -q test to verify. Now invoking the endpoint-creation skill with full spec path and instructions.

---

<sub>1m 2s</sub>

### `skill`

**crear-endpoint-taskflow**

<details>
<summary>Arguments</summary>

```json
{
  "skill": "crear-endpoint-taskflow"
}
```

</details>

```
Skill "crear-endpoint-taskflow" loaded successfully. Follow the instructions in the skill context.
```


---

<sub>1m 22s</sub>

### Copilot

Reading the specification and templates, and locating the ProjectController to add the new route. This lets implementing the DTO, mapper, service method and controller consistent with nearby code. Running parallel file reads and a code search.

---

<sub>1m 22s</sub>

### `view`

**view the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\specs\summary.md.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\specs\summary.md

<details>
<summary>52 lines</summary>

````
## Qué se pide

Un endpoint nuevo, `GET /projects/{id}/summary`, que resume un proyecto: **cuántas tareas tiene en
cada estado y cuántas están vencidas**. Sirve para pintar un tablero sin descargar todas las tareas del
proyecto. Pide el token de `POST /auth/login`, como todo lo que no es público.

## Respuesta 200

```json
{
  "projectId": 2,
  "projectName": "App Móvil",
  "totalTasks": 4,
  "byStatus": { "TODO": 1, "IN_PROGRESS": 2, "DONE": 1 },
  "overdue": 1
}
```

| Campo | Tipo | Qué es |
|---|---|---|
| `projectId` | número | el `id` del proyecto pedido |
| `projectName` | texto | el `name` del proyecto |
| `totalTasks` | número | cuántas tareas tiene el proyecto |
| `byStatus` | objeto | una clave por cada valor de `TaskStatus` (`TODO`, `IN_PROGRESS`, `DONE`), **siempre las tres**, aunque valgan 0 |
| `overdue` | número | cuántas tareas del proyecto cumplen `Task.estaVencida()` |

## Reglas

1. **Vencida** es exactamente lo que ya dice `Task.estaVencida()`: tiene fecha límite, la fecha ya pasó y
   el estado no es `DONE`. Se reutiliza ese método; no se reescribe la regla en el servicio.
2. Un proyecto **sin tareas** responde 200 con `totalTasks` 0, las tres claves de `byStatus` en 0 y `overdue` 0.
3. Un proyecto **que no existe** responde **404** con el `ErrorResponse` uniforme de siempre
   (`ProjectNotFoundException`, igual que `GET /projects/{id}`).
4. **Sin token** responde **401**, como todo lo que no es público.
5. Cualquier usuario autenticado puede pedir el resumen de cualquier proyecto (igual que `GET /projects/{id}/tasks`).

## Resultado esperado con la semilla del perfil `h2`

| Petición | `totalTasks` | `byStatus` | `overdue` |
|---|---|---|---|
| `GET /projects/1/summary` | 5 | `TODO` 3 · `IN_PROGRESS` 1 · `DONE` 1 | 0 (la tarea 2 tiene fecha pasada, pero está `DONE`) |
| `GET /projects/2/summary` | 4 | `TODO` 1 · `IN_PROGRESS` 2 · `DONE` 1 | 1 (la tarea 7) |
| `GET /projects/3/summary` | 0 | `TODO` 0 · `IN_PROGRESS` 0 · `DONE` 0 | 0 |
| `GET /projects/99/summary` | — | — | 404 |

Si antes de probar creaste tareas en un proyecto (por la UI o con el servidor MCP), `totalTasks` y
`TODO` de ese proyecto suben en la misma cantidad. Al reiniciar la app con el perfil `h2` vuelven a
ser los de la tabla.

## Cómo se construye (convenciones de TaskFlow)

- DTO de salida: un `record` en `com.taskflow.dto`, `ProjectSummaryResponse`.
- La lógica de conteo va en el servicio (`ProjectService`), no en el controller.
- El controller solo traduce HTTP: busca el proyecto (404 si no existe) y delega.
- Tests:
  - **unit** del servicio con Mockito: un proyecto con tareas en los tres estados y una vencida; un proyecto sin tareas.
  - **slice** `@WebMvcTest` del controller: 200 con el JSON de arriba y 404 con un proyecto inexistente.
- `mvn -q test` en verde al terminar, sin modificar tests existentes.

## Criterios de aceptación

- [ ] `GET /projects/2/summary` con el token de `ana` devuelve el JSON del ejemplo.
- [ ] `GET /projects/3/summary` devuelve ceros en los cinco números.
- [ ] `GET /projects/99/summary` devuelve 404.
- [ ] `mvn -q test` en verde: los tests de antes más los nuevos.
````

</details>


---

<sub>1m 22s</sub>

### `view`

**view the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\.github\skills\crear-endpoint-taskflow\plantillas.md.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\.github\skills\crear-endpoint-taskflow\plantillas.md

<details>
<summary>167 lines</summary>

````
# Plantillas de la skill `crear-endpoint-taskflow`

> **Ejemplo, no el endpoint que te piden.** Todo este archivo implementa un endpoint inventado,
> `GET /projects/{id}/carga`, que cuenta cuántas tareas de un proyecto tienen responsable y cuántas
> no. Úsalo para copiar la **forma** (anotaciones, imports, nombres, estructura de los tests), no el
> contenido. El código de este archivo compila y sus tests pasan contra `taskflow-api` (verificado el
> 12-sep-2026 con Spring Boot 3.5.3).

Respuesta del ejemplo:

```json
{"projectId":1,"conResponsable":4,"sinResponsable":1}
```

## 1. DTO de salida — `src/main/java/com/taskflow/dto/CargaProyectoResponse.java`

```java
package com.taskflow.dto;

/**
 * CargaProyectoResponse — contrato de salida de GET /projects/{id}/carga.
 * Un record: Jackson lo serializa por sus componentes, en este orden.
 */
public record CargaProyectoResponse(
        Long projectId,
        long conResponsable,
        long sinResponsable
) {
}
```

## 2. Mapper — método nuevo en `src/main/java/com/taskflow/mapper/ProjectMapper.java`

```java
    /** Conteos ya calculados por el service -> DTO de salida de GET /projects/{id}/carga. */
    public static CargaProyectoResponse aCarga(Long projectId, long conResponsable, long sinResponsable) {
        return new CargaProyectoResponse(projectId, conResponsable, sinResponsable);
    }
```

(y el import `com.taskflow.dto.CargaProyectoResponse` arriba del archivo).

## 3. Service — método nuevo en `src/main/java/com/taskflow/service/ProjectService.java`

`ProjectService` ya tiene `taskRepository`: no hace falta inyectar nada nuevo. Recibe el proyecto ya
encontrado (el 404 lo resuelve el controller, como en `GET /projects/{id}/tasks`).

```java
    /**
     * Carga de un proyecto: cuántas de sus tareas tienen responsable y cuántas no.
     * Reutiliza ReportService.SIN_ASIGNAR (no reescribe la regla).
     */
    public CargaProyectoResponse carga(Project proyecto) {
        List<Task> tareas = taskRepository.findByProjectId(proyecto.getId());
        long sinResponsable = tareas.stream().filter(ReportService.SIN_ASIGNAR).count();
        return ProjectMapper.aCarga(proyecto.getId(), tareas.size() - sinResponsable, sinResponsable);
    }
```

(imports: `com.taskflow.dto.CargaProyectoResponse`, `com.taskflow.mapper.ProjectMapper`).

## 4. Controller — método nuevo en `src/main/java/com/taskflow/controller/ProjectController.java`

```java
    /** GET /projects/{id}/carga — 200 con los conteos; 404 uniforme si el proyecto no existe. */
    @Operation(summary = "Carga de un proyecto",
            description = "Cuántas tareas del proyecto tienen responsable y cuántas no. 404 si el proyecto no existe.")
    @GetMapping("/projects/{id}/carga")
    public CargaProyectoResponse getCarga(@PathVariable("id") Long id) {
        Project proyecto = projectService.buscarPorId(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        return projectService.carga(proyecto);
    }
```

(import: `com.taskflow.dto.CargaProyectoResponse`; `Project` y `ProjectNotFoundException` ya están importados).

## 5. Test unitario (clase nueva) — `src/test/java/com/taskflow/unit/CargaProyectoServiceTest.java`

```java
package com.taskflow.unit;

import com.taskflow.dto.CargaProyectoResponse;
import com.taskflow.exception.TaskValidationException;
import com.taskflow.model.Priority;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import com.taskflow.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/** Unit de ProjectService.carga: sin Spring, repositorios mockeados, tareas reales. */
@ExtendWith(MockitoExtension.class)
class CargaProyectoServiceTest {

    // Un @Mock por CADA dependencia del constructor de ProjectService.
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService service;

    private final Project proyecto = new Project(1L, "Plataforma", "d", 1L, null);

    @Test
    void carga_cuentaConYSinResponsable() throws TaskValidationException {
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(
                tarea(1L, 1L), tarea(2L, null), tarea(3L, 2L)));

        // 3 tareas y solo la 2 sin responsable -> 2 con, 1 sin (contado a mano, no copiado del código)
        assertEquals(new CargaProyectoResponse(1L, 2, 1), service.carga(proyecto));
    }

    @Test
    void carga_proyectoSinTareas_todoEnCero() {
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of());

        assertEquals(new CargaProyectoResponse(1L, 0, 0), service.carga(proyecto));
    }

    private Task tarea(Long id, Long assigneeId) throws TaskValidationException {
        return new Task(id, "Tarea " + id, "d", TaskStatus.TODO, Priority.MED, 1L, assigneeId, null);
    }
}
```

## 6. Test slice (clase nueva) — `src/test/java/com/taskflow/slice/CargaProyectoControllerTest.java`

```java
package com.taskflow.slice;

import com.taskflow.controller.ProjectController;
import com.taskflow.dto.CargaProyectoResponse;
import com.taskflow.model.Project;
import com.taskflow.security.JwtAuthenticationFilter;
import com.taskflow.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Slice web de GET /projects/{id}/carga: solo la capa HTTP; la lógica se prueba en el unit. */
@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)   // la seguridad se prueba en integration/, no aquí
class CargaProyectoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    // Sin este mock el slice intenta crear el filtro JWT real y falla por JwtService.
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getCarga_existente_devuelve200ConCadaCampo() throws Exception {
        when(projectService.buscarPorId(1L)).thenReturn(Optional.of(new Project(1L, "Plataforma", "d", 1L, null)));
        when(projectService.carga(any(Project.class))).thenReturn(new CargaProyectoResponse(1L, 4, 1));

        mockMvc.perform(get("/projects/1/carga"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.conResponsable").value(4))
                .andExpect(jsonPath("$.sinResponsable").value(1));
    }

    @Test
    void getCarga_proyectoInexistente_devuelve404() throws Exception {
        when(projectService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/projects/99/carga"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
```

## 7. Comprobar

```powershell
mvn -q test
```

Termina sin líneas `[ERROR]` y con código de salida 0. `git status --porcelain src/test` muestra solo
líneas que empiezan por `??` (archivos nuevos): ningún test existente cambió.
````

</details>


---

<sub>1m 22s</sub>

### `grep`

**class ProjectController**

"class ProjectController" in **/*Controller.java (src)

```
src\main\java\com\taskflow\controller\ProjectController.java
```


---

<sub>1m 36s</sub>

### Copilot

Reading relevant source files (controller, service, mapper, Task model, repositories) in parallel to implement the endpoint consistently.

---

<sub>1m 37s</sub>

### `view`

**view the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\controller\ProjectController.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\controller\ProjectController.java

<details>
<summary>123 lines</summary>

```
package com.taskflow.controller;

import com.taskflow.dto.ProjectRequest;
import com.taskflow.dto.ProjectResponse;
import com.taskflow.dto.TaskResponse;
import com.taskflow.exception.ProjectNotFoundException;
import com.taskflow.mapper.ProjectMapper;
import com.taskflow.mapper.TaskMapper;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * ProjectController — la puerta HTTP de los proyectos. HOY (integrador) crece a CRUD completo, en
 * DTOs (ProjectRequest / ProjectResponse); las tareas de un proyecto salen como TaskResponse.
 *
 * Repetición deliberada del patrón de TaskController: mismos gestos (201 + Location, @Valid, 404 vía
 * orElseThrow -> advice). El lado Project cierra la tabla de endpoints del capstone (menos /auth/*).
 */
@RestController
@Tag(name = "Projects", description = "CRUD de proyectos y sus tareas. Borrar un proyecto arrastra sus tareas (cascada).")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /** GET /projects — todos los proyectos como ProjectResponse. */
    @Operation(summary = "Lista todos los proyectos",
            description = "Devuelve todos los proyectos como ProjectResponse (lista vacía si no hay ninguno).")
    @GetMapping("/projects")
    public List<ProjectResponse> getProjects() {
        return projectService.listar().stream()
                .map(ProjectMapper::aResponse)
                .toList();
    }

    /** GET /projects/{id} — 200 con ProjectResponse; 404 uniforme si no existe. */
    @Operation(summary = "Obtiene un proyecto por id",
            description = "200 con el ProjectResponse; 404 uniforme si el id no existe.")
    @GetMapping("/projects/{id}")
    public ProjectResponse getProject(@PathVariable("id") Long id) {
        Project proyecto = projectService.buscarPorId(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        return ProjectMapper.aResponse(proyecto);
    }

    /**
     * GET /projects/{id}/tasks — las tareas de un proyecto como TaskResponse. Conserva la distinción
     * de D2: proyecto inexistente -> 404 (orElseThrow); proyecto sin tareas -> 200 con []. El filtro
     * ?status= es STRETCH (mismo enum que /tasks). Sigue delegando en ProjectService.tareasDe.
     */
    @Operation(summary = "Lista las tareas de un proyecto",
            description = "Tareas del proyecto como TaskResponse; 404 si el proyecto no existe, 200 con [] si no tiene tareas. Filtro opcional ?status= (stretch).")
    @GetMapping("/projects/{id}/tasks")
    public List<TaskResponse> getTareasDeProyecto(
            @PathVariable("id") Long id,
            @RequestParam(name = "status", required = false) TaskStatus status) {   // status: STRETCH
        projectService.buscarPorId(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        List<Task> tareas = projectService.tareasDe(id);
        if (status != null) {                          // STRETCH: filtro opcional por estado
            tareas = tareas.stream().filter(t -> t.getStatus() == status).toList();
        }
        return tareas.stream().map(TaskMapper::aResponse).toList();
    }

    /**
     * POST /projects — 201 + Location a /projects/{id}. @Valid dispara Bean Validation (400 si falla).
     * MP-9: el owner sale del JWT — el Authentication (inyectado por Spring Security) trae el username
     * del token; el service lo resuelve a ownerId. El dueño es QUIEN crea, no una constante.
     */
    @Operation(summary = "Crea un proyecto",
            description = "El owner se toma del usuario autenticado (el username del JWT).")
    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request,
                                                         Authentication authentication) {
        Project creado = projectService.crear(request, authentication.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/projects/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(ProjectMapper.aResponse(creado));
    }

    /** PUT /projects/{id} — reemplazo COMPLETO. 200 con ProjectResponse; 404 si no existe. */
    @Operation(summary = "Reemplaza un proyecto",
            description = "Conserva id, owner y createdAt; name y description vienen del cuerpo.")
    @PutMapping("/projects/{id}")
    public ProjectResponse updateProject(@PathVariable("id") Long id,
                                         @Valid @RequestBody ProjectRequest request) {
        Project actualizado = projectService.reemplazar(id, request);
        return ProjectMapper.aResponse(actualizado);
    }

    /**
     * DELETE /projects/{id} — 204 No Content; 404 si no existe. Borra en cascada las tareas del
     * proyecto (regla "no Task sin Project").
     *
     * MP-9: la regla del capstone EN SERIO. @PreAuthorize se evalúa ANTES del método: pasa si el
     * usuario es ADMIN o es el owner del proyecto (bean @projectSecurity, data-driven). Un USER que no
     * es owner -> 403 (la API SÍ sabe quién es, por eso 403 y no 401). Proyecto inexistente ->
     * esOwner devuelve true a propósito para que hable el 404 del servicio.
     */
    @Operation(summary = "Borra un proyecto y sus tareas",
            description = "Solo ADMIN o el owner. Un USER no-owner recibe 403.")
    @PreAuthorize("hasRole('ADMIN') or @projectSecurity.esOwner(#id, authentication.name)")
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") Long id) {
        projectService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
```

</details>


---

<sub>1m 37s</sub>

### `view`

**view the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\service\ProjectService.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\service\ProjectService.java

<details>
<summary>93 lines</summary>

```
package com.taskflow.service;

import com.taskflow.dto.ProjectRequest;
import com.taskflow.exception.ProjectNotFoundException;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ProjectService — la capa de negocio del lado Project. Cero HTTP aquí (el "no existe" se traduce con
 * orElseThrow -> ProjectNotFoundException; el advice le pone el 404).
 *
 * Cambios de HOY (S2D4) — y por qué la promesa del día NO lo cubre:
 *   - MP-4: el modelo Project se aplanó (User owner -> Long ownerId), así que 'crear' y 'reemplazar'
 *     manejan un ownerId (Long), no un objeto User. Antes 'crear' construía con 0L (convención del
 *     InMemory de "aún sin id"); ahora construye con null y la BD asigna el id (IDENTITY).
 *   - MP-6: 'tareasDe' cambió su stream-filter de findAll() por taskRepository.findByProjectId(id)
 *     — la nota de D3 ("en D4 esto se vuelve un query method") se paga AQUÍ. La promesa "no tocar el
 *     servicio" protege a TaskService (que no se tocó), no a ProjectService (que ya se tocó en MP-4).
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;   // D5: para resolver el owner desde el username del JWT

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /** Todos los proyectos. */
    public List<Project> listar() {
        return projectRepository.findAll();
    }

    /** Un proyecto por id (el Optional sube tal cual: el controller decide 404 vs 200). */
    public Optional<Project> buscarPorId(Long id) {
        return projectRepository.findById(id);
    }

    /**
     * Las tareas de un proyecto: MP-6 sustituye el stream-filter de D2 por la derived query
     * taskRepository.findByProjectId(projectId) — el LIKE... perdón, el WHERE project_id = ? lo hace
     * la BD (viajan solo las filas del proyecto). "no existe el proyecto" (404) lo decide el
     * controller con buscarPorId; ESTE método, si el proyecto existe pero no tiene tareas, devuelve
     * [] (-> 200 con []). No son lo mismo.
     */
    public List<Task> tareasDe(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    /**
     * Crea un proyecto (POST): el request trae name y description; el ownerId ya NO es una constante
     * (murió el 1L fijo de D3) — se resuelve del USERNAME AUTENTICADO (el que puso el JWT en el
     * Authentication). El dueño no lo decide el cliente ni una semilla: es QUIEN llama. createdAt = hoy;
     * id == null -> la BD lo asigna (IDENTITY).
     */
    public Project crear(ProjectRequest request, String ownerUsername) {
        User owner = userRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new UsernameNotFoundException(ownerUsername));
        Project nuevo = new Project(null, request.name(), request.description(),
                owner.getId(), LocalDate.now());
        return projectRepository.save(nuevo);
    }

    /**
     * Reemplazo COMPLETO (PUT): existe -> reconstruye con el MISMO id, conservando ownerId y createdAt
     * (el cliente no los toca); name y description vienen del request. save con id presente = update.
     * No existe -> 404.
     */
    public Project reemplazar(Long id, ProjectRequest request) {
        Project actual = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        Project reemplazo = new Project(actual.getId(), request.name(), request.description(),
                actual.getOwnerId(), actual.getCreatedAt());
        return projectRepository.save(reemplazo);
    }

    /**
     * Elimina un proyecto (DELETE) con CASCADA MANUAL: borra primero sus tareas (regla "no Task sin
     * Project" — y además la FK task.project_id lo EXIGE: borrar el proyecto con tareas colgando daría
     * una violación de integridad) y luego el proyecto. No existe -> 404. La regla owner/ADMIN llega
     * en D5 (hoy queda abierto, documentado en el @Operation del controller).
     */
    public void eliminar(Long id) {
        projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        tareasDe(id).forEach(t -> taskRepository.deleteById(t.getId()));   // cascada manual (la FK obliga el orden)
        projectRepository.deleteById(id);
    }
}
```

</details>


---

<sub>1m 37s</sub>

### `view`

**view the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\mapper\ProjectMapper.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\mapper\ProjectMapper.java

<details>
<summary>21 lines</summary>

```
package com.taskflow.mapper;

import com.taskflow.dto.ProjectResponse;
import com.taskflow.model.Project;

/**
 * ProjectMapper — puente DTO &lt;-&gt; dominio del lado Project. Estático, a mano, sin MapStruct.
 *
 * HOY se SIMPLIFICÓ (lo prometía D3): la entidad ya guarda 'ownerId' directo (se aplanó el 'User
 * owner' en MP-4), así que aResponse ya no deriva el id desde un objeto (p.getOwner().id()) — lee
 * p.getOwnerId() tal cual. El contrato de salida (ProjectResponse con ownerId Long) no cambió; el
 * mapeo se volvió trivial porque el dominio por fin coincide con la forma canónica.
 */
public final class ProjectMapper {

    private ProjectMapper() {
        // no instanciable
    }

    /** Entidad -> DTO de salida. Ahora ownerId sale directo del campo (sin puente por objeto). */
    public static ProjectResponse aResponse(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(),
                p.getOwnerId(), p.getCreatedAt());
    }
}
```

</details>


---

<sub>1m 37s</sub>

### `view`

**view the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\model\Task.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\model\Task.java

<details>
<summary>225 lines</summary>

```
package com.taskflow.model;

import com.taskflow.exception.TaskValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Task — entidad central del dominio TaskFlow. ESTADO FINAL del Día 4 (persistida con JPA).
 *
 * La entidad ES la clase que YA teníamos (canónica S1, 8 campos): NO se escribió un "modelo nuevo",
 * se ANOTÓ el Task de D3. Novedades de HOY, todas por anotación:
 *   - @Entity + @Table(name = "tasks"): esta clase mapea a la tabla TASKS.
 *   - @Id + @GeneratedValue(IDENTITY): el id lo asigna la BD (columna autoincremental). La secuencia
 *     manual del InMemoryTaskRepository (el Math.max de S1D3) MURIÓ: el id lo pone la BD y nadie más.
 *   - @Enumerated(STRING) en status y priority: se guardan como TEXTO ('TODO'), NUNCA como ORDINAL
 *     (0,1,2): reordenar o insertar un enum corrompería datos históricos. El orden LOW,MED,HIGH de
 *     S1D3 es contrato del SORT (compareTo), no del almacenamiento.
 *   - relación @ManyToOne Task->Project (patrón "asociación de solo lectura"): la COLUMNA la sigue
 *     mandando el escalar 'projectId' (por eso Task.crear, TaskService, mappers y DTOs NO cambian);
 *     el objeto 'project' solo NAVEGA. Frase ancla: "la columna manda; el objeto navega".
 *
 * Tensión JPA vs validaciones (paga de S1D2 MP-8, fijada por el apéndice del CAPSTONE): JPA rehidrata
 * por reflexión con un constructor sin args protegido, SIN pasar por las validaciones. Está bien:
 * Task.crear(...) sigue siendo el ÚNICO camino de CREACIÓN de negocio; JPA solo REHIDRATA datos que
 * ya existían (la misma distinción crear/rehidratar de siempre).
 *
 * OJO: las columnas persistentes NO pueden ser 'final' (JPA las escribe por reflexión tras el no-arg);
 * por eso title/description/projectId/dueDate perdieron el 'final' que traían de D3. La inmutabilidad
 * de negocio la sigue garantizando la ausencia de setters públicos para esos campos.
 */
@Entity
@Table(name = "tasks")
public class Task implements Comparable<Task> {

    // Reglas de longitud del título (del capstone).
    private static final int TITULO_MIN = 3;
    private static final int TITULO_MAX = 120;

    // id nace null; lo asigna la BD (IDENTITY). Ver setId (uso interno de JPA/repo).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    private String description;                 // por convención -> columna DESCRIPTION

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;                  // texto 'TODO'/'IN_PROGRESS'/'DONE' (nunca 0/1/2)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;                  // texto 'LOW'/'MED'/'HIGH'

    // La COLUMNA project_id la posee este escalar (insertable/updatable por default). Sigue siendo
    // el dueño: Task.crear, el mapper y el servicio hablan con projectId, no con el objeto.
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    // assigneeId se queda Long a secas (juicio explícito): es nullable, NUNCA navegamos al User
    // completo al leer tareas, y las derived queries operan igual. No toda FK necesita ser objeto:
    // mapea objeto cuando NAVEGAS, deja el id cuando solo REFERENCIAS.
    @Column(name = "assignee_id")
    private Long assigneeId;

    private LocalDate dueDate;                  // por convención -> columna DUE_DATE (camelCase->snake_case)

    /**
     * Asociación de SOLO LECTURA hacia el proyecto (MP-8). Comparte la MISMA columna project_id con
     * el escalar de arriba, pero marcada insertable=false/updatable=false: no escribe la columna (el
     * escalar lo hace), solo permite NAVEGAR (task.getProject().getName()). LAZY: no se carga hasta
     * que se toca — y fuera de una sesión abierta, tocarla lanza LazyInitializationException (por eso
     * la API responde con DTOs, nunca con la entidad).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    /**
     * Constructor NO-ARG protegido: EXCLUSIVO de JPA (rehidratación por reflexión). Nunca es un camino
     * de creación de negocio. Su existencia es obligatoria para cualquier @Entity: sin él, al arrancar
     * Hibernate lanza "No default constructor for entity: com.taskflow.model.Task".
     */
    protected Task() {
        // solo para JPA: rehidratación, nunca creación de negocio
    }

    /**
     * Constructor de REHIDRATACIÓN: reconstruye una tarea que YA existía (semilla del DataSeeder,
     * datos releídos). Valida las INVARIANTES que siempre deben cumplirse (título y proyecto), pero
     * NO la regla temporal de dueDate: una tarea vencida es un dato válido que hay que poder releer.
     */
    public Task(Long id, String title, String description, TaskStatus status,
                Priority priority, Long projectId, Long assigneeId, LocalDate dueDate)
            throws TaskValidationException {
        // Invariante 1: título obligatorio, 3-120 chars.
        if (title == null || title.isBlank()) {
            throw new TaskValidationException("El título es obligatorio (no puede ir vacío).");
        }
        if (title.length() < TITULO_MIN || title.length() > TITULO_MAX) {
            throw new TaskValidationException(
                    "El título debe tener entre " + TITULO_MIN + " y " + TITULO_MAX
                            + " caracteres; recibí " + title.length() + ": \"" + title + "\".");
        }
        // Invariante 2: una tarea no puede existir sin proyecto (regla del capstone).
        if (projectId == null) {
            throw new TaskValidationException(
                    "Una tarea no puede existir sin proyecto (projectId == null).");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.dueDate = dueDate;
    }

    /**
     * Factory de CREACIÓN de negocio: añade la regla temporal (dueDate no en el pasado) y delega en
     * el constructor. id nace null, status nace TODO. Firma canónica S1 (apéndice del CAPSTONE-SPEC):
     * la usan el mapper y el servicio tal cual — por eso NO cambia hoy pese a que Task ya sea @Entity.
     */
    public static Task crear(String title, String description, Priority priority,
                             LocalDate dueDate, Long projectId, Long assigneeId)
            throws TaskValidationException {
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            throw new TaskValidationException(
                    "La fecha límite no puede estar en el pasado: " + dueDate + ".");
        }
        return new Task(null, title, description, TaskStatus.TODO, priority, projectId, assigneeId, dueDate);
    }

    // ---- Comportamiento: vive CON los datos ----

    /** true si tiene fecha, ya pasó, y la tarea aún no está terminada. */
    public boolean estaVencida() {
        return dueDate != null && dueDate.isBefore(LocalDate.now()) && status != TaskStatus.DONE;
    }

    // ---- Getters ----

    public Long getId() {
        return id;
    }

    /** Uso interno (JPA/repo) para el id autoincremental. Nadie más lo llama a mano. */
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Navega al proyecto asociado (asociación de solo lectura). Fuera de una sesión JPA abierta puede
     * lanzar LazyInitializationException (por eso la API expone DTOs, no la entidad). En el runtime
     * nadie del camino principal lo usa; existe para NAVEGAR cuando haga falta.
     */
    public Project getProject() {
        return project;
    }

    // ---- Setters SOLO donde el dominio muta ----

    /**
     * Cambia el estado aplicando la regla del capstone: no se puede pasar a DONE una tarea sin
     * responsable (assigneeId). La regla vive AQUÍ, no en el servicio ni en el menú.
     */
    public void setStatus(TaskStatus status) throws TaskValidationException {
        if (status == TaskStatus.DONE && assigneeId == null) {
            throw new TaskValidationException(
                    "No se puede marcar como " + TaskStatus.DONE
                            + " una tarea sin responsable: \"" + title + "\".");
        }
        this.status = status;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    // ---- Identidad de ENTIDAD por id (S1D3 MP-6 = literalmente lo que hace JPA) ----

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task otra = (Task) o;
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ---- Orden natural por prioridad (Comparable) ----

    @Override
    public int compareTo(Task otra) {
        return this.priority.compareTo(otra.priority);
    }

    @Override
    public String toString() {
        return "Task{id=" + id
                + ", title='" + title + '\''
                + ", status=" + status
                + ", priority=" + priority
                + ", assigneeId=" + (assigneeId == null ? "sin asignar" : assigneeId)
                + ", dueDate=" + (dueDate == null ? "sin fecha" : dueDate)
                + (estaVencida() ? " *VENCIDA*" : "")
                + '}';
    }
}
```

</details>


---

<sub>1m 37s</sub>

### `view`

**view the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\exception\ProjectNotFoundException.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\exception\ProjectNotFoundException.java

<details>
<summary>21 lines</summary>

```
package com.taskflow.exception;

/**
 * ProjectNotFoundException — se lanza cuando se busca un proyecto por id y NO existe. Nace HOY
 * (S2D3, integrador paso 4). El GlobalExceptionHandler la mapea a 404, en el MISMO handler que
 * TaskNotFoundException (un solo @ExceptionHandler con dos tipos).
 *
 * Gemela de TaskNotFoundException (S1D4): UNCHECKED (extiende RuntimeException), guarda el id que no
 * se encontró y sube sola hasta el advice. La usan ProjectService (GET/PUT/DELETE de un proyecto
 * inexistente) y el cableo Task↔Project (POST /projects/{id}/tasks contra un proyecto que no existe).
 */
public class ProjectNotFoundException extends RuntimeException {

    private final Long id;

    public ProjectNotFoundException(Long id) {
        super("No existe proyecto con id " + id + ".");
        this.id = id;
    }

    /** El id que se buscó y no existía. */
    public Long getId() {
        return id;
    }
}
```

</details>


---

<sub>1m 37s</sub>

### `view`

**view the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\repository\TaskRepository.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\repository\TaskRepository.java

<details>
<summary>36 lines</summary>

```
package com.taskflow.repository;

import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * TaskRepository — EL MOMENTO NARRATIVO DEL DÍA (MP-6). Ayer era una interfaz que redeclaraba
 * save/findById/findAll/deleteById, implementada por InMemoryTaskRepository. HOY extiende
 * JpaRepository&lt;Task, Long&gt; y esos 4 métodos VIENEN GRATIS (+ count/existsById/etc.): NO se
 * implementa nadie, un PROXY de runtime lo hace (la misma "magia" de DI de S2D1).
 *
 * La paga de la interfaz extraída en S1D5: TaskService/ReportService dependían de ESTE contrato desde
 * entonces, y sus firmas (Optional&lt;Task&gt; findById, void deleteById) se alinearon a Spring Data
 * desde S2D1 — por eso hoy el swap memoria->BD NO toca el servicio. "git diff de TaskService = vacío".
 * InMemoryTaskRepository dejó de compilar y SE ELIMINÓ (git lo recuerda; borrar código es sano
 * cuando la interfaz sobrevive).
 *
 * DERIVED QUERIES: el NOMBRE del método es la query. Gramática findBy + propiedad + And/Not/
 * Containing/IgnoreCase/OrderBy, y countBy. Se validan AL ARRANCAR: un nombre con una propiedad que
 * no existe (p.ej. findByEstado) lanza QueryCreationException y la app NO levanta — error de arranque,
 * no de runtime. Estas seis se prueban en TaskRepositoryTest (@DataJpaTest).
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /** Tareas en un estado dado. La consume ProjectService/ReportService y las vistas por estado. */
    List<Task> findByStatus(TaskStatus status);

    /** Tareas de un proyecto (por la columna project_id). La consume ProjectService.tareasDe (MP-6). */
    List<Task> findByProjectId(Long projectId);

    /** Combina dos criterios con And: tareas de un asignado en un estado. La consume el stretch de D5. */
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);

    /** LIKE case-insensitive: título que CONTIENE el fragmento (la BD hace el filtrado). */
    List<Task> findByTitleContainingIgnoreCase(String fragment);

    /** El complemento de findByStatus: todas MENOS las de ese estado (Not). Sirve a "pendientes". */
    List<Task> findByStatusNot(TaskStatus status);

    /** Cuenta en la BD (no trae filas para contarlas): conteo por estado. */
    long countByStatus(TaskStatus status);
}
```

</details>


---

<sub>3m 3s</sub>

### Copilot

Creating DTO, tests, and applying three edits (ProjectMapper, ProjectService, ProjectController) in parallel, then running mvn -q test to verify.

---

<sub>3m 48s</sub>

### `create`

**create a new file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\dto\ProjectSummaryResponse.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\dto\ProjectSummaryResponse.java

```
Created file C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\dto\ProjectSummaryResponse.java with 314 characters
```


---

<sub>3m 48s</sub>

### `create`

**create a new file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\test\java\com\taskflow\unit\ProjectSummaryServiceTest.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\test\java\com\taskflow\unit\ProjectSummaryServiceTest.java

```
Created file C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\test\java\com\taskflow\unit\ProjectSummaryServiceTest.java with 2566 characters
```


---

<sub>3m 48s</sub>

### `create`

**create a new file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\test\java\com\taskflow\slice\ProjectSummaryControllerTest.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\test\java\com\taskflow\slice\ProjectSummaryControllerTest.java

```
Created file C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\test\java\com\taskflow\slice\ProjectSummaryControllerTest.java with 2801 characters
```


---

<sub>3m 48s</sub>

### `edit`

**edit the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\mapper\ProjectMapper.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\mapper\ProjectMapper.java

```
File C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\mapper\ProjectMapper.java updated with changes.
```


---

<sub>3m 48s</sub>

### `edit`

**edit the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\service\ProjectService.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\service\ProjectService.java

```
File C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\service\ProjectService.java updated with changes.
```


---

<sub>3m 48s</sub>

### `edit`

**edit the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\controller\ProjectController.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\controller\ProjectController.java

```
File C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\controller\ProjectController.java updated with changes.
```


---

<sub>3m 49s</sub>

### `edit`

**edit the file at C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\controller\ProjectController.java.**

C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\controller\ProjectController.java

```
File C:\Users\carlo\OneDrive\Desktop\taskflow-copilot-hiiramsan\src\main\java\com\taskflow\controller\ProjectController.java updated with changes.
```


---

<sub>3m 49s</sub>

### `powershell`

**Run tests quietly**

<details>
<summary>Arguments</summary>

```json
{
  "command": "mvn -q test",
  "description": "Run tests quietly",
  "initial_wait": 120,
  "mode": "sync"
}
```

</details>

<details>
<summary>6 lines</summary>

```
Output too large to read at once (29.3 KB). Saved to: C:\Users\carlo\AppData\Local\Temp\1789667661783-copilot-tool-output-17064-cefa37a5-967d-4815-9597-fa49c89f3541.txt
Consider using tools like grep (for searching), head/tail (for viewing start/end), view with view_range (for specific sections), or jq (for JSON) to examine portions of the output.

Preview (first 500 chars):
10:53:55.189 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils -- Could not detect default configuration classes for test class [com.taskflow.integration.AuthControllerTest]: AuthControllerTest does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
10:53:55.799 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper -- Found @SpringBootConfiguration com.taskflow.TaskflowApiApplication fo
<shellId: 0 completed with exit code 0>
```

</details>


---

<sub>4m 22s</sub>

### Copilot

Implementación completa del endpoint GET /projects/{id}/summary.

Archivos creados:
- src/main/java/com/taskflow/dto/ProjectSummaryResponse.java
- src/test/java/com/taskflow/unit/ProjectSummaryServiceTest.java
- src/test/java/com/taskflow/slice/ProjectSummaryControllerTest.java

Archivos modificados:
- src/main/java/com/taskflow/mapper/ProjectMapper.java
- src/main/java/com/taskflow/service/ProjectService.java
- src/main/java/com/taskflow/controller/ProjectController.java

Resultado de pruebas: mvn -q test terminó con exit code 0 — todos los tests pasaron.

---

<sub>Generated by [GitHub Copilot CLI](https://github.com/features/copilot/cli)</sub>
