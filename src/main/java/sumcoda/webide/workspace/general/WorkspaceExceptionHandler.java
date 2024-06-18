package sumcoda.webide.workspace.general;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sumcoda.webide.entry.exception.RootEntryFoundException;
import sumcoda.webide.member.exception.MemberFoundException;
import sumcoda.webide.workspace.exception.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class WorkspaceExceptionHandler {

    @ExceptionHandler(MemberFoundException.class)
    public ResponseEntity<Map<String, Object>> memberFoundExceptionHandler(WorkspaceAccessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WorkspaceNotCreateException.class)
    public ResponseEntity<Map<String, Object>> workspaceNotCreateExceptionHandler(WorkspaceNotCreateException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WorkspaceAccessException.class)
    public ResponseEntity<Map<String, Object>> workspaceAccessExceptionHandler(WorkspaceAccessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WorkspaceFoundException.class)
    public ResponseEntity<Map<String, Object>> workspaceFoundExceptionHandler(WorkspaceFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WorkspaceUpdateException.class)
    public ResponseEntity<Map<String, Object>> workspaceUpdateExceptionHandler(WorkspaceUpdateException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WorkspaceStatusException.class)
    public ResponseEntity<Map<String, Object>> workspaceStatusExceptionHandler(WorkspaceStatusException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RootEntryFoundException.class)
    public ResponseEntity<Map<String, Object>> RootEntryFoundExceptionHandler(WorkspaceAccessException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
