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
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tatait.com.noteex.adapter.TreeAdapter;
import tatait.com.noteex.model.GsonNoteTipModel;
import tatait.com.noteex.model.GsonNoteTreeModel;
import tatait.com.noteex.model.TreeModel;
import tatait.com.noteex.tree.Node;
import tatait.com.noteex.tree.TreeListViewAdapter;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.ToastUtil;


public class NewNoteTagActivity extends BaseActivity {
    private TextView title_top, tid, op_tv;
    private EditText title;
    private LinearLayout back, list_ll;
    private ListView list_tag;
    private List<TreeModel> mDatas = new ArrayList<>();
    private TreeListViewAdapter mAdapter;
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
        setContentView(R.layout.activity_add_tag);
        back = (LinearLayout) findViewById(R.id.activity_add_tag_title_bar).findViewById(R.id.title_layout_back);
        title_top = (TextView) findViewById(R.id.activity_add_tag_title_bar).findViewById(R.id.title_layout_title);
        op_tv = (TextView) findViewById(R.id.activity_add_tag_title_bar).findViewById(R.id.title_layout_op_tv);
        list_ll = (LinearLayout) findViewById(R.id.activity_add_tag_list_ll);
        list_tag = (ListView) findViewById(R.id.list_tag);
        tid = (TextView) findViewById(R.id.activity_add_tag_tid);
        title = (EditText) findViewById(R.id.activity_add_tag_title);
    }

    @Override
    protected void setListener() {
        op_tv.setOnClickListener(this);
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
        title_top.setText("新建标签");
        op_tv.setVisibility(View.VISIBLE);
        op_tv.setText("完成");
        op_tv.setAlpha(0.1f);
        op_tv.setClickable(false);
        getData();
    }

    public void getData() {
        CommonUtil.selectTagHandler = mHandler;
        showLoadingDialog();
        String getDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_GET_NOTE_TAG + "?uid=" + uid + "&token=" + token;
        OkHttpClientManager.getAsyn(getDataUrl, new OkHttpClientManager.ResultCallback<GsonNoteTreeModel>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                dismissLoadingDialog();
            }

            @Override
            public void onResponse(GsonNoteTreeModel gsonModel) {
                dismissLoadingDialog();
                if (gsonModel != null) {
                    dismissLoadingDialog();
                    mDatas.clear();
                    if (gsonModel.getData() != null && gsonModel.getData().size() > 0) {
                        for (int i = 0; i < gsonModel.getData().size(); i++) {
                            int par = gsonModel.getData().get(i).getPid();
                            int tid = gsonModel.getData().get(i).getTid();
                            String name = gsonModel.getData().get(i).getName();
                            mDatas.add((new TreeModel(tid, par, name)));
                            if (gsonModel.getData() != null && gsonModel.getData().get(i).getSubs() != null && gsonModel.getData().get(i).getSubs().size() > 0) {
                                for (int ii = 0; ii < gsonModel.getData().get(i).getSubs().size(); ii++) {
                                    int par_sub = gsonModel.getData().get(i).getSubs().get(ii).getPid();
                                    int tid_sub = gsonModel.getData().get(i).getSubs().get(ii).getTid();
                                    String name_sub = gsonModel.getData().get(i).getSubs().get(ii).getName();
                                    mDatas.add((new TreeModel(tid_sub, par_sub, name_sub)));
                                }
                            }
                        }
                    }
                    try {
                        mAdapter = new TreeAdapter<>(list_tag, getApplicationContext(), mDatas, 1);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                        @Override
                        public void onClick(Node node, int position) {
                            if (CommonUtil.selectTagHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.selectTagCode;
                                Map<String, String> map = new HashMap<>();
                                map.put("tid", Integer.valueOf(node.getId()).toString());
                                map.put("name", node.getName());
                                message.obj = map;
                                CommonUtil.selectTagHandler.sendMessage(message);
                            }
                        }
                    });
                    list_tag.setAdapter(mAdapter);
                }
            }
        });
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
        String add_treeURL = HttpRoute.URL_HEAD + HttpRoute.URL_POST_NOTE_TAG;
        CommonUtil.addNoteTagHandler = mHandler;
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("name", title.getText().toString()),
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
                        if (CommonUtil.addNoteTagHandler != null) {
                            Message message = new Message();
                            message.what = CommonUtil.addOrUptNoteTagCode;
                            message.obj = gsonModel.getInfo();
                            CommonUtil.addNoteTagHandler.sendMessage(message);
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
     * 回调
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.addOrUptNoteTagCode:
                    if (CommonUtil.refreshDataFileFragHandler != null) {
                        Message message = new Message();
                        message.what = CommonUtil.refreshFileFragDataCode;
                        message.obj = msg.obj.toString();
                        CommonUtil.refreshDataFileFragHandler.sendMessage(message);
                    }
                    finish();
                    break;
                case CommonUtil.selectTagCode:
                    Map<String, String> map = (Map<String, String>) msg.obj;
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("tid", map.get("tid"));
                    bundle.putString("name", map.get("name"));
                    intent.putExtras(bundle);
                    setResult(CommonUtil.selectTagResultCode, intent);
                    finish();
                    break;
            }
        }
    };
}
