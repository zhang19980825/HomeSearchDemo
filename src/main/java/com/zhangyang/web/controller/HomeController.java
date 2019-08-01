package com.zhangyang.web.controller;

import com.zhangyang.base.ApiResponse;
import com.zhangyang.base.LoginUserUtil;
import com.zhangyang.service.ServiceResult;
import com.zhangyang.service.user.SmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: ZhangYang
 * @Date: 2019/7/20 18:22
 */
@Controller
public class HomeController {
    @Autowired
    private SmsServiceImpl smsService;

    @GetMapping(value = {"/","/index"})
    public String index(){
        return "index";
    }

    @GetMapping("/get")
    @ResponseBody
    public ApiResponse get(){
        return ApiResponse.ofMessage(200,"成功了");
    }

    @GetMapping("/500")
    public String error1(){
        return "500";
    }
    @GetMapping("/403")
    public String error2(){
        return "403";
    }
    @GetMapping("/404")
    public String error3(){
        return "404";
    }

    @GetMapping("/logout/page")
    public String error4(){
        return "logout";
    }

    @GetMapping(value = "sms/code")
    @ResponseBody
    public ApiResponse smsCode(@RequestParam("telephone")String telephone){
        if(!LoginUserUtil.checkTelephone(telephone)){
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),"请输入正确的手机号");
        }
        ServiceResult<String> result = smsService.sendSms(telephone);
        if(result.isSuccess()){
            return ApiResponse.ofSuccess("");
        }else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),result.getMessage());
        }
    }
}
