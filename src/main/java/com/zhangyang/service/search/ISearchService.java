package com.zhangyang.service.search;

import com.zhangyang.service.ServiceMultiResult;
import com.zhangyang.service.ServiceResult;
import com.zhangyang.web.form.MapSearch;
import com.zhangyang.web.form.RentSearch;

import java.util.List;

/**
 * @Author: ZhangYang
 * @Date: 2019/7/28 12:40
 */
public interface ISearchService {

    /*
    * 索引目标房源
    * */
    void   index(Long houseId);

    /*
    * 移除房源索引
    * */
    void remove(Long houseId);


    /*
    *查询房源接口
    * */
    ServiceMultiResult<Long> query(RentSearch rentSearch);

    /*
    * 获取补全建议关键词
    * */
    ServiceResult<List<String>>suggest(String prefix);

    /*
    * 聚合特定小区的房间数
    * */
    ServiceResult<Long> arrregateDistrictHouse(String cityEnName ,String regionEnName,String district);

    /*
    * 聚合城市数据
    * */
    ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName);

    /*/
    * 城市级别查询
    * */

    ServiceMultiResult<Long> mapQuery(String cityEnName,String orderBy,String orderDirection,int start,int size);


    /*/
     * 精确范围数据级别查询
     * */

    ServiceMultiResult<Long> mapQuery(MapSearch mapSearch);
}
