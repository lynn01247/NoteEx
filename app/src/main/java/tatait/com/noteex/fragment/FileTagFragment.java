package tatait.com.noteex.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

import tatait.com.noteex.LoginActivity;
import tatait.com.noteex.MainActivity;
import tatait.com.noteex.NewNoteTagActivity;
import tatait.com.noteex.NewNoteTreeActivity;
import tatait.com.noteex.NoteByGroupActivity;
import tatait.com.noteex.R;
import tatait.com.noteex.adapter.TreeAdapter;
import tatait.com.noteex.model.GsonNoteTipModel;
import tatait.com.noteex.model.GsonNoteTreeModel;
import tatait.com.noteex.model.TreeModel;
import tatait.com.noteex.tree.Node;
import tatait.com.noteex.tree.TreeListViewAdapter;
import tatait.com.noteex.util.ActionSheetDialog;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CircleImageView;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.PopupDialog;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.ToastUtil;

/**
 * 分组·标签
 */
public class FileTagFragment extends BaseFragment implements View.OnClickListener {
    private List<TreeModel> mDatas = new ArrayList<>();
    private ListView mTree;
    private TreeListViewAdapter mAdapter;
    private int uid, del_tag;
    private String token;
    private Context mContext;

    private RelativeLayout fun_rl1, fun_rl2, fragment_list_tip;
    private LinearLayout list_no_data_ll;
    private ImageView file_iv, tag_iv;
    private CircleImageView fragment_list_del;
    private PopupDialog dialog;

