package com.gener.chat.database;

import com.gener.chat.dtos.request.CreateConversationReq;
import com.gener.chat.dtos.request.CreateUserReq;
import com.gener.chat.enums.ConversationType;
import com.gener.chat.enums.DepartmentRole;
import com.gener.chat.models.Department;
import com.gener.chat.models.DepartmentMember;
import com.gener.chat.models.User;
import com.gener.chat.repositories.DepartmentMemberRepository;
import com.gener.chat.repositories.DepartmentRepository;
import com.gener.chat.repositories.UserRepository;
import com.gener.chat.services.ConversationService;
import com.gener.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class database {
    private final PasswordEncoder passwordEncoder;
    @Bean
    CommandLineRunner initDatabase(
            UserService userService,
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            DepartmentMemberRepository departmentMemberRepository
    ) {
        return args -> {

            // 1. tạo user
            CreateUserReq userReq = CreateUserReq.builder()
                    .password("123456")
                    .email("test@dev.com")
                    .displayName("Tester")
                    .phone("1234567891")
                    .hireDate(LocalDate.now())
                    .build();

            userService.createUser(userReq);

            // 2. lấy lại user vừa tạo
            User user = userRepository.findByEmail("test@dev.com")
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 3. lấy hoặc tạo department
            Department department = departmentRepository.findByName("IT")
                    .orElseGet(() -> {
                        Department newDept = Department.builder()
                                .name("IT")
                                .code("IT")
                                .build();
                        return departmentRepository.save(newDept);
                    });

            // 4. tạo department member
            DepartmentMember member = DepartmentMember.builder()
                    .user(user)
                    .department(department)
                    .role(DepartmentRole.HEAD)
                    .joinedAt(LocalDateTime.now())
                    .build();

            departmentMemberRepository.save(member);

            log.info("Account Tester + DepartmentMember created");
        };
    }
}
