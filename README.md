## 搜房网小Demo

体验地址：首页：http://47.93.16.29:8081/  
管理员后台：http://47.93.16.29:8081/admin/center（因为session的关系需要退出账户清除session）  
用户名：admin  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  密码：admin

```
项目介绍：基于es的搜房网的小Demo  
1.网站采用SpringSecurity作为安全验证的框架，以此区分用户和管理员的资源访问的限制。
2.首页登录对接阿里云短信服务，采用手机号验证码登录，登录之后可在个人中心修改昵称，密码等信息便于下次的密码登录。
3.首页选取城市进行房源的选择。房源的话首先在es中会有对应的索引。  当添加房源或者对房源状态（未审核/审核通过/已出租）或者对房源信息进行更新的时候会建立或者更新es中的索引。 进行逻辑删除的时候会删除es中的索引。  有关索引的操作都是在Kafka中异步进行的。更加保证了es的稳定性。  搜索的时候会先从es中搜索出对应的HouseId然后从数据库中去拿。
其中对小区名和地铁站集成了search_as_you_type功能，实现简单的自动补全。 每个房源信息详情页使用es对小区的聚合查询查出当前小区的租房数量进行显示。
4.地图找房功能：对接百度地图实现通过前端js的缩放事件和地图的拖拽完成事件实现通过经纬度来完成对当前地图房源信息的动态显示。
5.房源详情页点击预约看房加入个人中心的待看清单，通过确认预约之后，管理员可在后台查看预约的各种信息，确认看房之后，当前用户看的房源会在看房记录中。
6.管理员后台有添加房源，对房源状态审核的功能实现。  房源图片存储到七牛云服务器。

```   
建立的索引如下：
```
{
  "settings": {
    "number_of_replicas": 0
  },
  "mappings": {
    "house": {
      "dynamic": false,
      "properties": {
        "houseId": {
          "type": "long"
        },
        "title": {
          "type": "text",
          "index": "true",
          "analyzer": "ik_smart",
          "search_analyzer": "ik_smart"
        },
        "price": {
          "type": "integer"
        },
        "area": {
          "type": "integer"
        },
        "createTime": {
          "type": "date",
          "format": "strict_date_optional_time||epoch_millis"
        },
        "lastUpdateTime": {
          "type": "date",
          "format": "strict_date_optional_time||epoch_millis"
        },
        "cityEnName": {
          "type": "keyword"
        },
        "regionEnName": {
          "type": "keyword"
        },
        "direction": {
          "type": "integer"
        },
        "distanceToSubway": {
          "type": "integer"
        },
        "subwayLineName": {
          "type": "keyword"
        },
        "subwayStationName": {
          "type": "keyword"
        },
        "tags": {
          "type": "text"
        },
        "street": {
          "type": "keyword"
        },
        "district": {
          "type": "keyword"
        },
        "description": {
          "type": "text",
          "index": "true",
          "analyzer": "ik_smart",
          "search_analyzer": "ik_smart"
        },
        "layoutDesc" : {
          "type": "text",
          "index": "true",
          "analyzer": "ik_smart",
          "search_analyzer": "ik_smart"
        },
        "traffic": {
          "type": "text",
          "index": "true",
          "analyzer": "ik_smart",
          "search_analyzer": "ik_smart"
        },
        "roundService": {
          "type": "text",
          "index": "true",
          "analyzer": "ik_smart",
          "search_analyzer": "ik_smart"
        },
        "rentWay": {
          "type": "integer"
        },
        "suggest": {
          "type": "completion"
        },
        "location": {
          "type": "geo_point"
        }
      }
    }
  }
}
```

