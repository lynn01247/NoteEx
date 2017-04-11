package tatait.com.noteex;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.SocializeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tatait.com.noteex.db.EmDBManager;
import tatait.com.noteex.db.EmUser;
import tatait.com.noteex.model.GsonUserModel;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.ToastUtil;
import tatait.com.noteex.widget.App;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private RelativeLayout relativeLayout;
    private LinearLayout back, wechat, QQ, sina;
    private static Context appContext;
    private static ProgressDialog dialog;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.login);
        initView();
        CommonUtil.loginTreeHandler = mHandler;
        UMShareAPI.get(this).fetchAuthResultWithBundle(this, savedInstanceState, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                SocializeUtils.safeShowDialog(dialog);
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                SocializeUtils.safeCloseDialog(dialog);
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                SocializeUtils.safeCloseDialog(dialog);
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                SocializeUtils.safeCloseDialog(dialog);
            }
        });
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        appContext = getApplicationContext();
    }

    //初始化
    private void initView() {
        back = (LinearLayout) findViewById(R.id.app_main_top_bar).findViewById(R.id.title_layout_back);
        relativeLayout = (RelativeLayout) findViewById(R.id.app_main_top_bar).findViewById(R.id.title_layout_rela);
        relativeLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));

        wechat = (LinearLayout) findViewById(R.id.wechat);
        QQ = (LinearLayout) findViewById(R.id.QQ);
        sina = (LinearLayout) findViewById(R.id.sina);

        back.setOnClickListener(this);
        wechat.setOnClickListener(this);
        QQ.setOnClickListener(this);
        sina.setOnClickListener(this);

        dialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_layout_back:
                finish();
                break;
            case R.id.wechat:
                UMShareAPI.get(getApplicationContext()).doOauthVerify(this, SHARE_MEDIA.WEIXIN, authListener);
                break;
            case R.id.QQ:
                UMShareAPI.get(getApplicationContext()).doOauthVerify(this, SHARE_MEDIA.QQ, authListener);
                break;
            case R.id.sina:
                UMShareAPI.get(getApplicationContext()).doOauthVerify(this, SHARE_MEDIA.SINA, authListener);
                break;
        }
    }

    public static void logoff(Activity mActivity, SHARE_MEDIA share_media) {
        UMShareAPI.get(mActivity).deleteOauth(mActivity, share_media, delAuthListener);
    }

    /**
     * 取消授权
     */
    static UMAuthListener delAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SocializeUtils.safeShowDialog(dialog);
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            SocializeUtils.safeCloseDialog(dialog);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            SocializeUtils.safeCloseDialog(dialog);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            SocializeUtils.safeCloseDialog(dialog);
        }
    };

    /**
     * 授权
     */
    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SocializeUtils.safeShowDialog(dialog);
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            SocializeUtils.safeCloseDialog(dialog);
            ToastUtil.show("授权成功,自动登录中...");
            CommonUtil.loginTreeSucHandler = mHandler;
            UMShareAPI.get(getApplicationContext()).getPlatformInfo(LoginActivity.this, platform, authResListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            SocializeUtils.safeCloseDialog(dialog);
            ToastUtil.show("授权失败：" + t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            SocializeUtils.safeCloseDialog(dialog);
            ToastUtil.show("操作被取消");
        }
    };

    static UMAuthListener authResListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
