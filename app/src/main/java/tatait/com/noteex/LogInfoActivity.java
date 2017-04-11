package tatait.com.noteex;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.onlineconfig.OnlineConfigLog;
import com.umeng.onlineconfig.UmengOnlineConfigureListener;

import org.json.JSONException;
import org.json.JSONObject;

import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.SharedPreferencesUtils;

public class LogInfoActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout back;
    private TextView debug_info;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.debuginfo);
        /**
         *  设置是否对日志信息进行加密, 默认false(不加密).
         */
        MobclickAgent.enableEncrypt(false);//6.0.0版本及以后
        initView();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    //初始化
    private void initView() {
        back = (LinearLayout) findViewById(R.id.debug_info_top_bar).findViewById(R.id.title_layout_back);

        debug_info = (TextView) findViewById(R.id.debug_info_tv);
        back.setOnClickListener(this);
        StringBuffer info = new StringBuffer();
        info.append(CommonUtil.TALKISREGISTER + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.TALKISREGISTER, false) + "\n");
        info.append(CommonUtil.TALKISLOGIN + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.TALKISLOGIN, false) + "\n");
        info.append(CommonUtil.LONGCLICKNUM + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.LONGCLICKNUM, 2) + "\n");
        info.append(CommonUtil.ORDER_GEM_SHARED + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ORDER_GEM_SHARED, 1) + "\n");
        info.append(CommonUtil.PAGESIZE_GEM_SHARED + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.PAGESIZE_GEM_SHARED, 10) + "\n");
        info.append(CommonUtil.ORDER_NEW_SHARED + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ORDER_NEW_SHARED, 10) + "\n");
        info.append(CommonUtil.ISFIRST + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISFIRST, false) + "\n");
        info.append(CommonUtil.ORDER + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ORDER, 1) + "\n");
        info.append(CommonUtil.PAGESIZE + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.PAGESIZE, 10) + "\n");
        info.append(CommonUtil.FRAGMENT_LIST_DEL + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.FRAGMENT_LIST_DEL, 0) + "\n");
        info.append(CommonUtil.CURVIEWPAGER + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.CURVIEWPAGER, 0) + "\n");
        info.append(CommonUtil.ORDER_GROUP + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ORDER_GROUP, 1) + "\n");
        info.append(CommonUtil.PAGESIZE_GROUP + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.PAGESIZE_GROUP, 10) + "\n");
        info.append(CommonUtil.FUNCTION + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.FUNCTION, 1) + "\n");
        info.append(CommonUtil.ORDER_GOOD_SHARED + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ORDER_GOOD_SHARED, 1) + "\n");
        info.append(CommonUtil.PAGESIZE_GOOD_SHARED + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.PAGESIZE_GOOD_SHARED, 10) + "\n");
        info.append(CommonUtil.ISLOGIN + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISLOGIN, false) + "\n");
        info.append(CommonUtil.UID + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.UID, 0) + "\n");
        info.append(CommonUtil.NAME + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.NAME, "未登录") + "\n");
        info.append(CommonUtil.TOKEN + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.TOKEN, "1") + "\n");
        info.append(CommonUtil.IMGURL + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.IMGURL, "") + "\n");
        info.append(CommonUtil.DEFAULTID + " 's value is : " + SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.DEFAULTID, "0") + "\n");

        JSONObject json = OnlineConfigAgent.getInstance().getConfigParamsJson(getApplicationContext());
        String value = "";
        try {
            value = json.getString("1024");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OnlineConfigAgent.getInstance().setOnlineConfigListener(configureListener);
        info.append(" 1024's value is : " + value + "\n");
        debug_info.setText(info);
    }

    UmengOnlineConfigureListener configureListener = new UmengOnlineConfigureListener() {
        @Override
        public void onDataReceived(JSONObject json) {
            // TODO Auto-generated method stub
            OnlineConfigLog.d("OnlineConfig", "json=" + json);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_layout_back:
                finish();
                break;
        }
    }
}
