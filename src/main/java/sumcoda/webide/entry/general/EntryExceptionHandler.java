package sumcoda.webide.entry.general;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sumcoda.webide.entry.exception.*;
import sumcoda.webide.workspace.exception.WorkspaceAccessException;
import sumcoda.webide.workspace.exception.WorkspaceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class EntryExceptionHandler {

    @ExceptionHandler(WorkspaceAccessException.class)
    public ResponseEntity<Map<String, Object>> WorkspaceAccessExceptionHandler(WorkspaceAccessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WorkspaceFoundException.class)
    public ResponseEntity<Map<String, Object>> WorkspaceFoundExceptionHandler(WorkspaceFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntryFoundException.class)
    public ResponseEntity<Map<String, Object>> EntryFoundExceptionHandler(EntryFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntryAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> EntryAlreadyExistsExceptionHandler(EntryAlreadyExistsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntryCreateException.class)
    public ResponseEntity<Map<String, Object>> EntryCreateExceptionHandler(EntryCreateException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}