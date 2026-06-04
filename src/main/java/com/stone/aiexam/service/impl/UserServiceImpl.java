package com.stone.aiexam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.aiexam.dto.LoginRequestDTO;
import com.stone.aiexam.dto.RegisterDTO;
import com.stone.aiexam.entity.User;
import com.stone.aiexam.exception.BusinessException;
import com.stone.aiexam.mapper.UserMapper;
import com.stone.aiexam.service.UserService;
import com.stone.aiexam.utils.JwtUtil;
import com.stone.aiexam.vo.LoginResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 管理员登录
     * @param dto
     * @return
     */
    @Override
    public LoginResponseVO login(LoginRequestDTO dto, String requiredRole) {
        // 1. 查用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验密码（BCrypt 哈希比对）
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 验角色
        if (!requiredRole.equals(user.getRole())) {
            throw new BusinessException("无此权限登录");
        }

        // 4. 签发 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 5. 组装响应
        LoginResponseVO vo = new LoginResponseVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setToken(token);
        return vo;
    }

    /**
     * 注册并登录
     * @param dto
     * @return
     */
    @Override
    public LoginResponseVO register(RegisterDTO dto) {
        // 1. 校验
        if (!StringUtils.hasText(dto.getUsername()) || !StringUtils.hasText(dto.getRealName())) {
            throw new BusinessException("用户名和姓名不能为空");
        }
        if (getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        if (dto.getPassword().length() < 6) {
            throw new BusinessException("密码长度不能小于6");
        }
        if (!dto.getPassword().equals(dto.getRePassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 2. 入库（密码 BCrypt 加密）
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setRealName(dto.getRealName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("STUDENT");
        user.setStatus("ACTIVE");
        save(user);

        // 3. 注册即登录，签发 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "STUDENT");

        LoginResponseVO vo = new LoginResponseVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole("STUDENT");
        vo.setToken(token);
        return vo;
    }
}
