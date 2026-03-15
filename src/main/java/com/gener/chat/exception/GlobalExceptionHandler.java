package com.gener.chat.exception;

import com.gener.chat.enums.ErrorCode;
import com.gener.chat.models.ResponseObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value= APIException.class)
    ResponseEntity<ResponseObject> handlingAPIException(APIException exception){
        return ResponseEntity.status(exception.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(exception.getStatus())
                        .message(exception.getMessage())
                        .build()
        );
    }



//    @ExceptionHandler(DataIntegrityViolationException.class)
//    ResponseEntity<ResponseObject> handleDuplicateKey(DataIntegrityViolationException ex) {
//        String message = "Data integrity violation (maybe duplicate key or unique constraint)";
//
//        Throwable cause = ex.getRootCause();
//        if (cause != null && cause.getMessage() != null) {
//            String causeMessage = cause.getMessage().toLowerCase();
//            if (causeMessage.contains("unique") || causeMessage.contains("duplicate")) {
//                message = "Value already exists (duplicate unique key)";
//            } else if (causeMessage.contains("not-null")) {
//                message = "Required field cannot be null";
//            } else if (causeMessage.contains("foreign key")) {
//                message = "Invalid reference (foreign key constraint failed)";
//            }
//        }
//
//        return ResponseEntity.status(ErrorCode.CONFLICT.getHttpStatusCode()).body(
//                ResponseObject.builder()
//                        .status(ErrorCode.CONFLICT.getStatus())
//                        .message(message)
//                        .build()
//        );
//    }



}
