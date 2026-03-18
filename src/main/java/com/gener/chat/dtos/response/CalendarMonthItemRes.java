package com.gener.chat.dtos.response;

import com.gener.chat.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CalendarMonthItemRes {

    private LocalDate date;
    private AttendanceMonthItemRes attendanceMonthItemRes;
    private CalendarEventsRes events;

}
