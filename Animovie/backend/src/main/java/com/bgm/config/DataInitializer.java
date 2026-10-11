package com.bgm.config;

import com.bgm.domain.User;
import com.bgm.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {
    @Autowired
    UserMapper userMapper;
    @Bean
    public ApplicationRunner init(){
        return args -> {
            Long count=userMapper.selectCount(null);
            if(count==0){
                BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
                User admin=new User();
                admin.setUsername("admin");
                admin.setPasswordHash(encoder.encode("admin123"));
                admin.setNickname("Admin");
                admin.setRole("ADMIN");
                admin.setStatus(1);
                admin.setAvatarPath("/uploads/avatars/default.jpg");
                admin.setCreatedAt(LocalDateTime.now());
                userMapper.insert(admin);
            }
        };
    }
}
