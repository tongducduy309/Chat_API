package com.gener.chat.repositories;

import com.gener.chat.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByUserIdAndWorkDate(Long userId, LocalDate workDate);

    Optional<Attendance> findByUserIdAndWorkDate(Long userId, LocalDate workDate);

    List<Attendance> findByUserIdAndWorkDateBetweenOrderByWorkDateDesc(Long userId, LocalDate from, LocalDate to);
}