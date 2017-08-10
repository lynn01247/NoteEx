# NoteEx

### 超级笔记Android客户端  
### 基于Material Design设计模式、嵌有轻量级的IM框架（环信）

## 效果图

### 整体效果

<img src="/screen/main.gif" title="" width="270" height="486" /> <br>

### 实验室效果：在线和本地换肤、在线和本地字体更换

<img src="/screen/second.gif" title="" width="270" height="486" /> <br>

### 其他功能效果
<img src="http://omzogcv8w.bkt.clouddn.com/user.png" width="270" height="486" title=""><br>

<img src="http://omzogcv8w.bkt.clouddn.com/tag.png" width="270" height="486" title="">

<img src="http://omzogcv8w.bkt.clouddn.com/talk.png" width="270" height="486" title="">

<img src="http://omzogcv8w.bkt.clouddn.com/share.png" width="270" height="486" title="">

<img src="http://omzogcv8w.bkt.clouddn.com/right.png" width="270" height="486" title="">

<img src="http://omzogcv8w.bkt.clouddn.com/setting.png" width="270" height="486" title="">

## 数据来源API接口 ##

### 1. 数据是自己写的后台，后台搭建在LeanCloud【非广告：这是免费的轻量级后台管理维护系统，可以基于Java、PHP等语言，具体信息请移步官网：https://leancloud.cn/】

部分接口示例:【更多信息，可以访问我当时开发另一个项目使用的API文档，本项目也是挂载在这个数据库里（懒得新建啦 ^_^ ）https://lynn01247.gitbooks.io/turtle_api_1-0/content/】
-----------------------------------------------------------------------------
// 根据page获取全部笔记信息
$turtle_api->get('/getNote', function(Request $request, Response $response) {
    if(empty($_REQUEST['pageIndex'])){
      $pageIndex = 1;
    }else{
      $pageIndex = $_REQUEST['pageIndex'];
    }
    // ....
    // ....
    //判断用户的会话token是否还有效
    $uidQueryUsers   = new Query("Users");
    $uidQueryUsers->equalTo("uid", intval($uid));

    $tokenQueryUsers = new Query("Users");
    $tokenQueryUsers->equalTo("token", $token);

    $queryUidToken   = Query::andQuery($uidQueryUsers, $tokenQueryUsers);
    try {
      $countUidToken = $queryUidToken->count();
    } catch (CloudException $ex) {
      return json_encode(array(
        "code"     => 0,
        "info"     => "获取全部笔记自检失败! 异常信息: ".$ex
      ));
    }
    if ($countUidToken > 0) {// 用户的会话token有效
      try {
          //.....
	  //...
            $data[] = array(
                "uid"          => $result->get("uid"),
                "nid"          => $result->get("nid"),
                "treeid"       => $result->get("treeid"),
                "tagid"        => $result->get("tagid"),
                "title"        => $result->get("title"),
                "content"      => $result->get("content"),
                "updatedTime"  => $result->get("updatedTime")
            );
          }
      } catch (\Exception $ex) {
          error_log("getNote failed!");
          $data = array();
      }
      return json_encode(array(
        "code"     => 1,
        "info"     => "查询成功!",
        "data"     => $data,
        "page"     => $pageIndex,
        "pageSize" => $pageSize,
        "total"    => ceil($count / $pageSize)
      ));
    }else{
      return json_encode(array(
        "code"     => 0,
        "info"     => "用户token无效!请重新登陆验证token信息!"
      ));
    }
});
-----------------------------------------------------------------------------

## 2. 部分干货数据是基于PHP爬虫实现，当用户访问APP请求最新数据时，自动去抓取【掘金】最新的数据。部分图片数据和主题字体等存于七牛
## 3. 由于掘金对接口访问采用【X-LC-Id】 + 【X-LC-Sign】认证方式，因此我将Sign搁置于友盟在线参数。

笔记Model的数据Json格式:
```
"code": 1,           //返回值,
"info": "查询成功!", //返回提示,
[
    {
        "uid": 1,
        "nid": 1,
        "treeid": "1",
        "title": "标题",
        "content": "内容",
        "updatedTime": "2017-4-12 10:00:00"
	...
    },
	......
    {
        "uid": 2,
        "nid": 2,
        "treeid": "2",
        "title": "标题2",
        "content": "内容2",
        "updatedTime": "2017-4-12 11:00:00"
	...
    }
],
"page": 1,
"pageSize": 10,
"total":1
```

## 功能点记录：
- Material Design 页面跳转动画，触摸响应和共享元素转场动画
- 侧边栏动画效果：ActionBarDrawerToggle、DrawerLayout、ToolBar 的结合
- RecycleView + BGARefreshLayout 下拉刷新上拉加载
- DrawerLayout 实现抽屉菜单
- Navigation 实现抽屉左边的导航
- ToolBar 实现沉浸式布局
- ViewPager 展示轮播图片（首尾循环，自动轮播）
- okhttp 封装请求 异步获取 Json数据
- skin 在线换肤和换字体【当然也支持本地】
- 多dimens 适配大部分系统
- CircleView 圆形头像
- 支持三方登录【微信、QQ、新浪】，使用的是友盟
- 支持三方分享【微信(含朋友圈/微信收藏)、QQ(含空间)、新浪、短信、邮件、文本、链接等 】
- HtextView 字体特效
- 轻量级环信聊天，实时红点信息提醒
- 自主提取封装的aar 防止Module过多累赘
- 集成友盟推送
- 集成腾讯bugly监测和应用升级框架 

## 部分依赖的开源库和工具

下拉刷新上拉加载[BGARefreshLayout](https://github.com/ylligang118/BGARefreshLayout-Android)

网络请求[okhttp](技术博客:http://blog.csdn.net/lmj623565791/article/details/47911083)

强大的图片处理工具[picasso](https://github.com/square/picasso)

炫酷的文字特效[htextview](https://github.com/hanks-zyh/HTextView)不过：minSdkVersion要15

主题切换一直是个功能要点，网上有许许多多的方式，不是操作太复杂，批量更换；就是效果不如意。
找了个比较满意的[ThemeSkinning](https://github.com/burgessjp/ThemeSkinning)