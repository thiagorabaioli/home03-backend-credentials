package home03.credenciais.controllers.handlers;

import home03.credenciais.dto.erros.CustomErrorDTO;
import home03.credenciais.dto.erros.ValidationErrorDTO;
import home03.credenciais.services.exceptions.BusinessException;
import home03.credenciais.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorDTO> resourceNotFound(ResourceNotFoundException e, HttpServletRequest req) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(
            new CustomErrorDTO(Instant.now(), status.value(), e.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomErrorDTO> business(BusinessException e, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(
            new CustomErrorDTO(Instant.now(), status.value(), e.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDTO> validacao(MethodArgumentNotValidException e, HttpServletRequest req) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationErrorDTO err = new ValidationErrorDTO(Instant.now(), status.value(), "Dados inválidos", req.getRequestURI());
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ValidationErrorDTO> bindError(BindException e, HttpServletRequest req) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationErrorDTO err = new ValidationErrorDTO(Instant.now(), status.value(), "Dados inválidos", req.getRequestURI());
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(err);
    }
}
