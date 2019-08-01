package com.zhangyang.repository;

import com.zhangyang.entity.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @Author: ZhangYang
 * @Date: 2019/7/25 12:38
 */
public interface HouseTagRepository extends CrudRepository<HouseTag,Long> {
    HouseTag findByNameAndHouseId(String name, Long houseId);

    List<HouseTag> findAllByHouseId(Long id);

    List<HouseTag> findAllByHouseIdIn(List<Long> houseIds);
}