    private FragmentManager fm;
    private String strDefault;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_list);

        mTree = getViewById(R.id.id_tree);
        list_no_data_ll = getViewById(R.id.list_no_data_ll);
        fragment_list_tip = getViewById(R.id.fragment_list_tip);
        fragment_list_del = getViewById(R.id.fragment_list_del);
        mContext = getActivity();
        fm = getFragmentManager();

        dialog = new PopupDialog(mContext, R.layout.popup_window_tree).builder();
        View view = dialog.getLayoutView();
        // 获取自定义Dialog布局中的控件
        fun_rl1 = (RelativeLayout) view.findViewById(R.id.fun_rl1);
        fun_rl2 = (RelativeLayout) view.findViewById(R.id.fun_rl2);
        file_iv = (ImageView) view.findViewById(R.id.file_iv);
        tag_iv = (ImageView) view.findViewById(R.id.tag_iv);
    }

    @Override
    protected void setListener() {
        fun_rl1.setOnClickListener(this);
        fun_rl2.setOnClickListener(this);
        fragment_list_del.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        initDatas();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

//    @Override
//    protected void onLazyLoadOnce() {
//        refreshGetData();
//    }

    public void refreshGetData() {
        if(uid != 0){
            if ((int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FUNCTION, 1) == 1) {
                getFileData();
            } else {
                getTagData();
            }
        }
    }

    /**
     * 获取文件夹数据
     */
    public void getFileData() {
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

                    mTree.setVisibility(View.VISIBLE);
                    list_no_data_ll.setVisibility(View.GONE);

                    if (gsonModel.getData() != null && gsonModel.getData().size() > 0) {
                        for (int i = 0; i < gsonModel.getData().size(); i++) {
                            int count = gsonModel.getData().get(i).getCount();
                            int countSum = gsonModel.getData().get(i).getCount();
                            if (gsonModel.getData() != null && gsonModel.getData().get(i).getSubs() != null && gsonModel.getData().get(i).getSubs().size() > 0) {
                                for (int ii = 0; ii < gsonModel.getData().get(i).getSubs().size(); ii++) {
                                    int par_sub = gsonModel.getData().get(i).getSubs().get(ii).getPid();
                                    int tid_sub = gsonModel.getData().get(i).getSubs().get(ii).getTid();
                                    int count_sub = gsonModel.getData().get(i).getSubs().get(ii).getCount();
                                    countSum += count_sub;
                                    String name_sub = gsonModel.getData().get(i).getSubs().get(ii).getName();
                                    mDatas.add((new TreeModel(tid_sub, par_sub, name_sub + "(" + count_sub + ")")));
                                }
                            }
                            int par = gsonModel.getData().get(i).getPid();
                            int tid = gsonModel.getData().get(i).getTid();
                            String name = gsonModel.getData().get(i).getName();
                            mDatas.add((new TreeModel(tid, par, name + "(" + count + "/" + countSum + ")")));
                        }
                    } else {
                        mDatas.add(new TreeModel(1, 0, strDefault));
                    }
                    try {
                        mAdapter = new TreeAdapter<>(mTree, mContext, mDatas, 2);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                        @Override
                        public void onClick(Node node, int position) {
                            Intent intentTag = new Intent(getContext(), NoteByGroupActivity.class);
                            int id = node.getId();
                            String name = node.getName();
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", id);
                            bundle.putString("name", name);
                            bundle.putString("type", "file");
                            intentTag.putExtras(bundle);
                            startActivity(intentTag);
                        }

                    });
                    mTree.setAdapter(mAdapter);
                    mTree.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            itemLongClick(position);
                            return true;
                        }
                    });
                }
            }
        });
    }

    /**
     * 获取标签数据
     */
    public void getTagData() {
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
                    mDatas.clear();
                    if (gsonModel.getData() != null && gsonModel.getData().size() > 0) {

                        mTree.setVisibility(View.VISIBLE);
                        list_no_data_ll.setVisibility(View.GONE);

                        for (int i = 0; i < gsonModel.getData().size(); i++) {
                            int count = gsonModel.getData().get(i).getCount();
                            int tid = gsonModel.getData().get(i).getTid();
                            String name = gsonModel.getData().get(i).getName();
                            mDatas.add((new TreeModel(tid, 0, name + "(" + count + ")")));
                        }
                    }
                    try {
                        mAdapter = new TreeAdapter<>(mTree, mContext, mDatas, 1);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                        @Override
                        public void onClick(Node node, int position) {
                            Intent intentTag = new Intent(getContext(), NoteByGroupActivity.class);
                            int id = node.getId();
                            String name = node.getName();
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", id);
                            bundle.putString("name", name);
                            bundle.putString("type", "tag");
                            intentTag.putExtras(bundle);
                            startActivity(intentTag);
                        }

                    });
                    mTree.setAdapter(mAdapter);
                    mTree.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                            itemLongClick(position);
                            return true;
                        }
                    });
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initDatas() {
        mContext = getActivity();
        CommonUtil.refreshDataFileFragHandler = mHandler;

        uid = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.UID, 0);
        token = (String) SharedPreferencesUtils.getParam(mApp, CommonUtil.TOKEN, "1");
        strDefault = "";
        if ((int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FUNCTION, 1) == 1) {
            file_iv.setVisibility(View.VISIBLE);
            tag_iv.setVisibility(View.GONE);
        } else {
            file_iv.setVisibility(View.GONE);
            tag_iv.setVisibility(View.VISIBLE);
        }

        if (!(Boolean) SharedPreferencesUtils.getParam(mApp, CommonUtil.ISLOGIN, false)) {
            strDefault = "默认(登录后才可同步获取个人数据)";
            fragment_list_tip.setVisibility(View.GONE);//提示不可见
        } else {
            strDefault = "默认(0/0)";
            del_tag = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FRAGMENT_LIST_DEL, 0);
            if (del_tag == 0) {
                fragment_list_tip.setVisibility(View.GONE);//提示可见,暂时都不可见
            }
        }
        mDatas.clear();

        int expandLevel = 1;
        if ((int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FUNCTION, 1) == 1) { //文件夹
            mDatas.add(new TreeModel(1, 0, strDefault));
            expandLevel = 2;
        } else { // 标签
            mTree.setVisibility(View.GONE);
            list_no_data_ll.setVisibility(View.VISIBLE);
        }
        try {
            mAdapter = new TreeAdapter<>(mTree, mContext, mDatas, expandLevel);
            mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
                @Override
                public void onClick(Node node, int position) {
                    Intent intentTag = new Intent(getContext(), NoteByGroupActivity.class);
                    int id = node.getId();
                    String name = node.getName();
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", id);
                    bundle.putString("name", name);
                    bundle.putString("type", "file");
                    intentTag.putExtras(bundle);
                    startActivity(intentTag);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        mTree.setAdapter(mAdapter);
        mTree.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                itemLongClick(position);
                return true;
            }
        });

        refreshGetData();
    }

    public void itemLongClick(final int position) {
        if (!(Boolean) SharedPreferencesUtils.getParam(mApp, CommonUtil.ISLOGIN, false)) {
            ToastUtil.show("还未登录, 请先登录再进行操作");
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            final Node item = (Node) mAdapter.getItem(position);
            new ActionSheetDialog(getContext())
                    .builder()
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(false)
                    .addSheetItem("删除", ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    String tipName = "确认删除该标签？";
                                    if ((int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FUNCTION, 1) == 1) { //文件夹
                                        tipName = "确认删除该文件夹？\n(若存在子级文件夹,则会一同删除!)";
                                    }
                                    AlertDialog dialog = new AlertDialog(mContext);
                                    dialog.builder().setMsg(tipName).setNegativeButton("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).setPositiveButton("提交", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            delNoteFileOrTagById(item);
                                            CommonUtil.longClickDeal(mApp, false);
                                        }
                                    }).show();
                                }
                            })
                    .addSheetItem("重命名", ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                }
                            }).show();
        }
    }

    private void delNoteFileOrTagById(Node item) {
        if (item != null) {
            int id = item.getId();
            CommonUtil.delFileTagHandler = mHandler;
            showLoadingDialog();
            String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_DEL_FILE_TAG + "?uid=" + uid + "&token=" + token + "&id=" + id + "&type=" + SharedPreferencesUtils.getParam(mApp, CommonUtil.FUNCTION, 1);
            OkHttpClientManager.getAsyn(delDataUrl, new OkHttpClientManager.ResultCallback<GsonNoteTipModel>() {
                @Override
                public void onError(Request request, Exception e) {
                    e.printStackTrace();
                    dismissLoadingDialog();
                }

                @Override
                public void onResponse(GsonNoteTipModel gsonModel) {
                    dismissLoadingDialog();
                    if (gsonModel != null) {
                        ToastUtil.show(gsonModel.getInfo());
                        if (Integer.parseInt(gsonModel.getCode()) == 1) {
                            if (CommonUtil.delFileTagHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.delFileTagCode;
                                message.obj = gsonModel.getInfo();
                                CommonUtil.delFileTagHandler.sendMessage(message);
                            }
                        } else {
                            ToastUtil.show(gsonModel.getInfo());
                        }
                    }
                }
            });
        }
    }

    /**
     * 点击更多按钮
     */
    public void MenuMoreClick() {
        dialog.show();
    }

    /**
     * 跳转新建文件夹
     *
     * @param context
     */
    public void newData(Context context) {
        if ((int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FUNCTION, 1) == 1) {
            startActivity(new Intent(context, NewNoteTreeActivity.class));
        } else {
            startActivity(new Intent(context, NewNoteTagActivity.class));
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.refreshFileFragDataCode:
                    if ((int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FUNCTION, 1) == 1) {
                        getFileData();
                    } else {
                        getTagData();
                    }
                    break;
                case CommonUtil.delFileTagCode:
                    if ((int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FUNCTION, 1) == 1) {
                        getFileData();
                    } else {
                        getTagData();
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fun_rl1:
                file_iv.setVisibility(View.VISIBLE);
                tag_iv.setVisibility(View.GONE);
                SharedPreferencesUtils.setParam(mApp, CommonUtil.FUNCTION, 1);
                ((MainActivity) getActivity()).changeAnimateText(1);
                getFileData();
                dialog.dismiss();
                break;
            case R.id.fun_rl2:
                file_iv.setVisibility(View.GONE);
                tag_iv.setVisibility(View.VISIBLE);
                SharedPreferencesUtils.setParam(mApp, CommonUtil.FUNCTION, 2);
                ((MainActivity) getActivity()).changeAnimateText(2);
                getTagData();
                dialog.dismiss();
                break;
            case R.id.fragment_list_del:
                SharedPreferencesUtils.setParam(mApp, CommonUtil.FRAGMENT_LIST_DEL, 1);
                fragment_list_tip.setVisibility(View.GONE);
                break;
        }
    }
}