package com.zhangyang.service.search;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: ZhangYang
 * @Date: 2019/7/28 17:15
 */
public class Test {

    private static int port = 9300;//通过http请求的端口号是9200，通过客户端请求的端口号是9300
    private static String host = "47.100.219.102";//elasticsearch的服务器地址
    public static void main(String[] args) throws Exception {

        SearchServiceImpl iSearchService=new SearchServiceImpl() ;

        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")//设置es集群名称
                .put("client.transport.sniff", true)//增加嗅探机制，找到es集群
                .build();
        //创建client
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
        System.out.println(client);

        Long targetHouseId=15L;
        iSearchService.index(targetHouseId);

    }

}
