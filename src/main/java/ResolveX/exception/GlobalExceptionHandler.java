package ResolveX.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex) {

        String message = ex.getMessage();

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (message != null) {

            if (message.contains("not found")) {
                status = HttpStatus.NOT_FOUND;
            } else if (
                    message.contains("not allowed")
                            || message.contains("not assigned to you")
                            || message.contains("not authorized")
            ) {
                status = HttpStatus.FORBIDDEN;
            } else if (
                    message.contains("Only PENDING")
                            || message.contains("Only ACCEPTED")
                            || message.contains("not in PENDING")
                            || message.contains("not available")
            ) {
                status = HttpStatus.CONFLICT;
            }
        }

        Map<String, Object> response = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message == null
                        ? "An unexpected error occurred"
                        : message
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .toList();

        Map<String, Object> response = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "messages", errors
        );

        return ResponseEntity.badRequest().body(response);
    }
}