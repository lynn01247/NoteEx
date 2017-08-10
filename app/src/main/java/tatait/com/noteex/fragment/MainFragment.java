package tatait.com.noteex.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import tatait.com.noteex.LoginActivity;
import tatait.com.noteex.NewNoteActivity;
import tatait.com.noteex.R;
import tatait.com.noteex.adapter.MainFragmentAdapter;
import tatait.com.noteex.model.GsonNoteModel;
import tatait.com.noteex.model.GsonNoteTipModel;
import tatait.com.noteex.model.RefreshModel;
import tatait.com.noteex.util.ActionSheetDialog;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CircleImageView;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.PopupDialog;
import tatait.com.noteex.util.SectionDecoration;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.ToastUtil;

/**
 * 笔记·心情
 */
public class MainFragment extends BaseFragment implements BGAOnRVItemClickListener, BGAOnRVItemLongClickListener,
        View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {
    private RecyclerView mDataRv;
    private SectionDecoration sectionDecoration;
    private MainFragmentAdapter mAdapter;
    private List<RefreshModel> dataList;
    private BGARefreshLayout mRefreshLayout;
    private RelativeLayout see_rl, see_rl2, see_rl3, sore_rl1, sore_rl2, fragment_list_tip, fragment_no_data,fragment_vip_tip;
    private Button no_data_tip_ll;
    private ImageView see_show_iv, see_show_iv2, see_show_iv3, sore_create_iv, sore_update_iv;
    private TextView no_data_tip_tv;
    private PopupDialog dialog;

    private int pageSize;
    private int mTotalPageNumber = 1;
    private int mMorePageNumber = 1;
    private int order;

    private int uid, del_tag,del_vip;
    private String token;
    private String getDataUrl;
    private Context mContext;

    private CircleImageView fragment_list_del,fragment_vip_del;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_recycler_list);
        mRefreshLayout = getViewById(R.id.refreshLayout);
        fragment_vip_tip = getViewById(R.id.fragment_vip_tip);
        fragment_list_tip = getViewById(R.id.fragment_list_tip);
        fragment_list_del = getViewById(R.id.fragment_list_del);
        fragment_vip_del = getViewById(R.id.fragment_vip_del);
        fragment_no_data = getViewById(R.id.fragment_no_data);
        mDataRv = getViewById(R.id.data);
        no_data_tip_ll = getViewById(R.id.no_data_tip_ll);
        no_data_tip_tv = getViewById(R.id.no_data_tip_tv);

        mContext = getActivity();
        dialog = new PopupDialog(mContext, R.layout.popup_window_note).builder();

        View view = dialog.getLayoutView();
        // 获取自定义Dialog布局中的控件
        sore_rl1 = (RelativeLayout) view.findViewById(R.id.sore_rl1);
        sore_rl2 = (RelativeLayout) view.findViewById(R.id.sore_rl2);
        see_rl = (RelativeLayout) view.findViewById(R.id.see_rl);
        see_rl2 = (RelativeLayout) view.findViewById(R.id.see_rl2);
        see_rl3 = (RelativeLayout) view.findViewById(R.id.see_rl3);
        see_show_iv = (ImageView) view.findViewById(R.id.see_show_iv);
        see_show_iv2 = (ImageView) view.findViewById(R.id.see_show_iv2);
        see_show_iv3 = (ImageView) view.findViewById(R.id.see_show_iv3);
        sore_create_iv = (ImageView) view.findViewById(R.id.sore_create_iv);
        sore_update_iv = (ImageView) view.findViewById(R.id.sore_update_iv);

        CommonUtil.itemClickHandler = mHandler;
    }

    @Override
    protected void setListener() {
        mAdapter = new MainFragmentAdapter(mDataRv);
        mAdapter.setOnRVItemClickListener(this);
        mAdapter.setOnRVItemLongClickListener(this);

        //自定义listener
        sore_rl1.setOnClickListener(this);
        sore_rl2.setOnClickListener(this);
        see_rl.setOnClickListener(this);
        see_rl2.setOnClickListener(this);
        see_rl3.setOnClickListener(this);
        fragment_list_del.setOnClickListener(this);
        fragment_vip_del.setOnClickListener(this);
        no_data_tip_ll.setOnClickListener(this);

        mRefreshLayout.setDelegate(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        sectionDecoration = new SectionDecoration(dataList, mApp, new SectionDecoration.DecorationCallback() {
            //返回标记id (即每一项对应的标志性的字符串)
            @Override
            public String getGroupId(int position) {
                if (position > 0 && dataList.get(position) != null && dataList.get(position).getName() != null) {
                    return dataList.get(position).getName();
                }
                return "-1";
            }

            //获取同组中的第一个内容
            @Override
            public String getGroupFirstLine(int position) {
                if (position > 0 && dataList.get(position) != null && dataList.get(position).getName() != null) {
                    return dataList.get(position).getName();
                }
                return "";
            }
        });
//        mDataRv.addItemDecoration(new Divider(mApp));
        mDataRv.addItemDecoration(sectionDecoration);
        mDataRv.setLayoutManager(new LinearLayoutManager(mApp, LinearLayoutManager.VERTICAL, false));
        mDataRv.setAdapter(mAdapter);

        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mApp, true));

        uid = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.UID, 0);
        token = (String) SharedPreferencesUtils.getParam(mApp, CommonUtil.TOKEN, "1");
        pageSize = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.PAGESIZE, 10);
        order = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.ORDER, 1);

        if (!(Boolean) SharedPreferencesUtils.getParam(mApp, CommonUtil.ISLOGIN, false)) {
            fragment_list_tip.setVisibility(View.GONE);//提示不可见
        }else{
            del_tag = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FRAGMENT_LIST_DEL, 0);
            if (del_tag == 0) {
                fragment_list_tip.setVisibility(View.VISIBLE);//提示可见
            }
        }


        if (order == 1) {
            sore_create_iv.setVisibility(View.VISIBLE);
            sore_update_iv.setVisibility(View.GONE);
        } else {
            sore_create_iv.setVisibility(View.GONE);
            sore_update_iv.setVisibility(View.VISIBLE);
        }
        if (pageSize == 10) {
            see_show_iv2.setVisibility(View.GONE);
            see_show_iv3.setVisibility(View.GONE);
            see_show_iv.setVisibility(View.VISIBLE);
        } else if (pageSize == 20) {
            see_show_iv.setVisibility(View.GONE);
            see_show_iv3.setVisibility(View.GONE);
            see_show_iv2.setVisibility(View.VISIBLE);
        } else {
            see_show_iv.setVisibility(View.GONE);
            see_show_iv2.setVisibility(View.GONE);
            see_show_iv3.setVisibility(View.VISIBLE);
        }
        String link = (String)SharedPreferencesUtils.getParam(getActivity().getApplicationContext(),"link","false");
        if("true".equals(link)){
            fragment_vip_tip.setVisibility(View.VISIBLE);
        }else{
            fragment_vip_tip.setVisibility(View.GONE);
        }
        del_vip = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.FRAGMENT_VIP_DEL, 0);
        if (del_vip == 1) {
            fragment_vip_tip.setVisibility(View.GONE);//提示可见
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onLazyLoadOnce() {
        refreshGetData(true);
    }


    @Override
    public void onRVItemClick(ViewGroup viewGroup, View itemView, int position) {
        Map<String, Object> map = new HashMap<>();
        map.put("nid", mAdapter.getItem(position).nid);
        map.put("title", mAdapter.getItem(position).title);
        map.put("content", mAdapter.getItem(position).content);
        map.put("tag", mAdapter.getItem(position).tag);
        map.put("category", mAdapter.getItem(position).category);
        map.put("tagid", mAdapter.getItem(position).tagid);
        map.put("treeid", mAdapter.getItem(position).treeid);
        CommonUtil.map = map;
    }

    @Override
    public boolean onRVItemLongClick(ViewGroup viewGroup, View itemView, final int position) {
        if (!(Boolean) SharedPreferencesUtils.getParam(mApp, CommonUtil.ISLOGIN, false)) {
            ToastUtil.show("还未登录, 请先登录再进行操作");
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            CommonUtil.itemClickHandler = null;
            CommonUtil.dialogHandler = mHandler;
            final GsonNoteModel.NoteModel item = mAdapter.getItem(position);
            String collectStr = "收藏";
            String sharedStr = "分享";
            if (item != null && "true".equals(item.getCollected())) {
                collectStr = "取消收藏";
            }
            if (item != null && ("new_shared".equals(item.getType()) || "best_shared".equals(item.getType())  || "gem_shared".equals(item.getType()))) {
                sharedStr = "取消分享";
            }
            SharedPreferencesUtils.setParam(mApp, CommonUtil.LONGCLICKNUM, ((int) SharedPreferencesUtils.getParam(mApp, CommonUtil.LONGCLICKNUM, 1)) + 1);
            ActionSheetDialog actionSheetDialog = new ActionSheetDialog(getContext()).builder();
            actionSheetDialog.setCancelable(false)
                    .setCanceledOnTouchOutside(false)
                    .addSheetItem("删除", ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    AlertDialog dialog = new AlertDialog(mContext);
                                    dialog.builder().setMsg("确认删除该笔记?").setNegativeButton("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).setPositiveButton("提交", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            delNoteById(item);
                                            mAdapter.removeItem(position);
                                            CommonUtil.longClickDeal(mApp, true);
                                        }
                                    }).show();
                                }
                            })
                    .addSheetItem(collectStr, ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    String collectStrTip = "确认收藏该笔记?";
                                    if (item != null && "true".equals(item.getCollected())) {
                                        collectStrTip = "确认取消收藏该笔记?";
                                    }
                                    AlertDialog dialog = new AlertDialog(mContext);
                                    dialog.builder().setMsg(collectStrTip).setNegativeButton("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).setPositiveButton("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CommonUtil.longClickDeal(mApp, true);
                                            if (item != null && "true".equals(item.getCollected())) {
                                                delNoteCollectById(item);
                                            } else {
                                                collectNoteById(item);
                                            }
                                        }
                                    }).show();
                                }
                            })
                    .addSheetItem(sharedStr, ActionSheetDialog.SheetItemColor.Blue,
                            new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    String sharedStrTip = "确认分享该笔记?";
                                    if (item != null && ("new_shared".equals(item.getType()) || "best_shared".equals(item.getType())  || "gem_shared".equals(item.getType()))) {
                                        sharedStrTip = "确认取消分享该笔记?";
                                    }
                                    AlertDialog dialog = new AlertDialog(mContext);
                                    dialog.builder().setMsg(sharedStrTip).setNegativeButton("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).setPositiveButton("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CommonUtil.longClickDeal(mApp, true);
                                            if (item != null && "shared".equals(item.getType())) {
                                                delNoteSharedById(item);
                                            } else {
                                                shareNoteById(item);
                                            }
                                        }
                                    }).show();
                                }
                            });
            if ((int) SharedPreferencesUtils.getParam(mApp, CommonUtil.LONGCLICKNUM, 1) <= 2) {
                actionSheetDialog.show();
            }
        }
        return true;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        refreshGetData(true);
    }


    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (mMorePageNumber >= mTotalPageNumber) {
            mRefreshLayout.endLoadingMore();
            showToast("没有更多数据了");
            return false;
        }
        refreshGetData(false);
        return false;
    }

    public void refreshGetData(final Boolean isRefreshed) {
        if (isRefreshed) {
            mDataRv.scrollToPosition(0);
            mMorePageNumber = 1;
        } else {
            mMorePageNumber++;
        }
        uid = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.UID, 0);
        token = (String) SharedPreferencesUtils.getParam(mApp, CommonUtil.TOKEN, "1");
        order = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.ORDER, 1);
        pageSize = (int) SharedPreferencesUtils.getParam(mApp, CommonUtil.PAGESIZE, 10);
        showLoadingDialog();
        getDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_GET_NOTE + "?uid=" + uid + "&token=" + token + "&order=" + order + "&pageIndex=" + mMorePageNumber + "&pageSize=" + pageSize;
        OkHttpClientManager.getAsyn(getDataUrl, new OkHttpClientManager.ResultCallback<GsonNoteModel>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                dismissLoadingDialog();
                mRefreshLayout.endRefreshing();
            }

            @Override
            public void onResponse(GsonNoteModel gsonModel) {
                dismissLoadingDialog();
                mRefreshLayout.endRefreshing();
                if (gsonModel != null && gsonModel.getData() != null && gsonModel.getData().size() > 0) {
                    fragment_no_data.setVisibility(View.GONE);
                    mDataRv.setVisibility(View.VISIBLE);
                    Log.i(TAG, gsonModel.getData().toString());
                    mTotalPageNumber = gsonModel.getTotal();
                    mMorePageNumber = gsonModel.getPage();
                    if (isRefreshed) {
                        setPullAction(gsonModel.getData(), isRefreshed);
                        mAdapter.setData(gsonModel.getData());
                    } else {
                        setPullAction(gsonModel.getData(), isRefreshed);
                        mAdapter.addMoreData(gsonModel.getData());
                    }
                } else {
                    fragment_list_tip.setVisibility(View.GONE);
                    fragment_no_data.setVisibility(View.VISIBLE);
                    if (!(Boolean) SharedPreferencesUtils.getParam(mApp, CommonUtil.ISLOGIN, false)) {
                        no_data_tip_tv.setText("登录后可以同步获取在线笔记信息哦 ^_^");
                        no_data_tip_ll.setText("立即登录");
                    }else{
                        no_data_tip_tv.setText("暂无笔记哦, 赶快新建笔记上传吧!");
                        no_data_tip_ll.setText("立即新建");
                    }
                    mDataRv.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setPullAction(List<GsonNoteModel.NoteModel> noteModels, Boolean isRefreshed) {
        if (isRefreshed || dataList == null) {
            dataList = new ArrayList<>();
        }
        for (int i = 0; i < noteModels.size(); i++) {
            RefreshModel refreshModel = new RefreshModel();
            String name0 = noteModels.get(i).getUpdatedTime().substring(0, noteModels.get(i).getUpdatedTime().length() - 9);
            refreshModel.setName(name0);
            dataList.add(refreshModel);
        }
        sectionDecoration.setListDate(dataList);
    }

    public void MenuMoreClick() {
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sore_rl1:
                sore_create_iv.setVisibility(View.VISIBLE);
                sore_update_iv.setVisibility(View.GONE);
                SharedPreferencesUtils.setParam(mApp, CommonUtil.ORDER, 1);
                dialog.dismiss();
                refreshGetData(true);
                break;
            case R.id.sore_rl2:
                sore_create_iv.setVisibility(View.GONE);
                sore_update_iv.setVisibility(View.VISIBLE);
                SharedPreferencesUtils.setParam(mApp, CommonUtil.ORDER, 2);
                dialog.dismiss();
                refreshGetData(true);
                break;
            case R.id.see_rl:
                see_show_iv2.setVisibility(View.GONE);
                see_show_iv3.setVisibility(View.GONE);
                see_show_iv.setVisibility(View.VISIBLE);
                SharedPreferencesUtils.setParam(mApp, CommonUtil.PAGESIZE, 10);
                dialog.dismiss();
                refreshGetData(true);
                break;
            case R.id.see_rl2:
                see_show_iv.setVisibility(View.GONE);
                see_show_iv3.setVisibility(View.GONE);
                see_show_iv2.setVisibility(View.VISIBLE);
                SharedPreferencesUtils.setParam(mApp, CommonUtil.PAGESIZE, 20);
                dialog.dismiss();
                refreshGetData(true);
                break;
            case R.id.see_rl3:
                see_show_iv2.setVisibility(View.GONE);
                see_show_iv.setVisibility(View.GONE);
                see_show_iv3.setVisibility(View.VISIBLE);
                SharedPreferencesUtils.setParam(mApp, CommonUtil.PAGESIZE, 40);
                dialog.dismiss();
                refreshGetData(true);
                break;
            case R.id.fragment_list_del:
                SharedPreferencesUtils.setParam(mApp, CommonUtil.FRAGMENT_LIST_DEL, 1);
                fragment_list_tip.setVisibility(View.GONE);
                break;
            case R.id.fragment_vip_del:
                SharedPreferencesUtils.setParam(mApp, CommonUtil.FRAGMENT_VIP_DEL, 1);
                fragment_vip_tip.setVisibility(View.GONE);
                break;
            case R.id.no_data_tip_ll:
                CommonUtil.map = null;
                //判断是否登录
                if ((Boolean) SharedPreferencesUtils.getParam(mApp, CommonUtil.ISLOGIN, false)) {
                    if (android.os.Build.VERSION.SDK_INT > 20) {
                        startActivity(new Intent(getContext(), NewNoteActivity.class));
                    } else {
                        startActivity(new Intent(getContext(), NewNoteActivity.class));
                    }
                } else {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
                break;
        }
    }

    /**
     * 删除笔记
     *
     * @param item
     */
    private void delNoteById(GsonNoteModel.NoteModel item) {
        if (item != null) {
            int nid = item.getNid();
            CommonUtil.delNoteHandler = mHandler;
            showLoadingDialog();
            String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_DEL_NOTE + "?uid=" + uid + "&token=" + token + "&nid=" + nid;
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
                            if (CommonUtil.delNoteHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.delNoteCode;
                                message.obj = gsonModel.getInfo();
                                CommonUtil.delNoteHandler.sendMessage(message);
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
     * 收藏笔记
     *
     * @param item
     */
    private void collectNoteById(GsonNoteModel.NoteModel item) {
        if (item != null) {
            int nid = item.getNid();
            CommonUtil.collectNoteHandler = mHandler;
            showLoadingDialog();
            String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_POST_COLLECT_NOTE;
            OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("nid", Integer.valueOf(nid).toString()),
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
                        ToastUtil.show(gsonModel.getInfo());
                        if (Integer.parseInt(gsonModel.getCode()) == 1) {
                            if (CommonUtil.collectNoteHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.collectNoteCode;
                                message.obj = gsonModel.getInfo();
                                CommonUtil.collectNoteHandler.sendMessage(message);
                            }
                        } else {
                            ToastUtil.show(gsonModel.getInfo());
                        }
                    }
                }
            }, params);
        }
    }

    /**
     * 删除笔记收藏信息
     *
     * @param item
     */
    private void delNoteCollectById(GsonNoteModel.NoteModel item) {
        if (item != null) {
            int nid = item.getNid();
            CommonUtil.delCollectNoteHandler = mHandler;
            showLoadingDialog();
            String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_DEL_NOTE_COLLECT + "?uid=" + uid + "&token=" + token + "&nid=" + nid;
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
                            if (CommonUtil.delCollectNoteHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.delCollectNoteCode;
                                message.obj = gsonModel.getInfo();
                                CommonUtil.delCollectNoteHandler.sendMessage(message);
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
     * 分享笔记
     *
     * @param item
     */
    private void shareNoteById(GsonNoteModel.NoteModel item) {
        if (item != null) {
            int nid = item.getNid();
            CommonUtil.sharedNoteHandler = mHandler;
            showLoadingDialog();
            String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_POST_SHARED_NOTE;
            OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[]{
                    new OkHttpClientManager.Param("nid", Integer.valueOf(nid).toString()),
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
                        ToastUtil.show(gsonModel.getInfo());
                        if (Integer.parseInt(gsonModel.getCode()) == 1) {
                            if (CommonUtil.sharedNoteHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.sharedNoteCode;
                                message.obj = gsonModel.getInfo();
                                CommonUtil.sharedNoteHandler.sendMessage(message);
                            }
                        } else {
                            ToastUtil.show(gsonModel.getInfo());
                        }
                    }
                }
            }, params);
        }
    }

    /**
     * 删除笔记分享信息
     *
     * @param item
     */
    private void delNoteSharedById(GsonNoteModel.NoteModel item) {
        if (item != null) {
            int nid = item.getNid();
            CommonUtil.delSharedNoteHandler = mHandler;
            showLoadingDialog();
            String delDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_DEL_NOTE_SHARE + "?uid=" + uid + "&token=" + token + "&nid=" + nid;
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
                            if (CommonUtil.delSharedNoteHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.delSharedNoteCode;
                                message.obj = gsonModel.getInfo();
                                CommonUtil.delSharedNoteHandler.sendMessage(message);
                            }
                        } else {
                            ToastUtil.show(gsonModel.getInfo());
                        }
                    }
                }
            });
        }
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.itemClickCode:
                    startActivity(new Intent(getContext(), NewNoteActivity.class));
                    break;
                case CommonUtil.dialogCode:
                    CommonUtil.itemClickHandler = mHandler;
                    break;
                case CommonUtil.delNoteCode:
                    refreshGetData(true);
                    break;
                case CommonUtil.collectNoteCode:
                    refreshGetData(true);
                    break;
                case CommonUtil.delCollectNoteCode:
                    refreshGetData(true);
                    break;
                case CommonUtil.sharedNoteCode:
                    refreshGetData(true);
                    break;
                case CommonUtil.delSharedNoteCode:
                    refreshGetData(true);
                    break;
            }
        }
    };
}