package com.gener.chat.mapper;

import com.gener.chat.dtos.response.DepartmentMemberRes;
import com.gener.chat.enums.DepartmentRole;
import com.gener.chat.models.DepartmentMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "displayName", source = "user.displayName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(
            target = "isLeader",
            expression = "java(isLeader(departmentMember))"
    )
    @Mapping(target = "positionName", source = "positionName")

//    @Mapping(target = "role", source = "role")
    DepartmentMemberRes toDepartmentMemberRes(DepartmentMember departmentMember);
    List<DepartmentMemberRes> toListDepartmentMemberRes(List<DepartmentMember> departmentMembers);

    default Boolean isLeader(DepartmentMember departmentMember){
        return departmentMember.getRole()== DepartmentRole.HEAD;
    }
}
