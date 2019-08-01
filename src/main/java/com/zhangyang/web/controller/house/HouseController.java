package com.zhangyang.web.controller.house;

import com.zhangyang.base.ApiResponse;
import com.zhangyang.base.RentValueBlock;
import com.zhangyang.entity.SupportAddress;
import com.zhangyang.service.IUserSerivce;
import com.zhangyang.service.ServiceMultiResult;
import com.zhangyang.service.ServiceResult;
import com.zhangyang.service.house.IAddressService;
import com.zhangyang.service.house.IHouseService;
import com.zhangyang.service.search.HouseBucketDTO;
import com.zhangyang.service.search.ISearchService;
import com.zhangyang.web.dto.*;
import com.zhangyang.web.form.MapSearch;
import com.zhangyang.web.form.RentSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.print.DocFlavor;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZhangYang
 * @Date: 2019/7/22 17:10
 */
@Controller
public class HouseController {

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private IUserSerivce userSerivce;

    @Autowired
    private ISearchService searchService;

    /**
     * 获取支持城市列表
     * @return
     */
    @GetMapping("address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities() {
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if (result.getResultSize() == 0) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }
    /**
     * 获取对应城市支持区域列表
     * @param cityEnName
     * @return
     */
    @GetMapping("address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(name = "city_name") String cityEnName) {
        ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionsByCityName(cityEnName);
        if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(addressResult.getResult());
    }

    /**
     * 获取具体城市所支持的地铁线路
     * @param cityEnName
     * @return
     */
    @GetMapping("address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubwayLine(@RequestParam(name = "city_name") String cityEnName) {
        List<SubwayDTO> subways = addressService.findAllSubwayByCity(cityEnName);
        System.out.println(subways
        );
        if (subways.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }

        return ApiResponse.ofSuccess(subways);
    }
    /**
     * 获取对应地铁线路所支持的地铁站点
     * @param subwayId
     * @return
     */
    @GetMapping("address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportSubwayStation(@RequestParam(name = "subway_id") Long subwayId) {
        List<SubwayStationDTO> stationDTOS = addressService.findAllStationBySubway(subwayId);
        if (stationDTOS.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }

        return ApiResponse.ofSuccess(stationDTOS);
    }

    @GetMapping("rent/house")
    public String rentHousePage(@ModelAttribute RentSearch rentSearch, Model model, HttpSession httpSession, RedirectAttributes redirectAttributes){
        if(rentSearch.getCityEnName()==null){
            String cityEnNameInSession=(String)httpSession.getAttribute("cityEnName");
            if(cityEnNameInSession==null){
                redirectAttributes.addAttribute("must_choose_city");
                return "redirect:/index";
            }else{
                rentSearch.setCityEnName(cityEnNameInSession);
            }
        }else {
            httpSession.setAttribute("cityEnName",rentSearch.getCityEnName());
        }

        ServiceResult<SupportAddressDTO>city=addressService.findCity(rentSearch.getCityEnName());
        if(!city.isSuccess()){
            redirectAttributes.addAttribute("must_choose_city");
            return "redirect:/index";
        }
        model.addAttribute("currentCity",city.getResult());
        ServiceMultiResult<SupportAddressDTO> addressDTOServiceMultiResult = addressService.findAllRegionsByCityName(rentSearch.getCityEnName());
        if(addressDTOServiceMultiResult.getResult()==null||addressDTOServiceMultiResult.getTotal()<1){
            redirectAttributes.addAttribute("must_choose_city");
            return "redirect:/index";
        }

        ServiceMultiResult<HouseDTO> serviceMultiResult=houseService.query(rentSearch);
        model.addAttribute("total",serviceMultiResult.getTotal());
        model.addAttribute("houses",serviceMultiResult.getResult());

        if(rentSearch.getRegionEnName()==null){
            rentSearch.setRegionEnName("*");
        }
        model.addAttribute("searchBody",rentSearch);
        model.addAttribute("regions",addressDTOServiceMultiResult.getResult());

        model.addAttribute("priceBlocks",RentValueBlock.PRICE_BLOCK);
        model.addAttribute("areaBlocks",RentValueBlock.AREA_BLOCK);

        model.addAttribute("currentPriceBlock",RentValueBlock.matchPrice(rentSearch.getPriceBlock()));
        model.addAttribute("currentAreaBlock",RentValueBlock.matchArea(rentSearch.getAreaBlock()));

        return "rent-list";

    }

