package com.gener.chat.mapper;

import com.gener.chat.dtos.response.UserRes;
import com.gener.chat.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "departmentName", source = "department.name")
    UserRes toUserRes(User user);
}
