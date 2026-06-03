package com.stone.aiexam.controller;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.dto.LoginRequestDTO;
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

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<LoginResponseVO> adminLogin(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseVO response  = userService.adminLogin(loginRequestDTO);
        log.info("管理员登录成功");
        return Result.success(response);
    }

}
