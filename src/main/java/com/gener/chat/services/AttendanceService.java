package com.gener.chat.services;

import com.gener.chat.dtos.request.AttendanceCheckInReq;
import com.gener.chat.enums.AttendanceStatus;
import com.gener.chat.enums.ErrorCode;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.Attendance;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.models.User;
import com.gener.chat.repositories.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserService userService;

    public ResponseEntity<ResponseObject> checkIn(AttendanceCheckInReq req) throws APIException {
        User currentUser = userService.getUserFromToken();
        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByUserIdAndWorkDate(currentUser.getId(), today)) {
            throw new APIException(ErrorCode.ATTENDANCE_ALREADY_CHECKED_IN);
        }

        LocalDateTime now = LocalDateTime.now();

        AttendanceStatus attendanceStatus = now.toLocalTime().isAfter(LocalTime.of(8, 0))
                ? AttendanceStatus.LATE
                : AttendanceStatus.PRESENT;

        Attendance attendance = Attendance.builder()
                .user(currentUser)
                .workDate(today)
                .checkInAt(now)
                .status(attendanceStatus)
                .note(req != null ? req.getNote() : null)
                .build();

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return ResponseEntity.status(200).body(
                ResponseObject.builder()
                        .status(200)
                        .message("Điểm danh thành công")
                        .data(savedAttendance)
                        .build()
        );
    }

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
}