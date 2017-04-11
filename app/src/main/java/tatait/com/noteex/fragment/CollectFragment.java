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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import tatait.com.noteex.EmChatActivity;
import tatait.com.noteex.LoginActivity;
import tatait.com.noteex.NewNoteActivity;
import tatait.com.noteex.R;
import tatait.com.noteex.WebActivity;
import tatait.com.noteex.adapter.GemFragmentAdapter;
import tatait.com.noteex.model.GsonNoteModel;
import tatait.com.noteex.model.GsonNoteTipModel;
import tatait.com.noteex.model.RefreshModel;
import tatait.com.noteex.util.ActionSheetDialog;
import tatait.com.noteex.util.AlertDialog;
import tatait.com.noteex.util.CommonUtil;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.PopupDialog;
import tatait.com.noteex.util.SectionDecoration;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.ToastUtil;
import tatait.com.noteex.widget.App;

/**
 * 收藏界面
 */
public class CollectFragment extends BaseFragment implements BGAOnItemChildClickListener, BGAOnRVItemClickListener, BGAOnRVItemLongClickListener,
        View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {
    private RecyclerView mDataRv;
    private SectionDecoration sectionDecoration;
    private GemFragmentAdapter mAdapter;
    private List<RefreshModel> dataList;
    private BGARefreshLayout mRefreshLayout;
    private RelativeLayout list_tip, see_rl, see_rl2, see_rl3, sore_rl1, sore_rl2;
    private ImageView see_show_iv, see_show_iv2, see_show_iv3, sore_create_iv, sore_update_iv;
    private PopupDialog dialog;

    private int pageSize;
    private int mTotalPageNumber = 1;
    private int mMorePageNumber = 1;
    private int order;

    private int uid;
    private String token;
    private String getDataUrl;
    private Context mContext;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_recycler_list);
        mRefreshLayout = getViewById(R.id.refreshLayout);
        mDataRv = getViewById(R.id.data);
        list_tip = getViewById(R.id.fragment_list_tip);

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

        CommonUtil.itemClickGemHandler = mHandler;
    }

    @Override
    protected void setListener() {
        mAdapter = new GemFragmentAdapter(mDataRv);
        mAdapter.setOnRVItemClickListener(this);
        mAdapter.setOnRVItemLongClickListener(this);
        mAdapter.setOnItemChildClickListener(this);

        //自定义listener
        sore_rl1.setOnClickListener(this);
        sore_rl2.setOnClickListener(this);
        see_rl.setOnClickListener(this);
        see_rl2.setOnClickListener(this);
        see_rl3.setOnClickListener(this);

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
        mDataRv.addItemDecoration(sectionDecoration);
        mDataRv.setLayoutManager(new LinearLayoutManager(mApp, LinearLayoutManager.VERTICAL, false));
        mDataRv.setAdapter(mAdapter);

        list_tip.setVisibility(View.GONE);
        mRefreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mApp, true));

        uid = (int) SharedPreferencesUtils.getParam(getContext(), CommonUtil.UID, 0);
        token = (String) SharedPreferencesUtils.getParam(getContext(), CommonUtil.TOKEN, "1");
        pageSize = (int) SharedPreferencesUtils.getParam(getContext(), CommonUtil.PAGESIZE_COLL, 10);
        order = (int) SharedPreferencesUtils.getParam(getContext(), CommonUtil.ORDER_GROUP, 1);

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
    public void onItemChildClick(ViewGroup parent, View childView, int position) {
        if (!(Boolean) SharedPreferencesUtils.getParam(getContext(), CommonUtil.ISLOGIN, false)) {
            ToastUtil.show("还未登录, 登录后才能发起会话");
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            if (mAdapter.getItem(position).from.equals("JueJin")) {
                ToastUtil.show("该用户暂时关闭聊天功能");
            } else if (mAdapter.getItem(position).emid.equals(App.getInstance().getCurrentUserName())) {
                ToastUtil.show("不能和自己聊天");
            } else {
                Intent intent = new Intent(mContext, EmChatActivity.class);
                intent.putExtra("username", mAdapter.getItem(position).emid);
                intent.putExtra("cickname", mAdapter.getItem(position).name);
                intent.putExtra("imgurl", mAdapter.getItem(position).imgurl);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View itemView, int position) {
        if(mAdapter.getItem(position).from.equals("JueJin")){
            Intent intentLink3 = new Intent(mContext, WebActivity.class);
            intentLink3.putExtra("url", mAdapter.getItem(position).jj_originalUrl);
            startActivity(intentLink3);
        }else {
            Map<String, Object> map = new HashMap<>();
            map.put("nid", mAdapter.getItem(position).nid);
            map.put("title", mAdapter.getItem(position).title);
            map.put("content", mAdapter.getItem(position).content);
            map.put("tag", mAdapter.getItem(position).tag);
            map.put("category", mAdapter.getItem(position).category);
            map.put("tagid", mAdapter.getItem(position).tagid);
            map.put("treeid", mAdapter.getItem(position).treeid);
            CommonUtil.map = map;

            Intent intent = new Intent(mContext, NewNoteActivity.class);
            intent.putExtra("type", "shared");
            startActivity(intent);
        }
    }

    @Override
    public boolean onRVItemLongClick(ViewGroup viewGroup, View itemView, final int position) {
        if (!(Boolean) SharedPreferencesUtils.getParam(getContext(), CommonUtil.ISLOGIN, false)) {
            ToastUtil.show("还未登录, 请先登录再进行操作");
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {
            final GsonNoteModel.NoteModel item = mAdapter.getItem(position);
            String collectStr = "收藏";
            if (item != null && "true".equals(item.getCollected())) {
                collectStr = "取消收藏";
            }
            SharedPreferencesUtils.setParam(getContext(), CommonUtil.LONGCLICKNUM, ((int) SharedPreferencesUtils.getParam(getContext(), CommonUtil.LONGCLICKNUM, 1)) + 1);
            ActionSheetDialog actionSheetDialog = new ActionSheetDialog(getContext()).builder();
            actionSheetDialog.setCancelable(false)
                    .setCanceledOnTouchOutside(false)
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
                                    }).setPositiveButton("提交", new View.OnClickListener() {
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
                            });
            if ((int) SharedPreferencesUtils.getParam(getContext(), CommonUtil.LONGCLICKNUM, 1) <= 2) {
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
        showLoadingDialog();
        if (isRefreshed) {
            mMorePageNumber = 1;
            mDataRv.scrollToPosition(0);
        } else {
            mMorePageNumber++;
        }
        order = (int) SharedPreferencesUtils.getParam(getContext(), CommonUtil.ORDER_COLL, 1);
        pageSize = (int) SharedPreferencesUtils.getParam(getContext(), CommonUtil.PAGESIZE_COLL, 10);

        getDataUrl = HttpRoute.URL_HEAD + HttpRoute.URL_GET_COLL + "?uid=" + uid + "&token=" + token +
                "&order=" + order + "&pageIndex=" + mMorePageNumber + "&pageSize=" + pageSize;
        OkHttpClientManager.getAsyn(getDataUrl, new OkHttpClientManager.ResultCallback<GsonNoteModel>() {
            @Override
            public void onError(Request request, Exception e) {
                e.printStackTrace();
                dismissLoadingDialog();
                mRefreshLayout.endLoadingMore();
            }

            @Override
            public void onResponse(GsonNoteModel gsonModel) {
                dismissLoadingDialog();
                if (gsonModel != null && gsonModel.getData() != null && gsonModel.getData().size() > 0) {
                    Log.i(TAG, gsonModel.getData().toString());
                    mTotalPageNumber = gsonModel.getTotal();
                    mMorePageNumber = gsonModel.getPage();
                    mRefreshLayout.endRefreshing();
                    if (isRefreshed) {
                        setPullAction(gsonModel.getData(), isRefreshed);
                        mAdapter.setData(gsonModel.getData());
                    } else {
                        setPullAction(gsonModel.getData(), isRefreshed);
                        mAdapter.addMoreData(gsonModel.getData());
                    }
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
                SharedPreferencesUtils.setParam(getContext(), CommonUtil.ORDER_COLL, 1);
                dialog.dismiss();
                refreshGetData(true);
                break;
            case R.id.sore_rl2:
                sore_create_iv.setVisibility(View.GONE);
                sore_update_iv.setVisibility(View.VISIBLE);
                SharedPreferencesUtils.setParam(getContext(), CommonUtil.ORDER_COLL, 2);
                dialog.dismiss();
                refreshGetData(true);
                break;
            case R.id.see_rl:
                see_show_iv2.setVisibility(View.GONE);
                see_show_iv3.setVisibility(View.GONE);
                see_show_iv.setVisibility(View.VISIBLE);
                SharedPreferencesUtils.setParam(getContext(), CommonUtil.PAGESIZE_COLL, 10);
                dialog.dismiss();
                refreshGetData(true);
                break;
            case R.id.see_rl2:
                see_show_iv.setVisibility(View.GONE);
                see_show_iv3.setVisibility(View.GONE);
                see_show_iv2.setVisibility(View.VISIBLE);
                SharedPreferencesUtils.setParam(getContext(), CommonUtil.PAGESIZE_COLL, 20);
                dialog.dismiss();
                refreshGetData(true);
                break;
            case R.id.see_rl3:
                see_show_iv2.setVisibility(View.GONE);
                see_show_iv.setVisibility(View.GONE);
                see_show_iv3.setVisibility(View.VISIBLE);
                SharedPreferencesUtils.setParam(getContext(), CommonUtil.PAGESIZE_COLL, 40);
                dialog.dismiss();
                refreshGetData(true);
                break;
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
            CommonUtil.collectNoteCollHandler = mHandler;
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
                            if (CommonUtil.collectNoteCollHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.collectNoteCollCode;
                                message.obj = gsonModel.getInfo();
                                CommonUtil.collectNoteCollHandler.sendMessage(message);
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
            CommonUtil.delCollectNoteCollHandler = mHandler;
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
                            if (CommonUtil.delCollectNoteCollHandler != null) {
                                Message message = new Message();
                                message.what = CommonUtil.delCollectNoteCollCode;
                                message.obj = gsonModel.getInfo();
                                CommonUtil.delCollectNoteCollHandler.sendMessage(message);
                            }
                        } else {
                            ToastUtil.show(gsonModel.getInfo());
                        }
                    }
                }
            });
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonUtil.collectNoteCollCode:
                    refreshGetData(true);
                    break;
                case CommonUtil.delCollectNoteCollCode:
                    refreshGetData(true);
                    break;
            }
        }
    };
}