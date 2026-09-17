package com.taskflow.dto;

/**
 * ProjectSummaryResponse — contrato de salida de GET /projects/{id}/summary.
 */
public record ProjectSummaryResponse(
        Long projectId,
        String projectName,
        long totalTasks,
        java.util.Map<String, Long> byStatus,
        long overdue
) {
}
