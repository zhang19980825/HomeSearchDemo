package com.zhangyang.repository;

import com.zhangyang.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * 角色数据DAO
 * @Author: ZhangYang
 * @Date: 2019/7/21 13:40
 */
public interface RoleRepository extends CrudRepository<Role,Long> {
    List<Role> findRolesByUserId(Long userId);

}
