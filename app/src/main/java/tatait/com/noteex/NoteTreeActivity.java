package tatait.com.noteex;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tatait.com.noteex.adapter.TreeAdapter;
import tatait.com.noteex.model.GsonNoteTreeModel;
import tatait.com.noteex.model.TreeModel;
import tatait.com.noteex.tree.Node;
import tatait.com.noteex.tree.TreeListViewAdapter;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.SharedPreferencesUtils;


public class NoteTreeActivity extends BaseActivity {
    private TextView title_top, title, back_tv, op_tv;
    private LinearLayout back;
    private List<TreeModel> mDatas = new ArrayList<>();
    private ListView mTree;
    private TreeListViewAdapter mAdapter;
    private int uid;
    private String token;
    private static long DURATION = 300;
    private String treeType = "0";

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
        setContentView(R.layout.note_tree);

        back = (LinearLayout) findViewById(R.id.note_tree_title_bar).findViewById(R.id.title_layout_back);
        back_tv = (TextView) findViewById(R.id.note_tree_title_bar).findViewById(R.id.title_layout_back_tv);
        title_top = (TextView) findViewById(R.id.note_tree_title_bar).findViewById(R.id.title_layout_title);
        op_tv = (TextView) findViewById(R.id.note_tree_title_bar).findViewById(R.id.title_layout_op_tv);
        title = (TextView) findViewById(R.id.note_tree_title);
        mTree = (ListView) findViewById(R.id.note_tree);
    }

    @Override
    protected void setListener() {
        back.setOnClickListener(this);
    }


    @Override
    protected void processLogic(Bundle savedInstanceState) {
        uid = (int) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.UID, 0);
        token = (String) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.TOKEN, "1");
        String category = getIntent().getExtras().get("category") == null ? "" : getIntent().getExtras().get("category").toString();
        String back = getIntent().getExtras().get("back_tv") == null ? "" : getIntent().getExtras().get("back_tv").toString();
        treeType = getIntent().getExtras().get("type") == null ? "0" : getIntent().getExtras().get("type").toString();
        back_tv.setText(back);
        if (category == null || category == "") {
            category = "根文件夹";
        }
        title.setText(category);
        getData();
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
        }
    }

    public void getData() {
        CommonUtil.selectTreeHandler = mHandler;
        showLoadingDialog();
        String getDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_GET_NOTE_TREE + "?uid=" + uid + "&token=" + token;
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
                    } else {
                        mDatas.add(new TreeModel(1, 0, "默认"));
                    }
                    try {
                        mAdapter = new TreeAdapter<>(mTree, getApplicationContext(), mDatas, 2);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                        @Override
                        public void onClick(Node node, int position) {
                            if (treeType.equals("1") && node.getpId() != 0) {
                                AlertDialog dialog = new AlertDialog(NoteTreeActivity.this);
                                dialog.builder().setMsg("文件夹层级数不能大于两层,请选择一级文件夹进行操作!").setNegativeButton("知道了", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).show();
                            } else if (CommonUtil.selectTreeHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.selectTreeCode;
                                Map<String, String> map = new HashMap<>();
                                map.put("tid", Integer.valueOf(node.getId()).toString());
                                map.put("name", node.getName());
                                message.obj = map;
                                CommonUtil.selectTreeHandler.sendMessage(message);
                            }
                        }

                    });
                    mTree.setAdapter(mAdapter);
                }
            }
        });
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.selectTreeCode:
                    Map<String, String> map = (Map<String, String>) msg.obj;
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("tid", map.get("tid"));
                    bundle.putString("name", map.get("name"));
                    intent.putExtras(bundle);
                    setResult(CommonUtil.selectTreeResultCode, intent);
                    finish();
                    break;
            }
        }
    };
}
