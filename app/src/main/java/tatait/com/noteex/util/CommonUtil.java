package tatait.com.noteex.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.squareup.okhttp.Request;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author lynn
 */
public class CommonUtil {
    public static Handler itemClickHandler;//笔记列表点击监听事件
    public final static int itemClickCode = 0x90001;//笔记列表点击监听事件
    public static Handler itemClickGemHandler;//笔记Gem列表点击监听事件
    public final static int itemClickGemCode = 0x90002;//笔记Gem列表点击监听事件
    public static Handler itemClickGoodHandler;//笔记Good列表点击监听事件
    public final static int itemClickGoodCode = 0x90002;//笔记Good列表点击监听事件
    public static Handler itemClickNewHandler;//笔记New列表点击监听事件
    public final static int itemClickNewCode = 0x90003;//笔记New列表点击监听事件

    public static Handler refreshDataFileFragHandler;//notifyDataSetChanged事件
    public final static int refreshFileFragDataCode = 0x90004;//notifyDataSetChanged事件
    public static Handler selectTreeHandler;//selectTree事件
    public final static int selectTreeCode = 0x90005;//selectTree事件
    public static Handler selectTagHandler;//selectTree事件
    public final static int selectTagCode = 0x90006;//selectTag事件

    public static Handler dialogHandler;//底部弹出框监听事件
    public final static int dialogCode = 0x90007;//底部弹出框

    public static Handler addNoteHandler;//新增笔记监听事件
    public final static int addOrUptNoteCode = 0x00001;//笔记列表点击监听事件
    public final static int addRequestCode = 0x00002;//跳转新增/修改笔记
    public final static int addSucResultCode = 0x00003;//新增/修改笔记成功
    public final static int selectTreeRequestCode = 0x00004;//跳转selectTree
    public final static int selectTreeResultCode = 0x00005;//selectTree成功
    public static Handler addNoteTreeHandler;//新增笔记文件夹监听事件
    public final static int addOrUptNoteTreeCode = 0x00006;//笔记列表文件夹事件
    public static Handler addNoteTagHandler;//新增笔记标签监听事件
    public final static int addOrUptNoteTagCode = 0x00007;//笔记标签事件
    public final static int selectTagRequestCode = 0x00008;//跳转selectTag事件
    public final static int selectTagResultCode = 0x00009;//selectTag事件成功

    public static Handler delNoteGropHandler;//Grop删除笔记监听事件
    public final static int delNoteGropCode = 0x00025;//Grop删除笔记事件
    public static Handler delNoteHandler;//删除笔记监听事件
    public final static int delNoteCode = 0x00010;//删除笔记事件

    public static Handler collectNoteHandler;//收藏笔记监听事件
    public final static int collectNoteCode = 0x00011;//收藏笔记事件
    public static Handler delCollectNoteHandler;//取消收藏笔记监听事件
    public final static int delCollectNoteCode = 0x00012;//取消收藏笔记事件

    public static Handler collectNoteGropHandler;//Grop收藏笔记监听事件
    public final static int collectNoteGropCode = 0x00026;//Grop收藏笔记事件
    public static Handler delCollectNoteGropHandler;//Grop取消收藏笔记监听事件
    public final static int delCollectNoteGropCode = 0x00027;//Grop取消收藏笔记事件

    public static Handler collectNoteCollHandler;//Coll收藏笔记监听事件
    public final static int collectNoteCollCode = 0x00040;//Coll收藏笔记事件
    public static Handler delCollectNoteCollHandler;//Coll取消收藏笔记监听事件
    public final static int delCollectNoteCollCode = 0x00041;//Coll取消收藏笔记事件

    public static Handler collectNoteGemHandler;//Gem收藏笔记监听事件
    public final static int collectNoteGemCode = 0x00028;//Gem收藏笔记事件
    public static Handler delCollectNoteGemHandler;//Gem取消收藏笔记监听事件
    public final static int delCollectNoteGemCode = 0x00029;//Gem取消收藏笔记事件

