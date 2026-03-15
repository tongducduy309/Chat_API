package com.gener.chat.services;

import com.gener.chat.models.Department;
import com.gener.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserResolver {
    private final UserRepository userRepository;
    public String generateUserCode() {
        LocalDate now = LocalDate.now();
        String month = String.format("%02d", now.getMonthValue());
        String year = String.valueOf(now.getYear()).substring(2);

        String prefix = year + month;

        String maxCode = userRepository.findMaxUserCodeByPrefix(prefix);

        int nextSeq = 1;

        if (maxCode != null) {
            String seqPart = maxCode.substring(4);
            nextSeq = Integer.parseInt(seqPart) + 1;
        }

        return prefix + String.format("%04d", nextSeq);
    }
}
