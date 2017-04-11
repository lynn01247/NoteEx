package tatait.com.noteex;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import tatait.com.noteex.model.GsonNoteTipModel;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.StringUtils;
import tatait.com.noteex.util.ToastUtil;

/**
 * 意见反馈
 */
public class FeekBackActivity extends BaseActivity implements OnClickListener {

    private EditText phone_edi;
    private EditText content_edi;
    protected Button feekback_activity_submit;
    private final String mPageName = "FeekBackActivity";

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_feek_back);
        ((TextView) findViewById(R.id.feekback_activity_title_layout)
                .findViewById(R.id.title_layout_title))
                .setText("建议和反馈");
        ((TextView) findViewById(R.id.feekback_activity_title_layout)
                .findViewById(R.id.title_layout_back_tv))
                .setText("返回");
        feekback_activity_submit = (Button) findViewById(R.id.feekback_activity_submit);
        phone_edi = (EditText) findViewById(R.id.feekback_activity_phone_edi);
        content_edi = (EditText) findViewById(R.id.feekback_activity_content_edi);
    }

    @Override
    protected void setListener() {
        findViewById(R.id.feekback_activity_title_layout).findViewById(
                R.id.title_layout_back).setOnClickListener(this);
        findViewById(R.id.feekback_activity_submit).setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }

    /**
     * 各种 case
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回按钮
            case R.id.title_layout_back:
                finish();
                break;
            //提交反馈
            case R.id.feekback_activity_submit:
                if (!(Boolean) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.ISLOGIN, false)) {
                    ToastUtil.show("还未登录, 请先登录再进行操作");
                    startActivity(new Intent(FeekBackActivity.this, LoginActivity.class));
                } else {
                    String content = content_edi.getText().toString();
                    if (StringUtils.isEmpty2(content)) {
                        ToastUtil.show("请填写您的宝贵意见...");
                        return;
                    }
                    AlertDialog dialog = new AlertDialog(this);
                    dialog.builder().setMsg("是否提交反馈(意见)?").setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestSubmitData();
                        }
                    }).show();
                }
                break;
        }
    }

    /**
     * 提交反馈
     */
    private void requestSubmitData() {
        CommonUtil.feekbackHandler = mHandler;
        showLoadingDialog();
        int uid = (int) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.UID, 0);
        String token = (String) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.TOKEN, "1");
        String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_POST_FEEK_BACK;
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("contact", phone_edi.getText().toString()),
                new OkHttpClientManager.Param("content", content_edi.getText().toString()),
                new OkHttpClientManager.Param("uid", Integer.valueOf(uid).toString()),
                new OkHttpClientManager.Param("token", token)};
        OkHttpClientManager.postAsyn(delDataUrl, new OkHttpClientManager.ResultCallback<GsonNoteTipModel>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                dismissLoadingDialog();
            }

            @Override
            public void onResponse(GsonNoteTipModel gsonModel) {
                dismissLoadingDialog();
                if (gsonModel != null) {
                    if (Integer.parseInt(gsonModel.getCode()) == 1) {
                        if (CommonUtil.feekbackHandler != null) {
                            Message message = new Message();
                            message.what = CommonUtil.feekbackCode;
                            CommonUtil.feekbackHandler.sendMessage(message);
                        }
                    } else {
                        ToastUtil.show(gsonModel.getInfo());
                    }
                }
            }
        }, params);
    }

    /**
     * 提交反馈的回调函数，用于异步刷新主界面的UI或者弹出提示
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //隐藏标题
                case CommonUtil.feekbackCode:
                    ToastUtil.show("感谢您的反馈(意见)，小编会立即核实处理");
                    finish();
                    break;
            }
        }
    };
}