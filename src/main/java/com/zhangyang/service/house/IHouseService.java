package com.zhangyang.service.house;

import com.zhangyang.base.HouseSubscribeStatus;
import com.zhangyang.service.ServiceMultiResult;
import com.zhangyang.service.ServiceResult;
import com.zhangyang.web.dto.HouseDTO;
import com.zhangyang.web.dto.HouseSubscribeDTO;
import com.zhangyang.web.form.DatatableSearch;
import com.zhangyang.web.form.HouseForm;
import com.zhangyang.web.form.MapSearch;
import com.zhangyang.web.form.RentSearch;
import org.springframework.data.util.Pair;

import javax.xml.ws.Service;
import java.util.Date;

/**
 *
 *房屋管理接口
 * @Author: ZhangYang
 * @Date: 2019/7/25 12:39
 */
public interface IHouseService {
    /*
     * 新增房源信息
     * */
    ServiceResult<HouseDTO> save(HouseForm houseForm);

    ServiceResult update(HouseForm houseForm);

    ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody);

    /*
     * 查询完整房源信息
     * */
    ServiceResult<HouseDTO> findCompleteOne(Long id);

    /**
     * 移除图片
     *
     * @param id
     * @return
     */
    ServiceResult removePhoto(Long id);

    /**
     * 更新封面
     *
     * @param coverId
     * @param targetId
     * @return
     */
    ServiceResult updateCover(Long coverId, Long targetId);

    /**
     * 新增标签
     *
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult addTag(Long houseId, String tag);

    /**
     * 移除标签
     *
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult removeTag(Long houseId, String tag);

    /*
    * 更新房源状态
    * */
    ServiceResult updateStatus(Long id,int status);


    /*
    * 查询房源信息集
    * */
    ServiceMultiResult<HouseDTO> query(RentSearch rentSearch);

    /*
    * 全地图查询房源接口
    * */
    ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch);


    /*
     * 精确范围查询房源接口
     * */
    ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch);


    /**
     * 加入预约清单
     * @param houseId
     * @return
     */
    ServiceResult addSubscribeOrder(Long houseId);

    /**
     * 获取对应状态的预约列表
     */
    ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(HouseSubscribeStatus status, int start, int size);

    /**
     * 预约看房时间
     * @param houseId
     * @param orderTime
     * @param telephone
     * @param desc
     * @return
     */
    ServiceResult subscribe(Long houseId, Date orderTime, String telephone, String desc);

    /**
     * 取消预约
     * @param houseId
     * @return
     */
    ServiceResult cancelSubscribe(Long houseId);

    /**
     * 管理员查询预约信息接口
     * @param start
     * @param size
     */
    ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> findSubscribeList(int start, int size);

    /**
     * 完成预约
     */
    ServiceResult finishSubscribe(Long houseId);
}
