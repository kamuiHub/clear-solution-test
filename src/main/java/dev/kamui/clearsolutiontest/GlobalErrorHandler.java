package dev.kamui.clearsolutiontest;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import dev.kamui.clearsolutiontest.exception.InvalidAgeException;
import dev.kamui.clearsolutiontest.exception.UserAlreadyExistException;
import dev.kamui.clearsolutiontest.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e) {
        log.error("{}", e.getMessage());
        ResponseApiError responseApiError = new ResponseApiError(
                new ErrorDetails(404, "user not found"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseApiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("{}", errorMsg);
        ResponseApiError responseApiError = new ResponseApiError(
                new ErrorDetails(400, errorMsg));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseApiError);
    }

    @ExceptionHandler(InvalidAgeException.class)
    protected ResponseEntity<?> handleInvalidAgeException(InvalidAgeException e) {
        log.error("{}", e.getMessage());
        ResponseApiError responseApiError = new ResponseApiError(
                new ErrorDetails(400, "age is less than 18"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseApiError);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    protected ResponseEntity<?> handleUserAlreadyExistException(UserAlreadyExistException e) {
        log.error("{}", e.getMessage());
        ResponseApiError responseApiError = new ResponseApiError(
                new ErrorDetails(400, "user with this email is already registered"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseApiError);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("{}", e.getMessage());
        ResponseApiError responseApiError = new ResponseApiError(
                new ErrorDetails(400, "missing params"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseApiError);
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record ResponseApiError(ErrorDetails errorDetails) {
    }

    private record ErrorDetails(int code, String message) {
    }
}