//            String temp = "";
            String openid = "";
            String name = "";
            String imgurl = "";
            if (platform == SHARE_MEDIA.SINA) { // 针对新浪 提取uid
                for (String key : data.keySet()) {
//                temp = temp + key + " : " + data.get(key) + "\n";
                    if ("name".equalsIgnoreCase(key)) {
                        name = data.get(key);
                    } else if ("uid".equalsIgnoreCase(key)) {
                        openid = data.get(key);
                    } else if ("profile_image_url".equalsIgnoreCase(key)) {
                        imgurl = data.get(key);
                    }
                }
            } else {
                for (String key : data.keySet()) {
                    if ("name".equalsIgnoreCase(key)) {
                        name = data.get(key);
                    } else if ("openid".equalsIgnoreCase(key)) {
                        openid = data.get(key);
                    } else if ("profile_image_url".equalsIgnoreCase(key)) {
                        imgurl = data.get(key);
                    }
                }
            }
            SharedPreferencesUtils.setParam(appContext, CommonUtil.PLATFORM_TYPE, platform.name());
            SharedPreferencesUtils.setParam(appContext, CommonUtil.PLATFORM_NAME, name);
            if (CommonUtil.loginTreeHandler != null) {
                Message message = new Message();
                message.what = CommonUtil.loginTreeCode;
                Map<String, String> map = new HashMap<>();
                map.put("name", name);
                map.put("openid", openid);
                map.put("imgurl", imgurl);
                message.obj = map;
                CommonUtil.loginTreeHandler.sendMessage(message);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            com.umeng.socialize.utils.Log.i("错误" + t.getMessage());
            ToastUtil.show(t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            ToastUtil.show("取消");
        }
    };

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.loginTreeSucCode: // 登录成功
                    finish();
                    break;
                case CommonUtil.loginTreeCode: // 三方登录成功, 调用登录
                    Map<String, String> map = (Map<String, String>) msg.obj;
                    LoginUser(map.get("name"), map.get("openid"), map.get("imgurl"));
                    LoginTalk(map.get("name"), map.get("openid"));
                    break;
                case CommonUtil.registerCode: // 注册事件
                    Map<String, String> registerMap = (Map<String, String>) msg.obj;
                    RegisterUser(registerMap.get("name"), registerMap.get("openid"), registerMap.get("imgurl"));
                    RegisterTalk(registerMap.get("name"), registerMap.get("openid"));
                    break;
                case CommonUtil.loginSucCode: // 登录成功
                    ToastUtil.show("登录成功");
                    SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.ISLOGIN, true);
                    Map<String, String> loginSucMap = (Map<String, String>) msg.obj;
                    SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.UID, Integer.valueOf(loginSucMap.get("uid")));
                    SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.NAME, loginSucMap.get("name"));
                    SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.TOKEN, loginSucMap.get("token"));
                    SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.IMGURL, loginSucMap.get("imgurl"));
                    SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.DEFAULTID, loginSucMap.get("defaultid"));
                    if (CommonUtil.mainLoginHandler != null) {
                        Message message = new Message();
                        message.what = CommonUtil.mainLoginCode;
                        CommonUtil.mainLoginHandler.sendMessage(message);
                    }
                    if (CommonUtil.loginTreeSucHandler != null) {
                        Message message = new Message();
                        message.what = CommonUtil.loginTreeSucCode;
                        CommonUtil.loginTreeSucHandler.sendMessage(message);
                    }
                    break;
            }
        }
    };

    /**
     * 登录事件
     *
     * @param name
     * @param openid
     */
    private void LoginUser(final String name, final String openid, final String imgurl) {
        CommonUtil.loginHandler = mHandler;
        MobclickAgent.onProfileSignIn("QQ", name);
        String url = HttpRoute.URL_HEAD + HttpRoute.URL_GET_LOGIN_USER + "?name=" + name + "&password=" + openid;
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<GsonUserModel>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(GsonUserModel gsonModel) {
                if (gsonModel != null) {
                    if ("2".equals(gsonModel.getCode())) {// 还未注册过
                        if (CommonUtil.loginHandler != null) {
                            Message message = new Message();
                            message.what = CommonUtil.registerCode;
                            Map<String, String> map = new HashMap<>();
                            map.put("name", name);
                            map.put("openid", openid);
                            map.put("imgurl", imgurl);
                            message.obj = map;
                            CommonUtil.loginHandler.sendMessage(message);
                        }
                    } else if ("1".equals(gsonModel.getCode())) {// 登录成功
                        if (CommonUtil.loginHandler != null) {
                            Message message = new Message();
                            message.what = CommonUtil.loginSucCode;
                            Map<String, String> map = new HashMap<>();
                            List<GsonUserModel.UserModel> data = gsonModel.getData();
                            if (data != null && data.size() > 0) {
                                map.put("uid", data.get(0).getUid());
                                map.put("name", data.get(0).getName());
                                map.put("token", data.get(0).getToken());
                                map.put("imgurl", data.get(0).getImgurl());
                                map.put("defaultid", data.get(0).getDefaultid());
                                message.obj = map;
                                CommonUtil.loginHandler.sendMessage(message);
                            }
                        }
                    } else {
                        ToastUtil.show(gsonModel.getInfo());
                    }
                }
            }
        });
    }

    /**
     * 注册事件
     *
     * @param name
     * @param openid
     */
    private void RegisterUser(final String name, final String openid, final String imgurl) {
        CommonUtil.registerHandler = mHandler;
        String cid = SharedPreferencesUtils.getParam(getApplicationContext(),"deviceToken","").toString();
        String url = HttpRoute.URL_HEAD + HttpRoute.URL_GET_REGISTER_USER + "?name=" + name + "&password=" + openid + "&imgurl=" + imgurl + "&cid=" + cid + "&from=noteex";
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<GsonUserModel>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(GsonUserModel gsonModel) {
                if (gsonModel != null) {
                    if ("1".equals(gsonModel.getCode())) {// 登录成功
                        if (CommonUtil.registerHandler != null) {
                            Message message = new Message();
                            message.what = CommonUtil.loginSucCode;
                            Map<String, String> map = new HashMap<>();
                            List<GsonUserModel.UserModel> data = gsonModel.getData();
                            if (data != null && data.size() > 0) {
                                map.put("uid", data.get(0).getUid());
                                map.put("name", data.get(0).getName());
                                map.put("token", data.get(0).getToken());
                                map.put("imgurl", data.get(0).getImgurl());
                                map.put("defaultid", data.get(0).getDefaultid());
                                message.obj = map;
                                CommonUtil.registerHandler.sendMessage(message);
                            }
                        }
                    } else {
                        ToastUtil.show(gsonModel.getInfo());
                    }
                }
            }
        });
    }

    /**
     * 登录IM用户
     *
     * @param name
     * @param openid
     */
    private void LoginTalk(String name, String openid) {
        // close it before login to make sure DemoDB not overlap
        EmDBManager.getInstance().closeDB();
        // reset current user name before login
        App.getInstance().setCurrentUserName(openid);
        // 调用sdk登陆方法登陆聊天服务器
        EMClient.getInstance().login(openid, openid, new EMCallBack() {

            @Override
            public void onSuccess() {
                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                getFriends();

                SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.TALKISLOGIN, true);
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ToastUtil.show(getString(R.string.Login_failed) + message);
                    }
                });
            }
        });
    }

    private void getFriends() {
        try {
            List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
            Map<String, EmUser> users = new HashMap<String, EmUser>();
            for (String username : usernames) {
                EmUser user = new EmUser(username);
                users.put(username, user);
            }
            App.getInstance().setContactList(users);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册IM用户
     *
     * @param name
     * @param openid
     */
    private void RegisterTalk(final String name, final String openid) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMClient.getInstance().createAccount(openid, openid);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // 保存用户名
                            App.getInstance().setCurrentUserName(openid);
                            SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.TALKISREGISTER, true);
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NETWORK_ERROR) {
                                ToastUtil.show("网络异常，请检查网络！");
                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
//                                ToastUtil.show("(聊天系统)该用户名已存在！");
                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                ToastUtil.show("(聊天系统)用户注册失败，无权限！");
                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                ToastUtil.show("(聊天系统)用户名不合法！");
                            } else {
                                ToastUtil.show("(聊天系统)用户注册失败:" + e.getMessage());
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UMShareAPI.get(this).onSaveInstanceState(outState);
    }
}
