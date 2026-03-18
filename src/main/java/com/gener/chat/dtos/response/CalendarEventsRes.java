package com.gener.chat.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gener.chat.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CalendarEventsRes {
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime checkInAt;
    private AttendanceStatus attendanceStatus;
    private String note;
}

