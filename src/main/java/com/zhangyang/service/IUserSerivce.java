package com.zhangyang.service;

import com.zhangyang.entity.User;
import com.zhangyang.web.dto.UserDTO;

/**
 * @Author: ZhangYang
 * @Date: 2019/7/21 13:27
 */
public interface IUserSerivce {
    User findUserByName(String userName);

    ServiceResult<UserDTO> findById(Long adminId);

    /*
    * 通过电话号码寻找用户
    * */
    User findUserByTelephone(String telephone);

    /*
     * 通过电话号码注册用户
     * */
    User addUserByPhone(String telephone);

    /**
     * 修改指定属性值
     */
    ServiceResult modifyUserProfile(String profile, String value);
}
