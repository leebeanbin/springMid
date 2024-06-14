package com.sparta.areadevelopment.exception;


import jakarta.transaction.NotSupportedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolations(
            DataIntegrityViolationException ex) {
        // 데이터베이스 에러 메시지를 분석하여 필요한 정보를 추출
        String errorMessage = "Data integrity violation occurred.";
        if (ex.getCause() != null && ex.getCause().getCause() != null) {
            String causeMessage = ex.getCause().getCause().getMessage();
            errorMessage = parseErrorMessage(causeMessage);
        }

        Map<String, String> response = new HashMap<>();
        response.put("error", "Data Integrity Violation");
        response.put("message", errorMessage);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 서비스에서 지원하지 않는 기능을 요청했을때 발생합니다.
     *
     * @param e 익셉션의 발생시 작성한 오류 메세지
     * @return 상태코드 400을 보내고 바디에 오류메세지를 보냅니다.
     */
    @ExceptionHandler(NotSupportedException.class)
    public ResponseEntity<Object> handleNotSupportedException(NotSupportedException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /**
     * 메서드에 전달된 매개변수 값이 잘못되어 찾을 수 없을때 발생합니다.
     *
     * @param e 익셉션의 발생시 작성한 오류 메세지
     * @return 상태코드 400을 보내고 바디에 오류메세지를 보냅니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // 잘못된 URL 입력에 의한 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return new ResponseEntity<>(
                "The requested URL was not found on the server. If you entered the URL manually please check your spelling and try again.",
                HttpStatus.BAD_REQUEST);
    }

    // 특정 필드가 입력되지 않았을 경우 메세지를 반환합니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // 데이터베이스 오류 메시지를 파싱하여 좀 더 친절한 메시지를 반환
    private String parseErrorMessage(String dbErrorMessage) {
        if (dbErrorMessage.contains("duplicate key value violates unique constraint")) {
            return "An entry with the same key already exists in the database.";
        }
        return dbErrorMessage; // 기본 메시지 반환
    }

}

