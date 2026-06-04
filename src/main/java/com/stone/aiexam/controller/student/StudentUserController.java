package com.stone.aiexam.controller.student;

import com.stone.aiexam.common.Result;
import com.stone.aiexam.dto.ChangePasswordDTO;
import com.stone.aiexam.entity.User;
import com.stone.aiexam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@Tag(name = "用户端-个人信息")
@RequestMapping("/api/student/users")
public class StudentUserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        User user = userService.getUserProfile(username);
        log.info("获取个人信息: {}", username);
        return Result.success(user);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto,
                                        HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        userService.changePassword(username, dto);
        log.info("修改密码: {}", username);
        return Result.success();
    }
}