## **com.zhangyang.config文件夹：**
1.ElasticSearchConfig.java文件：主要对elasticsearch相关配置建立es的TransportClient  
2.JPAConfig.java文件：比如设置相关数据源 配置repository的包的位置等  
3.RedisSessionConfig.java文件：Redis保存session会话的相关配置  
4.WebFileUploadConfig.java文件：七牛云上传的配置  包含上传配置  注册解析器  上传工具实例  空间管理实例等。  
5.WebMvcConfig.java文件：主要包含静态资源加载配置   模板资源解析器  Thymeleaf标准方言解释器  视图解析器  
6.WebSecurityConfig.java文件：进行Http权限控制   限制资源的访问权限   和管理员和用户的登录入口
## **com.zhangyang.security文件夹：**
1.AuthFilter.java文件：对用户登录进行一个过滤器的作用    主要是用户用手机号登录的情况进行验证码的判断 （验证码式存储在redis里面的） 查询的时候还需判断用户是不是第一次登录如果是的话则加一步注册该用户的步骤。  
2.AuthProvider.java文件：用户以用户名密码登录的拦截   进行用户名和密码的数据库匹配  
3.LoginAuthFailHandler.java文件：登录失败处理逻辑  
4.LoginUrlEntryPoint.java文件：基于角色的登录入口控制器   进行普通用户和管理员登录入口的映射  
## **com.zhangyang.entity文件夹**  实体类  
1.House.java         房源实体类
```
CREATE TABLE `house` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'house唯一标识',
  `title` varchar(32) NOT NULL,
  `price` int(11) unsigned NOT NULL COMMENT '价格',
  `area` int(11) unsigned NOT NULL COMMENT '面积',
  `room` int(11) unsigned NOT NULL COMMENT '卧室数量',
  `floor` int(11) unsigned NOT NULL COMMENT '楼层',
  `total_floor` int(11) unsigned NOT NULL COMMENT '总楼层',
  `watch_times` int(11) unsigned DEFAULT '0' COMMENT '被看次数',
  `build_year` int(4) NOT NULL COMMENT '建立年限',
  `status` int(4) unsigned NOT NULL DEFAULT '0' COMMENT '房屋状态 0-未审核 1-审核通过 2-已出租 3-逻辑删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近数据更新时间',
  `city_en_name` varchar(32) NOT NULL COMMENT '城市标记缩写 如 北京bj',
  `region_en_name` varchar(255) NOT NULL COMMENT '地区英文简写 如昌平区 cpq',
  `cover` varchar(32) DEFAULT NULL COMMENT '封面',
  `direction` int(11) NOT NULL COMMENT '房屋朝向',
  `distance_to_subway` int(11) NOT NULL DEFAULT '-1' COMMENT '距地铁距离 默认-1 附近无地铁',
  `parlour` int(11) NOT NULL DEFAULT '0' COMMENT '客厅数量',
  `district` varchar(32) NOT NULL COMMENT '所在小区',
  `admin_id` int(11) NOT NULL COMMENT '所属管理员id',
  `bathroom` int(11) NOT NULL DEFAULT '0',
  `street` varchar(32) NOT NULL COMMENT '街道',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COMMENT='房屋信息表';
```

