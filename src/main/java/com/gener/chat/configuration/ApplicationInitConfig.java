package com.gener.chat.configuration;


import com.gener.chat.models.User;
import com.gener.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if(userRepository.findByEmail("admin@dev.com").isEmpty()){
                User user = User.builder()
                        .passwordHash(passwordEncoder.encode("123456"))
                        .email("admin@dev.com")
                        .displayName("Admin")
                        .userCode("00000001")
                        .phone("1234567890")
                        .build();

                User newUser = userRepository.save(user);
//                log.info(newUser.getId());

                log.info("Account Admin Has Been Created: "+newUser.getId());
            }
        };
    }
}
