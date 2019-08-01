package com.zhangyang.service.user;

import com.zhangyang.service.ServiceResult;

/**
 * @Author: ZhangYang
 * 验证码服务
 * @Date: 2019/7/31 15:55
 */
public interface ISmsService {

    /*
    * 发送验证码到指定手机  及缓存验证码10分钟  及  请求间隔时间1分钟
    * */
    ServiceResult<String> sendSms(String telephone);

    /*
    * 获取缓存中的验证码
    * */
    String getSmsCode(String telephone);


    /*
    * 移除指定手机号的验证码缓存
    * */
    void remove(String telephone);
}
