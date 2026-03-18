package com.gener.chat.database;

import com.gener.chat.dtos.request.CreateConversationReq;
import com.gener.chat.dtos.request.CreateUserReq;
import com.gener.chat.enums.ConversationType;
import com.gener.chat.models.User;
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
import java.util.List;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class database {
    private final PasswordEncoder passwordEncoder;
    @Bean
    CommandLineRunner initDatabase(UserService userService
                                   ){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                CreateUserReq user = CreateUserReq.builder()
                        .password("123456")
                        .email("test@dev.com")
                        .displayName("Tester")
                        .phone("1234567891")
                        .hireDate(LocalDate.now())
                        .build();
                userService.createUser(user);
                log.info("Account Tester Has Been Created");



            }
        };
    }
}
