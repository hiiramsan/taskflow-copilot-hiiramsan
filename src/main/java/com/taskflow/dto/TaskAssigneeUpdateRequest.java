package com.taskflow.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de entrada para reasignar el responsable de una tarea. Valida que assigneeId esté presente y
 * sea un número positivo.
 */
public record TaskAssigneeUpdateRequest(
        @NotNull
        @Positive
        Long assigneeId) {
}