2.HouseDetail.java      房源细节实体类
```
CREATE TABLE `house_detail` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL COMMENT '详细描述',
  `layout_desc` varchar(255) DEFAULT NULL COMMENT '户型介绍',
  `traffic` varchar(255) DEFAULT NULL COMMENT '交通出行',
  `round_service` varchar(255) DEFAULT NULL COMMENT '周边配套',
  `rent_way` int(2) NOT NULL COMMENT '租赁方式',
  `address` varchar(32) NOT NULL COMMENT '详细地址 ',
  `subway_line_id` int(11) DEFAULT NULL COMMENT '附近地铁线id',
  `subway_line_name` varchar(32) DEFAULT NULL COMMENT '附近地铁线名称',
  `subway_station_id` int(11) DEFAULT NULL COMMENT '地铁站id',
  `subway_station_name` varchar(32) DEFAULT NULL COMMENT '地铁站名',
  `house_id` int(11) NOT NULL COMMENT '对应house的id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_on_house_id` (`house_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4;
```

3.HousePicture .java      房屋图片信息
```
CREATE TABLE `house_picture` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `house_id` int(11) NOT NULL COMMENT '所属房屋id',
  `cdn_prefix` varchar(255) NOT NULL COMMENT '图片路径',
  `width` int(11) DEFAULT NULL COMMENT '宽',
  `height` int(11) DEFAULT NULL COMMENT '高',
  `location` varchar(32) DEFAULT NULL COMMENT '所属房屋位置',
  `path` varchar(255) NOT NULL COMMENT '文件名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COMMENT='房屋图片信息';
```

4.HouseSubscribe.java       预约看房信息表
```
CREATE TABLE `house_subscribe` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `house_id` int(11) NOT NULL COMMENT '房源id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `desc` varchar(255) DEFAULT NULL COMMENT '用户描述',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '预约状态 1-加入待看清单 2-已预约看房时间 3-看房完成',
  `create_time` datetime NOT NULL COMMENT '数据创建时间',
  `last_update_time` datetime NOT NULL COMMENT '记录更新时间',
  `order_time` datetime DEFAULT NULL COMMENT '预约时间',
  `telephone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `admin_id` int(11) NOT NULL COMMENT '房源发布者id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_on_user_and_house` (`house_id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='预约看房信息表';
```

5.HouseTag.java      房屋标签映射关系表
```
CREATE TABLE `house_tag` (
  `house_id` int(11) NOT NULL COMMENT '房屋id',
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_on_house_id_and_name` (`house_id`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COMMENT='房屋标签映射关系表';
```


6.Role.java      权限实体类 
```    
CREATE TABLE `role` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL COMMENT '用户id',
  `name` varchar(32) NOT NULL COMMENT '用户角色名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_and_name` (`user_id`,`name`) USING BTREE,
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色表';
```

7.Subway.java      地铁实体类
```
CREATE TABLE `subway` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL COMMENT '线路名',
  `city_en_name` varchar(32) NOT NULL COMMENT '所属城市英文名缩写',
  PRIMARY KEY (`id`),
  KEY `index_on_city` (`city_en_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;
```

8.SubwayStation.java         地铁站实体类
```
CREATE TABLE `subway_station` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `subway_id` int(11) NOT NULL COMMENT '所属地铁线id',
  `name` varchar(32) NOT NULL COMMENT '站点名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_station` (`subway_id`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4;
```

9.SupportAddress.java        支持区域的实体类   XX市XX区
```
CREATE TABLE `support_address` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `belong_to` varchar(32) NOT NULL DEFAULT '0' COMMENT '上一级行政单位名',
  `en_name` varchar(32) NOT NULL COMMENT '行政单位英文名缩写',
  `cn_name` varchar(32) NOT NULL COMMENT '行政单位中文名',
  `level` varchar(16) NOT NULL COMMENT '行政级别 市-city 地区-region',
  `baidu_map_lng` double NOT NULL COMMENT '百度地图经度',
  `baidu_map_lat` double NOT NULL COMMENT '百度地图纬度',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_en_name_and_belong_to` (`en_name`,`level`,`belong_to`) USING BTREE COMMENT '每个城市的英文名都是独一无二的'
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4;
```

10.User.java     用户实体类
```
CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户唯一id',
  `name` varchar(32) DEFAULT NULL COMMENT '用户名',
  `email` varchar(32) DEFAULT NULL COMMENT '电子邮箱',
  `phone_number` varchar(15) NOT NULL COMMENT '电话号码',
  `password` varchar(32) DEFAULT NULL COMMENT '密码',
  `status` int(2) unsigned NOT NULL DEFAULT '0' COMMENT '用户状态 0-正常 1-封禁',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户账号创建时间',
  `last_login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上次登录时间',
  `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上次更新记录时间',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_on_phone` (`phone_number`) USING BTREE COMMENT '用户手机号',
  UNIQUE KEY `index_on_username` (`name`) USING BTREE COMMENT '用户名索引',
  UNIQUE KEY `index_on_email` (`email`) USING BTREE COMMENT '电子邮箱索引'
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='用户基本信息表';
```

## **com.zhangyang.base文件夹**
1.ApiDataTableResponse.java文件：由于用到了插件Datatables表格   所以应构建其对应的响应结构  
2.ApiResponse.java文件：API格式封装用于返回统一的响应结果  
3.AppErrorController.java文件:  错误结果的对应的返回请求  
4.HouseOperation.java文件：房屋操作状态常量定义  1.通过审核   2.下架  3.逻辑删除  4.出租  
5.HouseSort.java文件：排序生成器    主要是就房源的创建时间  价格  地区进行排序所需要的类  
6.HouseStatus.java文件：房源状态  0--未审核   1--审核通过  2--已出租  3--逻辑删除  
7.HouseSubscribeStatus.java文件：房源预约状态码  0--未预约   1--已加入待看清单  2--已预约看房时间   3--已完成预约  
8.LoginUserUtil.java文件：用户登录的手机号  和用户信息的邮箱的正则表达式验证
9.RentValueBlock.java文件：带区间的常用数值定义    价格区间定义    面积区间定义     无限制区间
## **com.zhangyang.repository文件夹**
1.HouseDetailRepository.java     
2.HousePictureRepository.java   
3.HouseRepository.java   
4.HouseSubscribeRespository.java   
5.HouseTagRepository.java   
6.RoleRepository.java   
7.SubwayRepository.java   
8.SubwayStationRepository.java   
9.SupportAddressRepository.java   
10.UserRepository.java   
## **com.zhangyang.web.controller.admin文件夹**
1.AdminController.java  
页面的映射关系：管理员登录页   房源列表页   新增房源功能页  
* 图片上传功能    
* 添加房源信息  
* 房源信息编辑数据展示  
* 房源信息编辑接口  
* 移除图片接口  
* 修改封面接口  
* 增加标签接口  
* 移除标签接口  
* 商品审核接口  
* 预约房源展示  
## **com.zhangyang.web.controller.house文件夹**
1.HouseController.java  
* 获取支持城市列表  
* 获取对应城市支持区域列表  
* 获取具体城市所支持的地铁线路  
* 获取对应地铁线路所支持的地铁站点  
* 首页的房源显示  
* 租赁房源页面的信息展示  
* 搜索字段自动补全接口  
* 租赁房源页面粗略显示房源信息接口  
* 租赁房源页面达到一定的缩放级别显示房源信息接口  
## **com.zhangyang.web.controller.user文件夹**
1.UserController.java  
* 个人中心修改制定属性值  
* 个人中心房源预约列表  
* 个人中心房源预约列表清单接口  
* 个人中心房源预约房源日期接口  
## **com.zhangyang.web.dto文件夹**
1.HouseDetailDTO.java   
2.HouseDTO.java   
3.HousePictureDTO.java   
4.HouseSubscribeDTO.java   
5.QiNiuPutRet.java   
6.SubwayDTO.java   
7.SubwayStationDTO.java   
8.SupportAddressDTO.java   
9.UserDTO.java   
## **com.zhangyang.web.form文件夹**
1.DatatableSearch.java     Datatables规定的响应格式和表单实体类  
2.HouseForm.java         房源表达的填写页面对应的dao  
3.MapSearch.java         地图搜索对应的dto   包含地图的缩放级别和左上角和右下角的经纬度  
4.PhotoForm.java         上传的图片的dto  包含路径   宽度和长度  
5.RentSearch.java        租房请求参数结构体  
## **com.zhangyang.service.house文件夹**
1.IHouseService.java    主要功能  
* 新增房源信息  
* 查询完整房源信息  
* 更新房源信息  
* 房源信息的匹配查询  
* 移除图片  
* 更新封面  
* 新增标签  
* 移除标签  
* 更新房源状态  
* 查询房源信息集  
* 全地图查询房源接口  
* 精确范围查询房源接口  
* 加入预约清单  
* 获取对应状态的预约列表  
* 预约看房时间  
* 取消预约  
* 管理员查询预约信息接口  
* 完成预约  

2.IAddressService.java  主要功能  
* 根据英文简写获取具体区域的信息  
* 根据城市英文简写获取该城市所有支持的区域信息  
* 获取该城市所有的地铁线路  
* 获取地铁线路所有的站点  
* 获取地铁线信息  
* 获取地铁站点信息  
* 根据城市英文简写获取城市详细信息  
* 根据城市以及具体地址获取百度地图的经纬度  
* 上传百度LBS数据  
* 移除百度LBS数据  

2.QiNiuSeriviceImpl.java  
* 通过文件形式上传文件  
* 通过流的方式上传文件  
* 文件的删除  
* 获取上传凭证  
## **com.zhangyang.service.user文件夹**
1.ISmsService.java
* 发送验证码到指定手机  及缓存验证码10分钟  及  请求间隔时间1分钟  
* 获取缓存中的验证码  
* 移除指定手机号的验证码缓存 

2.IUserSerivce.java
* 通过电话号码寻找用户  
* 通过电话号码注册用户  
* 修改指定属性值  
## **com.zhangyang.service.search文件夹**
1.HouseIndexKey.java    索引关键字统一定义  
2.HouseIndexTemplate.java  索引结构模板  
3.BaiduMapLocation.java  百度位置信息  
4.ISearchService.java   功能  
* 索引目标房源  
* 移除房源索引  
* 获取补全建议关键词  
* 查询房源接口  
* 聚合特定小区的房间数  
* 聚合城市数据  
* 城市级别查询  
* 精确范围数据级别查询  




