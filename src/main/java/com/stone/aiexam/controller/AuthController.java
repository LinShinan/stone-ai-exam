package com.stone.aiexam.controller;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.dto.LoginRequestDTO;
import com.stone.aiexam.dto.RegisterDTO;
import com.stone.aiexam.service.UserService;
import com.stone.aiexam.vo.LoginResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@Slf4j
@Tag(name ="认证")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 管理员登录
     * @param loginRequestDTO
     * @return
     */
    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<LoginResponseVO> adminLogin(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseVO response  = userService.login(loginRequestDTO, "ADMIN");
        log.info("管理员登录成功");
        return Result.success(response);
    }

    /**
     * 学生登录
     * @param loginRequestDTO
     * @return
     */
    @Operation(summary = "学生登录")
    @PostMapping("/student-login")
    public Result<LoginResponseVO> studentLogin(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseVO response  = userService.login(loginRequestDTO, "STUDENT");
        log.info("学生登录成功");
        return Result.success(response);
    }

    /**
     * 学生注册并登录
     * @param registerDTO
     * @return
     */
    @PostMapping("/student-register")
    public Result<LoginResponseVO> register(@RequestBody RegisterDTO registerDTO){
        LoginResponseVO response = userService.register(registerDTO);
        log.info("学生{}注册成功", registerDTO.getUsername());
        return Result.success(response);
    }


}
