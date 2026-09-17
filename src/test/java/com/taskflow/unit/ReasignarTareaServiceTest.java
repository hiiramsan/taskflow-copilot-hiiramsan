package com.taskflow.unit;

import com.taskflow.exception.TaskStateException;
import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.TaskRepository;
import com.taskflow.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReasignarTareaServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    void reasignar_tareaTODO_sinResponsable_guardaConAssignee() throws Exception {
        Task t = new Task(1L, "Tarea", "d", TaskStatus.TODO, Priority.MED, 1L, null, null);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        service.reasignar(t, 2L);

        verify(repository).save(captor.capture());
        assertEquals(2L, captor.getValue().getAssigneeId());
    }

    @Test
    void reasignar_tareaDONE_lanzaYNoGuarda() throws Exception {
        Task t = new Task(2L, "Hecho", "d", TaskStatus.DONE, Priority.MED, 1L, 1L, null);

        assertThrows(TaskStateException.class, () -> service.reasignar(t, 3L));
        verify(repository, never()).save(any());
    }
}
