package com.stone.aiexam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.aiexam.dto.LoginRequestDTO;
import com.stone.aiexam.entity.User;
import com.stone.aiexam.exception.BusinessException;
import com.stone.aiexam.mapper.UserMapper;
import com.stone.aiexam.service.UserService;
import com.stone.aiexam.utils.JwtUtil;
import com.stone.aiexam.vo.LoginResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 管理员登录
     * @param dto
     * @return
     */
    @Override
    public LoginResponseVO adminLogin(LoginRequestDTO dto) {
        // 1. 查数据库
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验密码（TODO: 后续改成 BCrypt 加密比对）
        if (!dto.getPassword().equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 生成 token — role 来自数据库，不是前端
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 4. 组装响应
        LoginResponseVO vo = new LoginResponseVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setToken(token);
        return vo;
    }
}
