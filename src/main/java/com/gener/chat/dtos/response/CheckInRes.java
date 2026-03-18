package com.gener.chat.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gener.chat.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CheckInRes {
    private LocalDate date;
    private Long userId;
    private AttendanceStatus attendanceStatus;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime checkInAt;
    private String note;
}
