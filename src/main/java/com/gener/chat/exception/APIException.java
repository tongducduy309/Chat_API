package com.gener.chat.exception;

import com.gener.chat.enums.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatusCode;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class APIException extends Exception{
    private int status;
    private String message;
    private HttpStatusCode httpStatusCode;

    public APIException(ErrorCode errorCode){
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.httpStatusCode = errorCode.getHttpStatusCode();

    }

//    public APIException(int status,String message,HttpStatusCode httpStatusCode){
//        this.status = status;
//        this.message = message;
//        this.httpStatusCode = httpStatusCode;
//    }
}
