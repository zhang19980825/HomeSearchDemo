package com.zhangyang.repository;

import com.zhangyang.entity.HousePicture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @Author: ZhangYang
 * @Date: 2019/7/25 12:37
 */
public interface HousePictureRepository extends CrudRepository<HousePicture,Long> {
    List<HousePicture> findAllByHouseId(Long id);
}
