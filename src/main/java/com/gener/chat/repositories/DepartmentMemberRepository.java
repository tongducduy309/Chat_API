package com.gener.chat.repositories;

import com.gener.chat.models.DepartmentMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepartmentMemberRepository extends JpaRepository<DepartmentMember, Long> {


    Optional<DepartmentMember> findByUserId(Long userId);

    List<DepartmentMember> findAllByUserId(Long userId);

    List<DepartmentMember> findAllByDepartmentId(Long departmentId);

    Optional<DepartmentMember> findByUserIdAndDepartmentId(Long userId, Long departmentId);
}
