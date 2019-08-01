package com.zhangyang.service.house;

import com.zhangyang.entity.SupportAddress;
import com.zhangyang.service.ServiceMultiResult;
import com.zhangyang.service.ServiceResult;
import com.zhangyang.service.search.BaiduMapLocation;
import com.zhangyang.web.dto.SubwayDTO;
import com.zhangyang.web.dto.SubwayStationDTO;
import com.zhangyang.web.dto.SupportAddressDTO;

import java.util.List;
import java.util.Map;

/**
 *
 * 地址服务接口
 * @Author: ZhangYang
 * @Date: 2019/7/22 17:16
 */
public interface IAddressService {
    ServiceMultiResult<com.zhangyang.web.dto.SupportAddressDTO> findAllCities();

    /**
     * 根据英文简写获取具体区域的信息
     * @param cityEnName
     * @param regionEnName
     * @return
     */
    Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);
    /**
     * 根据城市英文简写获取该城市所有支持的区域信息
     * @param cityName
     * @return
     */
    ServiceMultiResult findAllRegionsByCityName(String cityName);

    /**
     * 获取该城市所有的地铁线路
     * @param cityEnName
     * @return
     */
    List<SubwayDTO> findAllSubwayByCity(String cityEnName);

    /**
     * 获取地铁线路所有的站点
     * @param subwayId
     * @return
     */
    List<SubwayStationDTO> findAllStationBySubway(Long subwayId);


    /**
     * 获取地铁线信息
     * @param subwayId
     * @return
     */
    ServiceResult<SubwayDTO> findSubway(Long subwayId);

    /**
     * 获取地铁站点信息
     * @param stationId
     * @return
     */
    ServiceResult<SubwayStationDTO> findSubwayStation(Long stationId);

    /**
     * 根据城市英文简写获取城市详细信息
     * @param cityEnName
     * @return
     */
    ServiceResult<SupportAddressDTO> findCity(String cityEnName);

    /*
     * 根据城市以及具体地址获取百度地图的经纬度
     * */
    ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city,String address);

    /*
    * 上传百度LBS数据
    * */
    ServiceResult lbsUpLoad(BaiduMapLocation location,String title,String address,long houseId,int price ,int area);

    /*
     * 移除百度LBS数据
     * */
    ServiceResult removeLbs(long houseId);

}
