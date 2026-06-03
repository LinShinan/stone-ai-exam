package com.stone.aiexam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.aiexam.dto.LoginRequestDTO;
import com.stone.aiexam.entity.User;
import com.stone.aiexam.vo.LoginResponseVO;

public interface UserService extends IService<User> {

    /**
     * 管理员登录
     * @param loginRequestDTO
     * @return
     */
    LoginResponseVO adminLogin(LoginRequestDTO loginRequestDTO);



}
