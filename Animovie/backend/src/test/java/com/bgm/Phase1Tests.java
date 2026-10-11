package com.bgm;

import com.bgm.domain.Subject;
import com.bgm.domain.User;
import com.bgm.mapper.UserMapper;
import com.bgm.service.SubjectService;
import com.bgm.service.UserService;
import com.bgm.utils.security.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Phase1Tests {
    @Autowired
    UserService userService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    UserMapper userMapper;

    @Test
    void registerAndLogin(){
        User u=userService.register("testuser","pass123","tu");
        if(u==null){
            u=userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, "testuser"));
        }
        Assertions.assertNotNull(u);
        User v=userService.verify("testuser","pass123");
        Assertions.assertNotNull(v);
        String token= JwtUtil.createToken(v);
        Assertions.assertNotNull(token);
        Assertions.assertTrue(JwtUtil.isValidToken(token));
    }

    @Test
    void subjectCrud(){
        Subject s=new Subject();
        s.setCnName("测试条目");
        s.setJpName("テスト");
        s.setAlias("alias");
        s.setCategory("TV");
        s.setEpisodes(12);
        s.setStudio("Studio Test");
        s.setOfficialSite("http://example.com");
        s.setTags("测试,标签");
        int r=subjectService.add(s);
        Assertions.assertEquals(1,r);
        s.setCnName("测试条目2");
        int u=subjectService.edit(s);
        Assertions.assertEquals(1,u);
    }
}
