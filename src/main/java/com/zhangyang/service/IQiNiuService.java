package com.zhangyang.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * @Author: ZhangYang
 * 七牛云服务
 * @Date: 2019/7/22 13:20
 */
public interface IQiNiuService {
    Response uploadFile(File file) throws QiniuException;

    Response uploadFile(InputStream inputStream) throws  QiniuException;

    Response delete(String key) throws  QiniuException;

}
