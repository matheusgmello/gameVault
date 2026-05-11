package dev.matheus.gameVault.config;

import dev.matheus.gameVault.controller.response.ApiErrorResponse;
import dev.matheus.gameVault.exception.BusinessRuleException;
import dev.matheus.gameVault.exception.DuplicateResourceException;
import dev.matheus.gameVault.exception.ResourceNotFoundException;
import dev.matheus.gameVault.exception.UsernameOrPasswordInvalidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ExceptionHandler(UsernameOrPasswordInvalidException.class)
    public ResponseEntity<ApiErrorResponse> handleUsuarioOuSenhaInvalida(UsernameOrPasswordInvalidException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleArgumentosInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (mensagemAtual, novaMensagem) -> mensagemAtual
                ));
        return buildError(HttpStatus.BAD_REQUEST, "Existem campos invalidos na requisicao.", erros);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRecursoNaoEncontrado(ResourceNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleRecursoDuplicado(DuplicateResourceException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiErrorResponse> handleRegraDeNegocio(BusinessRuleException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleIntegridadeDados(DataIntegrityViolationException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Nao foi possivel concluir a operacao por causa de dados vinculados.", Map.of());
    }

    private ResponseEntity<ApiErrorResponse> buildError(HttpStatus status, String message, Map<String, String> fieldErrors) {
        return ResponseEntity.status(status).body(ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .fieldErrors(fieldErrors)
                .build());
    }
}
