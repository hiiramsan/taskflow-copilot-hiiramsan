package com.taskflow.unit;

import com.taskflow.dto.ProjectSummaryResponse;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/** Unit de ProjectService.summary: sin Spring, repositorios mockeados, tareas reales. */
@ExtendWith(MockitoExtension.class)
class ProjectSummaryServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService service;

    private final Project proyecto = new Project(2L, "App Móvil", "d", 1L, null);

    @Test
    void resumen_cuentaPorEstadoYVencidas() throws TaskValidationException {
        when(taskRepository.findByProjectId(2L)).thenReturn(List.of(
                tarea(1L, TaskStatus.TODO, null),
                tarea(2L, TaskStatus.IN_PROGRESS, null),
                tarea(3L, TaskStatus.IN_PROGRESS, LocalDate.now().minusDays(2)),
                tarea(4L, TaskStatus.DONE, null)
        ));

        // total 4, TODO 1, IN_PROGRESS 2, DONE 1, overdue 1 (tarea 3)
        assertEquals(new ProjectSummaryResponse(2L, "App Móvil", 4L,
                Map.of("TODO", 1L, "IN_PROGRESS", 2L, "DONE", 1L), 1L),
                service.summary(proyecto));
    }

    @Test
    void resumen_proyectoSinTareas_devuelveCeros() {
        when(taskRepository.findByProjectId(2L)).thenReturn(List.of());

        assertEquals(new ProjectSummaryResponse(2L, "App Móvil", 0L,
                Map.of("TODO", 0L, "IN_PROGRESS", 0L, "DONE", 0L), 0L),
                service.summary(proyecto));
    }

    private Task tarea(Long id, TaskStatus status, LocalDate dueDate) throws TaskValidationException {
        return new Task(id, "Tarea " + id, "d", status, Priority.MED, 2L, null, dueDate);
    }
}
