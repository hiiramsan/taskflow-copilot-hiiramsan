package com.taskflow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2")
class ProjectSummaryE2ETest {

    @LocalServerPort
    int port;

    private final TestRestTemplate rest = new TestRestTemplate();

    @Test
    void getSummary_seededAna_project2_matchesSpec() {
        // Login as ana
        String loginUrl = "http://localhost:" + port + "/auth/login";
        var loginReq = new org.springframework.http.HttpEntity<>(java.util.Map.of("username", "ana", "password", "ana123"));
        ResponseEntity<java.util.Map> loginResp = rest.postForEntity(loginUrl, loginReq, java.util.Map.class);
        assertEquals(HttpStatus.OK, loginResp.getStatusCode());
        String token = (String) ((java.util.Map) loginResp.getBody()).get("token");

        // Request summary for project 2
        String summaryUrl = "http://localhost:" + port + "/projects/2/summary";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<java.util.Map> resp = rest.exchange(summaryUrl, HttpMethod.GET, entity, java.util.Map.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        java.util.Map body = resp.getBody();
        assertEquals(2, ((Number) body.get("projectId")).intValue());
        assertEquals("App Móvil", body.get("projectName"));
        assertEquals(4, ((Number) body.get("totalTasks")).intValue());
        java.util.Map byStatus = (java.util.Map) body.get("byStatus");
        assertEquals(1, ((Number) byStatus.get("TODO")).intValue());
        assertEquals(2, ((Number) byStatus.get("IN_PROGRESS")).intValue());
        assertEquals(1, ((Number) byStatus.get("DONE")).intValue());
        assertEquals(1, ((Number) body.get("overdue")).intValue());
    }
}
