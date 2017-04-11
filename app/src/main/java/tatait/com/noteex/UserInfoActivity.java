package tatait.com.noteex;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.StringUtils;

public class UserInfoActivity extends BaseActivity implements OnClickListener {

    private TextView wechat, QQ, sina;
    private LinearLayout back;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.user_info);
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
        back = (LinearLayout) findViewById(R.id.user_info_top_bar).findViewById(R.id.title_layout_back);

        wechat = (TextView) findViewById(R.id.user_info_wechat);
        QQ = (TextView) findViewById(R.id.user_info_qq);
        sina = (TextView) findViewById(R.id.user_info_sina);

        back.setOnClickListener(this);
        String type = (String) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.PLATFORM_TYPE, "");
        String name = (String) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.PLATFORM_NAME, "");
        if (!StringUtils.isEmpty2(type)) {
            String nameStr = "当前登录账号是: " + name;
            if ("WEIXIN".equals(type)) {
                wechat.setText(nameStr);
                QQ.setText("未登录");
                sina.setText("未登录");
            } else if ("SINA".equals(type)) {
                wechat.setText("未登录");
                QQ.setText("未登录");
                sina.setText(nameStr);
            } else if ("QQ".equals(type)) {
                wechat.setText("未登录");
                QQ.setText(nameStr);
                sina.setText("未登录");
            } else {
                wechat.setText("未登录");
                QQ.setText("未登录");
                sina.setText("未登录");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_layout_back:
                finish();
                break;
        }
    }
}
