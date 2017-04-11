package tatait.com.noteex;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import com.umeng.message.IUmengCallback;
import com.umeng.message.PushAgent;

import tatait.com.noteex.fragment.SettingFragment;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.ToastUtil;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {
    private LinearLayout title_layout_back;
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        CommonUtil.onPushHandler = mHandler;
        title_layout_back = getViewById(R.id.title_layout_back);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingFragment()).commit();
    }

    @Override
    protected void setListener() {
        title_layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
            switch (msg.what) {
                case CommonUtil.offPushCode:
                    mPushAgent.disable(new IUmengCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtil.show("已关闭推送服务");
                        }
                        @Override
                        public void onFailure(String s, String s1) {
                            ToastUtil.show("关闭推送服务失败，请稍候重试。 参考原因：" + s);
                        }
                    });
                    break;
                case CommonUtil.onPushCode:
                    mPushAgent.enable(new IUmengCallback() {
                        @Override
                        public void onSuccess() {
                            ToastUtil.show("已开启推送服务");
                        }
                        @Override
                        public void onFailure(String s, String s1) {
                            ToastUtil.show("开启推送服务失败，请稍候重试。 参考原因：" + s);
                        }
                    });
                    break;
            }
        }
    };
}
