package tatait.com.noteex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import tatait.com.noteex.model.GsonNoteTipModel;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.ToastUtil;


public class NewNoteTreeActivity extends BaseActivity {
    private TextView title_top, category, tid, op_tv;
    private EditText title;
    private LinearLayout back, category_ll;
    private int uid;
    private String token;
    private static long DURATION = 300;

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 20) {
            Slide slide = new Slide();
            slide.setDuration(DURATION);
            slide.setSlideEdge(Gravity.BOTTOM);

            Fade fade = new Fade();
            fade.setDuration(DURATION);

            getWindow().setEnterTransition(slide);
            getWindow().setExitTransition(fade);
        }
        setContentView(R.layout.activity_add_tree);

        back = (LinearLayout) findViewById(R.id.activity_add_tree_title_bar).findViewById(R.id.title_layout_back);
        title_top = (TextView) findViewById(R.id.activity_add_tree_title_bar).findViewById(R.id.title_layout_title);
        op_tv = (TextView) findViewById(R.id.activity_add_tree_title_bar).findViewById(R.id.title_layout_op_tv);
        category_ll = (LinearLayout) findViewById(R.id.activity_add_tree_category_ll);
        category = (TextView) findViewById(R.id.activity_add_tree_category);
        tid = (TextView) findViewById(R.id.activity_add_tree_tid);
        title = (EditText) findViewById(R.id.activity_add_tree_title);
    }

    @Override
    protected void setListener() {
        op_tv.setOnClickListener(this);
        category_ll.setOnClickListener(this);
        back.setOnClickListener(this);
        title.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() > 0) {
                    op_tv.setAlpha(1f);
                    op_tv.setClickable(true);
                } else {
                    op_tv.setAlpha(0.1f);
                    ;
                    op_tv.setClickable(false);
                }
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        uid = (int) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.UID, 0);
        token = (String) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.TOKEN, "1");
        title_top.setText("新建文件夹");
        category.setText("/根文件夹/");
        op_tv.setVisibility(View.VISIBLE);
        op_tv.setText("完成");
        op_tv.setAlpha(0.1f);
        op_tv.setClickable(false);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_layout_back:
                finish();
                break;
            case R.id.title_layout_op_tv:
                ClickSubmit(v);
                break;
            case R.id.activity_add_tree_category_ll:
                HideInput(v);
                Intent intent = new Intent(NewNoteTreeActivity.this, NoteTreeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("category", category.getText().toString());
                bundle.putString("back_tv", "新建文件夹");
                bundle.putString("type", "1");
                intent.putExtras(bundle);
                startActivityForResult(intent, CommonUtil.selectTreeRequestCode);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case CommonUtil.selectTreeResultCode:
                String name_data = data.getStringExtra("name");
                String tid_data = data.getStringExtra("tid");
                category.setText(name_data);
                tid.setText(tid_data);
                break;
        }
    }

    /**
     * 点击发布按钮
     *
     * @param view
     */
    public void ClickSubmit(View view) {
        HideInput(view);//隐藏键盘
        AlertDialog dialog = new AlertDialog(this);
        dialog.builder().setMsg("是否提交保存?").setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).setPositiveButton("提交", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        }).show();
    }

    /**
     * 提交保存到服务器
     */
    public void Submit() {
        showLoadingDialog();
        String add_treeURL = HttpRoute.URL_HEAD + HttpRoute.URL_POST_NOTE_TREE;
        CommonUtil.addNoteTreeHandler = mHandler;
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("name", title.getText().toString()),
                new OkHttpClientManager.Param("pid", tid.getText().toString()),
                new OkHttpClientManager.Param("uid", Integer.valueOf(uid).toString()),
                new OkHttpClientManager.Param("token", token)};

        OkHttpClientManager.postAsyn(add_treeURL, new OkHttpClientManager.ResultCallback<GsonNoteTipModel>() {
            @Override
            public void onError(Request request, Exception e) {
                dismissLoadingDialog();
                ToastUtil.show("提交失败, 信息" + e.getMessage());
            }

            @Override
            public void onResponse(GsonNoteTipModel gsonModel) {
                dismissLoadingDialog();
                if (gsonModel != null) {
                    ToastUtil.show(gsonModel.getInfo());
                    if (Integer.parseInt(gsonModel.getCode()) == 1) {
                        if (CommonUtil.addNoteTreeHandler != null) {
                            Message message = new Message();
                            message.what = CommonUtil.addOrUptNoteTreeCode;
                            message.obj = gsonModel.getInfo();
                            CommonUtil.addNoteTreeHandler.sendMessage(message);
                        }
                    } else {
                        ToastUtil.show(gsonModel.getInfo());
                    }
                }
            }
        }, params);
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public void HideInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * nav_setting 回调
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.addOrUptNoteTreeCode:
                    if (CommonUtil.refreshDataFileFragHandler != null) {
                        Message message = new Message();
                        message.what = CommonUtil.refreshFileFragDataCode;
                        message.obj = msg.obj.toString();
                        CommonUtil.refreshDataFileFragHandler.sendMessage(message);
                    }
                    finish();
                    break;
            }
        }
    };
}
