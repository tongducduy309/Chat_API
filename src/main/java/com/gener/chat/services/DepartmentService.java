package com.gener.chat.services;

import com.gener.chat.dtos.response.DepartmentMemberRes;
import com.gener.chat.enums.SuccessCode;
import com.gener.chat.mapper.DepartmentMapper;
import com.gener.chat.models.DepartmentMember;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.repositories.DepartmentMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentMemberRepository departmentMemberRepository;
    private final DepartmentMapper departmentMapper;

    public ResponseEntity<ResponseObject> getAllEmployees(){

        List<DepartmentMember> departmentMembers = departmentMemberRepository.findAll();
        List<DepartmentMemberRes> departmentMemberRes = departmentMapper.toListDepartmentMemberRes(departmentMembers);

        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message(SuccessCode.REQUEST.getMessage())
                        .data(departmentMemberRes)
                        .build()
        );
    }
}
