package tatait.com.noteex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import java.util.Map;

import tatait.com.noteex.model.GsonNoteTipModel;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.SoftKeyboardStateHelper;
import tatait.com.noteex.util.StringUtils;
import tatait.com.noteex.util.ToastUtil;


public class NewNoteActivity extends BaseActivity {
    private TextView back_tv, title_top, category, tag, nid, tid, tagid, op_tv;
    private EditText title, content;
    private ImageView publish_iv, keymap_iv, pic_iv, camela_iv;
    private LinearLayout back, category_ll, keymay_ll, main_ll, tag_ll;
    private RelativeLayout title_layout_rela;
    private Map<String, Object> myMap;
    private int uid;
    private String token;
    private static long DURATION = 300;

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 20) {
            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.curve);
            transition.setDuration(DURATION);

            getWindow().setSharedElementEnterTransition(transition);
            getWindow().setSharedElementExitTransition(transition);

            Fade fade = new Fade();
            fade.setDuration(DURATION);
            getWindow().setEnterTransition(fade);
        }
        overridePendingTransition(R.anim.activity_open, R.anim.activity_stay);

        setContentView(R.layout.activity_publish);

        back = (LinearLayout) findViewById(R.id.activity_publish_title_bar).findViewById(R.id.title_layout_back);
        title_top = (TextView) findViewById(R.id.activity_publish_title_bar).findViewById(R.id.title_layout_title);
        back_tv = (TextView) findViewById(R.id.activity_publish_title_bar).findViewById(R.id.title_layout_back_tv);
        publish_iv = (ImageView) findViewById(R.id.activity_publish_title_bar).findViewById(R.id.title_layout_op_iv);
        title_layout_rela = (RelativeLayout) findViewById(R.id.activity_publish_title_bar).findViewById(R.id.title_layout_rela);

        category_ll = (LinearLayout) findViewById(R.id.activity_publish_category_ll);
        tag_ll = (LinearLayout) findViewById(R.id.activity_publish_tag_ll);
        keymay_ll = (LinearLayout) findViewById(R.id.activity_publish_keymay_ll);
        main_ll = (LinearLayout) findViewById(R.id.activity_publish_ll);

        op_tv = (TextView) findViewById(R.id.title_layout_op_tv);
        category = (TextView) findViewById(R.id.activity_publish_category);
        tag = (TextView) findViewById(R.id.activity_publish_tag);
        nid = (TextView) findViewById(R.id.activity_publish_nid);
        tid = (TextView) findViewById(R.id.activity_publish_tid);
        tagid = (TextView) findViewById(R.id.activity_publish_tagid);

        title = (EditText) findViewById(R.id.activity_publish_title);
        content = (EditText) findViewById(R.id.activity_publish_content);

        keymap_iv = (ImageView) findViewById(R.id.activity_publish_keymap_iv);
        pic_iv = (ImageView) findViewById(R.id.activity_publish_pic_iv);
        camela_iv = (ImageView) findViewById(R.id.activity_publish_camela_iv);
    }

    @Override
    protected void setListener() {
        publish_iv.setOnClickListener(this);
        category_ll.setOnClickListener(this);
        tag_ll.setOnClickListener(this);
        keymap_iv.setOnClickListener(this);
        pic_iv.setOnClickListener(this);
        camela_iv.setOnClickListener(this);
        back.setOnClickListener(this);
        title.setOnClickListener(this);
        content.setOnClickListener(this);
        op_tv.setOnClickListener(this);
        title.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本

            //private int editStart;//光标开始位置
            //private int editEnd;//光标结束位置
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
//                editStart = title.getSelectionStart();
//                editEnd = title.getSelectionEnd();
                if (temp.length() > 0) {
                    publish_iv.setImageResource(R.drawable.main_publish_img);
                    publish_iv.setClickable(true);
                    back_tv.setText("退出编辑");
                } else {
                    publish_iv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.publish_img_btn));
                    publish_iv.setClickable(false);
                }
            }
        });
        content.addTextChangedListener(new TextWatcher() {
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
                    publish_iv.setImageResource(R.drawable.main_publish_img);
                    publish_iv.setClickable(true);
                    back_tv.setText("退出编辑");
                } else {
                    publish_iv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.publish_img_btn));
                    publish_iv.setClickable(false);
                }
            }
        });
        //通过布局高度的变化来判断软键盘的打开和收起事件
        SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(main_ll);
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                keymay_ll.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSoftKeyboardClosed() {
                keymay_ll.setVisibility(View.GONE);
            }
        });
        //设置键盘右下角动作
        title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“Next”键*/
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    content.setFocusable(true);
                    content.setFocusableInTouchMode(true);
                    content.requestFocus();
                    content.requestFocusFromTouch();
                    return true;
                }
                return false;
            }
        });
        content.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“Enter”键*/
                if (actionId == KeyEvent.KEYCODE_ENTER) {
                    ClickSubmit(v);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void processLogic(Bundle savedInstanceState) {
        keymay_ll.setVisibility(View.GONE);//默认键盘拓展是收缩状态

        String type = getIntent().getStringExtra("type");
        uid = (int) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.UID, 0);
        token = (String) SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.TOKEN, "1");
        myMap = CommonUtil.map;
        if (myMap != null) {
            title_top.setText("阅读笔记");//赋值初始标题
            op_tv.setText("编辑");
            title.setText(myMap.get("title") == null ? "" : myMap.get("title").toString());
            content.setText(myMap.get("content") == null ? "" : myMap.get("content").toString());
            category.setText(myMap.get("category") == null ? "" : myMap.get("category").toString());
            tag.setText(StringUtils.isEmpty2(myMap.get("tag") == null ? "" : myMap.get("tag").toString()) ? "无" : myMap.get("tag").toString());
            nid.setText(myMap.get("nid") == null ? "" : myMap.get("nid").toString());
            tid.setText(myMap.get("treeid") == null ? "" : myMap.get("treeid").toString());
            tagid.setText(myMap.get("tagid") == null ? "" : myMap.get("tagid").toString());

            title.setEnabled(false);
            content.setFocusable(false);//内容要能预览
            content.setFocusableInTouchMode(false);
            category.setEnabled(false);
            category_ll.setEnabled(false);
            tag_ll.setEnabled(false);
            if (!StringUtils.isEmpty2(type) && "shared".equals(type)) {//共享区只能读取
                op_tv.setVisibility(View.GONE);
            } else {
                op_tv.setVisibility(View.VISIBLE);
            }
            publish_iv.setVisibility(View.GONE);//发布按钮
        } else {
            title_top.setText("新建笔记");
            category.setText("/默认/");
            tid.setText(SharedPreferencesUtils.getParam(getApplicationContext(), CommonUtil.DEFAULTID, "0").toString());
            publish_iv.setVisibility(View.VISIBLE);
            op_tv.setVisibility(View.GONE);
            publish_iv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.publish_img_btn));
            publish_iv.setClickable(false);
        }
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
                doBack();
                break;
            case R.id.title_layout_op_iv:
                ClickSubmit(v);
                break;
            case R.id.title_layout_op_tv:
                back_tv.setText("退出编辑");
                title_top.setText("编辑笔记");
                title.setEnabled(true);
                title.setSelection(title.length());//将光标移至文字末尾
                content.setFocusable(true);
                content.setFocusableInTouchMode(true);
                content.requestFocus();
                category.setEnabled(true);
                category_ll.setEnabled(true);
                tag_ll.setEnabled(true);
                publish_iv.setVisibility(View.VISIBLE);
                op_tv.setVisibility(View.GONE);
                publish_iv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.publish_img_btn));
                break;
            case R.id.activity_publish_category_ll:
                HideInput(v);
                Intent intent = new Intent(NewNoteActivity.this, NoteTreeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("category", category.getText().toString());
                bundle.putString("back_tv", "选择文件夹");
                bundle.putString("type", "0");
                intent.putExtras(bundle);
                startActivityForResult(intent, CommonUtil.selectTreeRequestCode);
                break;
            case R.id.activity_publish_tag_ll:
                HideInput(v);
                Intent intentTag = new Intent(NewNoteActivity.this, NewNoteTagActivity.class);
                startActivityForResult(intentTag, CommonUtil.selectTagRequestCode);
                break;
            case R.id.activity_publish_keymap_iv:
                HideInput(v);
                break;
            case R.id.activity_publish_pic_iv:
                ToastUtil.show("点击了图片按钮");
                break;
            case R.id.activity_publish_camela_iv:
                ToastUtil.show("点击了相机按钮");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case CommonUtil.selectTreeResultCode:
                String tree_name_data = data.getStringExtra("name");
                String tree_tid_data = data.getStringExtra("tid");
                category.setText(tree_name_data);
                tid.setText(tree_tid_data);
                break;
            case CommonUtil.selectTagResultCode:
                String tag_name_data = data.getStringExtra("name");
                String tag_tid_data = data.getStringExtra("tid");
                tag.setText(tag_name_data);
                tagid.setText(tag_tid_data);
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
        String publishURL = HttpRoute.URL_HEAD + HttpRoute.URL_POST_NOTE;
        if (myMap != null) {
            publishURL = HttpRoute.URL_HEAD + HttpRoute.URL_POST_UPDATE_NOTE;
        }
        CommonUtil.addNoteHandler = mHandler;
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("title", title.getText().toString()),
                new OkHttpClientManager.Param("content", content.getText().toString()),
                new OkHttpClientManager.Param("category", tid.getText().toString()),
                new OkHttpClientManager.Param("tag", tagid.getText().toString()),
                new OkHttpClientManager.Param("nid", nid.getText().toString()),
                new OkHttpClientManager.Param("uid", Integer.valueOf(uid).toString()),
                new OkHttpClientManager.Param("token", token)};

        OkHttpClientManager.postAsyn(publishURL, new OkHttpClientManager.ResultCallback<GsonNoteTipModel>() {
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
                        if (CommonUtil.addNoteHandler != null) {
                            Message message = new Message();
                            message.what = CommonUtil.addOrUptNoteCode;
                            message.obj = gsonModel.getInfo();
                            CommonUtil.addNoteHandler.sendMessage(message);
                        }
                    } else {
                        ToastUtil.show(gsonModel.getInfo());
                    }
                }
            }
        }, params);
    }

    /**
     * 判断是否放弃编辑并返回
     */
    public void doBack() {
        if ((title.getText().toString().length() > 0 || content.getText().toString().length() > 0) && back_tv.getText().equals("退出编辑")) {
            AlertDialog dialog = new AlertDialog(this);
            dialog.builder().setMsg("是否放弃当前编辑,直接退出?").setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).setPositiveButton("退出", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myMap != null) {
                        // 修改笔记状态
                        title_top.setText("阅读笔记");
                        op_tv.setText("编辑");
                        back_tv.setText("返回");
                        title.setEnabled(false);
                        content.setFocusable(false);
                        content.setFocusableInTouchMode(false);
                        category.setEnabled(false);
                        category_ll.setEnabled(false);
                        tag_ll.setEnabled(false);
                        op_tv.setVisibility(View.VISIBLE);
                        publish_iv.setVisibility(View.GONE);
                        HideInput(content);
                    } else {
                        finish();
                    }
                }
            }).show();
        } else {
            finish();
        }
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
            doBack();//判断是否放弃编辑并返回
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
                case CommonUtil.addOrUptNoteCode:
                    ToastUtil.show(msg.obj.toString());
                    setResult(CommonUtil.addSucResultCode);
                    finish();
                    break;
            }
        }
    };
}