    public static Handler collectNoteGoodHandler;//Good收藏笔记监听事件
    public final static int collectNoteGoodCode = 0x00030;//Good收藏笔记事件
    public static Handler delCollectNoteGoodHandler;//Good取消收藏笔记监听事件
    public final static int delCollectNoteGoodCode = 0x00031;//Good取消收藏笔记事件

    public static Handler collectNoteNewHandler;//New收藏笔记监听事件
    public final static int collectNoteNewCode = 0x00032;//New收藏笔记事件
    public static Handler delCollectNoteNewHandler;//New取消收藏笔记监听事件
    public final static int delCollectNoteNewCode = 0x00033;//New取消收藏笔记事件

    public static Handler sharedNoteHandler;//分享笔记监听事件
    public final static int sharedNoteCode = 0x00013;//分享笔记事件
    public static Handler delSharedNoteHandler;//取消分享笔记监听事件
    public final static int delSharedNoteCode = 0x00014;//取消分享笔记事件

    public static Handler sharedNoteGropHandler;//Grop分享笔记监听事件
    public final static int sharedNoteGropCode = 0x00034;//Grop分享笔记事件
    public static Handler delSharedNoteGropHandler;//Grop取消分享笔记监听事件
    public final static int delSharedNoteGropCode = 0x00035;//Grop取消分享笔记事件

    public static Handler sharedNoteCollHandler;//Coll分享笔记监听事件
    public final static int sharedNoteCollCode = 0x00042;//Grop分享笔记事件
    public static Handler delSharedNoteCollHandler;//Coll取消分享笔记监听事件
    public final static int delSharedNoteCollCode = 0x00043;//Coll取消分享笔记事件

    public static Handler praisedNoteGemHandler;//Gem点赞笔记监听事件
    public final static int praisedNoteGemCode = 0x00022;//Gem点赞笔记事件
    public static Handler praisedNoteGoodHandler;//Good点赞笔记监听事件
    public final static int praisedNoteGoodCode = 0x00036;//Good点赞笔记事件
    public static Handler praisedNoteNewHandler;//New点赞笔记监听事件
    public final static int praisedNoteNewCode = 0x00037;//New点赞笔记事件

    public static Handler delFileTagHandler;//删除文件夹或者标签监听事件
    public final static int delFileTagCode = 0x00015;//删除文件夹或者标签事件

    public static Handler loginTreeHandler;//三方登录监听事件
    public static Handler loginTreeSucHandler;//三方登录监听事件
    public final static int loginTreeCode = 0x00016;//三方登录

    public static Handler loginHandler;//登录监听事件
    public final static int loginRequestCode = 0x00017;//登录
    public final static int loginSucCode = 0x00018;//登录
    public static Handler registerHandler;//注册监听事件
    public final static int registerCode = 0x00019;//注册
    public final static int loginTreeSucCode = 0x00020;//三方登录成功
    public static Handler setCurViewPagerHandler;//设置共享返回页
    public final static int setCurViewPagerCode = 0x00021;//设置共享返回页

    public static Handler mainLoginHandler;//主界面登录监听事件
    public final static int mainLoginCode = 0x00023;//主界面登录事件
    public static Handler conversationUserDataHandler;//转换聊天用户数据事件
    public final static int conversationUserDataCode = 0x00024;//转换聊天用户数据事件

    public static Handler showDialogHandler;//showDialog事件
    public final static int showDialogCode = 0x00038;//showDialog事件

    public static Handler logoutHandler;//logout
    public final static int logoutCode = 0x00039;//logout事件

    public static Handler feekbackHandler;//feekback
    public final static int feekbackCode = 0x00044;//feekback事件

    public static Handler unReadNumHandler;//unReadNum
    public final static int unReadNumCode = 0x00045;//unReadNum
    public final static int readNumCode = 0x00046;//readNum

    public static Handler loadFrontHandler;//loadFront
    public final static int loadFrontCode = 0x00046;//loadFront
    public final static int loadFrontAnimationsCode = 0x00047;//loadFrontAnimations

