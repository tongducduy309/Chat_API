package com.gener.chat.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor

@NoArgsConstructor
@Getter
public enum SuccessCode {
    REQUEST(200,"Yêu cầu thành công", HttpStatus.OK),
    CREATE(201,"Tạo mới thành công", HttpStatus.CREATED),
    NO_CONTENT(204,"Không dữ liệu", HttpStatus.NO_CONTENT),

    LOGIN_SUCCESS(0, "Đăng nhập thành công", HttpStatus.OK),
    TOKEN_VALID(1, "Token hợp lệ", HttpStatus.OK),
    CONVERSATION_CREATED(20100, "Tạo cuộc trò chuyện thành công", HttpStatus.CREATED),
    MESSAGE_SENT_SUCCESS(20101, "Gửi tin nhắn thành công", HttpStatus.CREATED),
    MESSAGE_RECIEVED_SUCCESS(20102, "Nhận tin nhắn thành công", HttpStatus.CREATED),
    REMOVE_USER_FROM_CONVERSATION_SUCCESS(
            20010,
            "Xóa người dùng khỏi cuộc trò chuyện thành công",
            HttpStatus.OK
    ),
    ;
    private int status;
    private String message;
    private HttpStatusCode httpStatusCode;
}
