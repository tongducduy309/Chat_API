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

    // ==================== COMMON ====================
    INVALID_DATA(40000, "Dữ liệu không hợp lệ, vui lòng kiểm tra lại", HttpStatus.BAD_REQUEST),

    // ==================== USER ====================
    PHONE_OR_EMAIL_REQUIRED(40010, "Vui lòng nhập số điện thoại hoặc email", HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT(40110, "Mật khẩu không chính xác", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(40410, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    SENDER_NOT_FOUND(40411, "Không tìm thấy người gửi", HttpStatus.NOT_FOUND),

    // ==================== TOKEN / AUTH ====================
    TOKEN_MISSING(40100, "Token không được để trống", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(40101, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(40102, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(40103, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(40300, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),

    // ==================== CONVERSATION ====================
    CONVERSATION_NOT_FOUND(40420, "Không tìm thấy cuộc trò chuyện", HttpStatus.NOT_FOUND),
    SENDER_NOT_IN_CONVERSATION(40320, "Người gửi không thuộc cuộc trò chuyện", HttpStatus.FORBIDDEN),
    USER_NOT_IN_CONVERSATION(40321, "Người dùng không thuộc cuộc trò chuyện", HttpStatus.FORBIDDEN),
    DIRECT_CONVERSATION_ALREADY_EXISTS(
            40920,
            "Cuộc trò chuyện trực tiếp giữa hai người dùng đã tồn tại",
            HttpStatus.CONFLICT
    ),

    // ==================== MESSAGE / CALL ====================
    REPLY_MESSAGE_NOT_FOUND(40430, "Tin nhắn phản hồi không tồn tại", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(40431, "Tin nhắn không tồn tại", HttpStatus.NOT_FOUND),
    CALL_NOT_FOUND(40432, "Không tìm thấy cuộc gọi", HttpStatus.NOT_FOUND),

    // ==================== FRIENDSHIP ====================
    FRIENDSHIP_ALREADY_EXISTS(40940, "Hai người đã là bạn bè", HttpStatus.CONFLICT),
    FRIEND_REQUEST_ALREADY_SENT(40941, "Yêu cầu kết bạn đã tồn tại", HttpStatus.CONFLICT),
    FRIEND_REQUEST_NOT_ALLOWED(40040, "Không thể gửi lời mời kết bạn", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_INVALID(40041, "Không thể xử lý yêu cầu kết bạn", HttpStatus.BAD_REQUEST),
    SELF_FRIEND_REQUEST(40042, "Không thể gửi lời mời kết bạn cho chính mình", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_NOT_FOUND(40440, "Không tìm thấy yêu cầu kết bạn", HttpStatus.NOT_FOUND),
    USER_ALREADY_BLOCKED(40942, "Người dùng đã bị chặn trước đó", HttpStatus.CONFLICT),
    FRIENDSHIP_NOT_FOUND(40441, "Không tìm thấy quan hệ bạn bè", HttpStatus.NOT_FOUND),
    FRIENDSHIP_INVALID(40043, "Quan hệ bạn bè không hợp lệ", HttpStatus.BAD_REQUEST),

    // ==================== ATTENDANCE ====================
    ATTENDANCE_ALREADY_CHECKED_IN(40950, "Bạn đã điểm danh hôm nay", HttpStatus.CONFLICT),
    ATTENDANCE_NOT_FOUND(40450, "Chưa có dữ liệu điểm danh", HttpStatus.NOT_FOUND);

    private int status;
    private String message;
    private HttpStatusCode httpStatusCode;
}