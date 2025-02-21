package supply.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import supply.server.configuration.exception.*;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerService {

    @ExceptionHandler({RedisLockException.class, DbException.class})
    public ResponseEntity<?> handleExceptionServer(Exception e) {
        log.error("Storage error: {}\n {}", e.getMessage(), e.getStackTrace());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler({DataNotFoundException.class})
    public ResponseEntity<?> handleExceptionBadRequest(DataNotFoundException e) {
        log.warn("Data not found: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IncorrectInputException.class)
    public ResponseEntity<?> handleExceptionBadRequest(IncorrectInputException e) {
        log.warn("Incorrect input: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IncorrectParameterException.class)
    public ResponseEntity<?> handleExceptionBadRequest(IncorrectParameterException e) {
        log.warn("Incorrect parameter: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleExceptionUnauthorized(AuthenticationException e) {
        log.warn("Authorization error: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handleExceptionPayment(PaymentException e) {
        log.warn("Payment error: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("Server error: {}\n {}", e.getMessage(), e.getStackTrace());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
