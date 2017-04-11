package tatait.com.noteex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.umeng.onlineconfig.OnlineConfigAgent;

import tatait.com.noteex.model.GsonVersionModel;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.CountDownProgressView;
import tatait.com.noteex.util.DownloadAppUtils;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.StringUtils;
import tatait.com.noteex.util.ToastUtil;


public class WelcomeActivity extends BaseActivity {
    private static final String TGA = "WelcomeActivity";
    private ImageView welcome_activity_img;
    private CountDownProgressView countDownProgressView;
    private boolean hasNewVersion = false;
    private boolean jumpToAd = false;
    private static int DELAY_TIME = 3500;
    private static String link = "";
    private Context mContext;
    private String mustUpdate;

    @Override
    protected void initView(Bundle savedInstanceState) {
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mContext = this;
        CommonUtil.setPermissions(WelcomeActivity.this);//针对6.0系统动态获取权限
        OnlineConfigAgent.getInstance().updateOnlineConfig(getApplicationContext());
        OnlineConfigAgent.getInstance().setDebugMode(false);
        //判断是否是第一次使用，是的话跳转引导页
        String mtype = android.os.Build.MODEL; // 手机型号
        Log.i(TGA, mtype);
        if (!mtype.contains("Samsung Galaxy") && (Boolean) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISFIRST, true)) {
            startActivity(new Intent(WelcomeActivity.this, GuideLightActivity.class));
            finish();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    if (EMClient.getInstance().isLoggedInBefore()) {
                        // ** 免登陆情况 加载所有本地群和会话
                        //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                        //加上的话保证进了主页面会话和群组都已经load完毕
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                    }
                }
            }).start();
            initView();
            initData();
        }
//        //初期开发，跳过引导页，直接进入首页
//        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }

    private void initView() {
        //加载主引导页的图片
        welcome_activity_img = (ImageView) findViewById(R.id.welcome_activity_img);
        //倒计时
        countDownProgressView = (CountDownProgressView) findViewById(R.id.countdownProgressView);
        countDownProgressView.setTimeMillis(DELAY_TIME);
        countDownProgressView.start();
        countDownProgressView.setProgressListener(new CountDownProgressView.OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                Log.i(TGA, "progress: " + progress);
                if (hasNewVersion) {
                    countDownProgressView.stop();
                    countDownProgressView.setVisibility(View.GONE);
                } else if (progress == 0) {
                    countDownProgressView.stop();
                    countDownProgressView.setVisibility(View.GONE);
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        countDownProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownProgressView.stop();
                countDownProgressView.setVisibility(View.GONE);
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        welcome_activity_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty2(link)) {
                    jumpToAd = true;
                    Intent intent1 = new Intent(WelcomeActivity.this, MainActivity.class);
                    Intent intent2 = new Intent(WelcomeActivity.this, WebActivity.class);
                    intent2.putExtra("url", link);
                    Intent[] intents = new Intent[]{intent1, intent2};
                    startActivities(intents);
                    finish();
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 检查版本丶获取首页图片和广告
        OkHttpClientManager.getAsyn(HttpRoute.URL_HEAD + HttpRoute.URL_GET_VERSION,
                new OkHttpClientManager.ResultCallback<GsonVersionModel>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(GsonVersionModel gsonModel) {
                        if (gsonModel != null && gsonModel.getData() != null & gsonModel.getData().size() > 0) {
                            //获取首页图片和广告
                            if (!StringUtils.isEmpty2(gsonModel.getData().get(0).getMainImg())) {
                                Picasso.with(mContext).load(gsonModel.getData().get(0).getMainImg()).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(welcome_activity_img);
                            }
                            //判断是否强制更新
                            if (!StringUtils.isEmpty2(gsonModel.getData().get(0).getMustUpdate())) {
                                mustUpdate = gsonModel.getData().get(0).getMustUpdate();
                            }
                            if (!StringUtils.isEmpty2(gsonModel.getData().get(0).getAdLink())) {
                                link = gsonModel.getData().get(0).getAdLink();
                            }
                            //判断版本信息
                            String android_version = gsonModel.getData().get(0).getAndroid_version();
                            String android_link = gsonModel.getData().get(0).getAndroid_link();
                            if (!StringUtils.isEmpty2(android_version) && Double.parseDouble(android_version) > Double.parseDouble(CommonUtil.getVersion(getApplicationContext()))) {
                                //当前版本大于本地版本
                                hasNewVersion = true;
                                showUpdateDialog(mContext, android_link);
                            } else {
                                hasNewVersion = false;
                            }
                        }
                    }
                });
    }

    /**
     * 提示更新版本
     *
     * @param context
     * @param url
     */
    public void showUpdateDialog(final Context context, final String url) {
        AlertDialog dialog = new AlertDialog(context);
        if (!StringUtils.isEmpty2(mustUpdate) && "true".equals(mustUpdate)) {
            dialog.builder().setTitle("提示").setMsg("检测到新版本，是否立即更新？")
                    .setNegativeButton("好的", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadAppUtils.downloadForAutoInstall(context, url, "note_ex.apk", "新版NoteEx正在下载中。。。");
                            SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.ISFIRST, true);
                            ((WelcomeActivity) context).finish();
                        }
                    }).show();
        } else {
            dialog.builder().setTitle("提示").setMsg("检测到新版本，是否立即更新？")
                    .setNegativeButton("好的", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Uri uri = Uri.parse(url);
//                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                            context.startActivity(intent);
                            DownloadAppUtils.downloadForAutoInstall(context, url, "note_ex.apk", "新版NoteEx正在下载中。。。");
                            SharedPreferencesUtils.setParam(getApplicationContext(), CommonUtil.ISFIRST, true);
                            ((WelcomeActivity) context).finish();
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).show();
        }
        if (!((WelcomeActivity) context).isFinishing()) {
            dialog.show();
        }
    }

    /**
     * 退出事件
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtil.show("再按一次退出应用程序");
                exitTime = System.currentTimeMillis();
            } else {
                // 程序退出
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
