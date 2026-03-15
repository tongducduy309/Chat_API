package com.gener.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ErrorCode {
//    Người dùng
    PHONE_REQUIRED(100, "Số điện thoại không được để trống", HttpStatus.BAD_REQUEST),
    PHONE_NOT_EXIST(101, "Số điện thoại không tồn tại", HttpStatus.NOT_FOUND),
    PASSWORD_INCORRECT(102, "Mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(103, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    SENDER_NOT_FOUND(104, "Không tìm thấy người gửi", HttpStatus.NOT_FOUND),
//    Token

    TOKEN_MISSING(40100, "Token không được để trống", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(40101, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(40102, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(40103, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(40300, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),
    // Conversation
    CONVERSATION_NOT_FOUND(40410, "Không tìm thấy cuộc trò chuyện", HttpStatus.NOT_FOUND),
    SENDER_NOT_IN_CONVERSATION(40310,"Người gửi không thuộc cuộc trò chuyện",HttpStatus.FORBIDDEN),
    USER_NOT_IN_CONVERSATION(40310,"Người dùng không thuộc cuộc trò chuyện",HttpStatus.FORBIDDEN),
    DIRECT_CONVERSATION_ALREADY_EXISTS(
            40910,
            "Cuộc trò chuyện trực tiếp giữa hai người dùng đã tồn tại",
            HttpStatus.CONFLICT
    ),

    // Message
    REPLY_MESSAGE_NOT_FOUND(40412, "Tin nhắn phản hồi không tồn tại", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(40430, "Tin nhắn không tồn tại", HttpStatus.NOT_FOUND),
    CALL_NOT_FOUND(40420, "Không tìm thấy cuộc gọi", HttpStatus.NOT_FOUND),
    ;
    private int status;
    private String message;
    private HttpStatusCode httpStatusCode;
}
