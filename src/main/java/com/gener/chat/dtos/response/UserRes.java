package com.gener.chat.dtos.response;

import com.gener.chat.enums.UserStatus;
import com.gener.chat.models.Department;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class UserRes {
    private Long id;
    private String userCode;
    private String phone;
    private String email;
    private String passwordHash;
    private String displayName;
    private String departmentName;
    private String avatarUrl;
    private UserStatus status ;
    private LocalDate hireDate;
    private Boolean verified;
    private String positionName;
}
