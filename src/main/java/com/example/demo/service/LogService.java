package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserLog;
import com.example.demo.repository.UserLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogService {

    private final UserLogRepository logRepository;

    public void log(User user, String action) {
        UserLog log = UserLog.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();
        logRepository.save(log);
    }
}
