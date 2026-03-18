package com.gener.chat.services;

import com.gener.chat.dtos.request.AttendanceCheckInReq;
import com.gener.chat.dtos.response.AttendanceMonthItemRes;
import com.gener.chat.dtos.response.CalendarMonthItemRes;
import com.gener.chat.dtos.response.CheckInRes;
import com.gener.chat.enums.AttendanceStatus;
import com.gener.chat.enums.ErrorCode;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.Attendance;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.models.User;
import com.gener.chat.repositories.AttendanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public ResponseEntity<ResponseObject> checkIn(AttendanceCheckInReq req) throws APIException {
        User currentUser = userService.getUserFromToken();
        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByUserIdAndWorkDate(currentUser.getId(), today)) {
            throw new APIException(ErrorCode.ATTENDANCE_ALREADY_CHECKED_IN);
        }

        LocalDateTime now = LocalDateTime.now();

//        AttendanceStatus attendanceStatus = now.toLocalTime().isAfter(LocalTime.of(8, 0))
//                ? AttendanceStatus.LATE
//                : AttendanceStatus.PRESENT;

        AttendanceStatus attendanceStatus = AttendanceStatus.PRESENT;

        Attendance attendance = Attendance.builder()
                .user(currentUser)
                .workDate(today)
                .checkInAt(now)
                .status(attendanceStatus)
                .note(req != null ? req.getNote() : null)
                .build();

        Attendance savedAttendance = attendanceRepository.save(attendance);
        publisher.publishEvent(CheckInRes.builder()
                        .attendanceStatus(attendanceStatus)
                        .userId(currentUser.getId())
                        .date(today)
                        .checkInAt(now)
                        .note(req != null ? req.getNote() : null)
                .build());

        return ResponseEntity.status(200).body(
                ResponseObject.builder()
                        .status(200)
                        .message("Điểm danh thành công")
                        .data(savedAttendance)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> getTodayAttendance() throws APIException {
        User currentUser = userService.getUserFromToken();
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByUserIdAndWorkDate(currentUser.getId(), today)
                .orElse(null);

        return ResponseEntity.status(200).body(
                ResponseObject.builder()
                        .status(200)
                        .message("Điểm danh hôm nay")
                        .data(attendance)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> getMyAttendanceByMonth(int month, int year) throws APIException {
        User currentUser = userService.getUserFromToken();

        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        List<Attendance> attendances = attendanceRepository
                .findByUserIdAndWorkDateBetweenOrderByWorkDateDesc(currentUser.getId(), from, to);

        return ResponseEntity.status(200).body(
                ResponseObject.builder()
                        .status(200)
                        .message("Danh sách điểm danh")
                        .data(attendances)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> checkTodayAttendance() throws APIException {

        User currentUser = userService.getUserFromToken();
        LocalDate today = LocalDate.now();

        boolean checked = attendanceRepository
                .existsByUserIdAndWorkDate(currentUser.getId(), today);

        return ResponseEntity.status(200).body(
                ResponseObject.builder()
                        .status(200)
                        .message("Kiểm tra điểm danh hôm nay")
                        .data(checked)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> getMyAttendanceInMonth(int month, int year) throws APIException {
        User currentUser = userService.getUserFromToken();

        if (currentUser.getHireDate() == null) {
            throw new APIException(ErrorCode.INVALID_DATA);
        }

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        LocalDate startDate = currentUser.getHireDate().isAfter(firstDayOfMonth)
                ? currentUser.getHireDate()
                : firstDayOfMonth;

        LocalDate endDate = lastDayOfMonth;
        LocalDate today = LocalDate.now();

        if (startDate.isAfter(endDate)) {
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(200)
                            .message("Tháng này không có dữ liệu điểm danh")
                            .data(List.of())
                            .build()
            );
        }

        List<Attendance> attendances = attendanceRepository
                .findByUserIdAndWorkDateBetweenOrderByWorkDateAsc(
                        currentUser.getId(),
                        startDate,
                        endDate
                );

        Map<LocalDate, Attendance> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(Attendance::getWorkDate, a -> a));

        List<CalendarMonthItemRes> result = startDate.datesUntil(endDate.plusDays(1))
                .map(date -> {
                    Attendance attendance = attendanceMap.get(date);

                    if (attendance != null) {
                        return CalendarMonthItemRes.builder()
                                .attendanceMonthItemRes(
                                        AttendanceMonthItemRes.builder()
                                                .checkInAt(attendance.getCheckInAt())
                                                .attendanceStatus(attendance.getStatus())
                                                .note(attendance.getNote())
                                                .build()
                                )
                                .date(date)
                                .build();
                    }

                    AttendanceStatus status = null;

                    if (date.isBefore(today)) {
                        status = AttendanceStatus.ABSENT;
                    }
                    if(date.isEqual(today)){
                        status = AttendanceStatus.NOT_CHECKED_IN;
                    }

                    return CalendarMonthItemRes.builder()
                            .attendanceMonthItemRes(
                                    AttendanceMonthItemRes.builder()
                                            .checkInAt(null)
                                            .attendanceStatus(status)
                                            .note(null)
                                            .build()
                            )
                            .date(date)
                            .build();
                })
                .toList();

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(200)
                        .message("Lấy điểm danh theo tháng (tính từ ngày vào làm)")
                        .data(result)
                        .build()
        );
    }
}