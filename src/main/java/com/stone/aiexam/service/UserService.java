package com.stone.aiexam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.aiexam.dto.ChangePasswordDTO;
import com.stone.aiexam.dto.LoginRequestDTO;
import com.stone.aiexam.dto.RegisterDTO;
import com.stone.aiexam.entity.User;
import com.stone.aiexam.vo.LoginResponseVO;

public interface UserService extends IService<User> {

    /**
     * 登录
     * @param dto  用户名+密码
     * @param role 要求的角色（ADMIN / STUDENT）
     */
    LoginResponseVO login(LoginRequestDTO dto, String role);

    /**
     * 注册并登录
     * @param registerDTO
     * @return 注册即登录，直接返回 token
     */
    LoginResponseVO register(RegisterDTO registerDTO);

    /**
     * 获取用户个人信息（不含密码）
     * @param username 从 token 解析的用户名
     */
    User getUserProfile(String username);

    /**
     * 修改密码
     * @param username 从 token 解析的用户名
     * @param dto
     */
    void changePassword(String username, ChangePasswordDTO dto);
}
