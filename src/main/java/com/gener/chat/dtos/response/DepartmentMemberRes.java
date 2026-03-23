package com.gener.chat.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DepartmentMemberRes {
    private Long userId;
    private String avatarUrl;
    private String displayName;
    private String email;
    private String departmentName;
    private String positionName;
    private Boolean isLeader;
}