    public static Handler onPushHandler;//onPush
    public final static int onPushCode = 0x00048;//onPush
    public final static int offPushCode = 0x00049;//onPush

    public final static int fuliCode = 0x00050;//fuli
    public static Handler autoHandler;//auto
    public final static int autoCode = 0x00051;//auto
    public final static int autoCaoliuCode = 0x00052;//autoCaoliu
    public static Handler onettwozerofourHandler;//onettwozerofour
    public final static int onettwozerofourCode = 0x00051;//onettwozerofour

    public static String TALKISREGISTER = "talkIsRegister";
    public static String TALKISLOGIN = "talkIsLogin";
    public static String LONGCLICKNUM = "longClickNum";

    public static String ISFIRST = "ISFIRST";
    public static String ORDER = "order";
    public static String PAGESIZE = "pageSize";
    public static String FRAGMENT_LIST_DEL = "fragment_list_del";
    public static String CURVIEWPAGER = "curViewPager";

    public static String FUNCTION = "function";
    public static String ORDER_GROUP = "order_group";
    public static String PAGESIZE_GROUP = "pageSize_group";
    public static String ORDER_COLL = "order_coll";
    public static String PAGESIZE_COLL = "pageSize_coll";
    public static String ORDER_GOOD_SHARED = "order_good_shared";
    public static String PAGESIZE_GOOD_SHARED = "pageSize_good_shared";
    public static String ORDER_GEM_SHARED = "order_gem_shared";
    public static String PAGESIZE_GEM_SHARED = "pageSize_gem_shared";
    public static String ORDER_NEW_SHARED = "order_new_shared";
    public static String PAGESIZE_NEW_SHARED = "pageSize_new_shared";

    public static String ISLOGIN = "isLogin";
    public static String UID = "uid";
    public static String NAME = "name";
    public static String TOKEN = "token";
    public static String IMGURL = "imgurl";
    public static String DEFAULTID = "defaultid";
    public static String EMTALKTIME = "EmTalkTime";
    public static String PLATFORM_TYPE = "platform_type";
    public static String PLATFORM_NAME = "platform_name";

    public static Map<String, Object> map;//笔记列表点击监听传值

    private static Context comConext;

