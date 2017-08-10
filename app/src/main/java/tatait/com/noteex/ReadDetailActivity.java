package tatait.com.noteex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;

import java.security.NoSuchAlgorithmException;

import tatait.com.noteex.model.Constants;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.ImageViewDialog;
import tatait.com.noteex.util.MD5;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.ToastUtil;

/**
 * 小说详情
 */
public class ReadDetailActivity extends BaseActivity {
    private LinearLayout mission_detail_layout_back;
    private TextView title, time, content, mission_layout_right;
    private String timeStr, titleStr, contentStr;
    private InterstitialAD iad;
    private ViewGroup bannerContainer;
    BannerView bv;
    private String vipId;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mission_detail);
        mission_detail_layout_back = getViewById(R.id.mission_detail_layout_back);
        title = getViewById(R.id.mission_detail_layout_title);
        time = getViewById(R.id.mission_detail_layout_time);
        content = getViewById(R.id.mission_detail_layout_content);
        mission_layout_right = getViewById(R.id.mission_layout_right);
        bannerContainer = getViewById(R.id.bannerContainer);
        boolean isVip = (boolean) SharedPreferencesUtils.getParam(getApplicationContext(), "isVip", false);
        if (!isVip) {
            this.bv = new BannerView(this, ADSize.BANNER, Constants.APPID, Constants.BannerPosID);
            // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
            // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
            bv.setRefresh(30);
            bv.setADListener(new AbstractBannerADListener() {

                @Override
                public void onNoAD(int arg0) {
                    Log.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
                }

                @Override
                public void onADReceiv() {
                    Log.i("AD_DEMO", "ONBannerReceive");
                }
            });
            bannerContainer.addView(bv);
            this.bv.loadAD();
            //
            getIAD().setADListener(new AbstractInterstitialADListener() {

                @Override
                public void onNoAD(int arg0) {
                    Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
                }

                @Override
                public void onADReceive() {
                    Log.i("AD_DEMO", "onADReceive");
                    iad.show();
                }
            });
            iad.loadAD();
            //
            showAsPopup();
            mission_layout_right.setVisibility(View.VISIBLE);
        }else{
            mission_layout_right.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setListener() {
        mission_detail_layout_back.setOnClickListener(this);
        mission_layout_right.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        titleStr = getIntent().getStringExtra("title");
        timeStr = getIntent().getStringExtra("time");
        contentStr = getIntent().getStringExtra("content");
        title.setText(titleStr);
        time.setText(timeStr);
        content.setText(contentStr);
    }

    private InterstitialAD getIAD() {
        if (iad == null) {
            iad = new InterstitialAD(this, Constants.APPID, Constants.InterteristalPosID);
        }
        return iad;
    }

    private void showAsPopup() {
        getIAD().setADListener(new AbstractInterstitialADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
            }

            @Override
            public void onADReceive() {
                iad.showAsPopupWindow();
            }
        });
        iad.loadAD();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            //返回
            case R.id.mission_detail_layout_back:
                finish();
                break;
            case R.id.mission_layout_right:
                if (!(Boolean) SharedPreferencesUtils.getParam(mApp, CommonUtil.ISLOGIN, false)) {
                    ToastUtil.show("还未登录, 请先登录再进行操作");
                    startActivity(new Intent(ReadDetailActivity.this, LoginActivity.class));
                } else {
                    int uid = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.UID, 0);
                    try {
                        vipId = MD5.MD5_16bit(uid + "");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    AlertDialog dialog = new AlertDialog(ReadDetailActivity.this);
                    dialog.builder().setTitle("1元开通VIP即可关闭广告,永久保持哦").setMsg("请截图或者使用支付宝扫描二维码进行付款，付款后将您的专属ID发送到客服QQ（2443938389）激活").setMsgId(vipId).setNegativeButton("获取二维码", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageViewDialog dialog = new ImageViewDialog(ReadDetailActivity.this);
                            dialog.builder().setTitle("无需加好友，扫描即付").setImg(R.drawable.vip).setNegativeButton("知道了", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();
                        }
                    }).setPositiveButton("继续看广告吧", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bannerContainer.removeAllViews();
        if (bv != null) {
            bv.destroy();
            bv = null;
        }
    }
}