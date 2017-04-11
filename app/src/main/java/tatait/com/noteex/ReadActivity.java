package tatait.com.noteex;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.squareup.okhttp.Request;

import java.util.ArrayList;

import tatait.com.noteex.adapter.MissionAdapter;
import tatait.com.noteex.model.Constants;
import tatait.com.noteex.model.GsonFictionModel;
import tatait.com.noteex.util.HttpRoute;
import tatait.com.noteex.util.OkHttpClientManager;
import tatait.com.noteex.util.ProgressDialogBar;
import tatait.com.noteex.util.SharedPreferencesUtils;
import tatait.com.noteex.util.SwipeRefreshView;
import tatait.com.noteex.util.ToastUtil;

/**
 * 列表
 */
public class ReadActivity extends BaseActivity {
    private Activity mContext;
    private LinearLayout mission_layout_back;
    private ProgressDialogBar progressDialogBar;
    private ListView mission_list;
    private TextView mission_layout_title, mission_layout_right;
    private ArrayList<GsonFictionModel.Fiction> listData;
    private MissionAdapter missionAdapter;
    private SwipeRefreshView mSwipeLayout;
    private int page, pageSize, total;
    private String type, category;
    private InterstitialAD iad;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mission);
//        SharedPreferencesUtils.setParam(getApplicationContext(), "autoCaoliuIsOk", "yes");
        mContext = this;
        mission_layout_back = getViewById(R.id.mission_layout_back);
        mission_layout_title = getViewById(R.id.mission_layout_title);
        mission_list = getViewById(R.id.mission_list);
        mission_layout_right = getViewById(R.id.mission_layout_right);
        mSwipeLayout = getViewById(R.id.mission_swipe_ly);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGetData(true);
            }
        });
        // 设置下拉进度的背景颜色，默认就是白色的
        mSwipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        mSwipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeLayout.setOnLoadListener(new SwipeRefreshView.OnLoadListener() {
            @Override
            public void onLoad() {
                refreshGetData(false);
            }
        });
        boolean isVip = (boolean) SharedPreferencesUtils.getParam(getApplicationContext(),"isVip",false);
        if(!isVip) {
            getIAD().setADListener(new AbstractInterstitialADListener() {

                @Override
                public void onNoAD(int arg0) {
                    Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
                }

                @Override
                public void onADReceive() {
                    Log.i("AD_DEMO", "onADReceive");
                    iad.show();
                }
            });
            iad.loadAD();
            //
            showAsPopup();
        }
    }

    @Override
    protected void setListener() {
        mission_layout_back.setOnClickListener(this);
        mission_layout_right.setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        listData = new ArrayList<>();
        missionAdapter = new MissionAdapter(mContext, listData);
        mission_list.setAdapter(missionAdapter);
        pageSize = 10;
        type = getIntent().getStringExtra("type");
        category = getIntent().getStringExtra("category");
        if ("fiction".equals(category)) {
            mission_layout_title.setText("文学区");
        } else if ("pic".equals(category)) {
            mission_layout_title.setText("图片区");
        } else {
            mission_layout_title.setText("下载区");
        }
        refreshGetData(true);
    }

    private void refreshGetData(final boolean isFirst) {
        //开启遮罩
        progressDialogBar = ProgressDialogBar.createDialogwithmsg(mContext, "请稍候...");
        if (!progressDialogBar.isShowing()) {
            progressDialogBar.show();
        }

//        // 自动刷新
//        CommonUtil.autoGetData(getApplicationContext(),"2");

        if (isFirst) {
            page = 1;
        } else {
            page = page + 1;
            if (page > total) {
                mSwipeLayout.setLoading(false);
                if (progressDialogBar.isShowing()) {
                    progressDialogBar.dismiss();
                }
                ToastUtil.show("无更多数据!");
                return;
            }
        }
        String url = "";
        if ("fiction".equals(category)) {
            url = HttpRoute.URL_HEAD + HttpRoute.URL_GET_FL_FICTION + "?type=" + type + "&pageIndex=" + page + "&pageSize=" + pageSize;
        } else {
            url = HttpRoute.URL_HEAD + HttpRoute.URL_GET_FL_PIC + "?type=" + type + "&pageIndex=" + page + "&pageSize=" + pageSize;
        }
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<GsonFictionModel>() {
            @Override
            public void onError(Request request, Exception e) {
                if (isFirst) {
                    mSwipeLayout.setRefreshing(false);
                } else {
                    mSwipeLayout.setLoading(false);
                }
                if (progressDialogBar.isShowing()) {
                    progressDialogBar.dismiss();
                }
                e.printStackTrace();
                ToastUtil.show("网络请求失败，请稍候再试！");
            }

            @Override
            public void onResponse(GsonFictionModel gsonModel) {
                if (isFirst) {
                    mSwipeLayout.setRefreshing(false);
                } else {
                    mSwipeLayout.setLoading(false);
                }
                if (progressDialogBar.isShowing()) {
                    progressDialogBar.dismiss();
                }
                if (gsonModel != null) {
                    if ("1".equals(gsonModel.getCode().toString())) {
                        if (!gsonModel.getData().isEmpty()) {
                            if (isFirst) {
                                listData.clear();
                            }
                            for (int i = 0; i < gsonModel.getData().size(); i++) {
                                listData.add(gsonModel.getData().get(i));
                            }
                            missionAdapter.notifyDataSetChanged();
                            total = gsonModel.getTotal();
                        }
                    } else {
                        ToastUtil.show(gsonModel.getInfo());
                    }
                }
            }
        });
    }

    private InterstitialAD getIAD() {
        if (iad == null) {
            iad = new InterstitialAD(this, Constants.APPID, Constants.InterteristalPosID);
        }
        return iad;
    }

    private void showAsPopup() {
        getIAD().setADListener(new AbstractInterstitialADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
            }

            @Override
            public void onADReceive() {
                iad.showAsPopupWindow();
            }
        });
        iad.loadAD();
    }


    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            //返回
            case R.id.mission_layout_back:
                finish();
                break;
            case R.id.mission_layout_right:
                if (iad != null) {
                    iad.closePopupWindow();
                }
                break;
        }
    }
}