    @GetMapping("rent/house/show/{id}")
    public String show(@PathVariable(value = "id")Long houseId,Model model){
        if(houseId<=0){
            return "404";
        }
        ServiceResult<HouseDTO> serviceResult=houseService.findCompleteOne(houseId);
        if(!serviceResult.isSuccess()){
            return "404";
        }
        HouseDTO houseDTO=serviceResult.getResult();
        Map<SupportAddress.Level,SupportAddressDTO> addressMap=addressService.findCityAndRegion(houseDTO.getCityEnName(),houseDTO.getRegionEnName());

        SupportAddressDTO city=addressMap.get(SupportAddress.Level.CITY);
        SupportAddressDTO region=addressMap.get(SupportAddress.Level.REGION);
        model.addAttribute("city",city);
        model.addAttribute("region",region);
        ServiceResult<UserDTO> userDTOServiceResult=userSerivce.findById(houseDTO.getAdminId());

        model.addAttribute("agent",userDTOServiceResult.getResult());
        model.addAttribute("house",houseDTO);

        ServiceResult<Long>aggResult1=searchService.arrregateDistrictHouse(city.getEnName(),region.getEnName(),houseDTO.getDistrict());
        model.addAttribute("houseCountInDistrict",aggResult1.getResult());
        return "house-detail";
    }


    /*
    * 搜索字段自动补全接口
    * */
    @GetMapping("rent/house/autocomplete")
    @ResponseBody
    public ApiResponse autocomplete(@RequestParam(value = "prefix")String prefix){
        if(prefix.isEmpty()){
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }
        ServiceResult<List<String>> result=this.searchService.suggest(prefix);
        System.out.println(result+"--------------------------------------");
        return ApiResponse.ofSuccess(result.getResult());
    }


    @GetMapping("rent/house/map")
    public String rentMapPage(@RequestParam(value = "cityEnName")String cityEnName,Model model,HttpSession session,RedirectAttributes redirectAttributes){
        ServiceResult<SupportAddressDTO> city=addressService.findCity(cityEnName);
        if(!city.isSuccess()){
            redirectAttributes.addAttribute("msg","must_chose_city");
            return "redirect:/index";
        }else {
            session.setAttribute("cityName",cityEnName);
            model.addAttribute("city",city.getResult());
        }
        ServiceMultiResult<SupportAddressDTO> regions = addressService.findAllRegionsByCityName(cityEnName);

        ServiceMultiResult<HouseBucketDTO>serviceMultiResult=searchService.mapAggregate(cityEnName);
        model.addAttribute("aggData",serviceMultiResult.getResult());
        model.addAttribute("total",serviceMultiResult.getTotal());
        model.addAttribute("regions",regions.getResult());

        return "rent-map";
    }

    @GetMapping("rent/house/map/houses")
    @ResponseBody
    public ApiResponse rentMapHouses(@ModelAttribute MapSearch mapSearch){
        if(mapSearch.getCityEnName()==null){
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),"必须选择一个城市");
        }
        ServiceMultiResult<HouseDTO> serviceMultiResult;
        if (mapSearch.getLevel() < 13) {
            serviceMultiResult = houseService.wholeMapQuery(mapSearch);
        } else {
            // 小地图查询必须要传递地图边界参数
            serviceMultiResult = houseService.boundMapQuery(mapSearch);
        }
        ApiResponse response=ApiResponse.ofSuccess(serviceMultiResult.getResult());
        response.setMore(serviceMultiResult.getTotal()>(mapSearch.getStart()+mapSearch.getSize()));
        return response;

    }

}
