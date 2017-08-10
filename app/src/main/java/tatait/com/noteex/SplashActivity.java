package tatait.com.noteex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.squareup.okhttp.Request;
import com.umeng.onlineconfig.OnlineConfigAgent;

import tatait.com.noteex.model.Constants;
import tatait.com.noteex.model.GsonVersionModel;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.DownloadAppUtils;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.StringUtils;


public class SplashActivity extends Activity implements SplashADListener {

    private SplashAD splashAD;
    private ViewGroup container;
    private TextView skipView;
    private ImageView splashHolder;
    private static final String SKIP_TEXT = "点击跳过 %d";

    public boolean canJump = false;

    private Context mContext;
    private String mustUpdate;
    private boolean hasNewVersion = false;
    private static String link = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        container = (ViewGroup) this.findViewById(R.id.splash_container);
        skipView = (TextView) findViewById(R.id.skip_view);
        splashHolder = (ImageView) findViewById(R.id.splash_holder);
        mContext = this;
        OnlineConfigAgent.getInstance().updateOnlineConfig(getApplicationContext());
        OnlineConfigAgent.getInstance().setDebugMode(false);

        if (Build.VERSION.SDK_INT >= 23) {
            CommonUtil.setPermissions(SplashActivity.this);//针对6.0系统动态获取权限
            //判断是否是第一次使用，是的话跳转引导页
//            String mtype = android.os.Build.MODEL; // 手机型号
//            if (!mtype.contains("Samsung Galaxy") && (Boolean) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISFIRST, true)) {
            if ((Boolean) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISFIRST, true)) {
                startActivity(new Intent(mContext, GuideLightActivity.class));
                finish();
            } else {
                initData();
            }
        }else{
            initData();
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        boolean isVip = (boolean) SharedPreferencesUtils.getParam(getApplicationContext(),"isVip",false);
        if(!isVip) {
            fetchSplashAD(this, container, skipView, Constants.APPID, Constants.SplashPosID, this, 0);
            // 检查版本
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
//                  if (!StringUtils.isEmpty2(gsonModel.getData().get(0).getMainImg())) {
//                    Picasso.with(mContext.getApplicationContext()).load(gsonModel.getData().get(0).getMainImg()).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(welcome_activity_img);
//                  }
                                //判断是否强制更新
                                if (!StringUtils.isEmpty2(gsonModel.getData().get(0).getMustUpdate())) {
                                    mustUpdate = gsonModel.getData().get(0).getMustUpdate();
                                }
                                if (!StringUtils.isEmpty2(gsonModel.getData().get(0).getAdLink())) {
                                    link = gsonModel.getData().get(0).getAdLink();
                                    SharedPreferencesUtils.setParam(mContext,"link",link);
                                }
                                if (!StringUtils.isEmpty2(gsonModel.getData().get(0).getIsOpen())) {
                                    SharedPreferencesUtils.setParam(mContext,"isOpen",gsonModel.getData().get(0).getIsOpen());
                                    SharedPreferencesUtils.setParam(mContext,"vip",gsonModel.getData().get(0).getVip());
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
        }else {
            // 检查版本
            OkHttpClientManager.getAsyn(HttpRoute.URL_HEAD + HttpRoute.URL_GET_VERSION,
                    new OkHttpClientManager.ResultCallback<GsonVersionModel>() {
                        @Override
                        public void onError(Request request, Exception e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(GsonVersionModel gsonModel) {
                            if (gsonModel != null && gsonModel.getData() != null & gsonModel.getData().size() > 0) {
                                //判断是否强制更新
                                if (!StringUtils.isEmpty2(gsonModel.getData().get(0).getMustUpdate())) {
                                    mustUpdate = gsonModel.getData().get(0).getMustUpdate();
                                }
                                if (!StringUtils.isEmpty2(gsonModel.getData().get(0).getAdLink())) {
                                    link = gsonModel.getData().get(0).getAdLink();
                                    SharedPreferencesUtils.setParam(mContext,"link",link);
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
                                    Intent intent = new Intent(mContext, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    });
        }
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
                            ((SplashActivity) context).finish();
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
                            ((SplashActivity) context).finish();
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).show();
        }
        if (!((SplashActivity) context).isFinishing()) {
            dialog.show();
        }
    }

    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity      展示广告的activity
     * @param adContainer   展示广告的大容器
     * @param skipContainer 自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
     * @param appId         应用ID
     * @param posId         广告位ID
     * @param adListener    广告状态监听器
     * @param fetchDelay    拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }

    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
        splashHolder.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
    }

    @Override
    public void onADClicked() {
        Log.i("AD_DEMO", "SplashADClicked");
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间。
     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    @Override
    public void onADTick(long millisUntilFinished) {
        Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
        skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
        next();
    }

    @Override
    public void onNoAD(int errorCode) {
        Log.i("AD_DEMO", "LoadSplashADFail, eCode=" + errorCode);
        /** 如果加载广告失败，则直接跳转 */
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {
        if (canJump) {
            this.startActivity(new Intent(this, MainActivity.class));
            this.finish();
        } else {
            canJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
