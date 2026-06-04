package com.stone.aiexam;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptPasswordEncoderTest {


    @Test
    public void testPassword(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        String password = "123456";
        String encode = passwordEncoder.encode(password);
        System.out.println(encode);
    }

}
