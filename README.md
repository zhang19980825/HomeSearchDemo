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



