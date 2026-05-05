package cz.tul.stin.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleRuntimeException_shouldReturn500AndMessage() {
        RuntimeException ex = new RuntimeException("boom");
        ResponseEntity<Map<String, Object>> resp = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.get("timestamp") instanceof LocalDateTime);
        assertEquals("boom", body.get("message"));
        assertEquals(500, body.get("status"));
    }

    @Test
    void handleIllegalArgumentException_shouldReturn500AndMessage() {
        IllegalArgumentException ex = new IllegalArgumentException("bad arg");
        ResponseEntity<Map<String, Object>> resp = handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.get("timestamp") instanceof LocalDateTime);
        assertEquals("bad arg", body.get("message"));
        assertEquals(500, body.get("status"));
    }

    @Test
    void handleGlobalException_shouldReturnBuildResponseMessage() {
        Exception ex = new Exception("uh oh");
        ResponseEntity<Map<String, Object>> resp = handler.handleGlobalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.get("timestamp") instanceof LocalDateTime);
        assertEquals("Unhandled Internal Application Error.", body.get("message"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals(500, body.get("status"));
    }

    @Test
    void handleNoResourceFound_shouldReturn404() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "", "");
        ResponseEntity<Map<String, Object>> resp = handler.handleNoResourceFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        Map<String, Object> body = resp.getBody();
        assertNotNull(body);
        assertTrue(body.get("timestamp") instanceof LocalDateTime);
        assertEquals("Resource not found.", body.get("message"));
        assertEquals(404, body.get("status"));
    }
}
