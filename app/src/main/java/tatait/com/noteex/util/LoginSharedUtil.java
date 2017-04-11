package tatait.com.noteex.util;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import tatait.com.noteex.R;

/**
 * @author lynn
 */
public class LoginSharedUtil {
    /**
     * 弹出分享界面
     *
     * @param mActivity
     * @param copy
     */
    public static void shredAction(final Activity mActivity, final String copy, final String APKUrl) {
        final UMShareListener mShareListener = new CustomShareListener(mActivity);
        /*增加自定义按钮的分享面板*/
        ShareAction mShareAction = new ShareAction(mActivity).setDisplayList(
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL)
                .addButton("umeng_sharebutton_copy", "umeng_sharebutton_copy", "umeng_socialize_copy", "umeng_socialize_copy")
                .addButton("umeng_sharebutton_copyurl", "umeng_sharebutton_copyurl", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (snsPlatform.mShowWord.equals("umeng_sharebutton_copy")) {
                            // 从API11开始android推荐使用android.content.ClipboardManager
                            // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                            ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                            // 将文本内容放到系统剪贴板里。
                            cm.setText(copy + APKUrl);
                            ToastUtil.show("文本已成功复制到剪切板");
                        } else if (snsPlatform.mShowWord.equals("umeng_sharebutton_copyurl")) {
                            // 从API11开始android推荐使用android.content.ClipboardManager
                            // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
                            ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                            // 将文本内容放到系统剪贴板里。
                            cm.setText(APKUrl);
                            ToastUtil.show("链接已成功复制到剪切板");
                        } else if (share_media.equals(SHARE_MEDIA.QQ) || share_media.equals(SHARE_MEDIA.SINA) || share_media.equals(SHARE_MEDIA.WEIXIN) || share_media.equals(SHARE_MEDIA.WEIXIN_CIRCLE)) {//微信QQ新浪的平台，分享的图片需要设置缩略图
                            UMImage thumb = new UMImage(mActivity, R.drawable.thumb);
                            UMWeb web = new UMWeb("http://sj.qq.com/myapp/detail.htm?apkName=com.hwl.huwolai");
                            web.setTitle("来自NoteEx应用");//标题
                            web.setThumb(thumb);//缩略图
                            web.setDescription(copy);//描述

                            new ShareAction(mActivity).withText(copy + APKUrl)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .withMedia(web)
                                    .share();
//                            image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
//                            image.compressStyle = UMImage.CompressStyle.QUALITY;//质量压缩，适合长图的分享
//                            压缩格式设置：
//                            image.compressFormat = Bitmap.CompressFormat.PNG;//用户分享透明背景的图片可以设置这种方式，但是qq好友，微信朋友圈，不支持透明背景图片，会变成黑色
                        } else {
                            new ShareAction(mActivity).withText(copy + APKUrl)
                                    .setPlatform(share_media)
                                    .setCallback(mShareListener)
                                    .share();
                        }
                    }
                });
        ShareBoardConfig config = new ShareBoardConfig();
        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR); // 圆角背景
        mShareAction.open(config);
    }

    private static class CustomShareListener implements UMShareListener {

        private WeakReference<Activity> mActivity;

        private CustomShareListener(Activity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                ToastUtil.show(platform + " 收藏成功");
            } else {
                if (platform != SHARE_MEDIA.SMS && platform != SHARE_MEDIA.EMAIL) {
                    ToastUtil.show(platform + " 分享成功");
                }
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.SMS && platform != SHARE_MEDIA.EMAIL) {
                if (t != null) {
                    Log.d("throw", "throw:" + t.getMessage());
                    if (t.getMessage().contains("点击查看错误")) {
                        ToastUtil.show(platform + " 分享失败, " + t.getMessage().substring(0, t.getMessage().indexOf("点击查看错误")));
                    } else {
                        ToastUtil.show(platform + " 分享失败, " + t.getMessage());
                    }
                } else {
                    ToastUtil.show(platform + " 分享失败");
                }
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtil.show(platform + " 分享取消");
        }
    }
}
