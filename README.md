# NoteEx

## �����ʼ�Android�ͻ���  ����Material Design���ģʽ��Ƕ����������IM��ܣ����ţ�

## Ч��ͼ

#����Ч��
<img src="http://omzogcv8w.bkt.clouddn.com/main.gif" title="" width="588" height="981" /> <br>

#ʵ����Ч�������ߺͱ��ػ��������ߺͱ����������
<img src="http://omzogcv8w.bkt.clouddn.com/second.gif" title="" width="588" height="981" /> <br>

#��������Ч��
<img src="http://omzogcv8w.bkt.clouddn.com/user.png" width="588" height="981" title=""><br>

<img src="http://omzogcv8w.bkt.clouddn.com/tag.png" width="588" height="981" title="">

<img src="http://omzogcv8w.bkt.clouddn.com/talk.png" width="588" height="981" title="">

<img src="http://omzogcv8w.bkt.clouddn.com/share.png" width="588" height="981" title="">

<img src="http://omzogcv8w.bkt.clouddn.com/right.png" width="588" height="981" title="">

<img src="http://omzogcv8w.bkt.clouddn.com/setting.png" width="588" height="981" title="">

## ������ԴAPI�ӿ� ##

##1. �������Լ�д�ĺ�̨����̨���LeanCloud���ǹ�棺������ѵ���������̨����ά��ϵͳ�����Ի���Java��PHP�����ԣ�������Ϣ���Ʋ�������https://leancloud.cn/��

���ֽӿ�ʾ��:��������Ϣ�����Է����ҵ�ʱ������һ����Ŀʹ�õ�API�ĵ�������ĿҲ�ǹ�����������ݿ�������½��� ^_^ ��https://lynn01247.gitbooks.io/turtle_api_1-0/content/��
-----------------------------------------------------------------------------
// ����page��ȡȫ���ʼ���Ϣ
$turtle_api->get('/getNote', function(Request $request, Response $response) {
    if(empty($_REQUEST['pageIndex'])){
      $pageIndex = 1;
    }else{
      $pageIndex = $_REQUEST['pageIndex'];
    }
    // ....
    // ....
    //�ж��û��ĻỰtoken�Ƿ���Ч
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
        "info"     => "��ȡȫ���ʼ��Լ�ʧ��! �쳣��Ϣ: ".$ex
      ));
    }
    if ($countUidToken > 0) {// �û��ĻỰtoken��Ч
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
        "info"     => "��ѯ�ɹ�!",
        "data"     => $data,
        "page"     => $pageIndex,
        "pageSize" => $pageSize,
        "total"    => ceil($count / $pageSize)
      ));
    }else{
      return json_encode(array(
        "code"     => 0,
        "info"     => "�û�token��Ч!�����µ�½��֤token��Ϣ!"
      ));
    }
});
-----------------------------------------------------------------------------

##2. ���ָɻ������ǻ���PHP����ʵ�֣����û�����APP������������ʱ���Զ�ȥץȡ��������µ����ݡ�����ͼƬ���ݺ���������ȴ�����ţ
##3. ���ھ��Խӿڷ��ʲ��á�X-LC-Id�� + ��X-LC-Sign����֤��ʽ������ҽ�Sign�������������߲�����

�ʼ�Model������Json��ʽ:
```
"code": 1,           //����ֵ,
"info": "��ѯ�ɹ�!", //������ʾ,
[
    {
        "uid": 1,
        "nid": 1,
        "treeid": "1",
        "title": "����",
        "content": "����",
        "updatedTime": "2017-4-12 10:00:00"
	...
    },
	......
    {
        "uid": 2,
        "nid": 2,
        "treeid": "2",
        "title": "����2",
        "content": "����2",
        "updatedTime": "2017-4-12 11:00:00"
	...
    }
],
"page": 1,
"pageSize": 10,
"total":1
```

## ���ܵ��¼��
- Material Design ҳ����ת������������Ӧ�͹���Ԫ��ת������
- ���������Ч����ActionBarDrawerToggle��DrawerLayout��ToolBar �Ľ��
- RecycleView + BGARefreshLayout ����ˢ����������
- DrawerLayout ʵ�ֳ���˵�
- Navigation ʵ�ֳ�����ߵĵ���
- ToolBar ʵ�ֳ���ʽ����
- ViewPager չʾ�ֲ�ͼƬ����βѭ�����Զ��ֲ���
- okhttp ��װ���� �첽��ȡ Json����
- skin ���߻����ͻ����塾��ȻҲ֧�ֱ��ء�
- ��dimens ����󲿷�ϵͳ
- CircleView Բ��ͷ��
- ֧��������¼��΢�š�QQ�����ˡ���ʹ�õ�������
- ֧����������΢��(������Ȧ/΢���ղ�)��QQ(���ռ�)�����ˡ����š��ʼ����ı������ӵ� ��
- HtextView ������Ч
- �������������죬ʵʱ�����Ϣ����
- ������ȡ��װ��aar ��ֹModule������׸
- ������������

## ���������Ŀ�Դ��͹���

����ˢ����������[BGARefreshLayout](https://github.com/ylligang118/BGARefreshLayout-Android)
��������[okhttp](��������:http://blog.csdn.net/lmj623565791/article/details/47911083)
ǿ���ͼƬ������[picasso](https://github.com/square/picasso)
�ſ��������Ч[htextview](https://github.com/hanks-zyh/HTextView)������minSdkVersionҪ15
�����л�һֱ�Ǹ�����Ҫ�㣬������������ķ�ʽ�����ǲ���̫���ӣ���������������Ч�������⡣
���˸��Ƚ������[ThemeSkinning](https://github.com/burgessjp/ThemeSkinning)

## д�����
�����Ŀǰǰ����˽϶��ʱ�䣬��Ϊ���Լ����ֵ���Ŀ���ڹ���֮�����޲���������Ҫ��Ҳ���Լ��趨�������������дƪ�ɻ������������̡�����ʱ��͹��ܲ�����һֱ���ţ���������Ҫ�±��ˡ��ڴ����´θ��³��ɻ���ַ��