package tatait.com.noteex;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;

import tatait.com.noteex.model.Constants;
import tatait.com.noteex.util.SharedPreferencesUtils;

/**
 * 小说详情
 */
public class ReadDetailActivity extends BaseActivity {
    private LinearLayout mission_detail_layout_back;
    private TextView title, time, content,mission_layout_right;
    private String timeStr, titleStr, contentStr;
    private InterstitialAD iad;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mission_detail);
        mission_detail_layout_back = getViewById(R.id.mission_detail_layout_back);
        title = getViewById(R.id.mission_detail_layout_title);
        time = getViewById(R.id.mission_detail_layout_time);
        content = getViewById(R.id.mission_detail_layout_content);
        mission_layout_right = getViewById(R.id.mission_layout_right);
        boolean isVip = (boolean) SharedPreferencesUtils.getParam(getApplicationContext(),"isVip",false);
        if(!isVip) {
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
                if (iad != null) {
                    iad.closePopupWindow();
                }
                break;
        }
    }
}