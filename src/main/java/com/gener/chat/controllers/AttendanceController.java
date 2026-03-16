package com.gener.chat.controllers;

import com.gener.chat.dtos.request.AttendanceCheckInReq;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    public ResponseEntity<ResponseObject> checkIn(@RequestBody(required = false) AttendanceCheckInReq req) throws APIException {
        return attendanceService.checkIn(req);
    }

    @GetMapping("/today")
    public ResponseEntity<ResponseObject> getTodayAttendance() throws APIException {
        return attendanceService.getTodayAttendance();
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseObject> getMyAttendanceByMonth(
            @RequestParam int month,
            @RequestParam int year
    ) throws APIException {
        return attendanceService.getMyAttendanceByMonth(month, year);
    }

    @GetMapping("/checked-today")
    public ResponseEntity<ResponseObject> checkTodayAttendance() throws APIException {
        return attendanceService.checkTodayAttendance();
    }
}