    public static byte[] bmpToByteArray(final Bitmap bmp,
                                        final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 20) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 2 * 获取版本号 3 * @return 当前应用的版本号 4
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得屏幕的宽度
     *
     * @param context 上下文
     * @return 屏幕的宽度 单位 像素
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static byte[] getBitmapBytes(Bitmap bitmap, boolean paramBoolean) {
        Bitmap localBitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);
        int i;
        int j;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            i = bitmap.getWidth();
            j = bitmap.getWidth();
        } else {
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
        while (true) {
            localCanvas.drawBitmap(bitmap, new Rect(0, 0, i, j), new Rect(0, 0,
                    0, 80), null);
            if (paramBoolean)
                bitmap.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {

            }
            i = bitmap.getHeight();
            j = bitmap.getHeight();
        }
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 开启activity 带参数
     *
     * @param context
     * @param activity
     * @param bundle
     */
    public static void jumpIntent(Context context, Class<?> activity,
                                  Bundle bundle) {
        Intent intent = new Intent(context, activity);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    /**
     * 判断指定包名的进程是否运行
     *
     * @param context
     * @param context 指定包名
     * @return 是否运行
     */
    public static boolean isRunning(Context context) {
//        ActivityManager am = (ActivityManager) context
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
//        for (RunningAppProcessInfo rapi : infos) {
//            if (rapi.processName.equals(packageName)) {
//                return true;
//            }
//        }
//        return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        String MY_PKG_NAME = "tatait.com.noteex";
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断当前应用程序处于前台还是后台
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            if (!tasks.get(0).topActivity.getPackageName().equals("tatait.com.noteex")) {
                return true;
            }
        }
        return false;
    }

    public static void longClickDeal(Context context, boolean res) {
        SharedPreferencesUtils.setParam(context, CommonUtil.LONGCLICKNUM, 1);
        if (res && CommonUtil.dialogHandler != null) {
            Message message = new Message();
            message.what = CommonUtil.dialogCode;
            CommonUtil.dialogHandler.sendMessage(message);
        }
    }

    static final String[] PERMISSION = new String[]{
            Manifest.permission.READ_CONTACTS,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,//读取权限
            Manifest.permission.WRITE_CALL_LOG,//读取设备信息权限
            Manifest.permission.READ_LOGS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,//联网权限
            Manifest.permission.RECORD_AUDIO,//麦克风权限
            Manifest.permission.CAMERA,//照相机权限
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
    };

    /**
     * 设置Android6.0的权限申请
     */
    public static void setPermissions(Activity mActivity) {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Android 6.0申请权限
            ActivityCompat.requestPermissions(mActivity, PERMISSION, 1);
        } else {
            Log.i("PERMISSIONS", "权限申请ok");
        }
    }

    public static void autoGetData(final Context mContext,String type) {
        if("1".equals(type)) {
            comConext = mContext;
            CommonUtil.autoHandler = mHandler;
            String num = (String) SharedPreferencesUtils.getParam(comConext, "autoNum", "1");
            Message message = new Message();
            message.what = CommonUtil.autoCode;
            message.obj = num;
            CommonUtil.autoHandler.sendMessage(message);
        }else{
            comConext = mContext;
            CommonUtil.autoHandler = mHandler;
            String ok = (String) SharedPreferencesUtils.getParam(comConext, "autoCaoliuIsOk", "yes");
            String num = (String) SharedPreferencesUtils.getParam(comConext, "autoCaoliuNumiu", "1");
            if("yes".equals(ok)){
                SharedPreferencesUtils.setParam(comConext, "autoCaoliuIsOk", "no");
                Message message = new Message();
                message.what = CommonUtil.autoCaoliuCode;
                message.obj = num;
                CommonUtil.autoHandler.sendMessage(message);
            }
        }
    }

    public static void autoRefreshData(final String string) {
        JSONObject json = OnlineConfigAgent.getInstance().getConfigParamsJson(comConext);
        String XLCSign = "";
        try {
            XLCSign = json.getString("XLCSign");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_GET_JUE_JIN_DATA + string + "?XLCSign = " + XLCSign;
        OkHttpClientManager.getAsyn(delDataUrl, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String gsonModel) {
                int ss = Integer.parseInt(string) + 1;
                if (ss > 8) ss = 1;
                SharedPreferencesUtils.setParam(comConext, "autoNum", String.valueOf(ss));
                Log.d("AutoData", ss + "----" + gsonModel);
            }
        });
    }
    public static void autoRefreshCaoliuData(final String string) {
        int ss = Integer.parseInt(string);
        String delDataUrl ="";
        String type ="new";
        if(ss%2 == 0){
            type = "best";
        }else if(ss%3 == 0){
            type = "good";
        }else{
            type = "new";
        }
        if(ss<5){
            delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_ADD_FL_FICTION + "?page=" + string + "&type=" + type;
        }else{
            delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_ADD_FL_PIC + "?page=" + string + "&type=" + type + "_pic";
        }
        OkHttpClientManager.getAsyn(delDataUrl, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                SharedPreferencesUtils.setParam(comConext, "autoCaoliuIsOk", "yes");
                Log.d("AutoCaoliuData", "onError----" + e.getMessage());
            }

            @Override
            public void onResponse(String gsonModel) {
                int ss = Integer.parseInt(string) + 1;
                if (ss > 10) ss = 1;
                SharedPreferencesUtils.setParam(comConext, "autoCaoliuNumiu", String.valueOf(ss));
                SharedPreferencesUtils.setParam(comConext, "autoCaoliuIsOk", "yes");
                Log.d("AutoCaoliuData", ss + "----" + gsonModel);
            }
        });
    }

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.autoCode:
                    CommonUtil.autoRefreshData(msg.obj.toString());
                    break;
                case CommonUtil.autoCaoliuCode:
                    CommonUtil.autoRefreshCaoliuData(msg.obj.toString());
                    break;
            }
        }
    };
}
