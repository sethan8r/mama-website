package dev.sethan8r.mama.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleENotFound(BusinessException ex) {
        log.warn("Ошибка бизнес логики: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    //BadCredentialsException кидает Spring Security, когда логин/пароль неверный.
    //UsernameNotFoundException кидает Spring Security, когда логина нету в БД
    public ResponseEntity<ErrorResponse> handleAuthenticationError(Exception ex) {
        log.warn("Ошибка аутентификации: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse("Неверный логин или пароль");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        List<ValidationErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationErrorResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        log.warn("Ошибка валидации: {} ошибок", fieldErrors.size());

        ValidationErrorResponse response = new ValidationErrorResponse(
                "Ошибка валидации",
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("Превышен максимальный размер загружаемого файла");

        ErrorResponse errorResponse = new ErrorResponse("Размер файла слишком большой. Максимальный размер: 10 МБ");

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFound(NoResourceFoundException ex) {
        return new ModelAndView("error/404");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Непредвиденная ошибка во время выполнения", ex);

        ErrorResponse error = new ErrorResponse("Произошла ошибка при обработке запроса");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Неожиданная ошибка", ex);

        ErrorResponse error = new ErrorResponse("Произошла неожиданная ошибка сервера");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable ex) {
        log.error("Критическая ошибка!", ex);

        ErrorResponse error = new ErrorResponse("Критическая ошибка сервера");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
