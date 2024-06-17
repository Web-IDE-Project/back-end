package sumcoda.webide.workspace.general;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sumcoda.webide.workspace.exception.WorkspaceAccessException;
import sumcoda.webide.workspace.exception.WorkspaceNotCreateException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class WorkspaceExceptionHandler {

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
}
